package com.example.may.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.may.Model.User;
import com.example.may.Model.Work;
import com.example.may.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class AddWorkGroupAdaper extends RecyclerView.Adapter<AddWorkGroupAdaper.ListWorkHolder> {
    private List<Work> arrWorks;
    private String idGroup;
    private FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
    public AddWorkGroupAdaper(List<Work> arrWorks, String idGroup) {
        this.arrWorks = arrWorks;
        this.idGroup=idGroup;
    }

    @NonNull
    @Override
    public ListWorkHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_add_work_group, parent, false);
        return new ListWorkHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListWorkHolder holder, int position) {
        Work work = arrWorks.get(position);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Groups");
        databaseReference.child(idGroup).child("work").child(work.getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()){
                    holder.workName.setText(work.getWorkName());

                    loadLeader(holder, work);
                    holder.workTime.setText("Thời gian: Từ: "+work.getWorkStart() +" đến: "+work.getWorkEnd());


                    holder.addWork.setOnClickListener(view->{
                        addWorkGroup(work);
                    });

                    holder.layoutWork.setOnClickListener(view ->{
                  /*      Intent intent = new Intent(view.getContext(), WorkActivity.class);
                        intent.putExtra("work_id", work.getId());
                        intent.putExtra("groupid", idGroup);

                        view.getContext().startActivity(intent);*/
                    });
                }else {
                    holder.layoutWork.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




    }



    @Override
    public int getItemCount() {
        return arrWorks.size();
    }

    static class ListWorkHolder extends RecyclerView.ViewHolder {
        private TextView workName, leader, workTime,  workComplete;
        private ImageView addWork;
        private RelativeLayout layoutWork;
        public ListWorkHolder(@NonNull View itemView) {
            super(itemView);
            workName = itemView.findViewById(R.id.work_name);
            leader = itemView.findViewById(R.id.leader);
            workTime = itemView.findViewById(R.id.work_time);
            addWork = itemView.findViewById(R.id.add);
            workComplete = itemView.findViewById(R.id.work_complete);
            layoutWork = itemView.findViewById(R.id.layout_work_group);
        }
    }
    private void loadLeader(ListWorkHolder holder, Work work) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(work.getLeader()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    User users = snapshot.getValue(User.class);
                    holder.leader.setText("Người phụ trách: "+users.getFullName());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void addWorkGroup(Work work) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Groups");
        databaseReference.child(idGroup).child(work.getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()){
                    databaseReference.child(idGroup).child("work").child(work.getId()).setValue(work);
                }
                else {
                    System.out.println(idGroup);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
