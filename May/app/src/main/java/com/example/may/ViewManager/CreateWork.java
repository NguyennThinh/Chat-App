package com.example.may.ViewManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListPopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.may.Adapter.LayoutUserChoosenAdapter;
import com.example.may.Adapter.ListPopupDepartmentAdapter;
import com.example.may.AdapterManager.LayoutUserSelectDepartmentAdapter;
import com.example.may.FcmNotification.ApiClient;
import com.example.may.Interface.iAddMemberGroup;
import com.example.may.Model.Department;
import com.example.may.Model.Participant;
import com.example.may.Model.User;
import com.example.may.Model.Work;
import com.example.may.R;
import com.example.may.Utilities.RandomString;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class CreateWork extends AppCompatActivity implements iAddMemberGroup {
    //Views
    private TextInputEditText workName, workDescription, userInChange,workStart, workEnd;
    private TextView userCreateName;
    ImageButton imgUserChange;
    private Button btnCreateWork;
    private RecyclerView listParticipant;
    ImageView addParticipant, imgReturn, imgStart, imgEnd,imgDrop;
    private LinearLayout layout_name;
    private AppCompatTextView filterDepartment;

    private List<Department> arrDepartment;
    private List<User> arrUsers, arrUserSelect;
    private List<Participant> arrParticipants ;
    private User users, user;

    //Declare Firebase
    private FirebaseUser mUser;

    private LayoutUserChoosenAdapter adapterUserChoosen;
    private LayoutUserSelectDepartmentAdapter adapterUserSelect;


    private ListPopupWindow listPopupDepartment;
    private  boolean isUp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_work);


        initUI();
        loadDepartment();

        imgStart.setOnClickListener(view -> {
            selectDateStart();
        });
        imgEnd.setOnClickListener(view->{
            selectDateEnd();
        });

        imgUserChange.setOnClickListener(view->{
            Intent i = new Intent(this, LeaderActivity.class);

            startActivityForResult(i, 1);
        });

        btnCreateWork.setOnClickListener(view->{
            createWork();
        });
        addParticipant.setOnClickListener(view->{
            listParticipant.setVisibility(View.VISIBLE);
            loadEmployee();
        });
        imgReturn.setOnClickListener(v -> {
            onBackPressed();
        });
        layout_name.setOnClickListener(v -> {
            showAnimationFiler();

        });

        setPopupListDepartment();
    }

    private void initUI() {
        workName = findViewById(R.id.work_name);
        userCreateName = findViewById(R.id.user_create);
        workDescription = findViewById(R.id.work_description);
        workStart = findViewById(R.id.work_start);
        workEnd = findViewById(R.id.work_end);
        userInChange = findViewById(R.id.user_in_change);
        imgUserChange = findViewById(R.id.img_user_change);
        btnCreateWork = findViewById(R.id.btn_create_work);
        listParticipant = findViewById(R.id.list_participant);
        addParticipant = findViewById(R.id.add_participant);
        imgReturn = findViewById(R.id.return_activity);
        imgStart = findViewById(R.id.date_start);
        imgEnd = findViewById(R.id.date_end);
        layout_name = findViewById(R.id.layout_name);

        mUser = FirebaseAuth.getInstance().getCurrentUser();

        imgDrop = findViewById(R.id.img_drop);
        filterDepartment = findViewById(R.id.filter_department);
        imgDrop = findViewById(R.id.img_drop);
        arrDepartment = new ArrayList<>();


        arrUsers = new ArrayList<>();
        arrUserSelect = new ArrayList<>();
        arrParticipants = new ArrayList<>();


        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        listParticipant.setLayoutManager(layoutManager);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                 user  = snapshot.getValue(User.class);
                userCreateName.setText(user.getFullName());
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

    private void showAnimationFiler() {
        if (isUp) {
            if (CreateWork.this.isFinishing()){
                listPopupDepartment.show();
            }
            listPopupDepartment.dismiss();
            imgDrop.startAnimation( AnimationUtils.loadAnimation(this, R.anim.rorate_180_360));

            //   myButton.setText("Slide up");
        } else {

            if (!CreateWork.this.isFinishing()){
                listPopupDepartment.show();
            }
            imgDrop.startAnimation( AnimationUtils.loadAnimation(this, R.anim.rorate_0_180));

            //   myButton.setText("Slide down");
        }
        isUp = !isUp;
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

                                        if (users != null){
                                            adapterUserSelect = new LayoutUserSelectDepartmentAdapter(arrUsers, users.getId(), CreateWork.this);
                                        }else {
                                            adapterUserSelect = new LayoutUserSelectDepartmentAdapter(arrUsers,"", CreateWork.this);
                                        }
                                        listParticipant.setAdapter(adapterUserSelect);

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
    private void selectDateStart() {

        Calendar date = Calendar.getInstance();
        int year = date.get(Calendar.YEAR);
        int month = date.get(Calendar.MONTH);
        int day = date.get(Calendar.DATE);


        DatePickerDialog pickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                date.set(year, month, dayOfMonth);
                DateFormat format = new SimpleDateFormat("dd/MM/yyyy");

                try {
                    if (format.parse(format.format(date.getTime())).compareTo(format.parse(format.format(new Date()))) <0){
                        workStart.setText("");
                        Toast.makeText(getApplicationContext(), "Ngày bắt đầu phải lớn hơn hoặc bằng ngày hiện tại", Toast.LENGTH_SHORT).show();
                    }else {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                        workStart.setText(dateFormat.format(date.getTime()));
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }


            }
        },year, month, day);
        pickerDialog.show();
    }

    private void selectDateEnd() {
        Calendar date = Calendar.getInstance();
        int year = date.get(Calendar.YEAR);
        int month = date.get(Calendar.MONTH);
        int day = date.get(Calendar.DATE);


        DatePickerDialog pickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                date.set(year, month, dayOfMonth);
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                workEnd.setText(dateFormat.format(date.getTime()));
            }
        },year, month, day);
        pickerDialog.show();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {
                users = (User) data.getExtras().get("leader");
                userInChange.setText(users.getFullName());
                loadEmployee();
            }
        }
    }
    private void loadEmployee() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrUsers.clear();
                for (DataSnapshot dn : snapshot.getChildren()){
                    User user = dn.getValue(User.class);

                    if (user != null ){

                        arrUsers.add(user);


                    }
                }

                if (users != null){
                    adapterUserSelect = new LayoutUserSelectDepartmentAdapter(arrUsers, users.getId(), CreateWork.this);
                    listParticipant.setAdapter(adapterUserSelect);
                }else {
                    adapterUserSelect = new LayoutUserSelectDepartmentAdapter(arrUsers,"", CreateWork.this);
                    listParticipant.setAdapter(adapterUserSelect);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void createWork() {
        String work_name = workName.getText().toString().trim();

        String description = workDescription.getText().toString().trim();
        String work_start = workStart.getText().toString().trim();
        String work_end = workEnd.getText().toString().trim();
        String user_change = userInChange.getText().toString().trim();

        if (work_name.equals("")||  work_start.equals("") || work_end.equals("") || user_change.equals("")){
            Toast.makeText(getApplicationContext(), "Bạn cần nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            
        }else {
            long timeCreate = System.currentTimeMillis();

            HashMap<String, Object> work = new HashMap<>();

            RandomString gen = new RandomString(15, ThreadLocalRandom.current());
            long time = System.currentTimeMillis();
            String idWork =gen.nextString();
            work.put("id", idWork);
            work.put("workName", work_name);
            work.put("userCreate",mUser.getUid());
            work.put("workDescription", description);
            work.put("workStart", work_start);
            work.put("workEnd", work_end);
            work.put("leader", users.getId());
            work.put("workCreateDate", time);
            work.put("workStatus", 1);
            work.put("dateComplete", 0);


            arrParticipants.add(new Participant(users.getId(), 1, timeCreate));
            for (User user: arrUserSelect){
                arrParticipants.add(new Participant(user.getId(),3, timeCreate));

            }

            Work w = new Work(idWork,work_name,mUser.getUid(),description,work_start,work_end,users.getId(), time,1,0);

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Work");
            databaseReference.child(users.getId()).child(idWork).setValue(work);

            DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("Work")
                    .child(users.getId()).child(idWork);
            databaseReference1.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    for (Participant p : arrParticipants) {
                        databaseReference1.child("participant").child(p.getId()).setValue(p);
                    }
                    getToken(arrParticipants, w);
                    Toast.makeText(getApplicationContext(), "Tạo công việc thành công", Toast.LENGTH_SHORT).show();

                    //Logs
                    HashMap<String, Object> log = new HashMap<>();
                    log.put("idUser", mUser.getUid());
                    log.put("log", "tạo công việc "+ w.getWorkName());
                    log.put("time", timeCreate);
                    DatabaseReference dbReff = FirebaseDatabase.getInstance().getReference("Logs");
                    dbReff.push().setValue(log);
                    onBackPressed();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    private void getToken(List<Participant> arrParticipants, Work w) {
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
                ApiClient.pushNotificationCreateWork(CreateWork.this,arrToken, user, w);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void addMemberGroup(List<User> arrEmployees) {
        arrUserSelect = arrEmployees;
    }
}