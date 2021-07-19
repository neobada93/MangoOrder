package com.todo.order.view.common;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Paint;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.todo.order.util.BackPressCloseHandler;

public class BasicActivity extends AppCompatActivity {

    private static Typeface typeface;
    private BackPressCloseHandler mHandler;
    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);

        mHandler = new BackPressCloseHandler(this);

        Paint paint = new Paint( Paint.ANTI_ALIAS_FLAG ); // or
        paint.setAntiAlias( true );
        paint.setSubpixelText(true);

        if(typeface == null) {
            //typeface = Typeface.createFromAsset(this.getAssets(), "lropkebatangm.ttf");
        }
        setGlobalFont(getWindow().getDecorView());
    }

    private void setGlobalFont(View view) {
        if(view != null) {
            if(view instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup)view;
                int vgCnt = viewGroup.getChildCount();
                for(int i = 0; i<vgCnt; i++) {
                    View v = viewGroup.getChildAt(i);
                    if(v instanceof TextView) {
                        ((TextView) v).setTypeface(typeface);
                    } else if (v instanceof EditText) {
                        ((EditText) v).setTypeface(typeface);
                    } else if (v instanceof Button) {
                        ((Button) v).setTypeface(typeface);
                    }
                    setGlobalFont(v);
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        mHandler.onBackPressed();
    }
}