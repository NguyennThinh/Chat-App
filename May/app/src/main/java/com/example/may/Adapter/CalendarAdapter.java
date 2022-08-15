package com.example.may.Adapter;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.CalendarViewholder> {
    private List<String> arrDayOfMonth;
    private OnItemClick onItemClick;
    private LocalDate localDate;

    private FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
    public CalendarAdapter(List<String> arrDayOfMonth, OnItemClick onItemClick, LocalDate localDate) {
        this.arrDayOfMonth = arrDayOfMonth;
        this.onItemClick = onItemClick;
        this.localDate = localDate;
    }

    @NonNull
    @Override
    public CalendarViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View  view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_calendar, parent, false);

        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.height = (int) (parent.getHeight()*0.1666666666);
        return new CalendarViewholder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull CalendarViewholder holder, @SuppressLint("RecyclerView") int position) {
        String day = arrDayOfMonth.get(position);


        DateTimeFormatter month = DateTimeFormatter.ofPattern("MM");
        DateTimeFormatter year = DateTimeFormatter.ofPattern("yyyy");





        Date now = new Date();
        SimpleDateFormat format = new SimpleDateFormat("MM");
        SimpleDateFormat fd = new SimpleDateFormat("dd");

        if (String.valueOf(localDate.format(month)).equals(format.format(now))){

          if (day.equals("")){
              holder.item_cell_calendar.setCardBackgroundColor(Color.parseColor("#efefefef"));
          }else {
              if (Integer.parseInt(day)> Integer.parseInt(fd.format(now))){
                  holder.item_cell_calendar.setCardBackgroundColor(Color.WHITE);
                    holder.dayOfMonth.setTextColor(Color.BLACK);
              }else {
                  getValueAttendance(day, year, month, holder);
              }
          }
        }else  if (Integer.parseInt(localDate.format(month)) ==4 && Integer.parseInt(localDate.format(year) )==2022){
            if (day.equals("")){
                holder.item_cell_calendar.setCardBackgroundColor(Color.parseColor("#efefefef"));
            }else {
                holder.item_cell_calendar.setCardBackgroundColor(Color.RED);

            }
        }else {

            holder.item_cell_calendar.setCardBackgroundColor(Color.WHITE);
            holder.dayOfMonth.setTextColor(Color.BLACK);
        }





        holder.dayOfMonth.setText(day);



        holder.dayOfMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClick.itemClick( day);
            }
        });
    }


    @Override
    public int getItemCount() {
        return arrDayOfMonth.size();
    }


    static class CalendarViewholder extends RecyclerView.ViewHolder {
        private TextView dayOfMonth;
        private CardView item_cell_calendar;
        public CalendarViewholder(@NonNull View itemView) {
            super(itemView);

            dayOfMonth = itemView.findViewById(R.id.cellCalendar);
            item_cell_calendar = itemView.findViewById(R.id.item_cell_calendar);

        }


    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void getValueAttendance(String date, DateTimeFormatter year, DateTimeFormatter month, CalendarViewholder holder) {

        String m = localDate.format(month);
        String y = localDate.format(year);
        if (date.equals("")){
            holder.item_cell_calendar.setCardBackgroundColor(Color.GRAY);
        }else {




            DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Attendance");
            dbRef.child(y).child(m).child(date).child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){

                        Attendance attendance = snapshot.getValue(Attendance.class);
                        if (attendance != null){
                            if (attendance.getId_user().equals(mUser.getUid())){
                                if (attendance.getStatus() ==1){
                                    holder.item_cell_calendar.setCardBackgroundColor(Color.GREEN);
                                }
                            }
                        }

                    }else {
                        holder.item_cell_calendar.setCardBackgroundColor(Color.RED);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }


    }

}
