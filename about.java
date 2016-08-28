package com.example.orestis.myapplication;


import android.app.Activity;

import android.graphics.Color;

import android.os.Bundle;

import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;

public class about extends Activity {
FrameLayout fl;
    ImageView about;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_about);

        fl = (FrameLayout)findViewById(R.id.fl);
        about = (ImageView)findViewById(R.id.imageView);

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        about.setImageResource(R.drawable.about);
        fl.setBackgroundResource(R.drawable.menu);
    }

    @Override
    protected void onStop() {
        super.onStop();
        fl.setBackgroundDrawable(null);
        fl.setBackgroundColor(Color.parseColor("#67d4ff"));
        about.setImageDrawable(null);
    }
}
