package com.example.may.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.may.Adapter.AddParticipantDepartmentAdapter;
import com.example.may.Adapter.AddParticipantGroupAdapter;
import com.example.may.Interface.iAddMemberGroup;
import com.example.may.Model.Department;
import com.example.may.Model.Participant;
import com.example.may.Model.User;
import com.example.may.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AddParticipantDepartmentActivity extends AppCompatActivity implements iAddMemberGroup {
    private RecyclerView rcv_list_friend;
    private TextView submit;
    private ImageView imgReturn;

    private List<Participant> arrParticipant;
    private List<User> arrUsers, mUsers;
    private String id, name;
    private List<Participant> arrMember;

    private AddParticipantDepartmentAdapter adapter;

    private FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_participant_department);

        initUI();

        loadUser();

        imgReturn.setOnClickListener(v -> {
            onBackPressed();
        });

        submit.setOnClickListener(v -> {
            addParticipant();
            this.finish();
        });

    }

    private void initUI() {
        rcv_list_friend = findViewById(R.id.list_user);
        submit = findViewById(R.id.submit);
        imgReturn = findViewById(R.id.return_activity);

        arrUsers = new ArrayList<>();

        arrMember = new ArrayList<>();

        arrUsers = new ArrayList<>();
        arrParticipant = (List<Participant>) getIntent().getSerializableExtra("list");
        id = getIntent().getStringExtra("id_department");
        name = getIntent().getStringExtra("name");

        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL);
        rcv_list_friend.setLayoutManager(layoutManager);
        rcv_list_friend.addItemDecoration(itemDecoration);
        adapter = new AddParticipantDepartmentAdapter(arrUsers, AddParticipantDepartmentActivity.this);
        rcv_list_friend.setAdapter(adapter);
    }

    private void loadUser() {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Users");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrUsers.clear();
                for (DataSnapshot dn : snapshot.getChildren()){
                    User user = dn.getValue(User.class);

                          dbRef.child(user.getId()).child("department").addValueEventListener(new ValueEventListener() {
                              @Override
                              public void onDataChange(@NonNull DataSnapshot snapshot) {
                                  if (!snapshot.exists()){
                                      arrUsers.add(user);
                                  }

                                  adapter.notifyDataSetChanged();
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





    private void addParticipant() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Department");
        databaseReference.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    Department department = snapshot.getValue(Department.class);
                    for (Participant userGroup : arrMember){
                        databaseReference.child(id).child("participant").child(userGroup.getId()).setValue(userGroup);
                    }
                    databaseReference.child(id).child("slParticipant").setValue(department.getSlParticipant()+ arrMember.size());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
      
    
        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("Users");

        HashMap<String, Object> map = new HashMap<>();
        map.put("department", id);

        for (User u : mUsers){
            databaseReference1.child(u.getId()).updateChildren(map);
        }


        //Logs
        HashMap<String, Object> log = new HashMap<>();
        log.put("idUser", mUser.getUid());
        log.put("log", "thêm "+ mUsers.size()+" thành viên vào "+ name);
        log.put("time", System.currentTimeMillis());
        DatabaseReference dbReff = FirebaseDatabase.getInstance().getReference("Logs");
        dbReff.push().setValue(log);

        Toast.makeText(getApplicationContext(), "Thêm thành viên thành công", Toast.LENGTH_SHORT).show();
    }



    @Override
    public void addMemberGroup(List<User> arrUser) {
        mUsers = arrUser;
        for (User e: mUsers){
            arrMember.add(new Participant(e.getId(), 3, System.currentTimeMillis()));
        }
    }
}