package com.example.may.Adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.may.AdapterManager.ManagerEmployeeAdapter;
import com.example.may.Interface.iAddMemberGroup;
import com.example.may.Model.Department;
import com.example.may.Model.Participant;
import com.example.may.Model.User;
import com.example.may.Model.Work;
import com.example.may.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class LayoutUserAddParticipantSelectWorkAdapter extends RecyclerView.Adapter<LayoutUserAddParticipantSelectWorkAdapter.ListEmployee> {
    List<User> arrUsers;
    iAddMemberGroup addMemberGroup;
    private Work work;
    private List<User> arrUsersSelect = new ArrayList<>();

    public LayoutUserAddParticipantSelectWorkAdapter(List<User> arrUsers, Work work , iAddMemberGroup addMemberGroup) {
        this.arrUsers = arrUsers;
        this.addMemberGroup = addMemberGroup;
        this.work = work;
    }

    @NonNull
    @Override
    public ListEmployee onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_select, parent, false);
        return new ListEmployee(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListEmployee holder, int position) {
        User users = arrUsers.get(position);


        CheckParticipant(users, holder);

        if (users.getAvatar().equals("default")){
            holder.imgAvatar.setImageResource(R.drawable.ic_launcher_background);
        }else {
            Picasso.get().load(users.getAvatar()).into(holder.imgAvatar);
        }
        holder.tvName.setText(users.getFullName());
        holder.tvPosition.setText(users.getPosition());
        holder.checkMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.checkMember.isChecked()) {
                    arrUsersSelect.add(users);

                } else {
                    arrUsersSelect.remove(users);
                }
                addMemberGroup.addMemberGroup(arrUsersSelect);
            }
        });

    }



    @Override
    public int getItemCount() {
        return arrUsers.size();
    }


    class ListEmployee extends RecyclerView.ViewHolder {
        private CircleImageView imgAvatar;
        private TextView tvName, tvPosition, checkLeader;
        private CheckBox checkMember;
        private RelativeLayout layout_user;
        public ListEmployee(@NonNull View itemView) {
            super(itemView);

            imgAvatar = itemView.findViewById(R.id.user_select_group);
            tvName = itemView.findViewById(R.id.tvNameMember);
            tvPosition = itemView.findViewById(R.id.tvPosition);
            checkMember = itemView.findViewById(R.id.checkMember);
            layout_user = itemView.findViewById(R.id.layout_user);
            checkLeader = itemView.findViewById(R.id.checkLeader);

        }
    }
    private void CheckParticipant(User users, ListEmployee holder) {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("WorkList");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dn : snapshot.getChildren()) {
                    databaseReference.child(dn.getKey()).child(work.getId()).child("participant").child(users.getId()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                    Participant participant = snapshot.getValue(Participant.class);
                                    if (participant != null) {

                                           if (participant.getRole() ==1){
                                               holder.checkMember.setVisibility(View.GONE);
                                               holder.checkLeader.setVisibility(View.VISIBLE);
                                               holder.checkLeader.setText("Người phụ trách");
                                           }else {
                                               holder.checkMember.setVisibility(View.GONE);
                                               holder.checkLeader.setVisibility(View.VISIBLE);
                                               holder.checkLeader.setText("Thành viên");
                                           }

                                    }else {
                                        holder.checkMember.setVisibility(View.VISIBLE);
                                        holder.checkLeader.setVisibility(View.GONE);
                                    }


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

}
