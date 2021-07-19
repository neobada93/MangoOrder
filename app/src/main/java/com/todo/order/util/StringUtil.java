package com.todo.order.util;

public class StringUtil {

    public static String LPAD(String value, char changer, int size) {
        for(int i=0; i < size - value.length(); i++){
            value = changer + value;
        }
        return value;
    }

    public static String nullValue(String str){
        return nullValue(str, "").trim();
    }

    public static String nullValue(Object obj) {
        return (obj == null) ? "" : obj.toString();
    }

    public static String nullValue(String str, String nullStr){
        if(str == null || str.length() == 0) return nullStr;
        else return str;
    }
}
