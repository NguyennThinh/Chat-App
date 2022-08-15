package com.example.may.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.may.AdapterManager.ManagerEmployeeAdapter;
import com.example.may.Model.Department;
import com.example.may.Model.User;
import com.example.may.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class PersonalActivity extends AppCompatActivity {
    private static final int REQUEST_PICK_IMAGE_CODE = 100;
    private static final int REQUEST_CAMERA_PICTURE_CODE = 101;
    private static final int REQUEST_PERMISSION_CODE_PICK_GALLERY = 102;
    private static final int REQUEST_PERMISSION_CODE_CAMERA = 103;
    //Views
    private ImageView imgBackground, imgBack;
    private CircleImageView imgUser;
    private TextView tvName, tvBirthday, tvGender, tvPhone, tvEmail, tvPosition;
    private FloatingActionButton btnEdit;

    private RelativeLayout view_progressbar;

    //Declare Firebase
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    private User users;

    String editOption;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal);

        initUI();

        loadProfile();

        btnEdit.setOnClickListener(view -> {
            openDialogUpdate();
        });
        imgUser.setOnClickListener(v -> {
            editOption = "avatar";
            ChooseOptionEditImage();
        });
        imgBackground.setOnClickListener(v -> {
            editOption = "background";
            ChooseOptionEditImage();
        });
        imgBack.setOnClickListener(v -> {
            onBackPressed();
        });
    }


    private void initUI() {
        imgBackground = findViewById(R.id.imgBackground);
        imgUser = findViewById(R.id.imgUser);
        tvName = findViewById(R.id.tv_Name);
        tvBirthday = findViewById(R.id.tv_birthday);
        tvGender = findViewById(R.id.tv_gender);
        tvPhone = findViewById(R.id.tv_phone);
        tvEmail = findViewById(R.id.tv_email);
        btnEdit = findViewById(R.id.btnEditProfile);
        imgBack = findViewById(R.id.img_back);
        tvPosition = findViewById(R.id.tv_position);
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        view_progressbar = findViewById(R.id.view_update_bar);



    }

    private void loadProfile() {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
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
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void ChooseOptionEditImage() {
        String[] option = {"Chọn từ thư viện", "Camera"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(option, (dialogInterface, i) -> {
            switch (i) {
                case 0:
                    PermissionPickGallery();

                    break;
                case 1:
                    PermissionCameraPicture();
                    break;

            }
        });

        builder.create().show();
    }


    private void PermissionPickGallery() {
        if (ActivityCompat.checkSelfPermission(PersonalActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(PersonalActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION_CODE_PICK_GALLERY);

        } else {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, REQUEST_PICK_IMAGE_CODE);
        }
    }

    private void PermissionCameraPicture() {
        if (ActivityCompat.checkSelfPermission(PersonalActivity.this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(PersonalActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(PersonalActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION_CODE_CAMERA);
        } else {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, REQUEST_CAMERA_PICTURE_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_CODE_PICK_GALLERY) {
            if (grantResults.length > 0 || grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, REQUEST_PICK_IMAGE_CODE);
            }
        }else if (requestCode == REQUEST_PERMISSION_CODE_CAMERA) {
            if (grantResults.length > 0 || grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, REQUEST_CAMERA_PICTURE_CODE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {

            if (requestCode == REQUEST_PICK_IMAGE_CODE) {
                if (data != null) {
                    Uri imageUri = data.getData();
                    sendImageMessage(imageUri);
                }
            } else if (requestCode == REQUEST_CAMERA_PICTURE_CODE) {
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                Uri uriImage = convertURI(getApplicationContext(), bitmap);
                sendImageMessage(uriImage);
            }
        }
    }

    private Uri convertURI(Context context, Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String time = String.valueOf(System.currentTimeMillis());
        String path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, time, null);
        return Uri.parse(path);
    }


    private void sendImageMessage(Uri uri) {
        view_progressbar.setVisibility(View.VISIBLE);
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        String idVideo = UUID.randomUUID().toString();


        StorageReference fileSaveURL = storageReference.child("image/" + uri.getLastPathSegment() + "_" + idVideo);
        fileSaveURL.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> task = taskSnapshot.getStorage().getDownloadUrl();
                while (!task.isSuccessful()) ;

                Uri downloadURI = task.getResult();
                if (editOption.equals("avatar")) {

                    HashMap<String, Object> map = new HashMap<>();
                    map.put("avatar", downloadURI.toString());
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
                    databaseReference.child(mUser.getUid()).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            imgUser.setImageURI(uri);
                            view_progressbar.setVisibility(View.GONE);
                            Toast.makeText(getApplicationContext(), "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {

                    HashMap<String, Object> map = new HashMap<>();
                    map.put("backgroundPhoto", downloadURI.toString());
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
                    databaseReference.child(mUser.getUid()).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            imgBackground.setImageURI(uri);
                            view_progressbar.setVisibility(View.GONE);
                            Toast.makeText(getApplicationContext(), "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }
        });

    }


    private void openDialogUpdate() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_update_user);
        Window window = dialog.getWindow();
        if (window == null) {
            return;
        }
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams layoutParams = window.getAttributes();

        layoutParams.gravity = Gravity.CENTER;

        window.setAttributes(layoutParams);


        //View dialog
        TextInputEditText inputName, inputBirth, inputPhone;
        RadioButton radNam, radNu;
        Button btnUpdate, btnCancel;

        //Initialize views
        inputName = dialog.findViewById(R.id.inputName);
        inputBirth = dialog.findViewById(R.id.inputBirth);
        inputPhone = dialog.findViewById(R.id.inputPhone);
        radNam = dialog.findViewById(R.id.radNam);
        radNu = dialog.findViewById(R.id.radNu);
        btnUpdate = dialog.findViewById(R.id.btn_update);
        btnCancel = dialog.findViewById(R.id.btn_cancel);

        //Set data for views
        inputBirth.setText(users.getBirthday());
        inputName.setText(users.getFullName());
        inputPhone.setText(users.getPhone());
        if (users.isGender()) {
            radNam.setChecked(true);
        } else {
            radNu.setChecked(true);
        }


        btnUpdate.setOnClickListener(v -> {

            HashMap<String, Object> mapUser = new HashMap<>();
            mapUser.put("fullName", inputName.getText().toString());
            mapUser.put("birthday", inputBirth.getText().toString());
            mapUser.put("phone", inputPhone.getText().toString());
            if (radNam.isChecked()){
                mapUser.put("gender", true);
            }else {
                mapUser.put("gender", false);

            }
            updateUser(mapUser, dialog);
        });
        btnCancel.setOnClickListener(v->{
            dialog.cancel();
        });


        dialog.show();
    }

    private void updateUser(HashMap<String, Object> mapUser, Dialog dialog) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(mUser.getUid()).updateChildren(mapUser).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

                Toast.makeText(PersonalActivity.this, "Cập nhật thành công",Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }
        });
    }

}