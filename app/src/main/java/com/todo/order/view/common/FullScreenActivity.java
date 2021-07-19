package com.todo.order.view.common;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.todo.order.R;

public class FullScreenActivity extends BasicActivity {

    private PhotoView myImage;
    private String url = "";
    private FloatingActionButton fab_close;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen);

        fab_close = (FloatingActionButton) findViewById(R.id.fab_close);

        url = getIntent().getStringExtra("image_url");

        myImage = findViewById(R.id.myImage);
        Glide.with(this).load(url)
                .into(myImage);

        fab_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    @Override
    public void onBackPressed() {
        finish();
    }
}