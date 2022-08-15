package com.example.may.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.may.AdapterManager.ManagerEmployeeAdapter;
import com.example.may.Model.Department;
import com.example.may.Model.Participant;
import com.example.may.Model.Position;
import com.example.may.Model.User;
import com.example.may.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class DepartmentDetailAdapter  extends RecyclerView.Adapter<DepartmentDetailAdapter.ListParticipantViewHolder>{
    private List<Participant> arrParticipants;

    public DepartmentDetailAdapter(List<Participant> arrParticipants) {
        this.arrParticipants = arrParticipants;
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



        loadUser(participant, holder);
        holder.imgMore.setVisibility(View.GONE);
      /*  holder.imgMore.setImageResource(R.drawable.ic_delete_work);

        holder.imgMore.setOnClickListener(v -> {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
            databaseReference.child(participant.getId()).child("department").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){
                        String idD = snapshot.getValue().toString();
                        databaseReference.child(participant.getId()).child("department").removeValue();
                        databaseReference.child(participant.getId()).child("position").removeValue();

                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Department");
                        reference.child(idD).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()){
                                    Department department = snapshot.getValue(Department.class);

                                  snapshot.getRef().child("participant").child(participant.getId()).removeValue();
                                    snapshot.getRef().child("slParticipant").setValue(department.getSlParticipant()-1);

                                    Toast.makeText(v.getContext(), "Xóa thành viên thành công", Toast.LENGTH_SHORT).show();
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
        });*/
    }

    @Override
    public int getItemCount() {
        return arrParticipants.size();
    }

    static class ListParticipantViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView imgUser;
        private TextView userName, position;
        private RelativeLayout layout_participant;
        private ImageView imgMore;
        public ListParticipantViewHolder(@NonNull View itemView) {
            super(itemView);
            imgUser = itemView.findViewById(R.id.img_user);
            userName = itemView.findViewById(R.id.user_name);
            position = itemView.findViewById(R.id.position_group);
            layout_participant = itemView.findViewById(R.id.layout_participant);
            imgMore = itemView.findViewById(R.id.img_more);
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
                        loadPosition(holder, users);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void loadPosition(ListParticipantViewHolder holder, User user) {
        DatabaseReference databaseReference  = FirebaseDatabase.getInstance().getReference("Position");
        databaseReference.child(user.getDepartment()).child(user.getPosition()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    Position position = snapshot.getValue(Position.class);
                    holder.position.setText(position.getName());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}
