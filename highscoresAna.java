package com.example.orestis.myapplication;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TextView;

public class highscoresAna extends TabActivity
{
    LinearLayout ll;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_highscores_ana);

        ll=(LinearLayout)findViewById(R.id.LinearLayout01);

        // create the TabHost that will contain the Tabs
        TabHost tabHost = (TabHost)findViewById(android.R.id.tabhost);


        TabHost.TabSpec tab1 = tabHost.newTabSpec("First Tab");

        // Set the Tab name and Activity
        // that will be opened when particular Tab will be selected
        if(ExpandableListMainActivity.requestType.equals("livePerioxi")) {
            tab1.setIndicator("Daily Top 100: " + MyExpandableAdapter.perioxiOnlySelected);
            tab1.setContent(new Intent(this, Top100DailyPerioxis.class));
        }else if(ExpandableListMainActivity.requestType.equals("generalPerioxi")){
            tab1.setIndicator("General Top 100: " + MyExpandableAdapter.perioxiOnlySelected);
            tab1.setContent(new Intent(this, Top100Perioxis.class));
        }else if(ExpandableListMainActivity.requestType.equals("liveNomos")){
            tab1.setIndicator("Daily Top 100: " + ExpandableListMainActivity.nomosSelected);
            tab1.setContent(new Intent(this, Top100DailyNomou.class));
        }else if(ExpandableListMainActivity.requestType.equals("generalNomos")){
            tab1.setIndicator("General Top 100: " + ExpandableListMainActivity.nomosSelected);
            tab1.setContent(new Intent(this, Top100Nomou.class));
        }


        /** Add the tabs  to the TabHost to display. */
        tabHost.addTab(tab1);

        int tabCount = tabHost.getTabWidget().getTabCount();
        for (int i = 0; i < tabCount; i++) {
            final View view = tabHost.getTabWidget().getChildTabViewAt(i);
            if ( view != null ) {
                // reduce height of the tab
                //view.getLayoutParams().height *= 0.66;

                //  get title text view
                final View textView = view.findViewById(android.R.id.title);
                if ( textView instanceof TextView ) {
                    // just in case check the type

                    // center text
                    ((TextView) textView).setGravity(Gravity.CENTER);
                    // wrap text
                    ((TextView) textView).setSingleLine(false);

                    // explicitly set layout parameters
                    textView.getLayoutParams().height = ViewGroup.LayoutParams.FILL_PARENT;
                    textView.getLayoutParams().width = ViewGroup.LayoutParams.WRAP_CONTENT;
                }
            }
        }

    }
} 