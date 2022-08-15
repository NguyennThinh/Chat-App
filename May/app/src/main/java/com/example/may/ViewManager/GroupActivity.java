package com.example.may.ViewManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListPopupWindow;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.may.Adapter.ListPopupDepartmentAdapter;
import com.example.may.AdapterManager.ManagerEmployeeAdapter;
import com.example.may.AdapterManager.ManagerGroupAdapter;
import com.example.may.AdapterManager.SpinnerDepartmentAdapter;
import com.example.may.Interface.IGroupListener;
import com.example.may.Interface.IUserCListener;
import com.example.may.Model.Department;
import com.example.may.Model.Group;
import com.example.may.Model.Participant;
import com.example.may.Model.User;
import com.example.may.R;
import com.example.may.View.CreateGroupDepartmentActivity;
import com.example.may.View.CreateGroupWorkActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.net.IDN;
import java.util.ArrayList;
import java.util.List;

public class GroupActivity extends AppCompatActivity implements IGroupListener {
    private ProgressDialog progressDialog;
    //View
    private ImageView imgBack, imgAddGroup, imgDrop;
    private RecyclerView listGroup;
    private RadioButton radWorkFilter, radDepartment;
    private LinearLayout layout_name;
    private AppCompatTextView filterDepartment;

    private List<Group> arrGroups;
    private List<Department> arrDepartment;

    //View dialog
    private RadioButton radWork;
    private Button btnCancel, btnSubmit;

    //Adapter
    private ManagerGroupAdapter adapter;

    private ListPopupWindow listPopupDepartment;
    private boolean isUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        initUI();
        loadListGroup();
        loadDepartment();

        setPopupListDepartment();
        imgBack.setOnClickListener(v -> {
            onBackPressed();
        });
        imgAddGroup.setOnClickListener(v -> {
            createGroup();
        });

        layout_name.setOnClickListener(v -> {
            showAnimationFiler();

        });

        radDepartment.setOnClickListener(v -> {
            layout_name.setVisibility(View.VISIBLE);
        });
        radWorkFilter.setOnClickListener(v -> {
            layout_name.setVisibility(View.GONE);
            filterWorkGroup();
        });



    }



    private void initUI() {
        imgBack = findViewById(R.id.img_back);
        imgAddGroup = findViewById(R.id.add_group);
        listGroup = findViewById(R.id.list_group);
        radWorkFilter = findViewById(R.id.rad_filter_work);
        radDepartment = findViewById(R.id.rad_filter_department);
        layout_name = findViewById(R.id.layout_name);
        filterDepartment = findViewById(R.id.filter_department);
        imgDrop = findViewById(R.id.img_drop);

        radWorkFilter.setChecked(false);
        radDepartment.setChecked(false);

        progressDialog = new ProgressDialog(this);
        arrGroups = new ArrayList<>();
        arrDepartment = new ArrayList<>();

        adapter = new ManagerGroupAdapter(arrGroups, this);


        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        listGroup.setLayoutManager(gridLayoutManager);
        listGroup.setAdapter(adapter);
    }


    private void showAnimationFiler() {
        if (isUp) {
            if (GroupActivity.this.isFinishing()) {
                listPopupDepartment.show();
            }
            listPopupDepartment.dismiss();
            imgDrop.startAnimation(AnimationUtils.loadAnimation(this, R.anim.rorate_180_360));

            //   myButton.setText("Slide up");
        } else {

            if (!GroupActivity.this.isFinishing()) {
                listPopupDepartment.show();
            }
            imgDrop.startAnimation(AnimationUtils.loadAnimation(this, R.anim.rorate_0_180));

            //   myButton.setText("Slide down");
        }
        isUp = !isUp;
    }

    private void loadListGroup() {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Groups");

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrGroups.clear();
                for (DataSnapshot dn : snapshot.getChildren()) {
                    Group group = dn.getValue(Group.class);
                    if (group != null) {
                        arrGroups.add(group);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void filterWorkGroup() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Groups");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrGroups.clear();
                for (DataSnapshot dn :snapshot.getChildren()){
                    Group group = dn.getValue(Group.class);
                    if (group!= null && group.getType() ==1){
                        arrGroups.add(group);
                    }
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void setPopupListDepartment() {

        ListPopupDepartmentAdapter departmentAdapter = new ListPopupDepartmentAdapter(this, R.layout.item_spinner_selected, arrDepartment);

        listPopupDepartment = new ListPopupWindow(this);

        listPopupDepartment.setAdapter(departmentAdapter);
        listPopupDepartment.setAnchorView(filterDepartment);
        listPopupDepartment.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                filterDepartment.setText("Phòng ban: " + arrDepartment.get(position).getName());


                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Groups");
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        arrGroups.clear();
                        for (DataSnapshot dn : snapshot.getChildren()) {
                            Group group = dn.getValue(Group.class);
                            if (group != null) {

                                databaseReference.child(group.getGroupID()).child("department").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()) {
                                            if (snapshot.getValue().toString().equals(arrDepartment.get(position).getId())){
                                                arrGroups.add(group);
                                            }
                                        }
                                        adapter.notifyDataSetChanged();

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

                showAnimationFiler();
            }
        });

    }

    @Override
    public void deleteGroup(String id) {
        delete(id);
    }

    @Override
    public void InfoGroup(Group group) {
        Intent intent = new Intent(this, GroupInforActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("my_group", group);
        intent.putExtras(bundle);
        startActivity(intent);
    }


    private void delete(String id) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Groups");
        databaseReference.child(id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(getApplicationContext(), "Xóa thành công", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createGroup() {
        final Dialog dialog = new Dialog(GroupActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_option_create_grouo);
        Window window = dialog.getWindow();
        if (window == null) {
            return;
        }
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams layoutParams = window.getAttributes();

        layoutParams.gravity = Gravity.CENTER;

        window.setAttributes(layoutParams);

        //init views

        radWork = dialog.findViewById(R.id.radWork);
        btnCancel = dialog.findViewById(R.id.btnCancel);
        btnSubmit = dialog.findViewById(R.id.btnSubmit);


        btnSubmit.setOnClickListener(v -> {
            if (radWork.isChecked()) {
                startActivity(new Intent(GroupActivity.this, CreateGroupWorkActivity.class));
            } else {
                startActivity(new Intent(GroupActivity.this, CreateGroupDepartmentActivity.class));
            }

            dialog.cancel();
        });

        btnCancel.setOnClickListener(v -> {
            dialog.cancel();
        });

        dialog.show();
    }

    private void loadDepartment() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Department");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrDepartment.clear();
                for (DataSnapshot dn : snapshot.getChildren()) {
                    Department department = dn.getValue(Department.class);
                    if (department != null) {
                        arrDepartment.add(department);
                    }
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void filterUser(String s) {


        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Groups");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrGroups.clear();
                for (DataSnapshot dn : snapshot.getChildren()) {
                    Group group = dn.getValue(Group.class);
                    if (group != null) {
                        databaseReference.child(group.getGroupID()).child("department").child(s).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    arrGroups.add(group);
                                    adapter.notifyDataSetChanged();
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