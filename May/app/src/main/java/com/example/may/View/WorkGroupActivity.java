package com.example.may.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.may.Adapter.AddWorkGroupAdaper;
import com.example.may.Adapter.WorkGroupAdapter;
import com.example.may.Model.Group;
import com.example.may.Model.Work;
import com.example.may.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class WorkGroupActivity extends AppCompatActivity {
    //Views
    private ImageView imgReturn, addWork;
    private RecyclerView listWork;

    //Adapter list work in group
    private WorkGroupAdapter listWorkAdapter;
    //Adapter add work group dialog
    private AddWorkGroupAdaper addWorkAdapter;


    private List<Work> arrWork;

    private Group group;

    private FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_group);
        initUI();
        loadListWork();
        imgReturn.setOnClickListener(view ->{
            finish();
        });
        addWork.setOnClickListener(view->{
            if (mUser.getUid().equals(group.getUserCreate())){
                openAddWorkDialog();
            }else {
                Toast.makeText(getApplicationContext(), "Bạn không có quyền thêm công việc", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initUI() {
        addWork = findViewById(R.id.add_work);
        imgReturn = findViewById(R.id.return_activity);
        listWork = findViewById(R.id.list_work_group);
        arrWork = new ArrayList<>();


        group = (Group) getIntent().getExtras().get("group");

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        RecyclerView.ItemDecoration itemDecoration =  new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        listWork.setLayoutManager(layoutManager);
        listWork.addItemDecoration(itemDecoration);


    }
    private void loadListWork() {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Groups");
        dbRef.child(group.getGroupID()).child("work").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrWork.clear();
                for (DataSnapshot dn : snapshot.getChildren()){
                    Work work = dn.getValue(Work.class);
                    if (work != null){
                        arrWork.add(work);
                    }
                }
                listWorkAdapter = new WorkGroupAdapter(arrWork, group.getGroupID(),group.getUserCreate());
                listWork.setAdapter(listWorkAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void openAddWorkDialog() {
        FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_add_work_group);
        Window window = dialog.getWindow();

        if (window == null) {
            return;
        }
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams layoutParams = window.getAttributes();

        layoutParams.gravity = Gravity.CENTER;

        window.setAttributes(layoutParams);

        Button btnCancel = dialog.findViewById(R.id.btn_cancel);

        List<Work> arrWorks = new ArrayList<>();
        RecyclerView list_work = dialog.findViewById(R.id.list_my_work);
        LinearLayoutManager layoutManager = new LinearLayoutManager(dialog.getContext());
        RecyclerView.ItemDecoration  itemDecoration = new DividerItemDecoration(dialog.getContext(), DividerItemDecoration.VERTICAL);
        list_work.setLayoutManager(layoutManager);
        list_work.addItemDecoration(itemDecoration);


        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Work");
        Query query = databaseReference.child(mUser.getUid()).orderByChild("workCreateDate");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrWorks.clear();
                for (DataSnapshot dn : snapshot.getChildren()){
                    Work work = dn.getValue(Work.class);
                    if (work != null){
                        if (work.getLeader().equals(mUser.getUid())){
                            if (work.getWorkStatus() == 1){
                                arrWorks.add(0,work);
                            }
                        }
                    }
                }
                addWorkAdapter = new AddWorkGroupAdaper(arrWorks, group.getGroupID());
                list_work.setAdapter(addWorkAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        btnCancel.setOnClickListener(view -> {
            dialog.dismiss();
        });

        if (!WorkGroupActivity.this.isFinishing()) {
            dialog.show();
        }
    }
}