package com.todo.order.view.common;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.todo.order.MainActivity;
import com.todo.order.R;
import com.todo.order.config.MangoPreferences;
import com.todo.order.view.notice.NoticeActivity;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CustomDialog extends Dialog {

    private Context mContext;
    private MangoPreferences pref;
    private String title, content;

    public CustomDialog(@NonNull Context context, String title, String content)
    {
        super(context);
        mContext = context;
        pref = new MangoPreferences(context);
        this.title = title;
        this.content = content;
    }

    private TextView tv_customdialog_close, tv_customdialog_detail, tv_customdialog_today;
    private TextView tv_customdialog_title, tv_customdialog_content;

    @Override
    protected  void onCreate(Bundle savedInstnaceState) {
        super.onCreate(savedInstnaceState);
        setContentView(R.layout.dialog_custom);

        tv_customdialog_close = findViewById(R.id.tv_customdialog_close);
        tv_customdialog_detail = findViewById(R.id.tv_customdialog_detail);
        tv_customdialog_today = findViewById(R.id.tv_customdialog_today);

        tv_customdialog_title = findViewById(R.id.tv_customdialog_title);
        tv_customdialog_content = findViewById(R.id.tv_customdialog_content);
        tv_customdialog_title.setText(title);
        tv_customdialog_content.setText(content);

        tv_customdialog_close.setOnClickListener(view -> dismiss());
        tv_customdialog_detail.setOnClickListener(view -> {
            // close & 새소식 열기
            Intent intent = new Intent(mContext, NoticeActivity.class);
            intent.putExtra("EXTRA_NOTICE_POSITION", "0");
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            mContext.startActivity(intent);
            dismiss();
        });
        tv_customdialog_today.setOnClickListener(view -> {
            // pref 작업 : 오늘보지않기
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            pref.putString("notice", sdf.format(new Date()));
            dismiss();
        });
    }
}
