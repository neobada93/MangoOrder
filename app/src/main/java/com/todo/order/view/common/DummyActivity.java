package com.todo.order.view.common;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.todo.order.R;

public class DummyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dummy);

        finish();
    }
}