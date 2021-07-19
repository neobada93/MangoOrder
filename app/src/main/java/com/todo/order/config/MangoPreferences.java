package com.todo.order.config;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class MangoPreferences {
    private final String PREF_NAME = "mango.pref";

    public final static String PREF_INTRO_USER_AGREEMENT = "PREF_USER_AGREEMENT";
    public final static String PREF_MAIN_VALUE = "PREF_MAIN_VALUE";
    public final static String GBN_LOGIN = "1";
    public final static String GBN_MAIN = "2";
    public final static String GBN_ORDER = "3";
    public final static String GBN_ITEMS = "4";
    public final static String GBN_ORDER_LIST = "5";

    public final static String GBN_NOTICE_LIST = "1";

    public final static String GBN_ORDER_LAST = "1";
    public final static String GBN_ORDER_LAST_DETAIL = "2";

    public final static String GBN_ORDER_SEND = "3";

    public final static String GBN_TALK_SEND = "2";
    public final static String GBN_TALK_LIST = "2";

    public final static String GBN_SELF_CHECK_SEND = "2";
    public final static String GBN_SELF_CHECK_LIST = "1";

    public final static String GBN_FAVORITES_LIST = "7";
    public final static String GBN_FAVORITES_SAVE = "8";

    public final static String GBN_ORDER_STATUS_LIST = "9";
    public final static String GBN_ORDER_STATUS_DETAIL = "10";

    public final static String GBN_ORDER_CANCEL = "11";


    static Context mContext;

    public MangoPreferences(Context c) {
        mContext = c;
    }

    // Preferences 초기화
    public void removeData(){
        SharedPreferences pref = mContext.getSharedPreferences(PREF_NAME,
                Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        editor.clear();
        editor.commit();
    }

    // Preferences 개별 삭제
    public void resetData(String key){
        SharedPreferences pref = mContext.getSharedPreferences(PREF_NAME,
                Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        editor.remove(key);
        editor.commit();
    }

    // Preferences String
    public void putString(String key, String value) {
        SharedPreferences pref = mContext.getSharedPreferences(PREF_NAME,
                Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        editor.putString(key, value);
        editor.commit();
    }

    // Preferences Boolean
    public void putBoolean(String key, boolean value) {
        SharedPreferences pref = mContext.getSharedPreferences(PREF_NAME,
                Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        editor.putBoolean(key, value);
        editor.commit();
    }

    // Preferences Integer
    public void putInt(String key, int value) {
        SharedPreferences pref = mContext.getSharedPreferences(PREF_NAME,
                Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        editor.putInt(key, value);
        editor.commit();
    }

    // Preferences getString
    public String getString(String key, String dftValue) {
        SharedPreferences pref = mContext.getSharedPreferences(PREF_NAME,
                Activity.MODE_PRIVATE);

        try {
            return pref.getString(key, dftValue);
        } catch (Exception e) {
            return dftValue;
        }

    }

    // Preferences getInteger
    public int getInt(String key, int dftValue) {
        SharedPreferences pref = mContext.getSharedPreferences(PREF_NAME,
                Activity.MODE_PRIVATE);

        try {
            return pref.getInt(key, dftValue);
        } catch (Exception e) {
            return dftValue;
        }

    }

    // Preferences getBoolean
    public boolean getBoolean(String key, boolean dftValue) {
        SharedPreferences pref = mContext.getSharedPreferences(PREF_NAME,
                Activity.MODE_PRIVATE);

        try {
            return pref.getBoolean(key, dftValue);
        } catch (Exception e) {
            return dftValue;
        }
    }
}
