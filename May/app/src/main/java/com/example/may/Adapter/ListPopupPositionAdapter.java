package com.example.may.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.may.Model.Department;
import com.example.may.Model.Position;
import com.example.may.R;

import java.util.List;

public class ListPopupPositionAdapter extends ArrayAdapter<Position> {
    public ListPopupPositionAdapter(@NonNull Context context, int resource, @NonNull List<Position> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_spinner_selected, parent, false);
        TextView tvSelect = convertView.findViewById(R.id.tv_selected);
        Position department = this.getItem(position);


        if (department != null){
            tvSelect.setText(department.getName());
        }
        return convertView;
    }


    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pupup_department, parent, false);

        TextView nameLeader = convertView.findViewById(R.id.tv_element);


        Position department = this.getItem(position);

        if (department != null){
            nameLeader.setText(department.getName());


        }
        return convertView;
    }
}
