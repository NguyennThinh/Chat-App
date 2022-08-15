package com.example.may.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.may.AdapterManager.ManagerEmployeeAdapter;
import com.example.may.Model.Department;
import com.example.may.Model.User;
import com.example.may.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SpinnerLeaderAdapter extends ArrayAdapter<User> {
    public SpinnerLeaderAdapter(@NonNull Context context, int resource, @NonNull List<User> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_spinner_selected, parent, false);
        TextView tvSelect = convertView.findViewById(R.id.tv_selected);

        User users = this.getItem(position);
        if (users != null){
            tvSelect.setText(users.getFullName());
        }
        return convertView;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_leader_work, parent, false);

        TextView nameLeader = convertView.findViewById(R.id.name_leader);
        TextView leader_position = convertView.findViewById(R.id.leader_position);
        CircleImageView imgLeader = convertView.findViewById(R.id.img_leader);

        User users = this.getItem(position);
        if (users != null){
            nameLeader.setText(users.getFullName());
            leader_position.setText(users.getPosition());
            if (users.getAvatar().equals("default")){
                imgLeader.setImageResource(R.drawable.ic_launcher_background);
            }else {
                Picasso.get().load(users.getAvatar()).into(imgLeader);
            }

        }
        return convertView;
    }


}
