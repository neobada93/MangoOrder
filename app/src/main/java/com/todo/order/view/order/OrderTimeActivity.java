package com.todo.order.view.order;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.todo.order.R;
import com.todo.order.view.common.BasicActivity;

public class OrderTimeActivity extends BasicActivity implements View.OnClickListener {

    private TextView tvOrderEnableTime;
    private ImageButton ibClose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_time);

        tvOrderEnableTime = findViewById(R.id.tv_order_enable_time);
        ibClose = findViewById(R.id.ib_close);

        tvOrderEnableTime.setText(getIntent().getStringExtra("EXTRA_ORDER_TIME"));

        ibClose.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.ib_close:
                finish();
                break;
        }

    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}