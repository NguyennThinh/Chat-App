package com.example.may.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.may.Adapter.AddParticipantAdapter;
import com.example.may.Adapter.AddParticipantWorkChildAdapter;
import com.example.may.Interface.iAddMemberGroup;
import com.example.may.Model.Participant;
import com.example.may.Model.User;
import com.example.may.Model.Work;
import com.example.may.R;
import com.example.may.Utilities.RandomString;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class AddWorkChildActivity extends AppCompatActivity implements iAddMemberGroup {
    //Views
    private TextInputEditText workName, workDescription,workStart, workEnd;
    private TextView userCreateName;

    private Button btnCreateWork;
    private RecyclerView listParticipant;
    private ImageView addParticipant, imgReturn, imgStart, imgEnd;

    private Work work;
    private String idGroup;
    private List<User> arrUsers, mUsers;
    private List<Participant> arrMember;

    private FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();

    private AddParticipantWorkChildAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_work_child);
        initUI();
        loadParticipant();

        btnCreateWork.setOnClickListener(v -> {
            createWork();
        });

        imgStart.setOnClickListener(v -> {
            selectDateStart();
        });
        imgEnd.setOnClickListener(v -> {
            selectDateEnd();
        });
        imgReturn.setOnClickListener(v -> {
            onBackPressed();
        });
    }




    private void initUI() {
        workName = findViewById(R.id.work_name);
        userCreateName = findViewById(R.id.user_create);
        workDescription = findViewById(R.id.work_description);
        workStart = findViewById(R.id.work_start);
        workEnd = findViewById(R.id.work_end);


        btnCreateWork = findViewById(R.id.btn_create_work);
        listParticipant = findViewById(R.id.list_participant);
        addParticipant = findViewById(R.id.add_participant);
        imgReturn = findViewById(R.id.return_activity);
        imgStart = findViewById(R.id.date_start);
        imgEnd = findViewById(R.id.date_end);

        arrUsers = new ArrayList<>();
        arrMember = new ArrayList<>();
        work = (Work) getIntent().getExtras().get("work_group");
        idGroup = getIntent().getStringExtra("id_group");


        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL);
        adapter = new AddParticipantWorkChildAdapter(arrUsers, this, work, idGroup);

        listParticipant.setLayoutManager(layoutManager);
        listParticipant.addItemDecoration(itemDecoration);
        listParticipant.setAdapter(adapter);


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User users = snapshot.getValue(User.class);
                if (users != null) {
                    userCreateName.setText(users.getFullName());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }



    private void loadParticipant() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Groups");
        databaseReference.child(idGroup).child("participant").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrUsers.clear();
                for (DataSnapshot dn : snapshot.getChildren()){
                    Participant participant = dn.getValue(Participant.class);
                    if (participant != null){
                        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Users");
                        dbRef.child(participant.getId()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()){
                                    User user = snapshot.getValue(User.class);
                                    arrUsers.add(user);

                                }
                                adapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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

                try {
                    if (dateFormat.parse(dateFormat.format(date.getTime())).compareTo(dateFormat.parse(work.getWorkEnd())) <0){

                        workEnd.setText(dateFormat.format(date.getTime()));
                    }else {
                        workStart.setText("");
                        Toast.makeText(getApplicationContext(), "Ngày kết thúc phải trc ngày kết thúc công việc lớn", Toast.LENGTH_SHORT).show();

                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }


            }
        },year, month, day);
        pickerDialog.show();
    }
    private void createWork() {
        String name  = workName.getText().toString().trim();
        String description = workDescription.getText().toString().trim();
        String start = workStart.getText().toString().trim();
        String end = workEnd.getText().toString().trim();
        RandomString gen = new RandomString(15, ThreadLocalRandom.current());
        long time = System.currentTimeMillis();
        String idWork =gen.nextString();

        if (name.equals("") || description.equals("")|| start.equals("") || end.equals("")){
            Toast.makeText(getApplicationContext(), "Bạn chưa nhập đủ thông tin", Toast.LENGTH_SHORT).show();
        }else {
            long timeCreate = System.currentTimeMillis();

            HashMap<String, Object> works = new HashMap<>();


            works.put("id", idWork);
            works.put("workName", name);
            works.put("userCreate",work.getLeader());
            works.put("workDescription", description);
            works.put("workStart", start);
            works.put("workEnd", end);
            works.put("leader", "");
            works.put("workCreateDate", time);
            works.put("workStatus", 1);
            works.put("dateComplete", 0);

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("WorkChild");
            databaseReference.child(work.getId()).child(idWork).setValue(works).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    databaseReference.child(work.getId()).child(idWork).child("participant").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                for (Participant p : arrMember) {
                                    snapshot.getRef().child(p.getId()).setValue(p);
                                }

                            }else {
                                for (Participant p : arrMember) {
                                    databaseReference.child(work.getId()).child(idWork).child("participant").child(p.getId()).setValue(p);
                                }

                            }
                            Toast.makeText(getApplicationContext(), "Tạo công việc thành công", Toast.LENGTH_SHORT).show();
                            onBackPressed();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            });


        }

    }
    @Override
    public void addMemberGroup(List<User> arrUser) {
        mUsers = arrUser;
        for (User e : mUsers) {
            arrMember.add(new Participant(e.getId(), 3, System.currentTimeMillis()));
        }
    }
}