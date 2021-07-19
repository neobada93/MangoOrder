package com.todo.order.view.splash;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.todo.order.MainActivity;
import com.todo.order.R;
import com.todo.order.config.MangoPreferences;
import com.todo.order.view.login.LoginActivity;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;


import java.util.ArrayList;

public class SplashActivity extends AppCompatActivity {

    private MangoPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        pref = new MangoPreferences(this);

        PermissionListener permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                new Handler().postDelayed(() -> {
                    boolean isLogin = pref.getBoolean("isLogin",false);

                    if(isLogin) {
                        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                }, 500);
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                finish();
            }
        };

        TedPermission.with(SplashActivity.this)
                .setPermissionListener(permissionListener)
                .setDeniedMessage("권한을 거부하면 앱을 사용할 수 없습니다.\n\n[설정] - [권한]에서 권한을 켜십시오.")
                .setPermissions(android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        android.Manifest.permission.READ_EXTERNAL_STORAGE,
                        android.Manifest.permission.CAMERA,
                        android.Manifest.permission.VIBRATE,
                        android.Manifest.permission.INTERNET,
                        android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION
                )
                .check();
    }
}