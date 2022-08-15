package com.example.may.Adapter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.may.FcmNotification.ApiClient;
import com.example.may.Model.Participant;
import com.example.may.Model.User;
import com.example.may.Model.Work;
import com.example.may.R;

import com.example.may.View.WorkChildGroupDetailActivity;
import com.example.may.View.WorkGroupDetailActivity;
import com.example.may.ViewManager.CreateWork;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WorkChildGroupAdapter extends RecyclerView.Adapter<WorkChildGroupAdapter.ListWorkHolder> {
    private List<Work> arrWorks;
    private String idGroup,idAdmin;
    private String idWork;
    private FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
    private User userRemind;

    public WorkChildGroupAdapter(List<Work> arrWorks, String idGroup, String idWork, String idAdmin) {
        this.arrWorks = arrWorks;
        this.idGroup = idGroup;
        this.idWork = idWork;
        this.idAdmin = idAdmin;
    }

    @NonNull
    @Override
    public ListWorkHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_work_group, parent, false);
        return new ListWorkHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ListWorkHolder holder, int position) {
        Work work = arrWorks.get(position);
        holder.workName.setText("Tên: "+work.getWorkName());

        loadLeader(holder, work);
        holder.workTime.setText("Thời gian: "+work.getWorkStart() +" - "+work.getWorkEnd());
        holder.workComplete.setVisibility(View.GONE);
        if (work.getWorkStatus()==2){
            holder.layoutWork.setBackgroundColor(Color.parseColor("#868BC34A"));
            holder.workComplete.setVisibility(View.VISIBLE);
            holder.imgMore.setVisibility(View.GONE);

            holder.workComplete.setText("Ngày hoàn thành: "+
                    new SimpleDateFormat("dd/MM/yyyyy").format(work.getDateComplete()));

        }
        holder.layoutWork.setOnClickListener(view ->{


            Intent intent = new Intent(view.getContext(), WorkChildGroupDetailActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("work", work);
            intent.putExtra("id_group", idGroup);
            intent.putExtra("idAdmin", idAdmin);
            intent.putExtra("idWorkParent", idWork);

            intent.putExtras(bundle);
            view.getContext().startActivity(intent);

        });
        holder.imgMore.setOnClickListener(view -> {
            showPopup(view, work);
        });
    }



    @Override
    public int getItemCount() {
        return arrWorks.size();
    }

    static class ListWorkHolder extends RecyclerView.ViewHolder {
        private TextView workName, leader, workTime,  workComplete;
        private ImageView imgMore;
        private RelativeLayout layoutWork;
        public ListWorkHolder(@NonNull View itemView) {
            super(itemView);
            workName = itemView.findViewById(R.id.work_name);
            leader = itemView.findViewById(R.id.leader);
            workTime = itemView.findViewById(R.id.work_time);
            imgMore = itemView.findViewById(R.id.more);
            workComplete = itemView.findViewById(R.id.work_complete);
            layoutWork = itemView.findViewById(R.id.layout_work);
        }
    }
    private void loadLeader(ListWorkHolder holder, Work work) {

        StringBuilder builder = new StringBuilder();
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("WorkChild");
        dbRef.child(idWork).child(work.getId()).child("participant").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                    for (DataSnapshot dn : snapshot.getChildren()){
                        Participant participant = dn.getValue(Participant.class);
                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
                        databaseReference.child(participant.getId()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()){
                                    User user = snapshot.getValue(User.class);
                                    builder.append(user.getFullName()+", ");
                                }
                                holder.leader.setText("Người phụ trách: "+ builder);
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
    public void showPopup(View v, Work work) {
        PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
        popupMenu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.complete:
                    CompleteWork(work, v);
                    break;
                case R.id.remind:
                    if (mUser.getUid().equals(idAdmin)){
                        pushNotificationRemind(work,v);
                    }else {
                        Toast.makeText(v.getContext(), "Bạn không có quyền nhắc công việc;", Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
            return false;
        });
        popupMenu.inflate(R.menu.work_menu);

        Object menuHelper;
        Class[] argTypes;
        try {
            @SuppressLint("DiscouragedPrivateApi") Field fMenuHelper = PopupMenu.class.getDeclaredField("mPopup");
            fMenuHelper.setAccessible(true);
            menuHelper = fMenuHelper.get(popupMenu);
            argTypes = new Class[]{boolean.class};
            assert menuHelper != null;
            menuHelper.getClass().getDeclaredMethod("setForceShowIcon", argTypes).invoke(menuHelper, true);
        } catch (Exception ignored) {

        }
        popupMenu.show();
    }
    private void CompleteWork(Work work, View v) {
        HashMap<String, Object> status = new HashMap<>();
        status.put("workStatus", 2);
        status.put("dateComplete",System.currentTimeMillis());
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("WorkChild");
        databaseReference.child(work.getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {

                    for (DataSnapshot dn : snapshot.getChildren()) {
                        Work works = dn.getValue(Work.class);
                        if (works != null) {
                            if (works.getWorkStatus() == 1) {
                                Toast.makeText(v.getContext(), "Công việc nhỏ chưa hoàn thành hết", Toast.LENGTH_SHORT).show();

                            } else {
                                UpdateWorkChild(work, status);


                            }
                        }
                    }

                } else {
                    UpdateWorkChild(work, status);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    private void UpdateWorkChild(Work work, HashMap<String, Object> status) {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("WorkChild");
        dbRef.child(idWork).child(work.getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    snapshot.getRef().updateChildren(status);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    private void pushNotificationRemind(Work work, View v) {
        List<Participant> arrParticipant = new ArrayList<>();
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Users");
        dbRef.child(mUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    userRemind = snapshot.getValue(User.class);
                   DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("WorkChild");
                   databaseReference.child(idWork).child(work.getId()).child("participant").addListenerForSingleValueEvent(new ValueEventListener() {
                       @Override
                       public void onDataChange(@NonNull DataSnapshot snapshot) {
                           for (DataSnapshot dn : snapshot.getChildren()){
                               Participant participant = dn.getValue(Participant.class);
                               if (participant != null){
                                   arrParticipant.add(participant);
                               }
                           }
                           getToken(arrParticipant, work, v, userRemind);
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
    private void getToken(List<Participant> arrParticipants, Work w, View v, User userRemind) {
        List<String> arrToken = new ArrayList<>();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrToken.clear();
                for (DataSnapshot dn : snapshot.getChildren()) {
                    User users = dn.getValue(User.class);

                    assert users != null;
                    for (Participant p : arrParticipants) {
                        if (p.getId().equals(users.getId())) {
                            arrToken.add(users.getToken());
                        }
                    }

                }
                ApiClient.pushNotificationWorkRemind(v.getContext(),arrToken, userRemind, w);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
