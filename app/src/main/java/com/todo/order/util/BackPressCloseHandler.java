package com.todo.order.util;

import android.app.Activity;
import android.webkit.CookieManager;
import android.widget.Toast;

public class BackPressCloseHandler {
    private long backKeyPressedTime = 0;
    private Toast toast;

    private Activity activity;

    public BackPressCloseHandler(Activity context) {
        this.activity = context;
    }

    public void onBackPressed() {
        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis();
            showGuide();
            return;
        }
        if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
            activity .moveTaskToBack(true);
            activity .finish();
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(0);
            toast.cancel();

            //캐쉬, 쿠키 삭제//
            clearApplicationCache(null);
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }

    private void showGuide() {
        toast = Toast.makeText(activity, "\'뒤로\'버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT);
        toast.show();
    }

    @SuppressWarnings("deprecation")
    private void clearApplicationCache(java.io.File dir){
        if(dir==null)
            dir = activity.getCacheDir();
        else;
        if(dir==null)
            return;
        else;
        java.io.File[] children = dir.listFiles();
        try{
            // 쿠키 삭제
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.removeSessionCookie();

            for(int i=0;i<children.length;i++)
                if(children[i].isDirectory())
                    clearApplicationCache(children[i]);
                else children[i].delete();
        }
        catch(Exception e){}
    }
}
