package com.todo.order.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MyCalendar {

    private String day; //week of day
    private String date, month, year;
    private int pos;

    public MyCalendar() {

    }

    public MyCalendar(String day, String date, String month, String year, int i) {
        this.day = day;
        this.date = date;
        this.month = month;
        this.year = year;
        this.pos = i;
    }

    private String getMonthStr(String month) {
        Calendar cal = Calendar.getInstance();

        SimpleDateFormat month_date = new SimpleDateFormat("MMM");
        int monthnum = Integer.parseInt(month);
        cal.set(Calendar.MONTH, monthnum);
        String month_name = month_date.format(cal.getTime());
        return month_name;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }
}
