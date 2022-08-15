package com.example.may.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.may.Model.User;
import com.example.may.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChangePasswordActivity extends AppCompatActivity {
    private ImageView  imgBackground,imgBack;
    private CircleImageView imgUser;
    private TextInputEditText inputPassword, inputNewPass, inputRePass;
    private Button btnChange;
    private FirebaseUser mUser;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        initUI();

        loadUser();

        imgBack.setOnClickListener(v -> {
            onBackPressed();
        });
        btnChange.setOnClickListener(v -> {
            changePassword();
        });
    }


    private void initUI() {
        imgBackground = findViewById(R.id.imgBackground);
        imgUser = findViewById(R.id.imgUser);
        inputPassword = findViewById(R.id.inputPassword);
        inputNewPass = findViewById(R.id.inputNewPass);
        inputRePass = findViewById(R.id.input_re_NewPass);
        btnChange = findViewById(R.id.btnChangePassord);
        imgBack = findViewById(R.id.img_back);

        progressDialog = new ProgressDialog(this);
        mUser = FirebaseAuth.getInstance().getCurrentUser();
    }


    private void loadUser() {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Users");
        dbRef.child(mUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    User user = snapshot.getValue(User.class);

                    if (user.getAvatar().equals("default")){
                        imgUser.setImageResource(R.drawable.ic_logo);
                    }else {
                        Picasso.get().load(user.getAvatar()).into(imgUser);
                    }
                    if (user.getBackgroundPhoto().equals("default")){
                        imgBackground.setImageResource(R.drawable.ic_launcher_foreground);
                    }else {
                        Picasso.get().load(user.getBackgroundPhoto()).into(imgBackground);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void changePassword() {
        String password = inputPassword.getText().toString();
        String newPasss = inputNewPass.getText().toString();
        String reNewPass = inputRePass.getText().toString();

        if (!newPasss.equals(reNewPass)){
            inputNewPass.setError("Vui lòng kiểm tra lại mật khẩu");
        }else if (password.isEmpty() ){
            inputPassword.setError("Vui lòng nhập mật khẩu");
            inputPassword.setFocusable(true);
        }else if (newPasss.isEmpty() ){
            inputNewPass.setError("Vui lòng nhập mật khẩu mới");
            inputNewPass.setFocusable(true);
        }else if (reNewPass.isEmpty() ){
            inputRePass.setError("Vui lòng nhập mật khẩu");
            inputRePass.setFocusable(true);
        }else if (password.length() <6 || newPasss.length()<6 || reNewPass.length()<6){
            Toast.makeText(getApplicationContext(), "Mật khẩu phải lớn hơn 6 ký tự", Toast.LENGTH_SHORT).show();

        }else {
            progressDialog.setMessage("Đang cập nhật");
            progressDialog.show();
            DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Users");
            dbRef.child(mUser.getUid()).child("password").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){
                    String pass = (String) snapshot.getValue();
                       if (!pass.isEmpty()){
                            if (pass.equals(password)){
                                mUser.updatePassword(newPasss)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    progressDialog.dismiss();
                                                    Toast.makeText(getApplicationContext(), "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                                                    FirebaseAuth mAuth = FirebaseAuth.getInstance();


                                                    HashMap<String, Object> map = new HashMap<>();
                                                    map.put("password", newPasss);
                                                    dbRef.child(mUser.getUid()).updateChildren(map);

                                                    mAuth.signOut();
                                                    startActivity(new Intent(ChangePasswordActivity.this, LoginActivity.class));
                                                    finish();
                                                }
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progressDialog.dismiss();
                                        Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }else {
                                inputPassword.setError("Mật khẩu không đúng");
                                inputPassword.setText("");
                                inputPassword.setFocusable(true);
                                progressDialog.dismiss();
                            }
                            Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }


    }

}