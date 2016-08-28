package com.example.orestis.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ScrollView;
import java.net.Socket;


public class otherHs extends Activity {

    Button perioxiSelect,nomosSelect;
    Socket socket;
    ScrollView sv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_other_hs);
        perioxiSelect = (Button) findViewById(R.id.perioxiSelect);
        sv = (ScrollView)findViewById(R.id.sv);
        nomosSelect = (Button) findViewById(R.id.nomosSelect);
        socket = SocketHandler.getSocket();

        perioxiSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(otherHs.this,ExpandableListMainActivity.class);
                startActivity(i);
                ExpandableListMainActivity.signup = false;
                ExpandableListMainActivity.requestType = "livePerioxi";
            }
        });
        nomosSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExpandableListMainActivity.requestType = "liveNomos";
                Intent i = new Intent(otherHs.this,ExpandableListMainActivity.class);
                startActivity(i);
                ExpandableListMainActivity.signup = false;
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        sv.setBackgroundResource(R.drawable.menu_op);
    }

    @Override
    protected void onStop() {
        super.onStop();
        sv.setBackgroundDrawable(null);
        sv.setBackgroundColor(Color.parseColor("#67d4ff"));
    }

}
