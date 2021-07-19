package com.todo.order.view.common;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.todo.order.R;

public class OwnerInfoDialog extends Dialog {
    private static Typeface typeface;
    private Context mContext;
    int type = 0;
    TextView tv_owner_info_bnum, tv_owner_info_addr, tv_owner_info_tel, tv_owner_info_manager;
    private String info_bnum, info_addr, info_tel, info_manager;

    public OwnerInfoDialog(@NonNull Context context) {
        super(context);
        mContext = context;
    }

    public OwnerInfoDialog(@NonNull Context context, int type) {
        super(context);
        this.type = type;
    }

    public OwnerInfoDialog(@NonNull Context context, String info_bnum, String info_addr, String info_tel, String info_manager) {
        super(context);
        this.info_bnum = info_bnum;
        this.info_addr = info_addr;
        this.info_tel = info_tel;
        this.info_manager = info_manager;
    }

    protected OwnerInfoDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_owner_info);

        tv_owner_info_bnum = findViewById(R.id.tv_owner_info_bnum);
        tv_owner_info_addr = findViewById(R.id.tv_owner_info_addr);
        tv_owner_info_tel = findViewById(R.id.tv_owner_info_tel);
        tv_owner_info_manager = findViewById(R.id.tv_owner_info_manager);
        tv_owner_info_bnum.setText(info_bnum);
        tv_owner_info_addr.setText(info_addr);
        tv_owner_info_tel.setText(info_tel);
        tv_owner_info_manager.setText(info_manager);
    }
}
