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
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListPopupWindow;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.may.Adapter.ListPopupDepartmentAdapter;
import com.example.may.Adapter.ListPopupWorkAdapter;
import com.example.may.AdapterManager.ManagerEmployeeAdapter;
import com.example.may.AdapterManager.SpinnerDepartmentAdapter;
import com.example.may.AdapterManager.SpinnerPositionAdapter;
import com.example.may.Api.ApiClient;
import com.example.may.Interface.IUserCListener;
import com.example.may.Model.Department;
import com.example.may.Model.Participant;
import com.example.may.Model.Position;
import com.example.may.Model.User;
import com.example.may.Model.Work;
import com.example.may.R;
import com.example.may.Utilities.RandomString;
import com.example.may.View.LoginActivity;
import com.example.may.View.RegisterActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EmployeeManager extends AppCompatActivity implements IUserCListener {
    private ProgressDialog progressDialog;
    //View
    private ImageView imgBack, imgAddEnployee, imgDrop;
    private RecyclerView listEmployee;

    private LinearLayout layout_name;
    private AppCompatTextView filterDepartment;

    private List<User> arrUsers;
    private List<Department> arrDepartment;

    //Views dialog
    TextInputEditText email, name, birthday, phone;
    RadioButton radNam, radNu;
    Button btnAdd, btnCancel;
    Spinner spPosition;

    SpinnerPositionAdapter positionAdapter;
    //Adapter
    private ManagerEmployeeAdapter adapter;
    private FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();


    private ListPopupWindow listPopupDepartment;
    private  boolean isUp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_manager);

        initUI();
        loadListUser();
        loadDepartment();
        imgAddEnployee.setOnClickListener(v -> {
        //     showDialogAddUser();
            Intent intent = new Intent(this, AddEmployee.class);
            startActivity(intent);
        });

        imgBack.setOnClickListener(v -> {
            onBackPressed();
        });



        layout_name.setOnClickListener(v -> {
            showAnimationFiler();

        });

        setPopupListDepartment();
    }

    private void setPopupListDepartment() {

        ListPopupDepartmentAdapter departmentAdapter = new ListPopupDepartmentAdapter(this, R.layout.item_spinner_selected, arrDepartment);

        listPopupDepartment = new ListPopupWindow(this);

        listPopupDepartment.setAdapter(departmentAdapter);
        listPopupDepartment.setAnchorView(filterDepartment);
        listPopupDepartment.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {



                filterDepartment.setText("Phòng ban: " +arrDepartment.get(position).getName());


                DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference("Department");
                firebaseDatabase.child(arrDepartment.get(position).getId()).child("participant").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        showAnimationFiler();
                        arrUsers.clear();
                        for (DataSnapshot dn : snapshot.getChildren()) {
                            Participant participant = dn.getValue(Participant.class);
                            DatabaseReference db = FirebaseDatabase.getInstance().getReference("Users");
                            db.child(participant.getId()).addValueEventListener(new ValueEventListener() {
                                @SuppressLint("NotifyDataSetChanged")
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {

                                        User user = snapshot.getValue(User.class);
                                        arrUsers.add(user);
                                        adapter.notifyDataSetChanged();

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


                listPopupDepartment.dismiss();
            }
        });

    }


    private void showAnimationFiler() {
        if (isUp) {
            if (EmployeeManager.this.isFinishing()){
                listPopupDepartment.show();
            }
            listPopupDepartment.dismiss();
            imgDrop.startAnimation( AnimationUtils.loadAnimation(this, R.anim.rorate_180_360));

            //   myButton.setText("Slide up");
        } else {

            if (!EmployeeManager.this.isFinishing()){
                listPopupDepartment.show();
            }
            imgDrop.startAnimation( AnimationUtils.loadAnimation(this, R.anim.rorate_0_180));

            //   myButton.setText("Slide down");
        }
        isUp = !isUp;
    }


    private void initUI() {
        layout_name = findViewById(R.id.layout_name);
        imgBack = findViewById(R.id.img_back);
        imgAddEnployee = findViewById(R.id.add_employee);
        listEmployee = findViewById(R.id.list_employee);

        filterDepartment = findViewById(R.id.filter_department);
        imgDrop = findViewById(R.id.img_drop);

        progressDialog = new ProgressDialog(this);
        arrUsers = new ArrayList<>();
        arrDepartment = new ArrayList<>();

        adapter = new ManagerEmployeeAdapter(arrUsers, this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        listEmployee.setLayoutManager(gridLayoutManager);
        listEmployee.setAdapter(adapter);

        positionAdapter = new SpinnerPositionAdapter(getApplicationContext(), R.layout.item_spinner_selected, getListPosition());



    }

    private void loadListUser() {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Users");
        Query query = dbRef.orderByChild("position");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrUsers.clear();
                for (DataSnapshot dn : snapshot.getChildren()) {
                    User user = dn.getValue(User.class);
                    if (user != null) {
                        arrUsers.add(user);

                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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


    @Override
    public void deleteUser(String id) {
        if (!id.equals(mUser.getUid())) {
            ApiClient.client.deleteUser(id).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    System.out.println(response.toString());
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    System.out.println(t.getMessage());
                }
            });

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
            databaseReference.child(id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Toast.makeText(getApplicationContext(), "Xoá thành công", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(getApplicationContext(), "Bạn không thể xóa bạn", Toast.LENGTH_SHORT).show();
        }
    }




    private List<Position> getListPosition() {
        List<Position> arrPosition = new ArrayList<>();


        return arrPosition;
    }

    private void RegisterUser(Dialog dialog, HashMap<String, Object> map) {

    }

}