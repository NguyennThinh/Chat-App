package com.example.may.ViewManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.may.Adapter.LayoutUserChoosenAdapter;
import com.example.may.AdapterManager.LayoutUserSelectDepartmentAdapter;
import com.example.may.Interface.iAddMemberGroup;
import com.example.may.Model.Participant;
import com.example.may.Model.User;
import com.example.may.R;
import com.example.may.Utilities.RandomString;
import com.example.may.View.AddParticipantDepartmentActivity;
import com.example.may.View.ListUserCreateDepartment;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class CreateDepartment extends AppCompatActivity implements iAddMemberGroup {
    //Views
    private RecyclerView list_user_select, list_user_choosen;
    private LinearLayout layout_participant;

    private TextInputEditText inputName, inputManager,inputMission;
    private ImageView imgCreate, imgBack, img_manager;
    private Button btnCreate;
    private List<User> arrUsers, arrUserChosen;
    private List<Participant> arrParticipant;
    private User users;

    private LayoutUserChoosenAdapter adapterUserChoosen;
    private LayoutUserSelectDepartmentAdapter adapterUserSelect;
    //Firebae
    private FirebaseUser mUser;
    private StorageReference storageReference;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_department);


        initUI();
        loadEmployee();
        img_manager.setOnClickListener(v -> {
            Intent i = new Intent(this, ListUserCreateDepartment.class);
            i.putExtra("list", (Serializable) arrParticipant);
            startActivityForResult(i, 1);
        });
        btnCreate.setOnClickListener(v -> {
            createDepartment();
        });
        imgBack.setOnClickListener(v -> {
            onBackPressed();
        });
    }

    private void initUI() {

        list_user_select = findViewById(R.id.list_user);
        list_user_choosen = findViewById(R.id.lisst_user_choosen);
        layout_participant = findViewById(R.id.layout_bottom);
        img_manager = findViewById(R.id.img_manager);
        inputName = findViewById(R.id.inputName);
        imgCreate = findViewById(R.id.create_group);
        imgBack = findViewById(R.id.img_back);
        inputManager = findViewById(R.id.inputManager);
        inputMission = findViewById(R.id.inputMission);
        btnCreate = findViewById(R.id.btn_create);

        mUser = FirebaseAuth.getInstance().getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference();

        arrUsers = new ArrayList<>();
        arrUserChosen = new ArrayList<>();
        arrParticipant = new ArrayList<>();

        progressDialog = new ProgressDialog(this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL);

        list_user_select.setAdapter(adapterUserSelect);
        list_user_select.setLayoutManager(layoutManager);
        list_user_select.addItemDecoration(itemDecoration);



    }
    private void loadEmployee() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrUsers.clear();
                for (DataSnapshot dn : snapshot.getChildren()){
                    User user = dn.getValue(User.class);

                    if (user != null ){
                        databaseReference.child(user.getId()).child("department").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (!snapshot.exists()){
                                    arrUsers.add(user);

                                    if (users != null){
                                        adapterUserSelect = new LayoutUserSelectDepartmentAdapter(arrUsers, users.getId(), CreateDepartment.this);
                                    }else {
                                        adapterUserSelect = new LayoutUserSelectDepartmentAdapter(arrUsers,"", CreateDepartment.this);
                                    }
                                    list_user_select.setAdapter(adapterUserSelect);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });



                    }
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {
                users = (User) data.getExtras().get("leader");
                inputManager.setText(users.getFullName());
                layout_participant.setVisibility(View.GONE);
                loadEmployee();
            }
        }
    }


    @Override
    public void addMemberGroup(List<User> arrEmployees) {
        arrUserChosen = arrEmployees;

        if (arrUserChosen.size() ==0){
            layout_participant.setVisibility(View.VISIBLE);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
            list_user_choosen.setLayoutManager(linearLayoutManager);
            adapterUserChoosen = new LayoutUserChoosenAdapter(arrUserChosen);
            list_user_choosen.setAdapter(adapterUserChoosen);

        }else {
            layout_participant.setVisibility(View.GONE);
        }
    }


    private void createDepartment() {
        progressDialog.setMessage("Đang tạo phòng ban "+ inputName.getText().toString());
        progressDialog.show();
        RandomString gen = new RandomString(15, ThreadLocalRandom.current());
        String id = gen.nextString();
        Long timeCreate = System.currentTimeMillis();

        String name = inputName.getText().toString().trim();
        String idManager = users.getId();
        String mission = inputMission.getText().toString();
        int slParticipant = arrParticipant.size();
        String imgDepartment = "default";

        if (name.equals("") ||idManager.equals("") ||mission.equals("")  ){
            Toast.makeText(getApplicationContext(),"Bạn chưa nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
        }else {
            arrParticipant.add(new Participant(users.getId(), 1, timeCreate));
            for (User u: arrUserChosen){
                arrParticipant.add(new Participant(u.getId(),3, timeCreate));

            }

            HashMap<String, Object> map = new HashMap<>();
            map.put("id", id);
            map.put("name", inputName.getText().toString().trim());
            map.put("idManager", "");
            map.put("mission", inputMission.getText().toString());
            map.put("slParticipant", arrParticipant.size());
            map.put("createDate", timeCreate);
            map.put("imgDepartment", "default");



            DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Department");
            dbRef.child(id).setValue(map);

            //Logs
            HashMap<String, Object> log = new HashMap<>();
            log.put("idUser", mUser.getUid());
            log.put("log", "tạo "+ inputName.getText().toString().trim());
            log.put("time", timeCreate);
            DatabaseReference dbReff = FirebaseDatabase.getInstance().getReference("Logs");
            dbReff.push().setValue(log);

            for (Participant p : arrParticipant){


                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
                databaseReference.child(p.getId()).child("department").push().setValue(id);


                dbRef.child(id).child("participant").child(p.getId()).setValue(p).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Tạo phòng ban thành công", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });


            }

        }

      }

}