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

public class LayoutUserSelectAdapter extends RecyclerView.Adapter<LayoutUserSelectAdapter.ListEmployee> {
    List<User> arrUsers;
    iAddMemberGroup addMemberGroup;

    private List<User> arrUsersSelect = new ArrayList<>();

    public LayoutUserSelectAdapter(List<User> arrUsers, iAddMemberGroup addMemberGroup) {
        this.arrUsers = arrUsers;
        this.addMemberGroup = addMemberGroup;
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
        if (users.getAvatar().equals("default")){
            holder.imgAvatar.setImageResource(R.drawable.ic_logo);
        }else {
            Picasso.get().load(users.getAvatar()).into(holder.imgAvatar);
        }
        holder.tvName.setText(users.getFullName());
        loadPosition(holder, users);

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
        holder.layoutUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.checkMember.isChecked()) {
                    holder.checkMember.setChecked(false);
                    arrUsersSelect.remove(users);


                } else {
                    holder.checkMember.setChecked(true);

                    arrUsersSelect.add(users);
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
        private TextView tvName, tvPosition;
        private CheckBox checkMember;
        private RelativeLayout layoutUser;

        public ListEmployee(@NonNull View itemView) {
            super(itemView);

            imgAvatar = itemView.findViewById(R.id.user_select_group);
            tvName = itemView.findViewById(R.id.tvNameMember);
            tvPosition = itemView.findViewById(R.id.tvPosition);
            checkMember = itemView.findViewById(R.id.checkMember);
            layoutUser = itemView.findViewById(R.id.layout_user);
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
