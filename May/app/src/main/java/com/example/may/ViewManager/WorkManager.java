package com.example.may.ViewManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.example.may.AdapterManager.LayoutWorkManagerAdapter;
import com.example.may.Model.Work;
import com.example.may.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class WorkManager extends AppCompatActivity {
    private RecyclerView listWork;
    private TextView tvFilter, tvSort;
    private ImageView imgAdd, imgBack;


    private LayoutWorkManagerAdapter adapter;
    private List<Work> arrWorks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_manager);

        initUI();

        loadListWork();

        imgAdd.setOnClickListener(v -> startActivity(new Intent(this, CreateWork.class)));


        tvFilter.setOnClickListener(this::showPopupFilter);
        tvSort.setOnClickListener(this::showPopupSort);

        imgBack.setOnClickListener(v -> onBackPressed());
    }


    private void initUI() {
        listWork = findViewById(R.id.list_work);
        imgAdd = findViewById(R.id.imgAdd);
        tvFilter = findViewById(R.id.filter);
        tvSort = findViewById(R.id.sort);
        imgBack = findViewById(R.id.img_back);
        arrWorks = new ArrayList<>();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        adapter = new LayoutWorkManagerAdapter(arrWorks, this, 1);
        listWork.setLayoutManager(linearLayoutManager);
        listWork.setAdapter(adapter);

    }

    private void loadListWork() {


        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Work");
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrWorks.clear();
                for (DataSnapshot dn : snapshot.getChildren()) {
                    dbRef.child(dn.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot d : snapshot.getChildren()) {
                                Work work = d.getValue(Work.class);
                                arrWorks.add(work);


                            }
                            adapter.notifyDataSetChanged();
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
    }

    public void showPopupFilter(View v) {
        PopupMenu popupMenu = new PopupMenu(this, v);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.process:
                        filterWork(1);
                        break;
                    case R.id.complete:
                        filterWork(2);
                        break;
                    case R.id.out_date:
                        filterWork(3);
                        break;
                }
                return true;
            }
        });
        popupMenu.inflate(R.menu.menu_filter);

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


    private void filterWork(int status) {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Work");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrWorks.clear();
                for (DataSnapshot dn : snapshot.getChildren()) {
                    dbRef.child(dn.getKey()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            for (DataSnapshot d : snapshot.getChildren()) {
                                Work work = d.getValue(Work.class);
                                if (work.getWorkStatus() == status) {
                                    arrWorks.add(work);
                                }

                            }

                            adapter.notifyDataSetChanged();
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
    }

    public void showPopupSort(View v) {
        PopupMenu popupMenu = new PopupMenu(this, v);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.ascending:
                        SortWork(1);
                        break;
                    case R.id.decrease:
                        SortWork(2);
                        break;

                }
                return true;
            }
        });
        popupMenu.inflate(R.menu.menu_sort);

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

    private void SortWork(int status) {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Work");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrWorks.clear();
                for (DataSnapshot dn : snapshot.getChildren()) {
                    Query query = dbRef.child(dn.getKey()).orderByChild("workCreateDate");
                    query.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            for (DataSnapshot d : snapshot.getChildren()) {
                                Work work = d.getValue(Work.class);
                                if (status == 2) {
                                    arrWorks.add(0, work);
                                } else {
                                    arrWorks.add(work);
                                }
                            }

                            adapter.notifyDataSetChanged();
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
    }
}