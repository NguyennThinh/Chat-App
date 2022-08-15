package com.example.may.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.ImageView;

import com.example.may.Adapter.GroupParticipantAdapter;
import com.example.may.Model.Group;
import com.example.may.Model.Participant;
import com.example.may.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class GroupParticipantActivity extends AppCompatActivity {
    private RecyclerView listUser;
    private ImageView imgReturn;
    private GroupParticipantAdapter adapter;

    private Group group;

    private FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_participant);
        listUser = findViewById(R.id.list_user);
        group = (Group) getIntent().getExtras().get("groups");
        imgReturn = findViewById(R.id.return_activity);

        imgReturn.setOnClickListener(v -> {
            onBackPressed();
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        listUser.setLayoutManager(layoutManager);
        listUser.addItemDecoration(itemDecoration);

        loadParticipant();
    }

    private void loadParticipant() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Groups");
        Query query = databaseReference.child(group.getGroupID()).child("participant").orderByChild("role");
        //  databaseReference.child(group.getGroupID()).child("participant")
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int role = 0;
                List<Participant> arrUserGroups = new ArrayList<>();
                for (DataSnapshot dn : snapshot.getChildren()) {
                    Participant participant = dn.getValue(Participant.class);
                    //  arrUserGroups.add(new Participant(dn.child("id").getValue() + "", dn.child("role").getValue(), dn.child("partDate").getValue() + ""));
                    arrUserGroups.add(participant);
                    if (mUser.getUid().equals(participant.getId())){
                        role = participant.getRole();
                    }
                }
                adapter = new GroupParticipantAdapter(arrUserGroups, group.getGroupID(),role);
                listUser.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}