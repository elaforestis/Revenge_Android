package com.example.orestis.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;

import android.graphics.Color;
import android.os.Bundle;

import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;


public class selectHighscores extends Activity implements View.OnTouchListener{
    ImageView image,imagemap;
    FrameLayout fl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_highscores);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        image = (ImageView)findViewById(R.id.image);
        imagemap=(ImageView)findViewById(R.id.image_areas);
        fl = (FrameLayout)findViewById(R.id.my_frame);
        image.setOnTouchListener(this);


    }

    @Override
    public boolean onTouch (View v, MotionEvent ev) {
        final int action = ev.getAction();
        // (1)
        final int evX = (int) ev.getX();
        final int evY = (int) ev.getY();
                 int touchColor = getHotspotColor(R.id.image_areas, evX, evY);
        if(touchColor!=0) {
            ColorTool ct = new ColorTool();
            int tolerance = 25;
            // (3)
            switch (action) {
                case MotionEvent.ACTION_UP:
                    if (ct.closeMatch(Color.RED, touchColor, tolerance)) {
                        Intent i = new Intent(this, generalHighscores.class);
                        startActivity(i);
                    } else if (ct.closeMatch(Color.YELLOW, touchColor, tolerance)) {
                        Intent i = new Intent(this, highscores.class);
                        startActivity(i);
                    }
                    break;
            }
        }
        return true;
    }
    public int getHotspotColor (int hotspotId, int x, int y) {
        ImageView img = (ImageView) findViewById (hotspotId);
        img.setDrawingCacheEnabled(true);
        System.gc();
        Bitmap cache = img.getDrawingCache();
        Bitmap hotspots = Bitmap.createBitmap(cache);
        img.setDrawingCacheEnabled(false);
        int pix = hotspots.getPixel(x,y);
        hotspots.recycle();
        hotspots = null;
        cache.recycle();
        cache = null;
        return pix;
    }

    @Override
    protected void onResume() {
        super.onResume();
        fl.setBackgroundResource(R.drawable.menu_nocactus);
        imagemap.setImageResource(R.drawable.cactusmap);
        image.setImageResource(R.drawable.cactus);
    }

    @Override
    protected void onStop() {
        super.onStop();
        fl.setBackgroundDrawable(null);
        image.setImageDrawable(null);
        imagemap.setImageDrawable(null);
        fl.setBackgroundColor(Color.parseColor("#67d4ff"));
    }
}
