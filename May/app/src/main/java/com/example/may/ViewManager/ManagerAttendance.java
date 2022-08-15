package com.example.may.ViewManager;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.may.Adapter.CalendarAdapter;
import com.example.may.AdapterManager.CalendarManagerAdapter;
import com.example.may.AdapterManager.UserAttendanceApdater;
import com.example.may.Interface.OnItemClick;
import com.example.may.Model.Attendance;
import com.example.may.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ManagerAttendance extends AppCompatActivity implements OnItemClick {
    private RecyclerView calendar,userAttendance;
    private TextView monthOfYear;
    private Button btnPrevious, btnNext;
    private ImageView imgBack;

    CalendarManagerAdapter adapter;
    UserAttendanceApdater userAdapter;

    private List<Attendance> arrAttendances;

    private LocalDate selectDate;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_attendance);

        initUI();

        selectDate = LocalDate.now();

        setMonthView();

        imgBack.setOnClickListener(v -> {
            onBackPressed();
        });
        btnNext.setOnClickListener(v -> {
            nextMonthOfYear();
        });
        btnPrevious.setOnClickListener(v -> {
            previousMonthOfYear();
        });
    }


    private void initUI() {

        calendar = findViewById(R.id.calendar);
        monthOfYear = findViewById(R.id.monthTv);
        btnNext = findViewById(R.id.btnNext);
        btnPrevious = findViewById(R.id.btnPrevious);
        userAttendance = findViewById(R.id.user_attendance);
        imgBack = findViewById(R.id.img_back);
        arrAttendances = new ArrayList<>();

        userAdapter = new UserAttendanceApdater(arrAttendances);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 4);
        RecyclerView.ItemDecoration VERTICAL = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        RecyclerView.ItemDecoration HORIZONTAL = new DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL);

        userAttendance.addItemDecoration(VERTICAL);
        userAttendance.addItemDecoration(HORIZONTAL);

        userAttendance.setLayoutManager(gridLayoutManager);
        userAttendance.setAdapter(userAdapter);


    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setMonthView() {
        monthOfYear.setText(monthYearFromDate(selectDate));


        List<String> arrDay = dayIsMonthList(selectDate);

        adapter = new CalendarManagerAdapter(arrDay, this, selectDate);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 7);


        calendar.setAdapter(adapter);
        calendar.setLayoutManager(layoutManager);

    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private List<String> dayIsMonthList(LocalDate date) {

        List<String> arrDayIsMonth = new ArrayList<>();

        YearMonth yearMonth = YearMonth.from(date);
        int dayIsMonth = yearMonth.lengthOfMonth();

        LocalDate firstOfMonth = selectDate.withDayOfMonth(1);
        int dayOfWeek = firstOfMonth.getDayOfWeek().getValue();

        for (int i = 1; i <= 42; i++) {
            if (i <= dayOfWeek || i > dayOfWeek + dayIsMonth) {
                arrDayIsMonth.add("");
            } else {
                arrDayIsMonth.add(String.valueOf(i - dayOfWeek));
            }
        }
        return arrDayIsMonth;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private String monthYearFromDate(LocalDate localDate) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MMM yyyy");
        return localDate.format(dateTimeFormatter);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void nextMonthOfYear() {
        selectDate = selectDate.plusMonths(1);
        setMonthView();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void previousMonthOfYear() {
        selectDate = selectDate.minusMonths(1);
        setMonthView();
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void itemClick(String day) {
        getUserAttendance(day);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void getUserAttendance(String day) {
        DateTimeFormatter month = DateTimeFormatter.ofPattern("MM");
        DateTimeFormatter year = DateTimeFormatter.ofPattern("yyyy");

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Attendance");
        databaseReference.child(String.valueOf(year.format(selectDate))).child(String.valueOf(month.format(selectDate)))
                .child(day).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrAttendances.clear();


                for (DataSnapshot dn : snapshot.getChildren()){
                    Attendance attendance = dn.getValue(Attendance.class);
                    if (attendance != null){

                        arrAttendances.add(attendance);
                    }
                }
                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}