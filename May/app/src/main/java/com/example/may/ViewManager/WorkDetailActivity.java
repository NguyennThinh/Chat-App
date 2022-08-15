package com.example.may.ViewManager;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.may.Adapter.LayoutUserAddParticipantSelectWorkAdapter;
import com.example.may.Adapter.ParticipantWorkAdapter;
import com.example.may.Interface.iAddMemberGroup;
import com.example.may.Model.Group;
import com.example.may.Model.Participant;
import com.example.may.Model.User;
import com.example.may.Model.Work;
import com.example.may.R;
import com.example.may.View.AddParticipantWorkActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class WorkDetailActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener, iAddMemberGroup {
    //Views
    private TextView workName, workDescription, workLeader, workTime, workStatus, workComplete;
    private ImageView imgReturn, imgMore;
    private RecyclerView listParticipant;

    //view dialog
    private EditText leader;
    private User users;

    private Work work;
    private List<Participant> arrParticipant;
    private List<User> arrUsers = new ArrayList<>();
    //Adapter participant work
    private ParticipantWorkAdapter adapter;
    private LayoutUserAddParticipantSelectWorkAdapter adapterUserSelect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_detail);
        initUI();

        loadInfoWork();


        imgMore.setOnClickListener(view -> {
            showPopup(view);
        });
        imgReturn.setOnClickListener(v -> {
            onBackPressed();
        });

    }


    private void initUI() {
        workName = findViewById(R.id.work_name);
        workDescription = findViewById(R.id.work_description);
        workLeader = findViewById(R.id.work_leader);
        workTime = findViewById(R.id.work_time);

        workStatus = findViewById(R.id.work_status);
        workComplete = findViewById(R.id.date_complete);

        listParticipant = findViewById(R.id.list_participant);

        imgMore = findViewById(R.id.img_more);
        imgReturn = findViewById(R.id.return_activity);

        work = (Work) getIntent().getExtras().get("work");
        arrParticipant = new ArrayList<>();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        RecyclerView.ItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        listParticipant.addItemDecoration(dividerItemDecoration);
        listParticipant.setLayoutManager(layoutManager);


    }

    private void loadInfoWork() {

        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Work");
        dbRef.child(work.getLeader()).child(work.getId()).addValueEventListener(new ValueEventListener() {
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

                    adapter = new ParticipantWorkAdapter(arrParticipant, work, 1, WorkDetailActivity.this);
                    listParticipant.setAdapter(adapter);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    public void showPopup( View v) {
        PopupMenu popupMenu = new PopupMenu(this, v);
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.inflate(R.menu.work_detail_menu);

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



    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()){
            case R.id.add_participant:

                Intent intent = new Intent(this, AddParticipantWorkActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("work", work);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case R.id.delete_work:
                deleteWork();
                break;

            case R.id.update_work:
                openUpdateWorkDialog();
                break;
        }
        return  true;
    }



    private void deleteWork() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Work");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dn : snapshot.getChildren()) {
                    Query query = databaseReference.child(dn.getKey()).child(work.getId());
                    query.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()){
                                snapshot.getRef().removeValue();
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


        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("WorkChild");
        dbRef.child(work.getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    snapshot.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        DatabaseReference dbRefGroup = FirebaseDatabase.getInstance().getReference("Groups");
        dbRefGroup.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dn : snapshot.getChildren()){
                    Group group = dn.getValue(Group.class);
                    if (group != null){
                        dbRefGroup.child(group.getGroupID()).child("work").child(work.getId()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()){
                                    snapshot.getRef().removeValue();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void openUpdateWorkDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_update_work);
        Window window = dialog.getWindow();

        if (window == null) {
            return;
        }
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams layoutParams = window.getAttributes();

        layoutParams.gravity = Gravity.CENTER;

        window.setAttributes(layoutParams);
        dialog.setCancelable(false);
        //Views dialog
        EditText name_work, description_work, work_start,work_end;
        ImageView imgStart, imgEnd, imgLeader;
        Button btnAdd, btnCancel;

        List<Participant> arrParticipants = new ArrayList<>();


        btnAdd = dialog.findViewById(R.id.btn_add);
        btnCancel = dialog.findViewById(R.id.btn_cancel);


        name_work = dialog.findViewById(R.id.work_name);
        description_work = dialog.findViewById(R.id.work_description);
        work_start = dialog.findViewById(R.id.work_start);
        work_end = dialog.findViewById(R.id.work_end);
        leader = dialog.findViewById(R.id.work_leader);
        imgStart = dialog.findViewById(R.id.imgStart);
        imgEnd = dialog.findViewById(R.id.imgEnd);
        imgLeader = dialog.findViewById(R.id.img_leader);



        //Set value dialog
        name_work.setText(work.getWorkName());
        description_work.setText(work.getWorkDescription());
        work_start.setText(work.getWorkStart());
        work_end.setText(work.getWorkEnd());
        btnAdd.setText("Cập nhật");

        if (users != null){
            leader.setText(users.getFullName());
        }else {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
            reference.child(work.getLeader()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User user = snapshot.getValue(User.class);
                    if (user != null) {
                        leader.setText(user.getFullName());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        Calendar date = Calendar.getInstance();
        int year = date.get(Calendar.YEAR);
        int month = date.get(Calendar.MONTH);
        int day = date.get(Calendar.DATE);


        imgStart.setOnClickListener(v -> {
            DatePickerDialog pickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    work_start.setText(dateFormat.format(date.getTime()));
                }
            },year, month, day);
            pickerDialog.show();
        });

        imgEnd.setOnClickListener(v -> {
            DatePickerDialog pickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    work_end.setText(dateFormat.format(date.getTime()));
                }
            },year, month, day);
            pickerDialog.show();
        });


        imgLeader.setOnClickListener(view->{
            Intent i = new Intent(this, ManagerActivity.class);
            i.putExtra("list", (Serializable) arrParticipant);
            startActivityForResult(i, 1);

        });



        btnAdd.setOnClickListener(v -> {
            String l = work.getLeader();
            HashMap<String, Object> works = new HashMap<>();
            works.put("workName", name_work.getText().toString());
            works.put("workDescription", description_work.getText().toString());
            works.put("workStart", work_start.getText().toString());
            works.put("workEnd", work_end.getText().toString());


            updateWork(works,l,dialog);



        });

        btnCancel.setOnClickListener(view -> {

            dialog.dismiss();
        });

        if (!WorkDetailActivity.this.isFinishing()) {
            dialog.show();
        }
    }

    private void updateWork(HashMap<String, Object> works, String l, Dialog dialog) {

        HashMap<String, Object> map = new HashMap<>();


        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Work");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dn : snapshot.getChildren()) {

                    Query query = databaseReference.child(dn.getKey()).child(work.getId());
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            if (snapshot.exists()){
                                if (users != null){
                                    map.put("leader", users.getId());
                                    map.put("participant/"+l+"/role", 3);
                                    map.put("participant/"+users.getId()+"/role", 1);

                                    snapshot.getRef().updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(WorkDetailActivity.this, "CẬp nhật thành công", Toast.LENGTH_SHORT).show();
                                            dialog.dismiss();
                                            //WorkDetailActivity.this.finish();
                                        }
                                    });
                                }

                                snapshot.getRef().updateChildren(works);
                                Toast.makeText(WorkDetailActivity.this, "CẬp nhật thành công", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();


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





        DatabaseReference dbRefGroup = FirebaseDatabase.getInstance().getReference("Groups");
        dbRefGroup.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dn : snapshot.getChildren()){
                    Group group = dn.getValue(Group.class);
                    if (group != null){
                        dbRefGroup.child(group.getGroupID()).child("work").child(work.getId()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()){
                                    snapshot.getRef().updateChildren(works);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }



    @Override
    public void addMemberGroup(List<User> arrEmployees) {
        arrUsers = arrEmployees;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {
                users = (User) data.getExtras().get("leader");
                leader.setText(users.getFullName());

            }
        }
    }
}