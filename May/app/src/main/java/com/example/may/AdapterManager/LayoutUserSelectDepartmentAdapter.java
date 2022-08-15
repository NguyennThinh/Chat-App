package com.example.may.AdapterManager;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.may.Interface.iAddMemberGroup;
import com.example.may.Model.Department;
import com.example.may.Model.Position;
import com.example.may.Model.User;
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

public class LayoutUserSelectDepartmentAdapter extends RecyclerView.Adapter<LayoutUserSelectDepartmentAdapter.ListEmployee> {
    List<User> arrUsers;
    iAddMemberGroup addMemberGroup;
    String idLeader;
    private List<User> arrUsersSelect = new ArrayList<>();

    public LayoutUserSelectDepartmentAdapter(List<User> arrUsers, String idLeader , iAddMemberGroup addMemberGroup) {
        this.arrUsers = arrUsers;
        this.addMemberGroup = addMemberGroup;
        this.idLeader = idLeader;
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
        if (users.getId().equals(idLeader)){
            holder.checkMember.setVisibility(View.GONE);
            holder.checkLeader.setVisibility(View.VISIBLE);
            holder.checkLeader.setText("Người phụ trách");
        }else {
            holder.checkMember.setVisibility(View.VISIBLE);
            holder.checkLeader.setVisibility(View.GONE);
        }
        if (users.getAvatar().equals("default")){
            holder.imgAvatar.setImageResource(R.drawable.ic_logo);
        }else {
            Picasso.get().load(users.getAvatar()).into(holder.imgAvatar);
        }
        holder.tvName.setText(users.getFullName());

        loadPosition(holder, users);
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
    private void loadPosition(ListEmployee holder, User user) {
        DatabaseReference databaseReference  = FirebaseDatabase.getInstance().getReference("Position");
        databaseReference.child(user.getDepartment()).child(user.getPosition()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    Position position = snapshot.getValue(Position.class);
                    holder.tvPosition.setText(position.getName());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}
