package com.example.may.AdapterManager;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.may.Interface.iAddManager;
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

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class LayouManagerSelectAdapter extends RecyclerView.Adapter<LayouManagerSelectAdapter.ListLeader> {
    List<User> arrUsers;
    iAddManager addMemberGroup;


    public LayouManagerSelectAdapter(List<User> arrUsers, iAddManager addMemberGroup) {
        this.arrUsers = arrUsers;
        this.addMemberGroup = addMemberGroup;
    }

    @NonNull
    @Override
    public ListLeader onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_manager, parent, false);
        return new ListLeader(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListLeader holder, int position) {
        User users = arrUsers.get(position);
        if (users.getAvatar().equals("default")){
            holder.imgAvatar.setImageResource(R.drawable.ic_logo);
        }else {
            Picasso.get().load(users.getAvatar()).into(holder.imgAvatar);
        }
        holder.tvName.setText(users.getFullName());


        loadPosition(holder, users);

        holder.imgSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                addMemberGroup.addManager(users);

            }
        });


    }

    @Override
    public int getItemCount() {
        return arrUsers.size();
    }

    static class ListLeader extends RecyclerView.ViewHolder {
        private CircleImageView imgAvatar;
        private TextView tvName, tvPosition;
        private ImageView imgSubmit;

        public ListLeader(@NonNull View itemView) {
            super(itemView);

            imgAvatar = itemView.findViewById(R.id.img_leader);
            tvName = itemView.findViewById(R.id.name_leader);
            tvPosition = itemView.findViewById(R.id.leader_position);
            imgSubmit = itemView.findViewById(R.id.img_submit);
        }
    }

    private void loadPosition(ListLeader holder, User user) {
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
