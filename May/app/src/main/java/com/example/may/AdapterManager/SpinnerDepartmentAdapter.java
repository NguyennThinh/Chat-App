package com.example.may.AdapterManager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.may.Model.Department;
import com.example.may.Model.User;
import com.example.may.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SpinnerDepartmentAdapter extends ArrayAdapter<Department> {
    public SpinnerDepartmentAdapter(@NonNull Context context, int resource, @NonNull List<Department> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_spinner_selected, parent, false);
        TextView tvSelect = convertView.findViewById(R.id.tv_selected);

        Department department = this.getItem(position);
        if (department != null){
            tvSelect.setText(department.getName());
        }
        return convertView;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_usser_create, parent, false);

        TextView nameLeader = convertView.findViewById(R.id.name_leader);


        Department department = this.getItem(position);

        if (department != null){
            nameLeader.setText(department.getName());


        }
        return convertView;
    }
}
