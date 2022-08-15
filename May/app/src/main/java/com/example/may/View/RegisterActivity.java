package com.example.may.View;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.may.Model.User;
import com.example.may.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "REGISTER";
    //Views
    private TextInputEditText inputEmail, inputPass, inputRepass;
    private Button btnRegister;
    private ProgressBar progressBarRegister;

    //Declare Firebase
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initUI();

        btnRegister.setOnClickListener(view -> {
            Register();
        });
    }

    private void initUI() {
        inputEmail = findViewById(R.id.inputEmail);
        inputPass = findViewById(R.id.inputPass);
        inputRepass = findViewById(R.id.inputRe_Pass);
        btnRegister = findViewById(R.id.btnRegister);
        progressBarRegister = findViewById(R.id.progressBar);

        mAuth = FirebaseAuth.getInstance();


    }
    private void Register(){
        progressBarRegister.setVisibility(View.VISIBLE);
        btnRegister.setVisibility(View.INVISIBLE);
        String email = inputEmail.getText().toString().trim();
        String pass = inputPass.getText().toString().trim();
        String re_pass = inputRepass.getText().toString().trim();

        mAuth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            mUser = mAuth.getCurrentUser();
                            User users = new User(mUser.getUid(),"Default",email,"default","default"
                                    ,false,"","default","","default", false, "", System.currentTimeMillis());
                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
                            HashMap<String, Object> map = new HashMap<>();
                            map.put("password", pass);
                            databaseReference.child(mUser.getUid()).setValue(users);
                            databaseReference.child(mUser.getUid()).updateChildren(map);
                            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                        } else {

                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            progressBarRegister.setVisibility(View.INVISIBLE);
                            btnRegister.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }

}