package com.example.may.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.may.Model.Log;
import com.example.may.Model.User;
import com.example.may.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.List;

public class ListLogAdapter extends RecyclerView.Adapter<ListLogAdapter.LogViewHolder>{
    private List<Log> arrLog;

    public ListLogAdapter(List<Log> arrLog) {
        this.arrLog = arrLog;
    }

    @NonNull
    @Override
    public LogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_log, parent, false);
        return new LogViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LogViewHolder holder, int position) {
        Log log = arrLog.get(position);

        setValue(log, holder);
    }


    @Override
    public int getItemCount() {
        return arrLog.size();
    }

    static class LogViewHolder extends RecyclerView.ViewHolder {
        private TextView log;
        public LogViewHolder(@NonNull View itemView) {
            super(itemView);
            log = itemView.findViewById(R.id.log);
        }
    }
    private void setValue(Log log, LogViewHolder holder) {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Users");
        dbRef.child(log.getIdUser()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    User user = snapshot.getValue(User.class);

                    holder.log.setText("- "+user.getFullName()+" vừa " +log.getLog()+ " vào lúc "+ new SimpleDateFormat("dd/MM/yyyy HH:mm a").format(log.getTime()));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}
