package com.example.may.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.may.Model.Department;
import com.example.may.Model.User;
import com.example.may.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class EmployeeDetailActivity extends AppCompatActivity {
    //Views
    private ImageView imgBackground, imgBack;
    private CircleImageView imgUser;
    private TextView tvName, tvBirthday, tvGender, tvPhone, tvEmail, tvPosition, tvDepartment;

    private String idUser;

    private User users;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_detail);

        initUI();
        loadProfile();
    }

    private void initUI() {
        imgBackground = findViewById(R.id.imgBackground);
        imgUser = findViewById(R.id.imgUser);
        tvName = findViewById(R.id.tv_Name);
        tvBirthday = findViewById(R.id.tv_birthday);
        tvGender = findViewById(R.id.tv_gender);
        tvPhone = findViewById(R.id.tv_phone);
        tvEmail = findViewById(R.id.tv_email);
        tvDepartment = findViewById(R.id.tv_department);
        imgBack = findViewById(R.id.img_back);
        tvPosition = findViewById(R.id.tv_position);


        idUser = getIntent().getStringExtra("id_em");

    }

    private void loadProfile() {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(idUser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    users = snapshot.getValue(User.class);
                    if (users != null) {

                        if (users.getAvatar().equals("default")) {
                            imgUser.setImageResource(R.drawable.ic_logo);
                        } else {
                            Picasso.get().load(users.getAvatar()).into(imgUser);
                        }

                        if (users.getBackgroundPhoto().equals("default")) {
                            imgBackground.setImageResource(R.drawable.ic_launcher_foreground);
                        } else {
                            Picasso.get().load(users.getBackgroundPhoto()).into(imgBackground);
                        }

                        tvName.setText(users.getFullName());
                        tvBirthday.setText(users.getBirthday());
                        tvPhone.setText(users.getPhone());
                        tvEmail.setText(users.getEmail());

                        tvPosition.setText(users.getPosition());
                        if (users.isGender()) {
                            tvGender.setText("Nam");
                        } else {
                            tvGender.setText("Nữ");
                        }

                        getDepartments(users);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }



    private void getDepartments(User user) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(user.getId()).child("department").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {

                    for (DataSnapshot dn : snapshot.getChildren()) {
                        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Department");
                        dbRef.child(dn.getValue().toString()).addValueEventListener(new ValueEventListener() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    Department department = snapshot.getValue(Department.class);
                                    tvDepartment.setText(department.getName());

                                }
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
}