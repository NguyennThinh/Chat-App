package com.example.may.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.may.Adapter.SpinnerLeaderAdapter;
import com.example.may.Adapter.WorkChildGroupAdapter;
import com.example.may.Model.Participant;
import com.example.may.Model.User;
import com.example.may.Model.Work;
import com.example.may.R;
import com.example.may.Utilities.RandomString;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class WorkGroupDetailActivity extends AppCompatActivity {
    //Views
    private TextView workName, workDescription, workLeader, workStart, workEnd, workStatus, workComplete;
    private ImageView addWorkChild, imgReturn, imgMore;
    private RecyclerView listWorkChild;

    //ListChild Adapter
    private WorkChildGroupAdapter childWorkAdapter;


    private List<Work> arrWork;
    private Work work;

    //Load list participant = idGroup
    private String idGroup, idAdmin;
    //List user of dialog
    private List<User> arrUsers;

    //Spinner adapter
    private SpinnerLeaderAdapter spAdapter;

    //User firebase
    private FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_group_detail);

        initUI();
        loadInfoWork();
        loadListWorkChild();


        imgReturn.setOnClickListener(view->{
            onBackPressed();
        });
        addWorkChild.setOnClickListener(view -> {
         //   openAddWorkDialog();
            Intent intent = new Intent(this, AddWorkChildActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("work_group", work);
            intent.putExtra("id_group", idGroup);
            intent.putExtras(bundle);
            startActivity(intent);
        });
    }


    private void initUI() {
        workName = findViewById(R.id.work_name);
        workDescription = findViewById(R.id.work_description);
        workLeader = findViewById(R.id.work_leader);
        workStart = findViewById(R.id.work_start);
        workEnd = findViewById(R.id.work_end);
        workStatus = findViewById(R.id.work_status);
        workComplete = findViewById(R.id.date_complete);
        addWorkChild = findViewById(R.id.add_child_work);
        listWorkChild = findViewById(R.id.listChildWork);
        imgMore = findViewById(R.id.img_more);
        imgReturn = findViewById(R.id.return_activity);

        arrWork = new ArrayList<>();

        work = (Work) getIntent().getExtras().get("work");
        idGroup = getIntent().getStringExtra("idGroup");
        idAdmin = getIntent().getStringExtra("idAdmin");



        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        listWorkChild.setLayoutManager(layoutManager);

        childWorkAdapter = new WorkChildGroupAdapter(arrWork, idGroup, work.getId(),idAdmin);
        listWorkChild.setAdapter(childWorkAdapter);

    }

    private void loadInfoWork() {
        workName.setText(work.getWorkName());
        workDescription.setText(work.getWorkDescription());
        workStart.setText(work.getWorkStart());
        workEnd.setText(work.getWorkEnd());

        if (work.getWorkStatus() == 1) {
            workStatus.setText("Đang tiến hành");
            workStatus.setTextColor(Color.parseColor("#FFFF9800"));
            workComplete.setVisibility(View.GONE);

        } else if (work.getWorkStatus() == 2) {
            workStatus.setText("Đã hoàn thành");
            workStatus.setTextColor(Color.GREEN);
            workComplete.setText(new SimpleDateFormat("dd/MM/yyyyy").format(work.getDateComplete()));

            addWorkChild.setVisibility(View.INVISIBLE);
        } else {
            workStatus.setText("Trễ thời hạn");
            workStatus.setTextColor(Color.RED);
            workComplete.setText(new SimpleDateFormat("dd/MM/yyyyy").format(work.getDateComplete()));

        }


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(work.getLeader()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User users = snapshot.getValue(User.class);
                if (users != null) {
                    workLeader.setText(users.getFullName());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }

    private void loadListWorkChild() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("WorkChild");
        databaseReference.child(work.getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrWork.clear();
                if (snapshot.exists()){

                    for (DataSnapshot dn : snapshot.getChildren()){
                        Work work = dn.getValue(Work.class);
                        arrWork.add(work);
                    }
                }
                childWorkAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void openAddWorkDialog(){
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_add_work_child_group);
        Window window = dialog.getWindow();

        if (window == null) {
            return;
        }
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams layoutParams = window.getAttributes();

        layoutParams.gravity = Gravity.CENTER;

        window.setAttributes(layoutParams);

        //Views dialog
        EditText name_work, description_work, work_start,work_end;
        ImageView imgStart, imgEnd;
        Button btnAdd, btnCancel;
        Spinner spLeader;

        //Initialize view
        arrUsers = new ArrayList<>();

        btnAdd = dialog.findViewById(R.id.btn_add);
        btnCancel = dialog.findViewById(R.id.btn_cancel);
        spLeader = dialog.findViewById(R.id.spLeader);

        name_work = dialog.findViewById(R.id.work_name);
        description_work = dialog.findViewById(R.id.work_description);
        work_start = dialog.findViewById(R.id.work_start);
        work_end = dialog.findViewById(R.id.work_end);
        imgStart = dialog.findViewById(R.id.imgStart);
        imgEnd = dialog.findViewById(R.id.imgEnd);

        //event click img date set date for work start and work end
        Calendar date = Calendar.getInstance();
        int year = date.get(Calendar.YEAR);
        int month = date.get(Calendar.MONTH);
        int day = date.get(Calendar.DATE);
        imgStart.setOnClickListener(view->{

            DatePickerDialog pickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    date.set(year,month,day);
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    work_start.setText(dateFormat.format(date.getTime()));
                }
            },year, month, day);
            pickerDialog.show();
        });

        imgEnd.setOnClickListener(view->{

            DatePickerDialog pickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    date.set(year,month,day);
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    work_end.setText(dateFormat.format(date.getTime()));
                }
            },year, month, day);
            pickerDialog.show();
        });

        //load list participant of group for spinner
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Groups");
        Query query = dbRef.child(idGroup).child("participant").orderByChild("role");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrUsers.clear();

                for (DataSnapshot dn : snapshot.getChildren()) {
                    Participant participant = dn.getValue(Participant.class);

                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
                    databaseReference.child(participant.getId()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            if (snapshot.exists()){
                                User users = snapshot.getValue(User.class);
                                if (users != null) {
                                    arrUsers.add(users);
                                }
                            }
                            spAdapter = new SpinnerLeaderAdapter(getApplicationContext(), R.layout.item_spinner_selected, arrUsers);
                            spLeader.setAdapter(spAdapter);
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


        //Get value spinner selected
        final String[] leader = new String[1];
        spLeader.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                User users = (User) parent.getItemAtPosition(position);
                leader[0] = users.getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        //Handle buttonAdd click
        btnAdd.setOnClickListener(view -> {
            HashMap<String, Object> works = new HashMap<>();

            RandomString gen = new RandomString(15, ThreadLocalRandom.current());

            String id = gen.nextString();
            works.put("id", id);
            works.put("workName", name_work.getText().toString());
            works.put("userCreate",mUser.getUid());
            works.put("workDescription", description_work.getText().toString());
            works.put("workStart", work_start.getText().toString());
            works.put("workEnd", work_end.getText().toString());
            works.put("leader", leader[0]);
            works.put("workCreateDate", System.currentTimeMillis());
            works.put("workStatus", 1);
            works.put("dateComplete", 0);

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("WorkChild");
            databaseReference.child(work.getId()).child(id).setValue(works).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(getApplicationContext(), "Thêm công việc thành công", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            });
        });

        btnCancel.setOnClickListener(view -> {
            dialog.dismiss();
        });

        if (!WorkGroupDetailActivity.this.isFinishing()) {
            dialog.show();
        }
    }
}