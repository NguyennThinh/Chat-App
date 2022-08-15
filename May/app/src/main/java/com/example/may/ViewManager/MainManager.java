package com.example.may.ViewManager;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager2.widget.ViewPager2;

import com.example.may.AdapterEmployee.MainEmployeeViewAdapter;
import com.example.may.AdapterManager.MainManagerViewAdapter;
import com.example.may.Model.ChatList;
import com.example.may.Model.GroupChat;
import com.example.may.Model.User;
import com.example.may.Model.UserChat;
import com.example.may.R;
import com.example.may.View.ChangePasswordActivity;
import com.example.may.View.ChatCompanyActivity;
import com.example.may.View.DepartmentDetailActivity;
import com.example.may.View.LoginActivity;
import com.example.may.View.PersonalActivity;
import com.example.may.ViewEmployee.EmployeeAttendance;
import com.example.may.ViewEmployee.ListMyWork;
import com.example.may.ViewEmployee.MainEmployee;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class MainManager extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    //Views
    private ViewPager2 viewEmployee;
    private BottomNavigationView menuEmployee;
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private NavigationView viewMenuLeft;

    //Menu view left
    private CircleImageView imgUser;
    private TextView userName, userEmail;
    private ImageView imgBackground;
    private View hView ;
    private ImageView text;
    //Adapter
    MainManagerViewAdapter adapter;

    View badge;
    BottomNavigationItemView itemView;
    List<UserChat> arrMessage = new ArrayList<>();
    List<GroupChat> arrGMessage = new ArrayList<>();
    //Firebase
    private FirebaseUser mUser;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_manager);
        initUI();

        loadUser();
        adapter = new MainManagerViewAdapter(this);
        viewEmployee.setAdapter(adapter);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setSubtitleTextColor(Color.WHITE);
        menuEmployee.setOnItemSelectedListener(item -> {

            int id = item.getItemId();

            if (id == R.id.message){
                viewEmployee.setCurrentItem(0);
                toolbar.setTitle("TIN NHẮN");


            }else if (id == R.id.employee){
                viewEmployee.setCurrentItem(1);
                toolbar.setTitle("NHÂN VIÊN");


            }else if (id == R.id.group){
                viewEmployee.setCurrentItem(2);
                toolbar.setTitle("NHÓM");

            }else if (id == R.id.department){


                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
                databaseReference.child(mUser.getUid()).child("department").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            Intent intent = new Intent(getApplicationContext(), DepartmentDetailActivity.class);
                            intent.putExtra("id_department", snapshot.getValue().toString());
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
            return true;
        });

        viewEmployee.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);

                switch (position){
                    case 0:
                        menuEmployee.getMenu().findItem(R.id.message).setChecked(true);
                        toolbar.setTitle("TIN NHẮN");

                        break;

                    case 1:
                        menuEmployee.getMenu().findItem(R.id.employee).setChecked(true);
                        toolbar.setTitle("NHÂN VIÊN");

                        break;

                    case 2:
                        menuEmployee.getMenu().findItem(R.id.group).setChecked(true);
                        toolbar.setTitle("NHÓM");

                        break;
                }
            }
        });
    }


    private void initUI() {
        viewEmployee = findViewById(R.id.main_employee);
        menuEmployee = findViewById(R.id.menu_employee);
        drawerLayout = findViewById(R.id.drawerLayout);
        toolbar = findViewById(R.id.toolbar);
        viewMenuLeft = findViewById(R.id.menu_left);


        setSupportActionBar(toolbar);
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        mAuth = FirebaseAuth.getInstance();

        //View header navigation
        hView = viewMenuLeft.getHeaderView(0);

        imgUser = hView.findViewById(R.id.user_avatar);
        userName = hView.findViewById(R.id.user_name);
        userEmail = hView.findViewById(R.id.email);
        imgBackground = hView.findViewById(R.id.user_background);

        toolbar.setTitle("TIN NHẮN");

        viewMenuLeft.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(MainManager.this,
                drawerLayout,
                toolbar,
                R.string.open_drawer,
                R.string.close_drawer);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();



        BottomNavigationMenuView menuView = (BottomNavigationMenuView) menuEmployee.getChildAt(0);
        itemView = (BottomNavigationItemView) menuView.getChildAt(0);

        badge = LayoutInflater.from(MainManager.this)
                .inflate(R.layout.badge_text, menuView, false);


        text = badge.findViewById(R.id.badge);
    }
    private void loadUser() {

        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Users");
        dbRef.child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
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
                    userName.setText(user.getFullName());
                    userEmail.setText(user.getEmail());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.logout){
            if (mUser != null){
                DialogLogout();
            }
        }else if (id ==R.id.personal){
            startActivity(new Intent(this, PersonalActivity.class));
        }else if (id ==R.id.change_pass){
            startActivity(new Intent(this, ChangePasswordActivity.class));
        }else if (id ==R.id.department_manager){
            startActivity(new Intent(this, ListDepartment.class));
        }else if (id == R.id.work_manager){
            startActivity(new Intent(this, WorkManager.class));
        }else if (id == R.id.list_work){
            startActivity(new Intent(this, ListMyWork.class));
        }else if (id == R.id.employee_manager){
            startActivity(new Intent(this, EmployeeManager.class));
        }else if (id == R.id.group_manager){
            startActivity(new Intent(this, GroupActivity.class));
        }else if (id == R.id.attendance){
            startActivity(new Intent(this, EmployeeAttendance.class));
        }else if (id == R.id.attendance_manager){
            startActivity(new Intent(this, ManagerAttendance.class));
        }else if (id == R.id.message_company){
            startActivity(new Intent(this, ChatCompanyActivity.class));
        }else if (id == R.id.log){
            startActivity(new Intent(this, ManagerLog.class));
        }
        return false;
    }

    private void DialogLogout() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Bạn muốn đăng xuất?");

        builder.setPositiveButton("Xác nhận", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mAuth.signOut();

                startActivity(new Intent(MainManager.this, LoginActivity.class));
                finish();

                dialogInterface.dismiss();

            }
        });
        //button cancel
        builder.setNegativeButton("Hủy bỏ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //disable dialog
                dialogInterface.dismiss();
            }
        });
        builder.create().show();
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

    }

    private void getNotification(){

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Message");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrMessage.clear();
                for (DataSnapshot dn: snapshot.getChildren()){
                    UserChat message = dn.getValue(UserChat.class);
                    if (message.getIdReceiver().equals(mUser.getUid()) && !message.isSeen()){
                        arrMessage.add(message);
                    }
                }

                if (arrMessage.size()>0 ||arrGMessage.size() >0){
                    text.setVisibility(View.VISIBLE);

                }else {
                    text.setVisibility(View.GONE);

                }
                if(badge.getParent() != null) {
                    ((ViewGroup)badge.getParent()).removeView(badge); // <- fix
                }

                itemView.addView(badge);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getNotificationGroup( ) {


        DatabaseReference databaseReferenceq = FirebaseDatabase.getInstance().getReference("ChatList").child(mUser.getUid());
        //get list id user đã chat
        Query query = databaseReferenceq.orderByChild("timeSend");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dn : snapshot.getChildren()) {
                    ChatList list = dn.getValue(ChatList.class);
                    if (list.getType() ==2){
                        DatabaseReference  dbRef = FirebaseDatabase.getInstance().getReference("Groups");
                        dbRef.child(list.getId()).child("Message").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                arrGMessage.clear();
                                for (DataSnapshot dn : snapshot.getChildren()){
                                    GroupChat chat = dn.getValue(GroupChat.class);
                                    if (!chat.isSeen()){
                                        arrGMessage.add(chat);
                                    }
                                }

                                if (arrMessage.size()>0 ||arrGMessage.size() >0){
                                    text.setVisibility(View.VISIBLE);

                                }else {
                                    text.setVisibility(View.GONE);

                                }
                                if(badge.getParent() != null) {
                                    ((ViewGroup)badge.getParent()).removeView(badge); // <- fix
                                }

                                itemView.addView(badge);
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