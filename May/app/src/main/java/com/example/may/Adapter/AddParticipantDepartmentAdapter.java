package com.example.may.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.may.Interface.iAddMemberGroup;
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

public class AddParticipantDepartmentAdapter extends RecyclerView.Adapter<AddParticipantDepartmentAdapter.MemberViewHolder> {

    private List<User> arrUser;
    iAddMemberGroup addMemberGroup;

    private List<User> mUsers = new ArrayList<>();

    public AddParticipantDepartmentAdapter(List<User> arrUser, iAddMemberGroup addMemberGroup) {
        this.arrUser = arrUser;

        this.addMemberGroup = addMemberGroup;

    }

    @NonNull
    @Override
    public MemberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_select, parent, false);
        return new MemberViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MemberViewHolder holder, int position) {
        User user = arrUser.get(position);
        if (user.getAvatar().equals("default")){
            holder.imgMember.setImageResource(R.drawable.ic_logo);
        }else {
            Picasso.get().load(user.getAvatar()).into(holder.imgMember);
        }
        holder.tvPosition.setText("Chức vụ: "+user.getPosition());
        holder.tvName.setText(user.getFullName());
        holder.checkMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.checkMember.isChecked()){
                    mUsers.add(user);

                }else {
                    mUsers.remove(user);
                }
                addMemberGroup.addMemberGroup(mUsers);
            }
        });


    }



    @Override
    public int getItemCount() {
        return arrUser.size();
    }

    class MemberViewHolder extends RecyclerView.ViewHolder {
        CircleImageView imgMember;
        TextView tvName, tvPosition, status;
        CheckBox checkMember;
        RelativeLayout layout_user;
        public MemberViewHolder(@NonNull View itemView) {
            super(itemView);
            imgMember = itemView.findViewById(R.id.user_select_group);
            tvName = itemView.findViewById(R.id.tvNameMember);
            checkMember = itemView.findViewById(R.id.checkMember);
            tvPosition = itemView.findViewById(R.id.tvPosition);
            layout_user = itemView.findViewById(R.id.layout_user);
            status = itemView.findViewById(R.id.status);

        }
    }
}
