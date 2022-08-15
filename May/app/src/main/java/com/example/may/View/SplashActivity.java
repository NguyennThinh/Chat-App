package com.example.may.View;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.example.may.Model.User;
import com.example.may.R;
import com.example.may.ViewEmployee.MainEmployee;
import com.example.may.ViewManager.MainManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class SplashActivity extends AppCompatActivity {
    FirebaseUser mUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mUser = FirebaseAuth.getInstance().getCurrentUser();

        CheckLogin();
    }

    private void CheckLogin() {

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                if(mUser != null){
                    CheckRoleUser();
                    checkAttendance();
                }else {
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    finish();
                }

            }
        }, 1000);
    }

    private void CheckRoleUser() {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Users");
        dbRef.child(mUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    User user = snapshot.getValue(User.class);
                    if (user.isManager()){
                        startActivity(new Intent(SplashActivity.this, MainManager.class));

                    }else {
                        startActivity(new Intent(SplashActivity.this, MainEmployee.class));

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