package com.example.may.AdapterManager;

import android.annotation.SuppressLint;
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

import de.hdodenhof.circleimageview.CircleImageView;

public class SpinnerPositionAdapter extends ArrayAdapter<Position> {
    public SpinnerPositionAdapter(@NonNull Context context, int resource, @NonNull List<Position> objects) {
        super(context, resource, objects);
    }

    @SuppressLint("ViewHolder")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_spinner_selected, parent, false);
        TextView tvSelect = convertView.findViewById(R.id.tv_selected);

        Position  p = this.getItem(position);
        if (p != null){
            tvSelect.setText(p.getName());
        }
        return convertView;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_usser_create, parent, false);

        TextView nameLeader = convertView.findViewById(R.id.name_leader);


        Position p = this.getItem(position);

        if (p != null){
            nameLeader.setText(p.getName());


        }
        return convertView;
    }
}
