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
import com.example.may.Model.Work;
import com.example.may.R;

import java.util.List;

public class ListPopupWorkAdapter extends ArrayAdapter<Work> {
    public ListPopupWorkAdapter(@NonNull Context context, int resource, @NonNull List<Work> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_spinner_selected, parent, false);
        TextView tvSelect = convertView.findViewById(R.id.tv_selected);
        Work work = this.getItem(position);


        if (work != null){
            tvSelect.setText(work.getWorkName());
        }
        return convertView;
    }


    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pupup_department, parent, false);

        TextView nameLeader = convertView.findViewById(R.id.tv_element);


        Work work = this.getItem(position);

        if (work != null){
            nameLeader.setText(work.getWorkName());


        }
        return convertView;
    }
}
