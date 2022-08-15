package com.example.may.AdapterManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.may.Interface.OnItemClick;
import com.example.may.Model.Attendance;
import com.example.may.Model.User;
import com.example.may.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAttendanceApdater extends RecyclerView.Adapter<UserAttendanceApdater.UserViewHolder>{
    List<Attendance> arrAttendance;


    public UserAttendanceApdater(List<Attendance> arrAttendance ) {
        this.arrAttendance = arrAttendance;

    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.iten_user_attendance, parent, false);

        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
            Attendance attendance = arrAttendance.get(position);

        DateFormat format = new SimpleDateFormat("hh:mm a");
        holder.timeAttendance.setText(format.format(attendance.getTime()));

        getUserAttendance(holder, attendance);
    }



    @Override
    public int getItemCount() {
        return arrAttendance.size();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
         private CircleImageView imgUser;
         private TextView userName, timeAttendance;
         public UserViewHolder(@NonNull View itemView) {
             super(itemView);

             imgUser = itemView.findViewById(R.id.img_user);
             userName = itemView.findViewById(R.id.user_name);
             timeAttendance = itemView.findViewById(R.id.time);

         }
     }
    private void getUserAttendance(UserViewHolder holder, Attendance attendance) {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Users");
        dbRef.child(attendance.getId_user()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    User user = snapshot.getValue(User.class);
                    holder.userName.setText(user.getFullName());

                    if (user.getAvatar().equals("default")){
                        holder.imgUser.setImageResource(R.drawable.ic_logo);
                    }else {
                        Picasso.get().load(user.getAvatar()).into(holder.imgUser);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
