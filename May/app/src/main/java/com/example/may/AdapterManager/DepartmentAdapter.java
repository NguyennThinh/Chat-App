package com.example.may.AdapterManager;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.may.Model.Department;
import com.example.may.Model.Participant;
import com.example.may.Model.User;
import com.example.may.R;
import com.example.may.View.DepartmentDetailActivity;
import com.example.may.ViewManager.ListDepartment;
import com.example.may.ViewManager.ManagerUpdateDepartmentActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class DepartmentAdapter extends RecyclerView.Adapter<DepartmentAdapter.ViewHoler> {

    List<Department> arrDepartments;
    User users;
    EditText manager;
    FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
    public DepartmentAdapter(List<Department> arrDepartments) {
        this.arrDepartments = arrDepartments;
    }

    @NonNull
    @Override
    public ViewHoler onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_department, parent, false);
        return new ViewHoler(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHoler holder, int position) {
        Department department = arrDepartments.get(position);

        setManager(holder, department);
        holder.tvName.setText(department.getName());
        if (department.getImgDepartment().equals("default")) {
            holder.imgDepartment.setImageResource(R.drawable.ic_department);
        } else {
            Picasso.get().load(department.getImgDepartment()).into(holder.imgDepartment);
        }
        holder.item_layout.setOnClickListener(v -> {
            showOption(v, department, holder);
        });
    }


    @Override
    public int getItemCount() {
        return arrDepartments.size();
    }

    public class ViewHoler extends RecyclerView.ViewHolder {
        private TextView tvName, tvManager;
        private CircleImageView imgDepartment;
        private LinearLayout item_layout;

        public ViewHoler(@NonNull View itemView) {
            super(itemView);

            tvManager = itemView.findViewById(R.id.manager);
            tvName = itemView.findViewById(R.id.department_name);
            item_layout = itemView.findViewById(R.id.item_layout);
            imgDepartment = itemView.findViewById(R.id.img_department);
        }
    }

    private void setManager(ViewHoler holder, Department department) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(department.getIdManager()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    User user = snapshot.getValue(User.class);
                    holder.tvManager.setText("TP: " + user.getFullName());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void showOption(View view, Department department, ViewHoler holder) {
        //Option edit
        String[] option = {"Xem thông tin " + department.getName(), "Cập nhật thông tin " + department.getName(), "Xóa " + department.getName()};

        //Dialog edit
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setItems(option, (dialogInterface, i) -> {
            switch (i) {
                case 0:
                    view.getContext().startActivity(new Intent(view.getContext(), DepartmentDetailActivity.class).putExtra("id_department",
                            department.getId()));
                    break;
                case 1:
                    updateDepartment(department, view, holder);
                    break;
                case 2:
                    deleteDepartment(view, department);

                    break;


            }
        });
        builder.create().show();
    }


    private void deleteDepartment(View view, Department department) {

        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(view.getContext());
        builder.setTitle("Xóa phongf ban");
        builder.setMessage("Bạn muốn xóa phòng ban này");
        builder.setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Users");

                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Department");


                databaseReference.child(department.getId()).child("participant").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dn : snapshot.getChildren()) {
                            Participant participant = dn.getValue(Participant.class);
                            if (participant != null) {
                                dbRef.child(participant.getId()).child("department").removeValue();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                databaseReference.child(department.getId()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(view.getContext(), "Xóa thành công", Toast.LENGTH_SHORT).show();
                    }
                });

                //Logs
                HashMap<String, Object> log = new HashMap<>();
                log.put("idUser", mUser.getUid());
                log.put("log", "xóa "+ department.getName());
                log.put("time", System.currentTimeMillis());
                DatabaseReference dbReff = FirebaseDatabase.getInstance().getReference("Logs");
                dbReff.push().setValue(log);
            }
        });
        builder.setNegativeButton("Hủy bỏ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void updateDepartment(Department department, View view, ViewHoler holder) {
        final Dialog dialog = new Dialog(view.getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_update_department);
        Window window = dialog.getWindow();

        if (window == null) {
            return;
        }
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams layoutParams = window.getAttributes();

        layoutParams.gravity = Gravity.CENTER;

        window.setAttributes(layoutParams);
        dialog.setCancelable(false);
        //Views dialog
        EditText name, description;
        ImageView imgLeader;
        Button btnAdd, btnCancel;


        name = dialog.findViewById(R.id.name);
        description = dialog.findViewById(R.id.department_title);
        manager = dialog.findViewById(R.id.manager);
        btnAdd = dialog.findViewById(R.id.btn_add);
        btnCancel = dialog.findViewById(R.id.btn_cancel);
        imgLeader = dialog.findViewById(R.id.img_manager);
        //Set value dialog
        name.setText(department.getName());
        description.setText(department.getMission());
        manager.setText(holder.tvManager.getText().toString());

        imgLeader.setOnClickListener(v -> {
            Intent i = new Intent(view.getContext(), ManagerUpdateDepartmentActivity.class);
            i.putExtra("id", department.getId());
            ((Activity) view.getContext()).startActivityForResult(i, 1);

        });


        btnAdd.setOnClickListener(v -> {

            HashMap<String, Object> map = new HashMap<>();

            map.put("name", name.getText().toString().trim());
            if (users != null) {
                map.put("idManager", users.getId());
            }
            map.put("mission", description.getText().toString());

            updateWork(map, dialog, department);

        });

        btnCancel.setOnClickListener(v -> {

            dialog.dismiss();
        });


        dialog.show();


    }


    private void updateWork(HashMap<String, Object> map, Dialog dialog, Department department) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Department");
        if (users != null) {
            HashMap<String, Object> m = new HashMap<>();
            m.put("participant/" + department.getIdManager() + "/role", 3);
            m.put("participant/" + users.getId() + "/role", 1);


            databaseReference.child(department.getId()).updateChildren(m);

        }

        databaseReference.child(department.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    snapshot.getRef().updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(dialog.getContext(), "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        users = (User) data.getExtras().get("leader");
        manager.setText("TP: " + users.getFullName());


    }
}
