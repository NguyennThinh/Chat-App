package com.example.may.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.example.may.Adapter.ItemChatAdapter;
import com.example.may.Adapter.LayoutMessagePickGallery;
import com.example.may.Adapter.ParentUserMessageAdapter;
import com.example.may.Adapter.UserChatApdapter;
import com.example.may.FcmNotification.ApiClient;
import com.example.may.Interface.iClickListener;
import com.example.may.Model.ItemChat;
import com.example.may.Model.User;
import com.example.may.Model.UserChat;
import com.example.may.Model.UserChatParent;
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
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserChatActivity extends AppCompatActivity {
    //Views
    private ImageView imgMessageType, imgSend, imgBack, imgCamera;
    private RecyclerView listItem, listMessage, layoutDisplayMessageSelect;
    private CircleImageView imgUser;
    private TextView userName, userStatus;
    private EditText inputMessage;
    private RelativeLayout layoutProgress;
    private ProgressBar loading;
    private TextView loadingView;
    private LinearLayout layout_item;

    private String idUserReceiver;
    private User user, userSender;
    private List<ItemChat> arrItemChats;
    private List<UserChat> arrMessage;
    private UserChat message;
    private String selectMessage;
    private String messageType;
    private  boolean isUp;
    //List message type pick gallery
    private List<Uri> arrImageUri;
    private List<Uri> arrVideoUri;
    private List<Uri> arrFileUri;


    //Adapter
    private ItemChatAdapter itemAdapter;
    private LayoutMessagePickGallery pickGallery;

    //  private UserChatAdapter adapter;
    private ParentUserMessageAdapter adapter;
    //Firebase
    private FirebaseUser mUser;
    private DatabaseReference seenRef;
    private ValueEventListener seenListener;

    //Declare request code pick image gallery
    private static final int REQUEST_PICK_IMAGE_CODE = 100;
    private static final int REQUEST_PERMISSION_CODE_CAMERA = 101;
    private static final int REQUEST_PERMISSION_CODE_PICK_GALLERY = 102;
    private static final int REQUEST_CAMERA_PICTURE_CODE = 103;
    private static final int REQUEST_PICK_PDF_CODE = 104;
    private static final int REQUEST_PICK_VIDEO_CODE = 105;

    private long timeSend = System.currentTimeMillis();
    private SimpleDateFormat dateSend = new SimpleDateFormat("dd/MM/yyyy");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_chat);

        initUI();

        //Load info user chat
        /************/
        loadInFoUserChat();
        loadMyInfo();
        /************/
        displayMessage();

        seenMessage(idUserReceiver);

        imgBack.setOnClickListener(v -> {
            onBackPressed();
        });

        imgCamera.setOnClickListener(v -> {
            PermissionCameraPicture();
        });

        imgSend.setOnClickListener(view -> {

            String msg = inputMessage.getText().toString();


            if (!msg.equals("")) {
                String idMessage= new RandomString(12, ThreadLocalRandom.current()).nextString();
                messageType = "text";
                UserChat message = new UserChat(idMessage,mUser.getUid(), user.getId(), msg, dateSend.format(System.currentTimeMillis()), timeSend, "text", "default", "default", false);
                sendMessage(message);
                listMessage.scrollToPosition(arrMessage.size() - 1);
                pushNotification(message);

            }else {
                if (arrImageUri.size() >0){
                    messageType = "image";
                    sendImageMessage();
                }else if (arrVideoUri.size()>0){
                    messageType = "video";
                    sendVideoMessage();
                }else if (arrFileUri.size()>0){
                    messageType = "pdf";
                    sendFileMessage();
                }
            }
            inputMessage.setText("");

        });
        imgMessageType.setOnClickListener(v -> {

            if (isUp) {
                slideDown(layout_item);
             //   myButton.setText("Slide up");
            } else {
                slideUp(layout_item);
             //   myButton.setText("Slide down");
            }
            isUp = !isUp;
        });

    }

    private void loadMyInfo() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    userSender = snapshot.getValue(User.class);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void initUI() {

        imgBack = findViewById(R.id.img_back);
        imgCamera = findViewById(R.id.select_camera);
        imgMessageType = findViewById(R.id.select_message_type);
        imgSend = findViewById(R.id.send_message);
        listItem = findViewById(R.id.list_item);
        imgUser = findViewById(R.id.img_user_receiver);
        userName = findViewById(R.id.tv_name_user_receiver);
        userStatus = findViewById(R.id.tv_user_receiver_status);
        listMessage = findViewById(R.id.layout_display_message);
        inputMessage = findViewById(R.id.input_message);
        layoutDisplayMessageSelect = findViewById(R.id.layout_message_select);
        layoutProgress = findViewById(R.id.layout_progress);
        loading = findViewById(R.id.loading);
        loadingView = findViewById(R.id.loading_view);
        layout_item = findViewById(R.id.layout_item);



        arrItemChats = new ArrayList<>();
        arrMessage = new ArrayList<>();

        arrImageUri = new ArrayList<>();
        arrVideoUri = new ArrayList<>();
        arrFileUri = new ArrayList<>();


        mUser = FirebaseAuth.getInstance().getCurrentUser();

        idUserReceiver = getIntent().getStringExtra("id_user");


        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        listMessage.setLayoutManager(layoutManager);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.HORIZONTAL, false);
        layoutDisplayMessageSelect.setLayoutManager(linearLayoutManager);


        /********************************************************************/
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        listItem.setLayoutManager(gridLayoutManager);

        arrItemChats.add(new ItemChat(1, R.drawable.ic_image, "Chọn ảnh"));
        arrItemChats.add(new ItemChat(2, R.drawable.ic_video, "Chọn video"));
        arrItemChats.add(new ItemChat(3, R.drawable.ic_file, "Chọn file"));

        itemAdapter = new ItemChatAdapter(arrItemChats, itemChat -> {
            if (itemChat.getId() == 1) {
                selectMessage = "image";
                PermissionPickGallery();
            } else if (itemChat.getId() == 2) {
                selectMessage = "video";
                PermissionPickGallery();
            } else {
                selectMessage = "file";
                PermissionPickGallery();
            }
        });
        listItem.setAdapter(itemAdapter);


    }


    public void slideUp(View view){

        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                view.getHeight(),  // fromYDelta
                0);                // toYDelta
        animate.setDuration(500);

        animate.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                view.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        view.startAnimation(animate);
    }

    // slide the view from its current position to below itself
    public void slideDown(View view){
        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                0,                 // fromYDelta
                view.getHeight()); // toYDelta
        animate.setDuration(500);

        animate.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                view.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.startAnimation(animate);
    }







    private void loadInFoUserChat() {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Users");
        dbRef.child(idUserReceiver).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    user = snapshot.getValue(User.class);


                    if (user.getAvatar().equals("default")) {
                        imgUser.setImageResource(R.drawable.ic_logo);

                    } else {
                        Picasso.get().load(user.getAvatar()).into(imgUser);
                    }
                    userName.setText(user.getFullName());

                    if (user.getStatus() == 1) {
                        userStatus.setText("Đang hoạt động");

                    } else {
                        userStatus.setText("Không hoạt dộng");

                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void displayMessage() {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Message");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //Initialize list message
                arrMessage = new ArrayList<>();

                for (DataSnapshot dn : snapshot.getChildren()) {
                    message = dn.getValue(UserChat.class);

                    if (dn.child("idSender").getValue().equals(mUser.getUid()) && dn.child("idReceiver").getValue().equals(user.getId())
                            || dn.child("idSender").getValue().equals(user.getId()) && dn.child("idReceiver").getValue().equals(mUser.getUid())) {
                        arrMessage.add(message);

                    }

                }
                groupDataIntoHashMap(arrMessage);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), error.getMessage() + "", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void seenMessage(String id) {
        seenRef = FirebaseDatabase.getInstance().getReference("Message");
        seenListener = seenRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dn : snapshot.getChildren()) {
                    UserChat message = dn.getValue(UserChat.class);
                    if (message.getIdReceiver().equals(mUser.getUid()) && message.getIdSender().equals(id)) {
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("seen", true);
                        dn.getRef().updateChildren(map);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void sendMessage(UserChat message) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Message");
        databaseReference.push().setValue(message);

        HashMap<String, Object> sender = new HashMap<>();
        sender.put("id", mUser.getUid());
        sender.put("type", 1);
        sender.put("timeSend", System.currentTimeMillis());

        HashMap<String, Object> receiver = new HashMap<>();
        receiver.put("id", user.getId());
        receiver.put("type", 1);
        receiver.put("timeSend", System.currentTimeMillis());

        DatabaseReference dbRefSend = FirebaseDatabase.getInstance().getReference("ChatList");
        dbRefSend.child(mUser.getUid()).child(user.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()){
                    snapshot.getRef().setValue(receiver);
                }else {
                    snapshot.getRef().updateChildren(receiver);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        DatabaseReference dbRefReceiver = FirebaseDatabase.getInstance().getReference("ChatList");
        dbRefReceiver.child(user.getId()).child(mUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()){
                    snapshot.getRef().setValue(sender);
                }else {
                    snapshot.getRef().updateChildren(sender);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void groupDataIntoHashMap(List<UserChat> chatModelList) {
        LinkedHashMap<String, List<UserChat>> groupedHashMap = new LinkedHashMap<>();
        List<UserChat> list = null;

        List<String> arrKey = new ArrayList<>();

        for (UserChat chatModel : chatModelList) {
            //Log.d(TAG, travelActivityDTO.toString());
            String hashMapKey = chatModel.getDateSend();
            //Log.d(TAG, "start date: " + DateParser.convertDateToString(travelActivityDTO.getStartDate()));
            if (groupedHashMap.containsKey(hashMapKey)) {
                // The key is already in the HashMap; add the pojo object
                // against the existing key.
                groupedHashMap.get(hashMapKey).add(chatModel);
            } else {
                // The key is not there in the HashMap; create a new key-value pair
                arrKey.add(hashMapKey);
                list = new ArrayList<>();
                list.add(chatModel);
                groupedHashMap.put(hashMapKey, list);
            }
        }

        List<UserChat> arrChat = null;
        List<UserChatParent> arrParent = new ArrayList<>();
        for (String key : arrKey) {
            arrChat = groupedHashMap.get(key);
            arrParent.add(new UserChatParent(key, arrChat));

        }
        adapter = new ParentUserMessageAdapter(arrParent);
        listMessage.setAdapter(adapter);
    }


    private void PermissionPickGallery() {
        if (ActivityCompat.checkSelfPermission(UserChatActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(UserChatActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION_CODE_PICK_GALLERY);

        } else {
            if (selectMessage.equals("image")) {
                pickImageGallery();
            } else if (selectMessage.equals("video")) {
                pickVideoGallery();
            } else if (selectMessage.equals("file")) {
                pickFile();
            }
        }
    }

    private void PermissionCameraPicture() {
        if (ActivityCompat.checkSelfPermission(UserChatActivity.this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(UserChatActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(UserChatActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION_CODE_CAMERA);
        } else {
            selectMessage = "image";
            pictureCamera();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_CODE_PICK_GALLERY) {
            if (grantResults.length > 0 || grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (selectMessage.equals("image")) {
                    pickImageGallery();
                } else if (selectMessage.equals("video")) {
                    pickVideoGallery();
                } else if (selectMessage.equals("file")) {
                    pickFile();
                }
            }
        } else if (requestCode == REQUEST_PERMISSION_CODE_CAMERA) {
            if (grantResults.length > 0 || grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pictureCamera();
            }
        }
    }

    private void pickImageGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(intent, REQUEST_PICK_IMAGE_CODE);
    }

    //Lấy video từ thư viện
    private void pickVideoGallery() {
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(intent, REQUEST_PICK_VIDEO_CODE);
    }

    //LẤy file PDF từ thư viện
    private void pickFile() {
        Intent intent = new Intent();
        intent.setType("*/*");
        String[] mimetypes = {"application/vnd.openxmlformats-officedocument.wordprocessingml.document", "application/msword, " +
                "application/vnd.ms-excel","application/vnd.openxmlformats-officedocument.spreadsheetml.sheet","application/pdf"};
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);


        startActivityForResult(intent, REQUEST_PICK_PDF_CODE);

    }

    //Lấy ảnh khi chụp
    private void pictureCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA_PICTURE_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {

            //Lấy ảnh thư viện
            if (requestCode == REQUEST_PICK_IMAGE_CODE) {
                if (data.getClipData() != null) {
                    //Lấy nhiều ảnh
                    int count = data.getClipData().getItemCount();
                    for (int i = 0; i < count; i++) {
                        Uri uriImage = data.getClipData().getItemAt(i).getUri();
                        arrImageUri.add(uriImage);
                    }
                } else {
                    //LẤy một ảnh
                    Uri imageUri = data.getData();
                    arrImageUri.add(imageUri);
                }

                //Lấy video thư viện
            } else if (requestCode == REQUEST_PICK_VIDEO_CODE) {
                if (data.getClipData() != null) {
                    //Lấy nhiều video
                    int count = data.getClipData().getItemCount();
                    for (int i = 0; i < count; i++) {
                        Uri uriVideo = data.getClipData().getItemAt(i).getUri();
                        arrVideoUri.add(uriVideo);
                    }
                } else {
                    //LẤy một video
                    Uri uriVideo = data.getData();
                    arrVideoUri.add(uriVideo);
                }

                //LẤy file từ thư viện
            } else if (requestCode == REQUEST_PICK_PDF_CODE) {
                if (data.getClipData() != null) {
                    //Lấy nhiều video
                    int count = data.getClipData().getItemCount();
                    for (int i = 0; i < count; i++) {
                        Uri uriPDF = data.getClipData().getItemAt(i).getUri();
                        arrFileUri.add(uriPDF);
                    }
                } else {
                    //LẤy một video
                    Uri uriPDF = data.getData();
                    arrFileUri.add(uriPDF);
                }

                //Lấy ảnh từ camera
            } else if (requestCode == REQUEST_CAMERA_PICTURE_CODE) {
                if (data != null) {
                    Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                    Uri uriImage = convertURI(bitmap);
                    arrImageUri.add(uriImage);

                }
            }

            if (arrImageUri.size() > 0) {
                layoutDisplayMessageSelect.setVisibility(View.VISIBLE);
                pickGallery = new LayoutMessagePickGallery(arrImageUri, selectMessage);
                layoutDisplayMessageSelect.setAdapter(pickGallery);

            } else if (arrVideoUri.size() > 0) {
                layoutDisplayMessageSelect.setVisibility(View.VISIBLE);
                pickGallery = new LayoutMessagePickGallery(arrVideoUri, selectMessage);
                layoutDisplayMessageSelect.setAdapter(pickGallery);

            } else if (arrFileUri.size() > 0) {
                layoutDisplayMessageSelect.setVisibility(View.VISIBLE);
                pickGallery = new LayoutMessagePickGallery(arrFileUri, selectMessage);
                layoutDisplayMessageSelect.setAdapter(pickGallery);
            } else {
                layoutDisplayMessageSelect.setVisibility(View.GONE);
            }
        }
    }

    private Uri convertURI( Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String time = String.valueOf(System.currentTimeMillis());
        String path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, time, null);
        return Uri.parse(path);
    }


    private void sendFileMessage() {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        String idFile = new RandomString(12, ThreadLocalRandom.current()).nextString();
        layoutProgress.setVisibility(View.VISIBLE);
        for (int i = 0; i < arrFileUri.size(); i++) {
            Uri uri = arrFileUri.get(i);
            String fileName = getFileName(uri, getApplicationContext());
            StorageReference fileSaveURL =storageReference.child("file/"+fileName+"_"+idFile);
            fileSaveURL.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> task = taskSnapshot.getStorage().getDownloadUrl();
                    while (!task.isSuccessful()) ;

                    String sizeFile = String.valueOf(taskSnapshot.getTotalByteCount());
                    Uri downloadURI = task.getResult();
                    UserChat message = new UserChat(idFile,mUser.getUid(), user.getId(), downloadURI.toString(),dateSend.format(System.currentTimeMillis()) ,timeSend, messageType, fileName,sizeFile, false);
                    sendMessage(message);
                    arrFileUri.clear();
                    layoutDisplayMessageSelect.setVisibility(View.GONE);
                    layoutProgress.setVisibility(View.GONE);
                    listMessage.scrollToPosition(arrMessage.size() - 1);
                    slideDown(listItem);
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                    double progress= 100 * snapshot.getBytesTransferred()/snapshot.getTotalByteCount();
                    loading.setProgress((int) progress);
                    loadingView.setText("Đang gửi: "+progress +"%");
                }
            });
        }
    }

    private void sendVideoMessage() {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        String idVideo = new RandomString(12, ThreadLocalRandom.current()).nextString();
        layoutProgress.setVisibility(View.VISIBLE);
        for (int i = 0; i < arrVideoUri.size(); i++) {
            Uri uri = arrVideoUri.get(i);
            String fileName = getFileName(uri, getApplicationContext());
            StorageReference fileSaveURL =storageReference.child("video/"+fileName+"_"+idVideo);
            fileSaveURL.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> task = taskSnapshot.getStorage().getDownloadUrl();
                    while (!task.isSuccessful()) ;

                    String sizeFile = String.valueOf(taskSnapshot.getTotalByteCount());
                    Uri downloadURI = task.getResult();
                    UserChat message = new UserChat(idVideo,mUser.getUid(), user.getId(), downloadURI.toString(),dateSend.format(System.currentTimeMillis()) ,timeSend, messageType, fileName,sizeFile, false);
                    sendMessage(message);
                    arrVideoUri.clear();
                    layoutDisplayMessageSelect.setVisibility(View.GONE);
                    layoutProgress.setVisibility(View.GONE);
                    listMessage.scrollToPosition(arrMessage.size() - 1);
                    slideDown(listItem);
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                    double progress= 100 * snapshot.getBytesTransferred()/snapshot.getTotalByteCount();
                    loading.setProgress((int) progress);
                    loadingView.setText("Đang gửi: "+progress +"%");
                }
            });
        }
    }

    private void sendImageMessage() {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        String idImage = new RandomString(12, ThreadLocalRandom.current()).nextString();
        layoutProgress.setVisibility(View.VISIBLE);
        for (int i = 0; i < arrImageUri.size(); i++) {
            Uri uri = arrImageUri.get(i);
            String fileName = getFileName(uri, getApplicationContext());
            StorageReference fileSaveURL =storageReference.child("image/"+fileName+"_"+idImage);
            fileSaveURL.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> task = taskSnapshot.getStorage().getDownloadUrl();
                    while (!task.isSuccessful()) ;

                    String sizeFile = String.valueOf(taskSnapshot.getTotalByteCount());
                    Uri downloadURI = task.getResult();
                    UserChat message = new UserChat(idImage,mUser.getUid(), user.getId(), downloadURI.toString(),dateSend.format(System.currentTimeMillis()) ,timeSend, messageType, fileName,sizeFile, false);
                    sendMessage(message);
                    arrImageUri.clear();
                    layoutDisplayMessageSelect.setVisibility(View.GONE);
                    layoutProgress.setVisibility(View.GONE);
                    listMessage.scrollToPosition(arrMessage.size() - 1);
                    slideDown(listItem);
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                    double progress= 100 * snapshot.getBytesTransferred()/snapshot.getTotalByteCount();
                    loading.setProgress((int) progress);
                    loadingView.setText("Đang gửi: "+progress +"%");
                }
            });
        }
    }









    @SuppressLint("Range")
    String getFileName(Uri data, Context context) {
        String res = null;
        if (data.getScheme().equals("content")) {
            Cursor cursor = context.getContentResolver().query(data, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    res = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
                ;
            }
            if (res == null) {
                res = data.getPath();
                int cut = res.lastIndexOf("/");
                if (cut != -1) {
                    res = res.substring(cut + 1);
                }
            }
        }
        return res;
    }


    private void pushNotification(UserChat message) {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Message");
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dn : snapshot.getChildren()){
                            UserChat msg = dn.getValue(UserChat.class);
                            assert msg != null;
                            if (message.getTimeSend()== msg.getTimeSend()){
                                if (!msg.isSeen()){

                                    ApiClient.pushNotificationUserChat(getApplicationContext(),user.getToken(), userSender,msg);

                                }
                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        }, 2000);
    }



    //Check user online
    private void CheckUserChatOnline(long status) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(mUser.getUid());
        HashMap<String, Object> map = new HashMap<>();
        map.put("status", status);
        databaseReference.updateChildren(map);
    }


    @Override
    protected void onResume() {
        super.onResume();
        CheckUserChatOnline(1);

    }

    @Override
    protected void onPause() {
        super.onPause();

        CheckUserChatOnline(System.currentTimeMillis());
        seenRef.removeEventListener(seenListener);
    }
}