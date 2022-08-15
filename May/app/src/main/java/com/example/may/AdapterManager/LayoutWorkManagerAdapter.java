package com.example.may.AdapterManager;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.may.Model.User;
import com.example.may.Model.Work;
import com.example.may.R;
import com.example.may.View.DetailMyWork;
import com.example.may.ViewManager.WorkDetailActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class LayoutWorkManagerAdapter extends RecyclerView.Adapter<LayoutWorkManagerAdapter.WorkViewHolder> {
    private List<Work> arrWorks;
    private Context context;
    private int TYPE;

    public LayoutWorkManagerAdapter(List<Work> arrWorks, Context context, int TYPE) {
        this.arrWorks = arrWorks;
        this.context = context;
        this.TYPE = TYPE;
    }

    @NonNull
    @Override
    public WorkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_work, parent, false);

        return new WorkViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkViewHolder holder, int position) {
        Work work = arrWorks.get(position);
        holder.workName.setText(work.getWorkName());
        holder.workTime.setText("Thời gian: "+ work.getWorkStart() +" - "+ work.getWorkEnd());

        loadLeader(holder, work);

        if (work.getDateComplete() != 0){
            holder.workComplete.setVisibility(View.VISIBLE);
            holder.workComplete.setText("Ngày hoàn thành: "+ new SimpleDateFormat("dd/MM/yyyy").format(work.getDateComplete()));
        }else {
            holder.workComplete.setVisibility(View.GONE);
        }



        DateFormat format = new SimpleDateFormat("dd/MM/yyyy");

        try {
            if (format.parse(work.getWorkEnd()).compareTo(new Date())<0){
                HashMap<String, Object> status = new HashMap<>();
                status.put("workStatus", 3);
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Work");
                databaseReference.child(work.getLeader()).child(work.getId()).updateChildren(status);
            }

            if (work.getWorkStatus() == 1){
                holder.workStatus.setImageResource(R.drawable.ic_load);
                holder.workStatus.setAnimation(AnimationUtils.loadAnimation(context, R.anim.rorate));
            }else if (work.getWorkStatus() ==2){
                holder.workStatus.setImageResource(R.drawable.ic_check);
            }else {
                holder.workStatus.setImageResource(R.drawable.ic_work_out_date);
                holder.layoutWork.setBackgroundColor(Color.parseColor("#32FF0000"));
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }


        holder.layoutWork.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable("work", work);
            if (TYPE ==1){
                Intent intent = new Intent(context, WorkDetailActivity.class);

                intent.putExtras(bundle);
                context.startActivity(intent);
            }else {
                Intent intent = new Intent(context, DetailMyWork.class);

                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });
    }



    @Override
    public int getItemCount() {
        return arrWorks.size();
    }

    static class WorkViewHolder extends RecyclerView.ViewHolder {
        private TextView workName, workTime, workLeader, workComplete;
        private ImageView workStatus;
        private RelativeLayout layoutWork;
        public WorkViewHolder(@NonNull View itemView) {
            super(itemView);

            workName = itemView.findViewById(R.id.work_name);
            workTime = itemView.findViewById(R.id.work_time);
            workLeader = itemView.findViewById(R.id.leader);
            workComplete = itemView.findViewById(R.id.complete);
            workStatus = itemView.findViewById(R.id.img_status);
            layoutWork = itemView.findViewById(R.id.layout_item_work);
        }
    }
    private void loadLeader(WorkViewHolder holder, Work work) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(work.getLeader()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                holder.workLeader.setText("Phụ trách: "+user.getFullName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
