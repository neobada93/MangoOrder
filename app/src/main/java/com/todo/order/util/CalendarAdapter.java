package com.todo.order.util;

import android.graphics.Color;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.todo.order.R;

import java.util.Calendar;
import java.util.List;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.MyViewHolder> {

    private SparseBooleanArray mSelectedItems = new SparseBooleanArray(0);
    private Calendar c = Calendar.getInstance();

    private List<MyCalendar> mCalendar;
    private int recyclecount=0;
    private Integer today = c.get(Calendar.DAY_OF_MONTH);
    private String strToday = String.valueOf(today);
    private boolean flagToday = false;
    private boolean flagMonth = false;

    private String ym = "";

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tb_day, tb_date;
        private View v_underline;

        public MyViewHolder(View view) {
            super(view);
            tb_day = (TextView) view.findViewById(R.id.day_1);
            tb_date = (TextView) view.findViewById(R.id.date_1);
            v_underline = (View) view.findViewById(R.id.order_underline);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int position = getAdapterPosition();
                    flagToday = true;

                    mSelectedItems.put(position, true);
                    v.setBackgroundResource(R.drawable.shape_rect_calendar);
                    tb_day.setTextColor(Color.BLACK);
                    tb_date.setTextColor(Color.BLACK);
                    v_underline.setVisibility(View.VISIBLE);

                }
            });
        }
    }

    public CalendarAdapter(List<MyCalendar> mCalendar) {
        this.mCalendar = mCalendar;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.date_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        MyCalendar calendar = mCalendar.get(position);

        holder.tb_day.setText(calendar.getDay());
        holder.tb_date.setText(calendar.getDate());

        if ( mSelectedItems.get(position, false) || calendar.getDate().equals(strToday) && !flagToday ) {
            mSelectedItems.put(position, false);
            holder.itemView.setBackgroundResource(R.drawable.shape_rect_calendar);
            holder.tb_day.setTextColor(Color.BLACK);
            holder.tb_date.setTextColor(Color.BLACK);
            holder.v_underline.setVisibility(View.VISIBLE);
        } else {
            holder.itemView.setBackgroundResource(0);
            holder.tb_day.setTextColor(Color.WHITE);
            holder.tb_date.setTextColor(Color.WHITE);
            holder.v_underline.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mCalendar.size();
    }

    @Override
    public void onViewRecycled (MyViewHolder holder){
        recyclecount++;
    }

    private void toggleItemSelected(int position) {
        if (mSelectedItems.get(position, false) == true) {
            mSelectedItems.delete(position);
            notifyItemChanged(position);
        } else {
            mSelectedItems.put(position, true);
            notifyItemChanged(position);
        }
    }

    private boolean isItemSelected(int position) {
        return mSelectedItems.get(position, false);
    }

    public void clearSelectedItem() {
        int position;
        flagToday = true;
        flagMonth = true;

        for (int i = 0; i < mSelectedItems.size(); i++) {
            position = mSelectedItems.keyAt(i);
            mSelectedItems.put(position, false);
            notifyItemChanged(position);
        }

        mSelectedItems.clear();
    }

    public String getYm() {
        return ym;
    }

    public void setYm(String ym) {
        this.ym = ym;
    }
}

