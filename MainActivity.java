package com.example.orestis.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Base64;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;


public class MainActivity extends Activity implements View.OnTouchListener {

    ImageView image,imagemap;
    FrameLayout fl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        image = (ImageView)findViewById(R.id.image);
        imagemap=(ImageView)findViewById(R.id.image_areas);
        fl = (FrameLayout)findViewById(R.id.my_frame);
        image.setOnTouchListener(this);

        SharedPreferences prefs = getSharedPreferences("revengePrefs", MODE_PRIVATE);
        String username;
        byte[] password;
        String nomos;
        String perioxi;
        username = prefs.getString("username", "No username found");//"No name defined" is the default value.
        password = Base64.decode(prefs.getString("password", "invalid"), Base64.DEFAULT);
        nomos = prefs.getString("nomos","σφαλμα");
        perioxi = prefs.getString("perioxi","σφαλμα");

        if(!username.equals("No username found")){
            Intent i = new Intent(this,gameHomeScreen.class);
            i.putExtra("username",username);
            i.putExtra("password",password);
            i.putExtra("nomos",nomos);
            i.putExtra("perioxi",perioxi);
            i.putExtra("justAutoLogged",true);
            startActivity(i);
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Display display = getWindowManager().getDefaultDisplay();
        int width = display.getWidth();  // deprecated
        int height = display.getHeight();  // deprecated
        if(height/(float)width>=1.6 && height/(float)width<=1.8) {
            imagemap.setImageResource(R.drawable.startmenumap16x9);
            image.setImageResource(R.drawable.startmenu16x9);
        }else{
            imagemap.setImageResource(R.drawable.startmenumap);
            image.setImageResource(R.drawable.startmenu);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        image.setImageDrawable(null);
        imagemap.setImageDrawable(null);
        fl.setBackgroundColor(Color.parseColor("#67d4ff"));
    }

    public void onLoginClick(){
        Intent i = new Intent(this, login.class);
        startActivity(i);
    }
    public void onSignupClick(){
        Intent i = new Intent(this, signup.class);
        startActivity(i);
    }
    public void onAboutClick(){
        Intent i =new Intent(this,about.class);
        startActivity(i);
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
                        onSignupClick();
                    } else if (ct.closeMatch(Color.YELLOW, touchColor, tolerance)) {
                        onLoginClick();
                    }else if(ct.closeMatch(Color.BLACK,touchColor,tolerance)){
                        onAboutClick();
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
}
