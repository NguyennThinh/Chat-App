package com.example.may.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.may.Model.Participant;
import com.example.may.Model.User;
import com.example.may.Model.Work;
import com.example.may.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Nguyễn Phúc Thịnh on 5/5/2022.
 * Đại học công nghiệp TP HCM
 * nguyenthinhc9@gmail.com
 */
public class ParticipantWorkAdapter extends RecyclerView.Adapter<ParticipantWorkAdapter.ListParticipantWorkViewHolder>{
    private List<Participant> arrParticipants;
    private FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
    private Work work;
    private int TYPE;
    private Context context;

    public ParticipantWorkAdapter(List<Participant> arrParticipants, Work work, int TYPE, Context context) {
        this.arrParticipants = arrParticipants;
        this.work=work;
        this.TYPE = TYPE;
        this.context = context;
    }

    @NonNull
    @Override
    public ListParticipantWorkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_participant_work, parent, false);
        return new ListParticipantWorkViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListParticipantWorkViewHolder holder, int position) {
        Participant participant = arrParticipants.get(position);
        if (participant.getRole() == 1){
            holder.positionGroup.setText("Người phụ trách");
        }else {
            holder.positionGroup.setText("Thành viên");
        }
       if (TYPE == 1){

           if (work.getLeader().equals(mUser.getUid()) || work.getUserCreate().equals(mUser.getUid())){
               holder.imgMore.setImageResource(R.drawable.ic_delete_work);
               holder.imgMore.setOnClickListener(v -> {
                   if (participant.getRole() ==1){
                       Toast.makeText(v.getContext(), "Không thể xóa người phụ trách công việc này!", Toast.LENGTH_SHORT).show();
                   }else {
                       deleteParticipant(participant);
                   }
               });
           }



       }
        loadUser(participant, holder);
    }


    @Override
    public int getItemCount() {
        return arrParticipants.size();
    }

    static class ListParticipantWorkViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView imgUser;
        private TextView userName, positionGroup;
        private RelativeLayout layout_participant;
        private ImageView imgMore;
        public ListParticipantWorkViewHolder(@NonNull View itemView) {
            super(itemView);
            imgUser = itemView.findViewById(R.id.img_user);
            userName = itemView.findViewById(R.id.user_name);
            positionGroup = itemView.findViewById(R.id.position);
            layout_participant = itemView.findViewById(R.id.layout_participant);
            imgMore = itemView.findViewById(R.id.img_more);
        }
    }
    private void loadUser(Participant participant, ListParticipantWorkViewHolder holder) {
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



                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void deleteParticipant(Participant participant) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Work");
        databaseReference.child(work.getLeader()).child(work.getId()).child("participant")
                .child(participant.getId()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(context, "Xóa thành công", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
