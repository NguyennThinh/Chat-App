package com.example.may.ViewManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListPopupWindow;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.may.Adapter.ListPopupDepartmentAdapter;
import com.example.may.Adapter.ListPopupPositionAdapter;
import com.example.may.AdapterManager.ManagerEmployeeAdapter;
import com.example.may.Api.ApiClient;
import com.example.may.Model.Department;
import com.example.may.Model.Position;
import com.example.may.Model.User;
import com.example.may.R;
import com.example.may.Utilities.RandomString;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateEmployee extends AppCompatActivity {
    //Views dialog
    private TextInputEditText email, name, birthday, phone;
    private AppCompatTextView filter_department, filter_position;
    private ImageView imgDrop, imgDropP, img;
    private RadioButton radNam, radNu;
    private Button btnAdd ;
    private LinearLayout layout_department, layout_position;
    private ProgressDialog progressDialog;

    private List<Department> arrDepartment;
    private List<Position> arrPositions;
    private ListPopupWindow listPopupDepartment, listPopupPosition;

    private  boolean isUp, isUpP;
    private User users;
    private String idDepartment, idPosition, idUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_employee);
        initUI();
        loadDepartment();
        setPopupListDepartment();
        loadEmployeee();
        layout_department.setOnClickListener(v -> {
            showAnimationFilerDepartment();

        });
        layout_position.setOnClickListener(v -> {
            showAnimationFilterPosition();

        });
        birthday.setOnClickListener(v -> {
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
                        if (format.parse(format.format(date.getTime())).compareTo(format.parse(format.format(new Date()))) >0){
                            birthday.setText("");
                            Toast.makeText(getApplicationContext(), "Ngày sinh phải nhỏ hơn ngày hiện tại", Toast.LENGTH_SHORT).show();
                        }else {
                            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                            birthday.setText(dateFormat.format(date.getTime()));
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }


                }
            },year, month, day);
            pickerDialog.show();
        });
        btnAdd.setOnClickListener(v -> {
            addEmployee();
        });
        img.setOnClickListener(v -> {
            onBackPressed();
        });

    }


    private void initUI() {
        email = findViewById(R.id.inputEmail);
        name = findViewById(R.id.inputName);
        birthday = findViewById(R.id.inputBirth);
        phone = findViewById(R.id.inputPhone);
        radNam = findViewById(R.id.radNam);
        radNu = findViewById(R.id.radNu);
        btnAdd = findViewById(R.id.btnAdd);
        img = findViewById(R.id.img_back);
        filter_department =  findViewById(R.id.filter_department);
        filter_position =  findViewById(R.id.filter_position);
        imgDrop = findViewById(R.id.img_drop);
        imgDropP = findViewById(R.id.img_drop_p);
        layout_department = findViewById(R.id.layout_name);
        layout_position = findViewById(R.id.layout_position);
        progressDialog = new ProgressDialog(this);

        arrDepartment = new ArrayList<>();
        arrPositions = new ArrayList<>();

        idUser = getIntent().getStringExtra("user_id");
    }
    private void loadEmployeee() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(idUser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                     users = snapshot.getValue(User.class);
                    name.setText(users.getFullName());
                    email.setText(users.getEmail());
                    birthday.setText(users.getBirthday());
                    phone.setText(users.getPhone());
                    if (users.isGender()){
                        radNam.setChecked(true);
                    }else {
                        radNu.setChecked(true);
                    }
                    loadEmployeeeDepartment();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void loadEmployeeeDepartment() {
        DatabaseReference databaseReference =FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(idUser).child("department").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (Department d: arrDepartment){
                        if (snapshot.getValue().toString().equals(d.getId())){
                            filter_department.setText(d.getName());

                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        databaseReference.child(idUser).child("position").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (Position d: arrPositions){
                        if (snapshot.getValue().toString().equals(d.getId())){
                            filter_position.setText(d.getName());
                        }
                    }
                }
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

    private void showAnimationFilerDepartment() {
        if (isUp) {
            if (UpdateEmployee.this.isFinishing()){
                listPopupDepartment.show();
            }
            listPopupDepartment.dismiss();
            imgDrop.startAnimation( AnimationUtils.loadAnimation(this, R.anim.rorate_180_360));

            //   myButton.setText("Slide up");
        } else {

            if (!UpdateEmployee.this.isFinishing()){
                listPopupDepartment.show();
            }
            imgDrop.startAnimation( AnimationUtils.loadAnimation(this, R.anim.rorate_0_180));

            //   myButton.setText("Slide down");
        }
        isUp = !isUp;
    }
    private void showAnimationFilterPosition() {
        if (isUpP) {
            if (UpdateEmployee.this.isFinishing()){
                listPopupPosition.show();
            }
            listPopupPosition.dismiss();
            imgDropP.startAnimation( AnimationUtils.loadAnimation(this, R.anim.rorate_180_360));

            //   myButton.setText("Slide up");
        } else {

            if (!UpdateEmployee.this.isFinishing()){
                listPopupPosition.show();
            }
            imgDropP.startAnimation( AnimationUtils.loadAnimation(this, R.anim.rorate_0_180));

            //   myButton.setText("Slide down");
        }
        isUpP = !isUpP;
    }
    private void setPopupListDepartment() {

        ListPopupDepartmentAdapter departmentAdapter = new ListPopupDepartmentAdapter(this, R.layout.item_spinner_selected, arrDepartment);

        listPopupDepartment = new ListPopupWindow(this);

        listPopupDepartment.setAdapter(departmentAdapter);
        listPopupDepartment.setAnchorView(filter_department);
        listPopupDepartment.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                showAnimationFilerDepartment();
                filter_department.setText("Phòng ban: " +arrDepartment.get(position).getName());

                layout_position.setVisibility(View.VISIBLE);
                loaListPosition(arrDepartment.get(position).getId());
                idDepartment = arrDepartment.get(position).getId();



                listPopupDepartment.dismiss();
            }
        });

    }

    private void loaListPosition(String id) {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Position");
        databaseReference.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrPositions.clear();
                for (DataSnapshot dn : snapshot.getChildren()){
                    Position position = dn.getValue(Position.class);
                    if (position != null){
                        arrPositions.add(position);


                    }
                }

                ListPopupPositionAdapter positionAdapter = new ListPopupPositionAdapter(getApplicationContext(), R.layout.item_spinner_selected, arrPositions);

                listPopupPosition = new ListPopupWindow(getApplicationContext());

                listPopupPosition.setAdapter(positionAdapter);
                listPopupPosition.setAnchorView(filter_department);
                listPopupPosition.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        showAnimationFilterPosition();

                        filter_position.setText("Chức vụ: " +arrPositions.get(position).getName());
                        idPosition = arrPositions.get(position).getId();





                        listPopupPosition.dismiss();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    private void addEmployee() {

        boolean gender;
        if (radNam.isChecked()) {
            gender = true;
        } else {
            gender = false;
        }
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Department");
        reference.child(users.getDepartment()).child("participant").child(users.getId()).removeValue();
        reference.child(users.getDepartment()).child("slParticipant").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    reference.child(users.getDepartment()).child("slParticipant").setValue(Integer.parseInt(snapshot.getValue().toString())-1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        HashMap<String, Object> p = new HashMap<>();
        p.put("id", users.getId());
        p.put("partDate", System.currentTimeMillis());
        p.put("role", 3);
        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Department");
        reference1.child(idDepartment).child("participant").child(users.getId()).setValue(p);
        reference1.child(idDepartment).child("slParticipant").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    reference1.child(idDepartment).child("slParticipant").setValue(Integer.parseInt(snapshot.getValue().toString())+1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(users.getId()).child("department").setValue(idDepartment);
        databaseReference.child(users.getId()).child("position").setValue(idPosition);



    }


}