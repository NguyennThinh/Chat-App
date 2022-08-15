package com.example.may.View;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.may.Model.User;
import com.example.may.R;
import com.example.may.ViewEmployee.MainEmployee;
import com.example.may.ViewManager.MainManager;
import com.google.android.material.textfield.TextInputEditText;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {
    //Views
    private TextInputEditText inputEmail, inputPass;
    private Button btnLogin;
    private ProgressBar progressBarLogin;

    //Declare Firebase
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initUI();

        TextView views = findViewById(R.id.textView2);



        views.setOnClickListener(view->{
            startActivity(new Intent(this, RegisterActivity.class));
        });

        btnLogin.setOnClickListener(view -> {
            userLogin();
        });
    }

    private void initUI() {
        inputEmail = findViewById(R.id.inputEmail);
        inputPass = findViewById(R.id.inputPass);

        btnLogin = findViewById(R.id.btnLogin);
        progressBarLogin = findViewById(R.id.progressBar);

        mAuth = FirebaseAuth.getInstance();


    }
    private void userLogin() {
        String email = inputEmail.getText().toString().trim();
        String pass = inputPass.getText().toString().trim();

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            inputEmail.setError("Email không đúng định dạng");
            inputEmail.setFocusable(true);
        } else if (pass.length() < 6) {
            inputPass.setError("Mật khẩu phải lớn hơn 6 ký tự");
            inputPass.setFocusable(true);
        } else {
            btnLogin.setVisibility(View.INVISIBLE);
            progressBarLogin.setVisibility(View.VISIBLE);
            mAuth.signInWithEmailAndPassword(email, pass)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            mUser = mAuth.getCurrentUser();
                            FirebaseMessaging.getInstance().getToken().addOnCompleteListener(tasks -> {
                                if (tasks.isSuccessful()) {

                                    DatabaseReference databaseReference  = FirebaseDatabase.getInstance().getReference("Users");
                                    databaseReference.child(mUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                                            User users = snapshot.getValue(User.class);


                                            String token = tasks.getResult();
                                            users.setToken(token);

                                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
                                            databaseReference.child(mUser.getUid()).child("token").setValue(token);

                                            checkAttendance();
                                            checkRole();

                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });

                                }

                            });
                        }else {
                            btnLogin.setVisibility(View.VISIBLE);
                            progressBarLogin.setVisibility(View.INVISIBLE);
                            Toast.makeText(getApplicationContext(), "Đăng nhập không thành công", Toast.LENGTH_SHORT).show();
                        }
                    });
        }

    }

    private void checkRole() {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Users");
        dbRef.child(mUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    User user = snapshot.getValue(User.class);
                    if (user.isManager()){
                        startActivity(new Intent(LoginActivity.this, MainManager.class));

                    }else {
                        startActivity(new Intent(LoginActivity.this, MainEmployee.class));

                    }


                }
                finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void checkAttendance() {
        Date date = new Date();
        SimpleDateFormat year = new SimpleDateFormat("yyyy");
        SimpleDateFormat month = new SimpleDateFormat("MM");
        SimpleDateFormat day = new SimpleDateFormat("dd");

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Attendance");
        databaseReference.child(year.format(date)).child(month.format(date)).child(day.format(date)).child(mUser.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()){
                            HashMap<String, Object> map = new HashMap<>();
                            map.put("id_user",mUser.getUid());
                            map.put("status", 1);
                            map.put("time", System.currentTimeMillis());
                            snapshot.getRef()
                                    .setValue(map);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}