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


import com.example.may.Model.User;
import com.example.may.Model.Work;
import com.example.may.R;


import com.example.may.View.WorkGroupDetailActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.lang.reflect.Field;
import java.text.SimpleDateFormat;

import java.util.HashMap;
import java.util.List;

public class WorkGroupAdapter extends RecyclerView.Adapter<WorkGroupAdapter.ListWorkHolder> {
    private List<Work> arrWorks;
    private String idGroup,idAdmin;
    private FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
    private User userRemind;


    public WorkGroupAdapter(List<Work> arrWorks, String idGroup, String idAdmin) {
        this.arrWorks = arrWorks;
        this.idGroup = idGroup;
        this.idAdmin = idAdmin;
    }

    @NonNull
    @Override
    public ListWorkHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_work_group, parent, false);
        return new ListWorkHolder(view);
    }

    @SuppressLint({"SetTextI18n", "SimpleDateFormat"})
    @Override
    public void onBindViewHolder(@NonNull ListWorkHolder holder, int position) {
        Work work = arrWorks.get(position);
        holder.workName.setText(work.getWorkName());

        loadLeader(holder, work);
        holder.workTime.setText("Thời gian: " + work.getWorkStart() + " - " + work.getWorkEnd());
        holder.workComplete.setVisibility(View.GONE);
        if (work.getWorkStatus()==2){
            holder.layoutWork.setBackgroundColor(Color.parseColor("#868BC34A"));
            holder.workComplete.setVisibility(View.VISIBLE);
            holder.imgMore.setVisibility(View.GONE);

            holder.workComplete.setText("Ngày hoàn thành: "+
                    new SimpleDateFormat("dd/MM/yyyyy").format(work.getDateComplete()));

        }

        holder.layoutWork.setOnClickListener(view -> {

            Intent intent = new Intent(view.getContext(), WorkGroupDetailActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("work", work);
            intent.putExtra("idGroup", idGroup);
            intent.putExtra("idAdmin", idAdmin);


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
        private TextView workName, leader, workTime, workComplete;
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
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(work.getLeader()).addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    User users = snapshot.getValue(User.class);
                    holder.leader.setText("Người phụ trách: " + users.getFullName());
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
                    //    pushNotificationRemind(work,v);
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
        status.put("dateComplete", System.currentTimeMillis());
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
                                UpdateWorkList(work, status);
                                UpdateWorkGroup(work, status);

                            }
                        }
                    }

                } else {
                    UpdateWorkList(work, status);
                    UpdateWorkGroup(work, status);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void UpdateWorkList(Work work, HashMap<String, Object> status) {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Work");
        dbRef.child(mUser.getUid()).child(work.getId()).addValueEventListener(new ValueEventListener() {
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

    private void UpdateWorkGroup(Work work, HashMap<String, Object> status) {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Groups");
        dbRef.child(idGroup).child("work").child(work.getId()).addValueEventListener(new ValueEventListener() {
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


   /* private void pushNotificationRemind(Work work, View v) {
        PreferenceManager preferenceManager = new PreferenceManager(v.getContext());
        String user = preferenceManager.getString("users");
        Gson gson = new Gson();
        userRemind = gson.fromJson(user, Users.class);

        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Users");
        dbRef.child(work.getLeader()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    Users users = snapshot.getValue(Users.class);
                    if (users != null){
                        ApiClient.pushNotificationRemindWork(
                                v.getContext(), users.getToken(), userRemind,work);

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }*/
}
