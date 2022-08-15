package com.example.may.ViewManager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListPopupWindow;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.may.Adapter.ListPopupDepartmentAdapter;
import com.example.may.AdapterManager.LayouManagerSelectAdapter;
import com.example.may.Interface.iAddManager;
import com.example.may.Model.Department;
import com.example.may.Model.Participant;
import com.example.may.Model.User;
import com.example.may.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class LeaderActivity extends AppCompatActivity {
    //Views
    private RecyclerView list_leader;
    private ImageView imgDrop, imgBack;

    private LinearLayout layout_name;
    private AppCompatTextView filterDepartment;

    //Adapter
    private LayouManagerSelectAdapter adapter;

    private List<Participant> arrParticipant;
    private List<User> arrUsers;
    private List<Department> arrDepartment;

    //Declare firebase
    private FirebaseUser mUser;


    private ListPopupWindow listPopupDepartment;
    private  boolean isUp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager);

        initUI();
        loadDepartment();
        setPopupListDepartment();
        layout_name.setOnClickListener(v -> {
            showAnimationFiler();

        });

        imgBack.setOnClickListener(v -> {
            onBackPressed();
        });

    }

    private void initUI() {
        list_leader = findViewById(R.id.list_leader_work);
        layout_name = findViewById(R.id.layout_name);
        imgBack = findViewById(R.id.img_back);
        filterDepartment = findViewById(R.id.filter_department);
        imgDrop = findViewById(R.id.img_drop);
        arrDepartment = new ArrayList<>();

        mUser = FirebaseAuth.getInstance().getCurrentUser();

        arrParticipant= (List<Participant>) getIntent().getSerializableExtra("list");


        arrUsers = new ArrayList<>();
        loadListUsersWork();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        list_leader.setLayoutManager(layoutManager);
        list_leader.addItemDecoration(itemDecoration);


    }

    private void loadListUsersWork() {


                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        arrUsers.clear();
                        for (DataSnapshot dn : snapshot.getChildren()){
                            User user = dn.getValue(User.class);
                            if (user != null) {
                                arrUsers.add(user);
                            }

                        }
                        adapter = new LayouManagerSelectAdapter(arrUsers, new iAddManager() {
                            @Override
                            public void addManager(User users) {

                                Intent intent = new Intent();
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("leader", users);
                                intent.putExtras(bundle);
                                setResult(RESULT_OK, intent);

                                finish();
                            }
                        });
                        list_leader.setAdapter(adapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
    private void loadDepartment() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Department");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrDepartment.clear();
                for (DataSnapshot dn : snapshot.getChildren()) {
                    Department department = dn.getValue(Department.class);
                    if (department != null) {
                        arrDepartment.add(department);
                    }
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void setPopupListDepartment() {

        ListPopupDepartmentAdapter departmentAdapter = new ListPopupDepartmentAdapter(this, R.layout.item_spinner_selected, arrDepartment);

        listPopupDepartment = new ListPopupWindow(this);

        listPopupDepartment.setAdapter(departmentAdapter);
        listPopupDepartment.setAnchorView(filterDepartment);
        listPopupDepartment.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {



                filterDepartment.setText("Phòng ban: " +arrDepartment.get(position).getName());


                DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference("Department");
                firebaseDatabase.child(arrDepartment.get(position).getId()).child("participant").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        showAnimationFiler();
                        arrUsers.clear();
                        for (DataSnapshot dn : snapshot.getChildren()) {
                            Participant participant = dn.getValue(Participant.class);
                            DatabaseReference db = FirebaseDatabase.getInstance().getReference("Users");
                            db.child(participant.getId()).addValueEventListener(new ValueEventListener() {
                                @SuppressLint("NotifyDataSetChanged")
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {

                                        User user = snapshot.getValue(User.class);
                                        arrUsers.add(user);
                                        adapter.notifyDataSetChanged();

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


                listPopupDepartment.dismiss();
            }
        });

    }

    private void showAnimationFiler() {
        if (isUp) {
            if (LeaderActivity.this.isFinishing()){
                listPopupDepartment.show();
            }
            listPopupDepartment.dismiss();
            imgDrop.startAnimation( AnimationUtils.loadAnimation(this, R.anim.rorate_180_360));

            //   myButton.setText("Slide up");
        } else {

            if (!LeaderActivity.this.isFinishing()){
                listPopupDepartment.show();
            }
            imgDrop.startAnimation( AnimationUtils.loadAnimation(this, R.anim.rorate_0_180));

            //   myButton.setText("Slide down");
        }
        isUp = !isUp;
    }




}