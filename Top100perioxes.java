package com.example.orestis.myapplication;

import android.app.Activity;

import android.app.ProgressDialog;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;

import android.view.Gravity;

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

public class Top100perioxes  extends Activity
{
    TableLayout table_layout;
    Socket socket;
    ProgressDialog pd;
    PrintWriter printwriter;
    BufferedReader bufferedReader;
    boolean notInTop100 = false;
    String serverReply;
    String points[];
    String perioxi[];
    ScrollView sv;
    String myPerioxi;
    String myPerioxiRank;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top100perioxes);
        table_layout = (TableLayout) findViewById(R.id.top100perioxestable);
        sv=(ScrollView)findViewById(R.id.sv);
        socket = SocketHandler.getSocket();
        pd = ProgressDialog.show(this, "", "Φόρτωση top περιοχών...");
        requestTop100 rt = new requestTop100();
        rt.execute();

        SharedPreferences prefs = getSharedPreferences("revengePrefs", MODE_PRIVATE);
        myPerioxi = prefs.getString("perioxi", "No username found");//"No name defined" is the default value.
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
                            if(i!=100) {
                                tv0.setText(Integer.valueOf(i + 1).toString());
                            }else{
                                tv0.setText(myPerioxiRank);
                            }
                            tv0.setGravity(Gravity.CENTER);
                            row.addView(tv0);
                            break;
                        case 1:
                            TextView tv = new TextView(this);
                            tv.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                                    TableRow.LayoutParams.WRAP_CONTENT));
                            tv.setPadding(5, 5, 5, 5);
                            tv.setTextSize(15);
                            if(i!=100) {
                                tv.setText(perioxi[i]);
                            }else{
                                tv.setText(myPerioxi);
                            }
                            tv.setGravity(Gravity.CENTER);
                            row.addView(tv);
                            break;
                        case 2:
                            TextView tv2 = new TextView(this);
                            tv2.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                                    TableRow.LayoutParams.WRAP_CONTENT));
                            tv2.setPadding(5, 5, 5, 5);
                            tv2.setText(points[i] + "%");
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
                            tv.setText("Περιοχή");
                            tv.setGravity(Gravity.CENTER);
                            row.addView(tv);
                            break;
                        case 2:
                            TextView tv2 = new TextView(this);
                            tv2.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                                    TableRow.LayoutParams.WRAP_CONTENT));
                            tv2.setPadding(5, 5, 5, 5);
                            tv2.setText("Εκδίκηση");
                            tv2.setGravity(Gravity.CENTER);
                            row.addView(tv2);
                            break;
                    }
                }
            }

            if(i!=-1) {
                if(perioxi[i].equalsIgnoreCase(myPerioxi)){
                    row.setBackgroundResource(R.drawable.row_border_user);
                }else {
                    row.setBackgroundResource(R.drawable.row_border);
                }
            }
            row.setTag(i);
            table_layout.addView(row);

        }
    }



    private class requestTop100 extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            try {
                printwriter = new PrintWriter(socket.getOutputStream(), true);
                printwriter.println("request_top100perioxes"); // write the message to output stream

                InputStreamReader inputStreamReader = new InputStreamReader(socket.getInputStream());
                bufferedReader = new BufferedReader(inputStreamReader);
                do {
                    serverReply = bufferedReader.readLine();
                }while(serverReply==null || !serverReply.contains("top100perioxes:"));

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
            serverReply = serverReply.replace("top100perioxes:","");
            String[] perioxesPoints = serverReply.split(Pattern.quote("-*-"));
            if(perioxesPoints.length==101){
                notInTop100=true;
                myPerioxiRank = perioxesPoints[100].split("666")[0];
            }


                perioxi = new String[perioxesPoints.length];
                points = new String[perioxesPoints.length];

                for(int i = 0;i<perioxesPoints.length;i++){
                    if(i!=100) {
                        perioxi[i] = perioxesPoints[i].split(Pattern.quote("-$-"))[0];
                        points[i] = perioxesPoints[i].split(Pattern.quote("-$-"))[1];
                    }else{
                        perioxi[i] = myPerioxi;
                        points[i] = perioxesPoints[i].split("666")[1];
                    }
                }



            table_layout.removeAllViews();
            BuildTable(perioxesPoints.length, 3);
            pd.dismiss();
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