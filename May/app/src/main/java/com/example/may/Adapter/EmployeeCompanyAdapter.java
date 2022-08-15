package com.example.may.Adapter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.may.AdapterManager.ManagerEmployeeAdapter;
import com.example.may.Model.Department;
import com.example.may.Model.User;
import com.example.may.R;
import com.example.may.View.UserChatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Nguyễn Phúc Thịnh on 5/11/2022.
 * Đại học công nghiệp TP HCM
 * nguyenthinhc9@gmail.com
 */
public class EmployeeCompanyAdapter extends RecyclerView.Adapter<EmployeeCompanyAdapter.UsersHolder> {
    List<User> arrUsers;

    public EmployeeCompanyAdapter(List<User> arrUsers) {
        this.arrUsers = arrUsers;
    }

    @NonNull
    @Override
    public UsersHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_employee, parent, false);
        return new UsersHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UsersHolder holder, int position) {
        User users = arrUsers.get(position);
        if (users.getAvatar().equals("default")){
            holder.imgAvatar.setImageResource(R.drawable.ic_logo);
        }else {
            Picasso.get().load(users.getAvatar()).into(holder.imgAvatar);
        }
        holder.EmployeeName.setText(users.getFullName());

        holder.itemListEmployee.setOnClickListener(view -> {
          Intent intent = new Intent(view.getContext(), UserChatActivity.class);
          intent.putExtra("id_user", users.getId());

            view.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return arrUsers.size();
    }

    static class UsersHolder extends RecyclerView.ViewHolder {
        private CircleImageView imgAvatar;
        private TextView EmployeeName;
        private TextView Position;
        private RelativeLayout itemListEmployee;
        public UsersHolder(@NonNull View itemView) {
            super(itemView);

            imgAvatar = itemView.findViewById(R.id.list_employee_avatar);
            EmployeeName = itemView.findViewById(R.id.list_employee_name);
            Position = itemView.findViewById(R.id.list_employee_position);
            itemListEmployee = itemView.findViewById(R.id.item_list_employee);
        }
    }

}
