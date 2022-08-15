package com.example.may.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListPopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.may.Adapter.AddParticipantAdapter;
import com.example.may.Adapter.AddParticipantGroupAdapter;
import com.example.may.Adapter.AddWorkGroupAdaper;
import com.example.may.Adapter.LayoutUserSelectAdapter;
import com.example.may.Adapter.ListPopupDepartmentAdapter;
import com.example.may.Interface.iAddMemberGroup;
import com.example.may.Model.Department;
import com.example.may.Model.Group;
import com.example.may.Model.Participant;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AddParticipantWorkActivity extends AppCompatActivity implements iAddMemberGroup {
    private RecyclerView rcv_list_friend;
    private TextView submit;
    private ImageView imgReturn,imgDropDepartment;
    private AppCompatTextView filterDepartment;


    List<User> arrUsers, mUsers;
    private List<Participant> arrMember;
    private List<Department> arrDepartment;

    private FirebaseUser mUser;
    private Work work;

    private LayoutUserSelectAdapter adapterUserSelect;
    private AddParticipantAdapter adapter;
    private ListPopupWindow  listPopupDepartment;

    private LinearLayout layout_filter;

    private  boolean isUp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_participant_work);

        initUI();

        imgReturn.setOnClickListener(v -> {
            onBackPressed();
        });
        loadUser();

        submit.setOnClickListener(view -> {
            addParticipant();
            this.finish();
        });

        layout_filter.setOnClickListener(v -> {

            showAnimationFiler();

        });
        loadDepartment();
        setPopupListDepartment();
    }



    private void initUI() {
        rcv_list_friend = findViewById(R.id.list_user);
        submit = findViewById(R.id.submit);
        imgReturn = findViewById(R.id.return_activity);
        layout_filter = findViewById(R.id.layout_filter);
        imgDropDepartment = findViewById(R.id.img_drop_department);
        filterDepartment = findViewById(R.id.filter_department);


        mUser = FirebaseAuth.getInstance().getCurrentUser();

        work = (Work) getIntent().getExtras().get("work");

        arrUsers = new ArrayList<>();
        arrMember = new ArrayList<>();
        arrDepartment = new ArrayList<>();

        adapterUserSelect = new LayoutUserSelectAdapter(arrUsers, AddParticipantWorkActivity.this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL);
        adapter = new AddParticipantAdapter(arrUsers, this, work);

        rcv_list_friend.setLayoutManager(layoutManager);
        rcv_list_friend.addItemDecoration(itemDecoration);
        rcv_list_friend.setAdapter(adapter);

    }

    private void loadUser() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrUsers.clear();
                for (DataSnapshot dn : snapshot.getChildren()) {
                    User employee = dn.getValue(User.class);

                    if (employee != null && !employee.getId().equals(mUser.getUid())) {
                        arrUsers.add(employee);
                    }
                }
            adapter.notifyDataSetChanged();

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

                DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Department");
                dbRef.child(arrDepartment.get(position).getId()).child("participant").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        showAnimationFiler();
                        arrUsers.clear();
                        for (DataSnapshot dn : snapshot.getChildren()){
                            Participant participant = dn.getValue(Participant.class);
                            DatabaseReference db = FirebaseDatabase.getInstance().getReference("Users");
                            db.child(participant.getId()).addValueEventListener(new ValueEventListener() {
                                @SuppressLint("NotifyDataSetChanged")
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()){
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
    private void loadDepartment() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Department");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrDepartment.clear();
                for (DataSnapshot dn : snapshot.getChildren()){
                    Department department = dn.getValue(Department.class);
                    if (department != null){
                        arrDepartment.add(department);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    private void addParticipant() {


        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Work");
        dbRef.child(work.getLeader()).child(work.getId()).child("participant").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (Participant p : arrMember) {
                        snapshot.getRef().child(p.getId()).setValue(p);
                    }

                    Toast.makeText(getApplicationContext(), "Thêm thành viên thành công", Toast.LENGTH_SHORT).show();

                }else {
                    for (Participant p : arrMember) {
                        dbRef.child(work.getLeader()).child(work.getId()).child("participant").child(p.getId()).setValue(p);
                    }

                }

                //Logs
                HashMap<String, Object> log = new HashMap<>();
                log.put("idUser", mUser.getUid());
                log.put("log", "thêm thành viên vào công việc "+ work.getWorkName());
                log.put("time", System.currentTimeMillis());
                DatabaseReference dbReff = FirebaseDatabase.getInstance().getReference("Logs");
                dbReff.push().setValue(log);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        //Logs



    }
    private void showAnimationFiler() {
        if (isUp) {

            listPopupDepartment.dismiss();
            imgDropDepartment.startAnimation( AnimationUtils.loadAnimation(this, R.anim.rorate_180_360));

            //   myButton.setText("Slide up");
        } else {

            listPopupDepartment.show();
            imgDropDepartment.startAnimation( AnimationUtils.loadAnimation(this, R.anim.rorate_0_180));

            //   myButton.setText("Slide down");
        }
        isUp = !isUp;
    }

    @Override
    public void addMemberGroup(List<User> arrUser) {
        mUsers = arrUser;
        for (User e : mUsers) {
            arrMember.add(new Participant(e.getId(), 3, System.currentTimeMillis()));
        }
    }
}