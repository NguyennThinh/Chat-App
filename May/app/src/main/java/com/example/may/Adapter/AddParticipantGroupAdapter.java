package com.example.may.Adapter;

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

public class AddParticipantGroupAdapter extends RecyclerView.Adapter<AddParticipantGroupAdapter.MemberViewHolder> {

    private List<User> arrUser;
    iAddMemberGroup addMemberGroup;
    private String idGroup;
    private List<User> mUsers = new ArrayList<>();

    public AddParticipantGroupAdapter(List<User> arrUser, String groupID, iAddMemberGroup addMemberGroup) {
        this.arrUser = arrUser;
        this.idGroup = groupID;
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

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(idGroup).child("participant").child(user.getId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()){
                            if (user.getAvatar().equals("default")){
                                holder.imgMember.setImageResource(R.drawable.ic_logo);
                            }else {
                                Picasso.get().load(user.getAvatar()).into(holder.imgMember);
                            }

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
                        }else {
                            if (user.getAvatar().equals("default")){
                                holder.imgMember.setImageResource(R.drawable.ic_logo);
                            }else {
                                Picasso.get().load(user.getAvatar()).into(holder.imgMember);
                            }

                            holder.tvName.setText(user.getFullName());
                            holder.checkMember.setVisibility(View.GONE);
                            holder.status.setVisibility(View.VISIBLE);
                            holder.status.setText("Đã là thành viên nhóm");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        loadPosition(holder, user);


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
    private void loadPosition(MemberViewHolder holder, User user) {
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
