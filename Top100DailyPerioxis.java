package com.example.orestis.myapplication;

import android.app.Activity;

import android.app.ProgressDialog;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;

import android.view.Gravity;
import android.view.View;

import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.regex.Pattern;

public class Top100DailyPerioxis  extends Activity
{
    TableLayout table_layout;
    Socket socket;
    ProgressDialog pd;
    PrintWriter printwriter;
    BufferedReader bufferedReader;
    String serverReply;
    String players[];
    String points[];
    String friends[];
    String[] playersPoints;
    ScrollView sv;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top100_daily_perioxis);
        table_layout = (TableLayout) findViewById(R.id.top100Dailytable);
        sv = (ScrollView)findViewById(R.id.sv);
        socket = SocketHandler.getSocket();
        pd = ProgressDialog.show(this, "", "Φόρτωση top 100...");




        serverReply = MyExpandableAdapter.serverReply.replace("replyTop100PerioxiDaily:", "");
        playersPoints = serverReply.split(Pattern.quote("-*-"));

        players = new String[playersPoints.length];
        points = new String[playersPoints.length];

        for(int i = 0;i<playersPoints.length;i++){
            players[i] = playersPoints[i].split(Pattern.quote("-$-"))[0];
            points[i] = playersPoints[i].split(Pattern.quote("-$-"))[1];
        }
        table_layout.removeAllViews();
        pd.dismiss();
        requestFriends rf = new requestFriends();
        rf.execute();
    }

    private void BuildTable(int rows, int cols) {

        // outer for loop
        for (int i = -1; i < rows; i++) {

            TableRow row = new TableRow(this);
            row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT));
            if(i!=-1) {
                for (int j = 0; j < cols; j++) {
                    switch (j) {
                        case 0:
                            TextView tv0 = new TextView(this);
                            tv0.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                                    TableRow.LayoutParams.WRAP_CONTENT));
                            tv0.setPadding(5, 5, 5, 5);
                            tv0.setTextSize(25);
                            tv0.setText(Integer.valueOf(i + 1).toString());
                            tv0.setGravity(Gravity.CENTER);
                            row.addView(tv0);
                            break;
                        case 1:
                            TextView tv = new TextView(this);
                            tv.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                                    TableRow.LayoutParams.WRAP_CONTENT));
                            tv.setPadding(5, 5, 5, 5);
                            tv.setTextSize(15);
                            tv.setText(players[i]);
                            tv.setGravity(Gravity.CENTER);
                            row.addView(tv);
                            break;
                        case 2:
                            TextView tv2 = new TextView(this);
                            tv2.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                                    TableRow.LayoutParams.WRAP_CONTENT));
                            tv2.setPadding(5, 5, 5, 5);
                            tv2.setText(points[i]);
                            tv2.setTextSize(15);
                            tv2.setGravity(Gravity.CENTER);
                            row.addView(tv2);
                            break;
                    }
                }
            }else{
                for (int j = 0; j < cols; j++) {
                    switch (j) {
                        case 0:
                            TextView tv0 = new TextView(this);
                            tv0.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                                    TableRow.LayoutParams.WRAP_CONTENT));
                            tv0.setPadding(5, 5, 5, 5);
                            tv0.setText("#");
                            tv0.setGravity(Gravity.CENTER);
                            row.addView(tv0);
                            break;
                        case 1:
                            TextView tv = new TextView(this);
                            tv.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                                    TableRow.LayoutParams.WRAP_CONTENT));
                            tv.setPadding(5, 5, 5, 5);
                            tv.setText("Χρήστης");
                            tv.setGravity(Gravity.CENTER);
                            row.addView(tv);
                            break;
                        case 2:
                            TextView tv2 = new TextView(this);
                            tv2.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                                    TableRow.LayoutParams.WRAP_CONTENT));
                            tv2.setPadding(5, 5, 5, 5);
                            tv2.setText("Πόντοι");
                            tv2.setGravity(Gravity.CENTER);
                            row.addView(tv2);
                            break;
                    }
                }
            }

            if(i!=-1) {
                if(players[i].equalsIgnoreCase(gameHomeScreen.username)){
                    row.setBackgroundResource(R.drawable.row_border_user);
                }else {
                    row.setBackgroundResource(R.drawable.row_border);
                }
                for(int j = 0;j<friends.length;j++){
                    if(players[i].equalsIgnoreCase(friends[j])){
                        row.setBackgroundResource(R.drawable.row_border_friends);
                        break;
                    }
                }
            }
            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TableRow tr = (TableRow)v;
                    if(!players[(int)tr.getTag()].equalsIgnoreCase("null")) {
                        Intent k = new Intent(Top100DailyPerioxis.this, profile.class);
                        k.putExtra("requestUser", players[(int) tr.getTag()]);
                        startActivity(k);
                    }
                }
            });
            row.setTag(i);
            table_layout.addView(row);

        }
    }





    private class requestFriends extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            try {
                PrintWriter printwriter = new PrintWriter(SocketHandler.getSocket().getOutputStream(), true);
                printwriter.println("request_friends"); // write the message to output stream

                InputStreamReader inputStreamReader = new InputStreamReader(SocketHandler.getSocket().getInputStream());
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                do {
                    serverReply = bufferedReader.readLine();
                }while(serverReply==null || !serverReply.contains("friends:"));


            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            serverReply = serverReply.replace("friends:","");
            String[] playersPointss;
            if(!serverReply.contains("~%~*")){
                playersPointss = new String[1];
                playersPointss[0] = serverReply;
            }else {
                playersPointss = serverReply.split(Pattern.quote("~%~*"));
            }
            friends = new String[playersPointss.length];

            for(int i = 0;i<playersPointss.length;i++){
                friends[i] = playersPointss[i].split(Pattern.quote("=========="))[0];
            }
            BuildTable(playersPoints.length, 3);
        }
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