package com.todo.order.view.order.additem;

import android.os.Bundle;
import android.view.View;

import com.todo.order.R;
import com.todo.order.view.common.BasicActivity;

public class AddItemDialog extends BasicActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void onClick(View view) {

    }
}