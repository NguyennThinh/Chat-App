package com.example.may.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.may.Adapter.AddParticipantGroupAdapter;
import com.example.may.Interface.iAddMemberGroup;
import com.example.may.Model.Department;
import com.example.may.Model.Group;
import com.example.may.Model.Participant;
import com.example.may.Model.User;
import com.example.may.Model.Work;
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

public class AddParticipantGroupActivity extends AppCompatActivity implements iAddMemberGroup {
    private RecyclerView rcv_list_friend;
    private TextView submit;
    private ImageView imgReturn;
    private List<String> arrIdUser;
    List<User> arrUsers, mUsers;
    private FirebaseUser mUser;
    private String groupID, type, idWork, idDepartment;
    private List<Participant> arrMember;

    private AddParticipantGroupAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_participant_group);
        initUI();


        if (type.equals("work")){
            idWork = getIntent().getStringExtra("id_work");
            loadUserWork(idWork);
        }else {
            idDepartment = getIntent().getStringExtra("id_department");
            loadUser(idDepartment);
        }


        submit.setOnClickListener(view->{
            addParticipant();
            this.finish();
        });
    }


    private void initUI() {
        rcv_list_friend = findViewById(R.id.list_user);
        submit = findViewById(R.id.submit);
        imgReturn = findViewById(R.id.return_activity);

        imgReturn.setOnClickListener(v -> {
            onBackPressed();
        });

        mUser = FirebaseAuth.getInstance().getCurrentUser();

        groupID = getIntent().getStringExtra("group_id");
        type = getIntent().getStringExtra("type");

        arrUsers = new ArrayList<>();
        arrIdUser = new ArrayList<>();
        arrMember = new ArrayList<>();

        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL);
        rcv_list_friend.setLayoutManager(layoutManager);
        rcv_list_friend.addItemDecoration(itemDecoration);

        adapter = new AddParticipantGroupAdapter(arrUsers,groupID, AddParticipantGroupActivity.this);
        rcv_list_friend.setAdapter(adapter);
    }
    private void loadUserWork(String idWork) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Work");
        databaseReference.child(mUser.getUid()).child(idWork).child("participant").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                    arrUsers.clear();
                   for (DataSnapshot dn : snapshot.getChildren()){
                       Participant participant = dn.getValue(Participant.class);
                       DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
                       reference.child(participant.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
                           @Override
                           public void onDataChange(@NonNull DataSnapshot snapshot) {
                               if (snapshot.exists()){
                                   User user = snapshot.getValue(User.class);
                                   arrUsers.add(user);
                                   adapter.notifyDataSetChanged();
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

    private void loadUser(String idDepartment) {
      DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Department");
      databaseReference.child(idDepartment).child("participant").addListenerForSingleValueEvent(new ValueEventListener() {
          @Override
          public void onDataChange(@NonNull DataSnapshot snapshot) {
              if (snapshot.exists()){
                  arrUsers.clear();
                  for (DataSnapshot dn : snapshot.getChildren()) {
                      Participant participant = dn.getValue(Participant.class);
                      DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
                      reference.child(participant.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
                          @Override
                          public void onDataChange(@NonNull DataSnapshot snapshot) {
                              if (snapshot.exists()) {
                                  User user = snapshot.getValue(User.class);
                                  arrUsers.add(user);
                                  adapter.notifyDataSetChanged();
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
    private void addParticipant() {
        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("Groups")
                .child(groupID);
        databaseReference1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Group group = snapshot.getValue(Group.class);
                for (Participant userGroup : arrMember){
                    databaseReference1.child("participant").child(userGroup.getId()).setValue(userGroup);
                }
                HashMap<String, Object> map = new HashMap<>();
                map.put("slParticipant", group.getSlParticipant()+arrMember.size());
                snapshot.getRef().updateChildren(map);


            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        //Logs

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Groups");
        databaseReference.child(groupID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    Group group = snapshot.getValue(Group.class);

                    HashMap<String, Object> log = new HashMap<>();
                    log.put("idUser", mUser.getUid());
                    log.put("log", "thêm "+ mUsers.size()+" thành viên vào nhóm "+ group.getGroupName());
                    log.put("time", System.currentTimeMillis());
                    DatabaseReference dbReff = FirebaseDatabase.getInstance().getReference("Logs");
                    dbReff.push().setValue(log);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }


    @Override
    public void addMemberGroup(List<User> arrUser) {
        mUsers = arrUser;
        for (User e: mUsers){
            arrMember.add(new Participant(e.getId(), 3, System.currentTimeMillis()));
        }
    }
}