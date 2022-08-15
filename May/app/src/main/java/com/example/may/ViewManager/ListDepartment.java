package com.example.may.ViewManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.may.AdapterManager.DepartmentAdapter;
import com.example.may.Model.Department;
import com.example.may.Model.User;
import com.example.may.R;
import com.example.may.Utilities.RandomString;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class ListDepartment extends AppCompatActivity {
    private RecyclerView tableData;
    private DepartmentAdapter adapter;
    private ImageView imgAddDepartment, imgBack;
    private List<Department> arrDepartments;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_department);

        initUI();


        loadDepartment();


        imgBack.setOnClickListener(v -> {
            onBackPressed();
        });

        imgAddDepartment.setOnClickListener(v -> {
          //  startActivity(new Intent(this, CreateDepartment.class));
            openDialog();
        });
    }



    private void initUI() {
        tableData = findViewById(R.id.list_department);
        imgAddDepartment = findViewById(R.id.img_add_department);
        imgBack = findViewById(R.id.img_back);

        arrDepartments = new ArrayList<>();


        tableData.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new DepartmentAdapter(arrDepartments);
        tableData.setAdapter(adapter);
    }

    private void loadDepartment() {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Department");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrDepartments.clear();
                for (DataSnapshot dn : snapshot.getChildren()){
                    Department department = dn.getValue(Department.class);
                    if (department != null){
                        arrDepartments.add(department);
                    }

                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {
                adapter.onActivityResult(requestCode, resultCode, data);
            }
        }

    }



    private void openDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_add_department);
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
        TextInputEditText inputName, inputMission;

        Button btnAdd, btnCancel;

        //Initialize views
        inputName = dialog.findViewById(R.id.inputName);
        inputMission = dialog.findViewById(R.id.inputMission);

        btnAdd = dialog.findViewById(R.id.btn_create);
        btnCancel = dialog.findViewById(R.id.btn_cancel);

        btnAdd.setOnClickListener(v -> {
            RandomString ran = new RandomString(10, ThreadLocalRandom.current());
            String id = ran.nextString();
            Long timeCreate = System.currentTimeMillis();



            if (inputName.getText().toString().trim().equals("") ||inputMission.getText().toString().equals("")   ){
                Toast.makeText(getApplicationContext(),"Bạn chưa nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            }else {

                HashMap<String, Object> map = new HashMap<>();
                map.put("id", id);
                map.put("name", inputName.getText().toString().trim());
                map.put("idManager", "");
                map.put("mission", inputMission.getText().toString());
                map.put("slParticipant", 0);
                map.put("createDate", timeCreate);
                map.put("imgDepartment", "default");

                DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Department");
                dbRef.child(id).setValue(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(getApplicationContext(), "Tạo phòng ban thành công", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });
                    
    
            }
        });

        btnCancel.setOnClickListener(v -> {
            dialog.dismiss();
        });
        dialog.show();
    }
}