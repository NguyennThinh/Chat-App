package com.example.may.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.may.Adapter.ParticipantWorkAdapter;
import com.example.may.Model.Participant;
import com.example.may.Model.User;
import com.example.may.Model.Work;
import com.example.may.R;
import com.example.may.Utilities.RandomString;
import com.example.may.ViewManager.WorkDetailActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class DetailMyWork extends AppCompatActivity {
    //Views
    private TextView workName, workDescription, workLeader, workTime, workStatus, workComplete;
    private ImageView imgReturn;
    private RecyclerView listParticipant;
    private TextView CreateGroup;
    private Button btnSuccess;
    private ImageView imgAddUser;
    private Work work;
    private List<Participant> arrParticipant;
    private List<User> arrUsers = new ArrayList<>();
    private ParticipantWorkAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_my_work);

        initUI();

        loadInfoWork();

        imgReturn.setOnClickListener(v -> {
            onBackPressed();
        });

        CreateGroup.setOnClickListener(v -> {
            showdialog();
        });

        btnSuccess.setOnClickListener(v -> {
            SuccessWork();
        });
        imgAddUser.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddParticipantWorkActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("work", work);
            intent.putExtras(bundle);
            startActivity(intent);
        });
    }



    private void initUI() {
        workName = findViewById(R.id.work_name);
        workDescription = findViewById(R.id.work_description);
        workLeader = findViewById(R.id.work_leader);
        workTime = findViewById(R.id.work_time);
        CreateGroup = findViewById(R.id.img_create_group);
        btnSuccess = findViewById(R.id.success);
        imgAddUser = findViewById(R.id.img_add_user);
        workStatus = findViewById(R.id.work_status);
        workComplete = findViewById(R.id.date_complete);

        listParticipant = findViewById(R.id.list_participant);


        imgReturn = findViewById(R.id.return_activity);

        work = (Work) getIntent().getExtras().get("work");
        arrParticipant = new ArrayList<>();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        listParticipant.setLayoutManager(layoutManager);
        RecyclerView.ItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        listParticipant.addItemDecoration(dividerItemDecoration);

        adapter = new ParticipantWorkAdapter(arrParticipant, work, 2, DetailMyWork.this);
        listParticipant.setAdapter(adapter);
    }
    private void loadInfoWork() {

        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Work");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dn : snapshot.getChildren()) {
                    dbRef.child(dn.getKey()).child(work.getId()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                work = snapshot.getValue(Work.class);
                                if (work != null) {
                                    workName.setText(work.getWorkName());
                                    workDescription.setText(work.getWorkDescription());
                                    workTime.setText(work.getWorkStart() + " - " + work.getWorkEnd());
                                    if (work.getWorkStatus() == 1) {
                                        workStatus.setText("Đang tiến hành");
                                        workStatus.setTextColor(Color.parseColor("#FFFF9800"));
                                        workComplete.setVisibility(View.GONE);
                                    } else if (work.getWorkStatus() == 2) {
                                        workStatus.setText("Đã hoàn thành");
                                        workStatus.setTextColor(Color.GREEN);
                                        workComplete.setText(new SimpleDateFormat("dd/MM/yyyy").format(work.getDateComplete()));

                                    } else {
                                        workStatus.setText("Trễ thời hạn");
                                        workStatus.setTextColor(Color.RED);
                                        workComplete.setText(new SimpleDateFormat("dd/MM/yyyy").format(work.getDateComplete()));

                                    }
                                }
                            }
                        }


                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        loadLeader();
        loadListParticipant();
    }



    private void loadLeader() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(work.getLeader()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                workLeader.setText(user.getFullName());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    private void loadListParticipant() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Work");
        databaseReference.child(work.getLeader()).child(work.getId()).child("participant").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrParticipant.clear();
                if (snapshot.exists()){
                    for (DataSnapshot ds: snapshot.getChildren()){
                        Participant participant = ds.getValue(Participant.class);
                        if (participant != null){
                            arrParticipant.add(0, participant);
                        }
                    }



                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    private void showdialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Tạo nhóm");
        builder.setMessage("Bạn muốn tạo nhóm cho công việc: "+ work.getWorkName());
        builder.setPositiveButton("Tạo nhóm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


                Long timeCreate = System.currentTimeMillis();
                RandomString gen = new RandomString(15, ThreadLocalRandom.current());
                String id = gen.nextString();

                HashMap<String, Object> map = new HashMap<>();
                map.put("groupID", id);
                map.put("groupName", work.getWorkName());
                map.put("userCreate", work.getLeader());
                map.put("description", "");
                map.put("groupImage", "default");
                map.put("createDate", timeCreate);
                map.put("slParticipant", arrParticipant.size());
                map.put("work", work.getId());
                map.put("type", 1);


                DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Groups");
                dbRef.child(id).setValue(map);




                for (Participant p : arrParticipant){
                    dbRef.child(id).child("participant").child(p.getId()).setValue(p).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {

                            Toast.makeText(getApplicationContext(), "Tạo nhóm thành công", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
                }

            }
        });
        builder.setNegativeButton("Hủy bỏ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.create().show();
    }
    private void SuccessWork() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(DetailMyWork.this);
        builder.setTitle("Hoàn thành công việc");
        builder.setMessage("Bạn muốn hoàn thành công việc");
        builder.setPositiveButton("Hoành thành", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                HashMap<String, Object> status = new HashMap<>();
                status.put("workStatus", 2);
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Work");
                    databaseReference.child(work.getLeader()).child(work.getId()).updateChildren(status).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(getApplicationContext(), "Đã hoàn thành công việc", Toast.LENGTH_SHORT).show();
                        }
                    });
            }
        });
        builder.setNegativeButton("Hủy bỏ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

}