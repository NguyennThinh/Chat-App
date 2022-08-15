package com.example.may.AdapterManager;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.may.Api.ApiClient;
import com.example.may.Interface.IUserCListener;
import com.example.may.Model.Department;
import com.example.may.Model.Participant;
import com.example.may.Model.Position;
import com.example.may.Model.User;
import com.example.may.Model.Work;
import com.example.may.R;
import com.example.may.View.EmployeeDetailActivity;
import com.example.may.ViewManager.UpdateEmployee;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Field;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ManagerEmployeeAdapter extends RecyclerView.Adapter<ManagerEmployeeAdapter.employeeViewHoler> {
    List<User> arrUser;
    private IUserCListener iUserListener;

    public ManagerEmployeeAdapter(List<User> arrUser, IUserCListener iUserListener) {
        this.arrUser = arrUser;
        this.iUserListener = iUserListener;
    }

    @NonNull
    @Override
    public employeeViewHoler onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_manager_employee, parent, false);

        return new employeeViewHoler(view);
    }

    @Override
    public void onBindViewHolder(@NonNull employeeViewHoler holder, int position) {
        User user = arrUser.get(position);
        if (user.getAvatar().equals("default")) {
            holder.imgEmployee.setImageResource(R.drawable.ic_logo);
        } else {
            Picasso.get().load(user.getAvatar()).into(holder.imgEmployee);
        }
        holder.employeeName.setText(user.getFullName());

        loadPosition(holder, user);
        holder.layoutUser.setOnClickListener(v -> {
            showPopup(v, user);
        });
    }



    @Override
    public int getItemCount() {
        return arrUser.size();
    }

    static class employeeViewHoler extends RecyclerView.ViewHolder {
        private CircleImageView imgEmployee;
        private TextView employeeName, employeePosition;
        private LinearLayout layoutUser;
        public employeeViewHoler(@NonNull View itemView) {
            super(itemView);
            imgEmployee = itemView.findViewById(R.id.img_employee);
            employeeName = itemView.findViewById(R.id.employee_name);
            employeePosition = itemView.findViewById(R.id.employee_position);
            layoutUser = itemView.findViewById(R.id.layoutUser);
        }
    }


    public void showPopup(View v, User user) {
        PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
        popupMenu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.information:
                    v.getContext().startActivity(new Intent(v.getContext(), EmployeeDetailActivity.class).putExtra("id_em", user.getId()));
                    break;
                case R.id.update_employee:
                    v.getContext().startActivity(new Intent(v.getContext(), UpdateEmployee.class).putExtra("user_id", user.getId()));
                    break;
                case R.id.delete_employee:
                    iUserListener.deleteUser(user.getId());
                    break;
            }
            return false;
        });
        popupMenu.inflate(R.menu.employee_manager);

        Object menuHelper;
        Class[] argTypes;
        try {
            @SuppressLint("DiscouragedPrivateApi") Field fMenuHelper = PopupMenu.class.getDeclaredField("mPopup");
            fMenuHelper.setAccessible(true);
            menuHelper = fMenuHelper.get(popupMenu);
            argTypes = new Class[]{boolean.class};
            assert menuHelper != null;
            menuHelper.getClass().getDeclaredMethod("setForceShowIcon", argTypes).invoke(menuHelper, true);
        } catch (Exception ignored) {

        }
        popupMenu.show();
    }
    private void loadPosition(employeeViewHoler holder, User user) {
        DatabaseReference databaseReference  = FirebaseDatabase.getInstance().getReference("Position");
        databaseReference.child(user.getDepartment()).child(user.getPosition()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    Position position = snapshot.getValue(Position.class);
                    holder.employeePosition.setText(position.getName());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}
