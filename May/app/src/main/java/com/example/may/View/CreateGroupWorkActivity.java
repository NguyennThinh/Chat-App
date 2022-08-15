package com.example.may.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListPopupWindow;
import android.widget.Toast;

import com.example.may.Adapter.LayoutUserChoosenAdapter;
import com.example.may.Adapter.LayoutUserSelectAdapter;
import com.example.may.Adapter.ListPopupDepartmentAdapter;
import com.example.may.Adapter.ListPopupWorkAdapter;
import com.example.may.Interface.iAddMemberGroup;
import com.example.may.Model.Department;
import com.example.may.Model.Participant;
import com.example.may.Model.User;
import com.example.may.Model.Work;
import com.example.may.R;
import com.example.may.Utilities.RandomString;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import de.hdodenhof.circleimageview.CircleImageView;

public class CreateGroupWorkActivity extends AppCompatActivity implements iAddMemberGroup {
    //Views
    private RecyclerView list_user_select, list_user_choosen;
    private LinearLayout layout_participant,layout_name;

    private AppCompatEditText inputGroupName;
    private AppCompatTextView filterDepartment;
    private ImageView imgCreateGroup, imgBack, imgDrop;
    private CircleImageView imgCamera;
    private LinearLayout layoutProgressbar;

    private List<User> arrUsers, arrUserChosen;
    private List<Participant> arrParticipant;
    private Uri imageUri;




    private List<Work> arrWork;

    //Adapter
    private LayoutUserSelectAdapter adapterUserSelect;
    private LayoutUserChoosenAdapter adapterUserChoosen;

    private  boolean isUp, isUpD;

    //Firebae
    private FirebaseUser mUser;
    private StorageReference  storageReference;
    //Request code
    private static final int REQUEST_PICK_IMAGE_CODE = 100;
    private static final int REQUEST_CAMERA_PICTURE_CODE = 101;
    private static final int REQUEST_PERMISSION_CODE_PICK_GALLERY = 102;
    private static final int REQUEST_PERMISSION_CODE_CAMERA = 103;

