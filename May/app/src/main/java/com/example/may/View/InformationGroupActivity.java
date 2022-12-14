package com.example.may.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.may.Model.Group;
import com.example.may.Model.Participant;
import com.example.may.Model.Work;
import com.example.may.R;
import com.google.android.gms.tasks.OnCompleteListener;
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
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class InformationGroupActivity extends AppCompatActivity {
    private static final int REQUEST_PICK_IMAGE_CODE = 100;
    private static final int REQUEST_CAMERA_PICTURE_CODE =101 ;
    private static final int REQUEST_PERMISSION_CODE_CAMERA = 102;
    private static final int REQUEST_PERMISSION_CODE_PICK_GALLERY = 103;
    //Views
    private ImageView imgBack, imgEdit;
    private CircleImageView imgGroup;
    private TextView groupName, groupDescription;
    private TextView listParticipant, addParticipant,deleteGroup, leaveGroup, listWork;
    private Group group;


    private ProgressDialog progressDialog;

    private FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information_group);


        initUI();
        loadInfoGroup();

        if (group.getType() ==2){
            listWork.setVisibility(View.GONE);
        }
        imgBack.setOnClickListener(view->{
            finish();
        });
        imgEdit.setOnClickListener(view->{
            ChooseOptionEdit();
        });
        listParticipant.setOnClickListener(view->{
            Intent intent = new Intent(this, GroupParticipantActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("groups", group);
            intent.putExtras(bundle);
            startActivity(intent);
        });
        addParticipant.setOnClickListener(view->{
            if (group.getType() ==1){
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Groups");
                databaseReference.child(group.getGroupID()).child("work").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            String idWork = snapshot.getValue().toString();
                            Intent intent = new Intent(getApplicationContext(), AddParticipantGroupActivity.class);
                            intent.putExtra("type","work");
                            intent.putExtra("group_id", group.getGroupID());
                            intent.putExtra("id_work", idWork);

                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }else {

                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Groups");
                databaseReference.child(group.getGroupID()).child("department").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            String idDepartment = snapshot.getValue().toString();


                            Intent intent = new Intent(getApplicationContext(), AddParticipantGroupActivity.class);
                            intent.putExtra("type","department");
                            intent.putExtra("group_id", group.getGroupID());
                            intent.putExtra("id_department", idDepartment);
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });
        listWork.setOnClickListener(view->{

            getWorkGroup();
        });

        deleteGroup.setOnClickListener(view->{
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Groups");
            databaseReference.child(group.getGroupID()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Group groupp = snapshot.getValue(Group.class);
                    if (groupp != null){
                        if (group.getUserCreate().equals(mUser.getUid())){
                            deleteGroupp();
                        }else {
                            Toast.makeText(getApplicationContext(), "B???n kh??ng c?? quy???n x??a nh??m", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        });

        leaveGroup.setOnClickListener(view->{
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Groups");
            databaseReference.child(group.getGroupID()).child("participant").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dn: snapshot.getChildren()){
                        Participant participant = dn.getValue(Participant.class);
                        if (participant.getId().equals(mUser.getUid())){
                            if (participant.getRole() == 1){
                                Toast.makeText(getApplicationContext(), "B???n l?? nh??m tr?????ng kh??ng th??? r???i nh??m", Toast.LENGTH_SHORT).show();
                            }else {
                                DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("Groups");
                                databaseReference1.child(group.getGroupID()).child("participant").child(mUser.getUid()).removeValue();

                                DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("ChatList");
                                dbRef.child(mUser.getUid()).child(group.getGroupID()).removeValue();



                                HashMap<String, Object> log = new HashMap<>();
                                log.put("idUser", mUser.getUid());
                                log.put("log", "r???i nh??m "+ group.getGroupName());
                                log.put("time", System.currentTimeMillis());
                                DatabaseReference dbReff = FirebaseDatabase.getInstance().getReference("Logs");
                                dbReff.push().setValue(log);


                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                finish();
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        });

    }



    private void initUI() {
        imgBack = findViewById(R.id.img_back);
        imgEdit = findViewById(R.id.img_edit_info_group);
        imgGroup = findViewById(R.id.img_group_avatar);
        groupName = findViewById(R.id.group_name);
        groupDescription = findViewById(R.id.group_description);
        listParticipant = findViewById(R.id.list_participant);
        addParticipant = findViewById(R.id.add_participant);

        deleteGroup = findViewById(R.id.delete_group);
        leaveGroup = findViewById(R.id.leave_group);
        listWork = findViewById(R.id.list_work);
        group = (Group) getIntent().getExtras().get("my_group");

        progressDialog = new ProgressDialog(this);
    }

    private void loadInfoGroup() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Groups");
        databaseReference.child(group.getGroupID()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    group = snapshot.getValue(Group.class);
                    if (group != null){
                        Picasso.get().load(group.getGroupImage()).into(imgGroup);
                        groupName.setText(group.getGroupName());
                        if (group.getDescription().equals("default")){
                            groupDescription.setVisibility(View.GONE);
                        }else {
                            groupDescription.setText(group.getDescription());
                        }

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void ChooseOptionEdit() {
        String[] option = {"C???p nh???t ???nh nh??m", "C???p nh???t t??n nh??m", "C???p nh???t gi???i thi???u nh??m"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(option, (dialogInterface, i) -> {
            switch (i){
                case 0:
                    ChooseOptionEditImage();

                    break;
                case 1:
                    EditNameDialog();
                    break;
                case 2:
                    EditDescriptionDialog();
                    break;
            }
        });

        builder.create().show();
    }
    private void EditNameDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("C???p nh???t t??n nh??m");
        //setLayout Dialog
        LinearLayout layout = new LinearLayout(this);

        //set views in dialog
        EditText edtKey = new EditText(this);
        edtKey.setMinEms(15);

        layout.addView(edtKey);
        layout.setPadding(10,10,10,0);

        builder.setView(layout);
        builder.setPositiveButton("X??c nh???n", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String value = edtKey.getText().toString().trim();
                HashMap<String, Object> map = new HashMap<>();
                map.put("groupName", value);
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Groups");
                databaseReference.child(group.getGroupID()).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getApplicationContext(), "C??p nh???t th??nh c??ng", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        //button cancel
        builder.setNegativeButton("H???y b???", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //disable dialog
                dialogInterface.dismiss();
            }
        });
        builder.create().show();
    }
    private void EditDescriptionDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("C???p nh???t gi???i thi???u");
        //setLayout Dialog
        LinearLayout layout = new LinearLayout(this);

        //set views in dialog
        EditText edtKey = new EditText(this);
        edtKey.setMinEms(15);

        layout.addView(edtKey);
        layout.setPadding(10,10,10,0);

        builder.setView(layout);
        builder.setPositiveButton("X??c nh???n", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String value = edtKey.getText().toString().trim();
                HashMap<String, Object> map = new HashMap<>();
                map.put("description", value);
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Groups");
                databaseReference.child(group.getGroupID()).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getApplicationContext(), "C??p nh???t th??nh c??ng", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        //button cancel
        builder.setNegativeButton("H???y b???", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //disable dialog
                dialogInterface.dismiss();
            }
        });
        builder.create().show();
    }

    private void ChooseOptionEditImage() {
        String[] option = {"Ch???n t??? th?? vi???n", "Camera"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(option, (dialogInterface, i) -> {
            switch (i){
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

    private void PermissionPickGallery(){
        if (ActivityCompat.checkSelfPermission(InformationGroupActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(InformationGroupActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION_CODE_PICK_GALLERY);

        }else {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, REQUEST_PICK_IMAGE_CODE);
        }
    }
    private void PermissionCameraPicture(){
        if (ActivityCompat.checkSelfPermission(InformationGroupActivity.this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(InformationGroupActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(InformationGroupActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION_CODE_CAMERA);
        }else {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, REQUEST_CAMERA_PICTURE_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){
            if (requestCode == REQUEST_PICK_IMAGE_CODE){
                if(data != null){
                    Uri imageUri = data.getData();
                    sendImageMessage(imageUri);
                }
            }else if (requestCode == REQUEST_CAMERA_PICTURE_CODE){
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
        progressDialog.setMessage("??ang c???p nh???t..");
        progressDialog.show();
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        String idVideo = UUID.randomUUID().toString();


        StorageReference fileSaveURL =storageReference.child("image/"+uri.getLastPathSegment()+"_"+idVideo);
        fileSaveURL.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> task = taskSnapshot.getStorage().getDownloadUrl();
                while (!task.isSuccessful()) ;

                Uri downloadURI = task.getResult();

                HashMap<String , Object> map = new HashMap<>();
                map.put("groupImage", downloadURI.toString());
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Groups");
                databaseReference.child(group.getGroupID()).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        imgGroup.setImageURI(uri);
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "C???p nh???t th??nh c??ng", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

    }

    private void deleteGroupp() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("X??a nh??m");
        builder.setMessage("B???n mu???n gi???i t??n nh??m");

        builder.setPositiveButton("?????ng ??", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("ChatList");
                dbRef.child(mUser.getUid()).child(group.getGroupID()).removeValue();
                DatabaseReference ref =FirebaseDatabase.getInstance().getReference("Groups");
                ref.child(group.getGroupID()).child("participant").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dn: snapshot.getChildren()){
                            Participant participant = dn.getValue(Participant.class);
                            if (!participant.getId().equals(mUser.getUid())){
                                dbRef.child(participant.getId()).child(group.getGroupID()).removeValue();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });



                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Groups");
                reference.child(group.getGroupID()).removeValue();



                //Logs
                HashMap<String, Object> log = new HashMap<>();
                log.put("idUser", mUser.getUid());
                if (group.getType() ==1){
                    log.put("log", "x??a nh??m c??ng vi???c "+ group.getGroupName());
                }else {
                        log.put("log", "x??a nh??m "+ group.getGroupName());
                }
                log.put("time", System.currentTimeMillis());
                DatabaseReference dbReff = FirebaseDatabase.getInstance().getReference("Logs");
                dbReff.push().setValue(log);



                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }
        });
        builder.setNegativeButton("H???y b???", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }


    private void getWorkGroup() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Groups");
        databaseReference.child(group.getGroupID()).child("work").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    DatabaseReference  reference = FirebaseDatabase.getInstance().getReference("Work");
                    reference.child(group.getUserCreate()).child(snapshot.getValue().toString()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()){
                                Work work = snapshot.getValue(Work.class);

                                Intent intent = new Intent(InformationGroupActivity.this, WorkGroupDetailActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("work", work);
                                intent.putExtra("idGroup", group.getGroupID());
                                intent.putExtra("idAdmin", group.getUserCreate());


                                intent.putExtras(bundle);
                                startActivity(intent);
                            }
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
    }
}