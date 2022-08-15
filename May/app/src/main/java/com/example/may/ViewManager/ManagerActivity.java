package com.example.may.ViewManager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.may.AdapterManager.LayouManagerSelectAdapter;
import com.example.may.Interface.iAddManager;
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
import java.util.List;

public class ManagerActivity extends AppCompatActivity {
    //Views
    private RecyclerView list_leader;
    private ImageView imgBack;
    //Adapter
    private LayouManagerSelectAdapter adapter;

    private List<Participant> arrParticipant;
    private List<User> arrUsers;
    //Declare firebase
    private FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager);

        imgBack = findViewById(R.id.img_back);
        list_leader = findViewById(R.id.list_leader_work);
        mUser = FirebaseAuth.getInstance().getCurrentUser();

        arrParticipant= (List<Participant>) getIntent().getSerializableExtra("list");


        arrUsers = new ArrayList<>();
        loadListUsersWork();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        list_leader.setLayoutManager(layoutManager);
        list_leader.addItemDecoration(itemDecoration);

        imgBack.setOnClickListener(v -> {
            onBackPressed();
        });
    }

    private void loadListUsersWork() {

        arrUsers.clear();
        if (arrParticipant.size() != 0){
            for (Participant p: arrParticipant){
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
                databaseReference.child(p.getId()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            User user = snapshot.getValue(User.class);
                            if (user != null) {
                                arrUsers.add(user);
                            }
                            adapter = new LayouManagerSelectAdapter(arrUsers, new iAddManager() {
                                @Override
                                public void addManager(User users) {

                                    Intent intent = new Intent();
                                    Bundle bundle = new Bundle();
                                    bundle.putSerializable("leader", users);
                                    intent.putExtras(bundle);
                                    setResult(RESULT_OK, intent);

                                    finish();
                                }
                            });
                        }
                        list_leader.setAdapter(adapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        }else {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    arrUsers.clear();
                    for (DataSnapshot dn : snapshot.getChildren()){
                        User user = dn.getValue(User.class);


                        if (user != null){
                            databaseReference.child(user.getId()).child("department").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (!snapshot.exists()){

                                        arrUsers.add(user);

                                        adapter = new LayouManagerSelectAdapter(arrUsers, new iAddManager() {
                                            @Override
                                            public void addManager(User users) {

                                                Intent intent = new Intent();
                                                Bundle bundle = new Bundle();
                                                bundle.putSerializable("leader", users);
                                                intent.putExtras(bundle);
                                                setResult(RESULT_OK, intent);

                                                finish();
                                            }
                                        });
                                        list_leader.setAdapter(adapter);
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
    }




}