    private ListPopupWindow listPopupWork;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group_work);

        initUI();

        loadWork();
       // loadEmployee();

        imgBack.setOnClickListener(v -> {
            onBackPressed();
        });

        imgCamera.setOnClickListener(view -> {
            ChooseOptionEditImage();
        });

        imgCreateGroup.setOnClickListener(view -> {
            String nameGroup = inputGroupName.getText().toString();
            if (imageUri ==  null){
                Toast.makeText(getApplicationContext(), "Bạn chưa chọn ảnh đại diện cho nhóm", Toast.LENGTH_SHORT).show();
            }else if (nameGroup.isEmpty()){
                Toast.makeText(getApplicationContext(), "Bạn đặt tên nhóm", Toast.LENGTH_SHORT).show();

            }else {
                createGroup(imageUri, nameGroup);
            }
        });

        layout_name.setOnClickListener(v -> {
            showAnimationName();

        });

        setPopupListWork();

    }




    private void initUI() {
        layout_name = findViewById(R.id.layout_name);

        list_user_select = findViewById(R.id.display_user);
        list_user_choosen = findViewById(R.id.lisst_user_choosen);
        layout_participant = findViewById(R.id.layout_bottom);
        imgCamera = findViewById(R.id.img_group_avatar);
        imgDrop = findViewById(R.id.img_drop);

        imgCreateGroup = findViewById(R.id.create_group);
        imgBack = findViewById(R.id.img_back);
        layoutProgressbar = findViewById(R.id.layout_progressbar);
        inputGroupName = findViewById(R.id.input_group_name);
        filterDepartment = findViewById(R.id.filter_department);

        mUser = FirebaseAuth.getInstance().getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference();

        arrUsers = new ArrayList<>();
        arrUserChosen = new ArrayList<>();
        arrParticipant = new ArrayList<>();

        arrWork = new ArrayList<>();

        adapterUserSelect = new LayoutUserSelectAdapter(arrUsers, CreateGroupWorkActivity.this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL);

        list_user_select.setAdapter(adapterUserSelect);
        list_user_select.setLayoutManager(layoutManager);
        list_user_select.addItemDecoration(itemDecoration);



    }


    private void loadWork() {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Work");
            dbRef.child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    arrWork.clear();
                    if (snapshot.exists()){
                        for (DataSnapshot dn : snapshot.getChildren()){
                            Work work = dn.getValue(Work.class);
                            if (work.getWorkStatus() == 1){
                                arrWork.add(work);
                                System.out.println(arrWork.size());
                            }
                        }
                    //    spWorkAdapter.notifyDataSetChanged();
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
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION_CODE_PICK_GALLERY);

        } else {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, REQUEST_PICK_IMAGE_CODE);
        }
    }
    private void PermissionCameraPicture() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION_CODE_CAMERA);
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
                     imageUri = data.getData();
                    imgCamera.setImageURI(imageUri);
                }
            } else if (requestCode == REQUEST_CAMERA_PICTURE_CODE) {
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                 imageUri = convertURI(getApplicationContext(), bitmap);
                imgCamera.setImageURI(imageUri);
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



    private void createGroup(Uri imageUri, String nameGroup) {
        layoutProgressbar.setVisibility(View.VISIBLE);
        RandomString gen = new RandomString(15, ThreadLocalRandom.current());
        String id = gen.nextString();

        StorageReference imgUrl = storageReference.child("image/"+id + "_" + mUser.getUid());
        imgUrl.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> task = taskSnapshot.getStorage().getDownloadUrl();

                while (!task.isSuccessful()) ;
                Uri downloadURI = task.getResult();

                if (task.isSuccessful()){
                    Long timeCreate = System.currentTimeMillis();

                    arrParticipant.add(new Participant(mUser.getUid(), 1, timeCreate));
                    for (User users: arrUserChosen){
                        arrParticipant.add(new Participant(users.getId(),2, timeCreate));

                    }


                    HashMap<String, Object> map = new HashMap<>();
                    map.put("groupID", id);
                    map.put("groupName", inputGroupName.getText().toString());
                    map.put("userCreate", mUser.getUid());
                    map.put("description", "");
                    map.put("groupImage", downloadURI.toString());
                    map.put("createDate", timeCreate);
                    map.put("slParticipant", arrParticipant.size());
                    map.put("type", 1);


                   DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Groups");
                    dbRef.child(id).setValue(map);

                    //Logs
                    HashMap<String, Object> log = new HashMap<>();
                    log.put("idUser", mUser.getUid());
                    log.put("log", "tạo nhóm công việc "+ inputGroupName.getText().toString());
                    log.put("time", timeCreate);
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Logs");
                    databaseReference.push().setValue(log);

                    for (Participant p : arrParticipant){
                        dbRef.child(id).child("participant").child(p.getId()).setValue(p).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                layoutProgressbar.setVisibility(View.GONE);
                                Toast.makeText(getApplicationContext(), "Tạo nhóm thành công", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        });
                    }



                }
            }
        });
    }

    private void setPopupListWork() {

        ListPopupWorkAdapter adapterWork = new ListPopupWorkAdapter(this, R.layout.item_spinner_selected, arrWork);

        listPopupWork = new ListPopupWindow(this);
        listPopupWork.setAdapter(adapterWork);
        listPopupWork.setAnchorView(inputGroupName);
        listPopupWork.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showAnimationName();
                inputGroupName.setText(arrWork.get(position).getWorkName());

                DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Work");
                dbRef.child(mUser.getUid()).child(arrWork.get(position).getId()).child("participant").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            arrUsers.clear();
                            for (DataSnapshot dn : snapshot.getChildren()){
                                Participant participant = dn.getValue(Participant.class);
                                if (participant!= null){
                                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
                                    databaseReference.child(participant.getId()).addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.exists()){

                                                User user = snapshot.getValue(User.class);
                                                arrUsers.add(user);

                                            }
                                            adapterUserSelect.notifyDataSetChanged();
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                }
                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                listPopupWork.dismiss();
            }
        });



    }



    private void showAnimationName() {
        if (isUp) {
            listPopupWork.dismiss();
            imgDrop.startAnimation( AnimationUtils.loadAnimation(this, R.anim.rorate_180_360));

            //   myButton.setText("Slide up");
        } else {
            listPopupWork.show();
            imgDrop.startAnimation( AnimationUtils.loadAnimation(this, R.anim.rorate_0_180));

            //   myButton.setText("Slide down");
        }
        isUp = !isUp;

    }

    @Override
    public void addMemberGroup(List<User> arrEmployees) {
        arrUserChosen = arrEmployees;

        if (arrUserChosen.size() >0){
            layout_participant.setVisibility(View.VISIBLE);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
            list_user_choosen.setLayoutManager(linearLayoutManager);
            adapterUserChoosen = new LayoutUserChoosenAdapter(arrUserChosen);
            list_user_choosen.setAdapter(adapterUserChoosen);

        }else {
            layout_participant.setVisibility(View.GONE);
        }
    }
}