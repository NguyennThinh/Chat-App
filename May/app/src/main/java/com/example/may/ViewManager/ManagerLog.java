package com.example.may.ViewManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.ImageView;

import com.example.may.Adapter.ListLogAdapter;
import com.example.may.Model.Log;
import com.example.may.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ManagerLog extends AppCompatActivity {
    private ImageView imgBack;
    private RecyclerView listLog;

    ListLogAdapter logAdapter;

    private List<Log> arrLogs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_log);

        initUI();

        loadLog();

        imgBack.setOnClickListener(v -> {
            onBackPressed();
        });
    }



    private void initUI() {
        imgBack = findViewById(R.id.img_back);
        listLog = findViewById(R.id.list_log);

        arrLogs = new ArrayList<>();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        logAdapter = new ListLogAdapter(arrLogs);

        listLog.setLayoutManager(layoutManager);
        listLog.setAdapter(logAdapter);

    }

    private void loadLog() {

        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Logs");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrLogs.clear();
                for (DataSnapshot dn : snapshot.getChildren()){
                    Log log = dn.getValue(Log.class);
                    if (log!= null){
                        arrLogs.add(log);
                    }
                }
                logAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}