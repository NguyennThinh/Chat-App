package com.example.may.ViewManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupInforActivity extends AppCompatActivity {
    //Views
    private ImageView imgBack, imgEdit;
    private CircleImageView imgGroup;
    private TextView groupName, groupDescription;
    private RecyclerView listParticipant;


    private GroupParticipantAdapter adapter;
    private Group group;
    private FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_infor);

        initUI();
        loadInfoGroup();
        loadParticipant();

        imgBack.setOnClickListener(v -> {
            onBackPressed();
        });
    }



    private void initUI() {
        imgBack = findViewById(R.id.img_back);
        imgEdit = findViewById(R.id.img_edit_info_group);
        imgGroup = findViewById(R.id.img_group_avatar);
        groupName = findViewById(R.id.group_name);
        groupDescription = findViewById(R.id.group_description);
        listParticipant = findViewById(R.id.list_participant);
        group = (Group) getIntent().getExtras().get("my_group");


        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        listParticipant.setLayoutManager(layoutManager);
        listParticipant.addItemDecoration(itemDecoration);
    }


    private void loadInfoGroup() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Groups");
        databaseReference.child(group.getGroupID()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    group = snapshot.getValue(Group.class);
                    if (group != null){
                        Picasso.get().load(group.getGroupImage()).into(imgGroup);
                        groupName.setText(group.getGroupName());
                        if (group.getDescription().equals("default")){
                            groupDescription.setVisibility(View.GONE);
                        }else {
                            groupDescription.setText(group.getDescription());
                        }

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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
                listParticipant.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}