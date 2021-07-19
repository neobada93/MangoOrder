package com.todo.order.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class myCalendarData {
    private int startday, currentmonth, currentyear, dayofweek;
    private String stringDayofWeek;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("E");
    private Calendar calendar;

    public myCalendarData(int start, String startDate) {
        this.calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, Integer.parseInt(startDate.substring(0, 4)));
        calendar.set(Calendar.MONTH, Integer.parseInt(startDate.substring(4, 6)) - 1);
        calendar.set(Calendar.DATE, Integer.parseInt(startDate.substring(6, 8)));

        calendar.add(Calendar.DATE, start);



        setThis();
    }

    private void setThis() {
        this.startday = calendar.get(Calendar.DAY_OF_MONTH);
        this.currentmonth = calendar.get(Calendar.MONTH) + 1;
        this.currentyear = calendar.get(Calendar.YEAR);
        this.dayofweek = calendar.get(Calendar.DAY_OF_WEEK);
        this.stringDayofWeek = dateFormat.format(calendar.getTime());
    }

    public void getNextWeekDay(int nxt) {
        calendar.add(Calendar.DATE, nxt);
        setThis();
    }

    public String getWeekDay() { return this.stringDayofWeek; }

    public int getYear() { return this.currentyear; }
    public int getMonth() { return this.currentmonth; }
    public int getDay() { return this.startday; }

}
