package com.example.may.Adapter;

import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.may.Model.Group;
import com.example.may.Model.Participant;
import com.example.may.Model.User;
import com.example.may.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupParticipantAdapter extends RecyclerView.Adapter<GroupParticipantAdapter.ListParticipantViewHolder>{
    private List<Participant> arrParticipants;
    private String idGroup;

    private FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();

    int role;
    public GroupParticipantAdapter(List<Participant> arrParticipants, String idGroup, int role) {
        this.arrParticipants = arrParticipants;
        this.idGroup = idGroup;
        this.role = role;
    }

    @NonNull
    @Override
    public ListParticipantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_group_participant, parent, false);
        return new ListParticipantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListParticipantViewHolder holder, int position) {
        Participant participant = arrParticipants.get(position);
        if (participant.getRole() == 1){
            holder.positionGroup.setText("Trưởng nhóm");
        }else {
            holder.positionGroup.setText("Thành viên");
        }

        loadUser(participant, holder);
    }


    @Override
    public int getItemCount() {
        return arrParticipants.size();
    }

    static class ListParticipantViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView imgUser;
        private TextView userName, positionGroup;
        private RelativeLayout layout_participant;
        public ListParticipantViewHolder(@NonNull View itemView) {
            super(itemView);
            imgUser = itemView.findViewById(R.id.img_user);
            userName = itemView.findViewById(R.id.user_name);
            positionGroup = itemView.findViewById(R.id.position_group);
            layout_participant = itemView.findViewById(R.id.layout_participant);
        }
    }
    private void loadUser(Participant participant, ListParticipantViewHolder holder) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dn : snapshot.getChildren()){
                    User users = dn.getValue(User.class);
                    if (participant.getId().equals(users.getId())){
                        holder.userName.setText(users.getFullName());
                        if (users.getAvatar().equals("default")){
                            holder.imgUser.setImageResource(R.drawable.ic_logo);
                        }else {
                            Picasso.get().load(users.getAvatar()).into(holder.imgUser);
                        }


                        holder.layout_participant.setOnClickListener(view->{

                            if (role ==1){
                                if (participant.getRole()!=1){
                                    showOption(view,participant, users);
                                }
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

    private void showOption(View view, Participant participant, User users) {
        //Option edit
        String[] option = {"Chuyển nhóm trưởng","Xóa thành viên"};

        //Dialog edit
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setItems(option, (dialogInterface, i) -> {
            switch (i){
                case 0:
                    setAdmin(participant, view);
                    break;
                case 1:
                    deleteMember(participant, view, users);

                    break;

            }
        });
        builder.create().show();
    }





    private void deleteMember(Participant participant, View view, User users) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Groups");

        databaseReference.child(idGroup).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    Group group = snapshot.getValue(Group.class);

                    snapshot.getRef().child("participant").child(participant.getId()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            //Logs
                            HashMap<String, Object> log = new HashMap<>();
                            log.put("idUser", mUser.getUid());
                            log.put("log", "xóa thành viên "+ users.getFullName()+" tại nhóm "+group.getGroupName());
                            log.put("time", System.currentTimeMillis());
                            DatabaseReference dbReff = FirebaseDatabase.getInstance().getReference("Logs");
                            dbReff.push().setValue(log);

                            //Update thanh vien
                            HashMap<String, Object> map = new HashMap<>();
                            map.put("slParticipant", group.getSlParticipant()-1);
                            snapshot.getRef().updateChildren(map);


                            Toast.makeText(view.getContext(), "Xóa thành viên thành công", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    private void setAdmin(Participant participant, View view) {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Groups");
        Query query= databaseReference.child(idGroup).child("participant").child(participant.getId()).orderByChild("id").equalTo(participant.getId());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                HashMap<String, Object> map = new HashMap<>();
                map.put("role",1);
                snapshot.getRef().updateChildren(map);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("Groups");
        Query query1= databaseReference1.child(idGroup).child("participant").child(mUser.getUid());
        query1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                HashMap<String, Object> map = new HashMap<>();
                map.put("role",2);
                snapshot.getRef().updateChildren(map);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
       /* DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Groups");
        dbRef.child(idGroup).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("userCreate",participant.getId());
                    snapshot.getRef().updateChildren(map);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });*/
    }


}
