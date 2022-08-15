package com.example.may.AdapterManager;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.may.Adapter.CalendarAdapter;
import com.example.may.Interface.OnItemClick;
import com.example.may.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public class CalendarManagerAdapter  extends RecyclerView.Adapter<CalendarManagerAdapter.CalendarViewholder>{
    private List<String> arrDayOfMonth;
    private OnItemClick onItemClick;
    private LocalDate localDate;
    public CalendarManagerAdapter(List<String> arrDayOfMonth, OnItemClick onItemClick, LocalDate localDate) {
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

    @Override
    public void onBindViewHolder(@NonNull CalendarViewholder holder, int position) {
        String day = arrDayOfMonth.get(position);
        holder.dayOfMonth.setText(day);

        holder.item_cell_calendar.setCardBackgroundColor(Color.parseColor("#efefefef"));
        if (!day.equals("")){
            if (Integer.parseInt(day) == Integer.parseInt(new SimpleDateFormat("dd").format(new Date()))){
                holder.item_cell_calendar.setCardBackgroundColor(Color.GREEN);
            }
        }
        holder.item_cell_calendar.setOnClickListener(v -> {
            if (!day.equals("")){
                onItemClick.itemClick(day);
            }
        });
    }

    @Override
    public int getItemCount() {
        return  arrDayOfMonth.size();
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

}
