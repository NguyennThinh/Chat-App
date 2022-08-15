package com.example.may.ViewEmployee;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.may.Adapter.CalendarAdapter;
import com.example.may.Interface.OnItemClick;
import com.example.may.Model.Attendance;
import com.example.may.Model.User;
import com.example.may.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.naishadhparmar.zcustomcalendar.CustomCalendar;
import org.naishadhparmar.zcustomcalendar.Property;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class EmployeeAttendance extends AppCompatActivity implements OnItemClick {
    private RecyclerView calendar;
    private TextView monthOfYear, dateAttendance, monthAttendance, userName, position, email;
    private Button btnPrevious, btnNext;
    private CircleImageView imgUser;
    private ImageView imgBack;
    CalendarAdapter adapter;


    private LocalDate selectDate;

    private FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_attendance);

        initUI();

        getInfo();


        selectDate = LocalDate.now();

        setMonthView();
        getNumberDayAttendance();


        btnNext.setOnClickListener(v -> {
            nextMonthOfYear();
        });
        btnPrevious.setOnClickListener(v -> {
            previousMonthOfYear();
        });
        imgBack.setOnClickListener(v -> {
            onBackPressed();
        });
    }


    private void initUI() {
        imgUser = findViewById(R.id.img_user);
        userName = findViewById(R.id.user_name);
        imgBack = findViewById(R.id.img_back);
        email = findViewById(R.id.email);
        position = findViewById(R.id.position);
        calendar = findViewById(R.id.calendar);
        monthAttendance = findViewById(R.id.attendance_month);
        monthOfYear = findViewById(R.id.monthTv);
        dateAttendance = findViewById(R.id.date_attendance);
        btnNext = findViewById(R.id.btnNext);
        btnPrevious = findViewById(R.id.btnPrevious);


    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setMonthView() {
        monthOfYear.setText(monthYearFromDate(selectDate));


        List<String> arrDay = dayIsMonthList(selectDate);

        adapter = new CalendarAdapter(arrDay, this, selectDate);
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
        if (!day.equals("")) {
            String message = "Select day :" + day + monthYearFromDate(selectDate);

            DateTimeFormatter month = DateTimeFormatter.ofPattern("MM");
            DateTimeFormatter year = DateTimeFormatter.ofPattern("yyyy");
            getValueAttendance(month, year, day);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void getValueAttendance(DateTimeFormatter month, DateTimeFormatter year, String day) {


        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Attendance");
        databaseReference.child(String.valueOf(year.format(selectDate))).child(String.valueOf(month.format(selectDate)))
                .child(day).child(mUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Attendance attendance = snapshot.getValue(Attendance.class);
                    DateFormat format = new SimpleDateFormat("hh:mm a");

                    dateAttendance.setTextColor(Color.GREEN);
                    dateAttendance.setText("Bạn đã điểm danh ngày " + day + "/" + month.format(selectDate) + "/" + year.format(selectDate) + " vào lúc: " + format.format(attendance.getTime()));
                } else {
                    dateAttendance.setText("Bạn chưa điểm danh ngày " + day + "/" + month.format(selectDate) + "/" + year.format(selectDate));
                    dateAttendance.setTextColor(Color.RED);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void getNumberDayAttendance() {
        DateTimeFormatter month = DateTimeFormatter.ofPattern("MM");
        DateTimeFormatter year = DateTimeFormatter.ofPattern("yyyy");

        YearMonth yearMonthObject = YearMonth.of(Integer.parseInt(year.format(selectDate)), Integer.parseInt(month.format(selectDate)));

        int daysInMonth = yearMonthObject.lengthOfMonth();
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Attendance");

        List<Attendance> arrAttendances = new ArrayList<>();
        for (int i = 0; i < daysInMonth; i++) {

            if (i < 10) {

                dbRef.child(String.valueOf(year.format(selectDate))).child(String.valueOf(month.format(selectDate)))
                        .child(String.valueOf("0" + i)).child(mUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            arrAttendances.add(snapshot.getValue(Attendance.class));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            } else {
                dbRef.child(String.valueOf(year.format(selectDate))).child(String.valueOf(month.format(selectDate)))
                        .child(String.valueOf(i)).child(mUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            arrAttendances.add(snapshot.getValue(Attendance.class));
                            monthAttendance.setText("Tháng này đã điểm danh: " + arrAttendances.size() + "/" + daysInMonth + " ngày");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

        }

    }

    private void getInfo() {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Users");
        dbRef.child(mUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    User user = snapshot.getValue(User.class);
                    if (user.getAvatar().equals("default")){
                        imgUser.setImageResource(R.drawable.ic_logo);
                    }else {
                        Picasso.get().load(user.getAvatar()).into(imgUser);
                    }
                    userName.setText(user.getFullName());
                    email.setText(user.getEmail());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}