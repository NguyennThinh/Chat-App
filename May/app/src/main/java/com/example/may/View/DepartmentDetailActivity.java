package com.example.may.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.may.Adapter.DepartmentDetailAdapter;
import com.example.may.Model.Department;
import com.example.may.Model.Participant;
import com.example.may.Model.User;
import com.example.may.R;
import com.example.may.Utilities.RandomString;
import com.example.may.ViewManager.ManagerActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import de.hdodenhof.circleimageview.CircleImageView;

public class DepartmentDetailActivity extends AppCompatActivity {
    //Views
    private ImageView imgBack, imgMore;
    private CircleImageView imgDepartment;
    private TextView departmentName, departmentMission, slParticipant;
    private RecyclerView listParticipant;
    private TextView manager;
    
    private String idDepartment;
    private List<Participant> arrParticipant;
    private    Department department;
    private User user;
    private User u;

    //Dialog
    private EditText edtManaer;

    //Adapter
    private DepartmentDetailAdapter  adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_department_detail);
        
        initUI();
        
        loadDepartment();

        loadParticipant();

        imgBack.setOnClickListener(v -> {
            onBackPressed();
        });
        imgMore.setOnClickListener(v -> {
            showPopup(v);
        });
    }


    private void initUI() {
        imgBack = findViewById(R.id.img_back);
        imgMore = findViewById(R.id.more);
        imgDepartment = findViewById(R.id.img_department);
        departmentName = findViewById(R.id.department_name);
        departmentMission = findViewById(R.id.department_mission);
        slParticipant = findViewById(R.id.sl_participant);
        listParticipant = findViewById(R.id.list_participant);
        manager = findViewById(R.id.manager);

        idDepartment = getIntent().getStringExtra("id_department");
        arrParticipant = new ArrayList<>();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        adapter = new DepartmentDetailAdapter(arrParticipant);

        listParticipant.setLayoutManager(linearLayoutManager);
        listParticipant.setAdapter(adapter);
    }

    private void loadDepartment() {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Department");
        dbRef.child(idDepartment).addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                  department = snapshot.getValue(Department.class);

                    if (department != null){
                        departmentName.setText(department.getName());
                        departmentMission.setText(department.getMission());
                        slParticipant.setText("Thành viên phòng ban: "+department.getSlParticipant());

                        if (department.getImgDepartment().equals("default")){
                            imgDepartment.setImageResource(R.drawable.ic_department);
                        }else {
                            Picasso.get().load(department.getImgDepartment()).into(imgDepartment);
                        }
                    }
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
                    databaseReference.child(department.getIdManager()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()){
                                  user = snapshot.getValue(User.class);

                                manager.setText("Trường phòng: "+user.getFullName());
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void loadParticipant() {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Department");
        Query query = dbRef.child(idDepartment).child("participant").orderByChild("role");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrParticipant.clear();
                for (DataSnapshot dn : snapshot.getChildren()){
                    Participant participant = dn.getValue(Participant.class);
                    if (participant != null){
                        arrParticipant.add(participant);
                    }

                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    public void showPopup(View v) {
        PopupMenu popupMenu = new PopupMenu(this, v);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
             /*       case R.id.add_participant:
                        Intent i = new Intent(DepartmentDetailActivity.this, AddParticipantDepartmentActivity.class);
                        i.putExtra("list", (Serializable) arrParticipant);
                        i.putExtra("id_department", department.getId());
                        i.putExtra("name", department.getName());
                        startActivity(i);
                        break;*/
                    case R.id.update_department:
                        openDialogUpdate();
                        break;
                    case R.id.add_position:
                        showDialog();
                        break;
                }
                return true;
            }
        });
        popupMenu.inflate(R.menu.menu_department);

        Object menuHelper;
        Class[] argTypes;
        try {
            @SuppressLint("DiscouragedPrivateApi") Field fMenuHelper = PopupMenu.class.getDeclaredField("mPopup");
            fMenuHelper.setAccessible(true);
            menuHelper = fMenuHelper.get(popupMenu);
            argTypes = new Class[]{boolean.class};
            assert menuHelper != null;
            menuHelper.getClass().getDeclaredMethod("setForceShowIcon", argTypes).invoke(menuHelper, true);
        } catch (Exception ignored) {

        }
        popupMenu.show();
    }


    private void openDialogUpdate() {

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_update_department);
        Window window = dialog.getWindow();

        if (window == null) {
            return;
        }
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams layoutParams = window.getAttributes();

        layoutParams.gravity = Gravity.CENTER;

        window.setAttributes(layoutParams);
        dialog.setCancelable(false);
        //Views dialog
        //Views dialog
        EditText name_department, mission_department;
        ImageView img_manager;
        Button btnAdd, btnCancel;


        btnAdd = dialog.findViewById(R.id.btn_add);
        btnCancel = dialog.findViewById(R.id.btn_cancel);

        name_department = dialog.findViewById(R.id.name);
        mission_department = dialog.findViewById(R.id.department_title);
        edtManaer = dialog.findViewById(R.id.manager);
        img_manager = dialog.findViewById(R.id.img_manager);


        name_department.setText(department.getName());
        mission_department.setText(department.getMission());
        manager.setText(user.getFullName());




        img_manager.setOnClickListener(v -> {
            Intent i = new Intent(this, ManagerActivity.class);
            i.putExtra("list", (Serializable) arrParticipant);
            startActivityForResult(i, 1);

        });


        btnAdd.setOnClickListener(v -> {
            HashMap<String, Object> departments = new HashMap<>();
            departments.put("name", name_department.getText().toString());
            departments.put("mission", mission_department.getText().toString());
            if (u != null){
                departments.put("idManager", u.getId());

            }

            updateWork(departments,dialog,department);
        });

        btnCancel.setOnClickListener(v -> {
            dialog.cancel();
        });

        dialog.show();
    }
    private void updateWork(HashMap<String, Object> map, Dialog dialog, Department department) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Department");
        if (u != null) {
            HashMap<String, Object> m = new HashMap<>();
            m.put("participant/" + department.getIdManager() + "/role", 3);
            m.put("participant/" + u.getId() + "/role", 1);


            databaseReference.child(department.getId()).updateChildren(m);

        }

        databaseReference.child(department.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    snapshot.getRef().updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(dialog.getContext(), "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    });
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
                u = (User) data.getExtras().get("leader");
                edtManaer.setText(u.getFullName());

            }
        }

    }

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Tham gia phòng");
        //setLayout Dialog
        LinearLayout layout = new LinearLayout(this);

        //set views in dialog

        TextView tv = new TextView(this);
        tv.setText("Nhập mã phòng:");
        EditText edtKey = new EditText(this);
        edtKey.setMinEms(15);

        layout.addView(edtKey);
        layout.setPadding(10,10,10,0);

        builder.setView(layout);
        builder.setPositiveButton("Xác nhận", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                RandomString gen = new RandomString(8, ThreadLocalRandom.current());
                String id = gen.nextString();

                HashMap<String, Object> position = new HashMap<>();
                position.put("id",id);
                position.put("name", edtKey.getText().toString());

                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Position");
                databaseReference.child(department.getId()).child(id).setValue(position).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(getApplicationContext(), "Thêm chức vụ thành công", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        builder.setNegativeButton("Hủy bỏ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
            }
        });

        if (!DepartmentDetailActivity.this.isFinishing()){
            builder.show();
        }
    }
}