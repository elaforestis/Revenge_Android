package com.example.orestis.myapplication;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.graphics.Color;
import android.os.AsyncTask;

import android.os.Bundle;

import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.regex.Pattern;

public class onNotificationClick extends Activity {

    String adder;
    ProgressDialog pd;
    FrameLayout fl;
    String challenger;
    Socket socket;
    PrintWriter printwriter;
    BufferedReader bufferedReader;
    String serverReply;
    String challengerNomos;
    String challengerPerioxi;
    String challengerAvatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_on_notification_click);

        fl=(FrameLayout)findViewById(R.id.fl);

        socket = SocketHandler.getSocket();

        if(SocketHandler.notificationMode){
            if(SocketHandler.notificationCode==1) {
                adder= SocketHandler.notificationer;


                final AlertDialog.Builder dlgAlert = new AlertDialog.Builder(this);
                dlgAlert.setMessage("Αποδοχή αιτήματος φιλίας απο τον/την " + adder + ";");
                dlgAlert.setTitle("Αίτημα φιλίας");
                dlgAlert.setCancelable(false);
                AlertDialog alert;
                dlgAlert.setPositiveButton("Οχι",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        });
                dlgAlert.setNegativeButton("Ναι",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                confirmFRequest cfr = new confirmFRequest();
                                cfr.execute();
                                finish();
                            }
                        });
                alert = dlgAlert.create();
                alert.show();
                SocketHandler.notificationMode=false;
            }else if(SocketHandler.notificationCode==2){

                String ns = Context.NOTIFICATION_SERVICE;
                NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);
                mNotificationManager.cancel(2);


                final AlertDialog.Builder dlgAlert = new AlertDialog.Builder(this);
                dlgAlert.setMessage("Αποδοχή πρόκλησης απο τον/την " + SocketHandler.notificationer + ";");
                dlgAlert.setTitle("Πρόκληση");
                dlgAlert.setCancelable(false);
                AlertDialog alert;
                dlgAlert.setPositiveButton("Οχι",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                challenger = SocketHandler.notificationer;
                                declineChallenge dc = new declineChallenge();
                                dc.execute();
                                finish();
                            }
                        });
                dlgAlert.setNegativeButton("Ναι",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                pd = ProgressDialog.show(onNotificationClick.this, "", "Παρακαλώ περιμένετε...");
                                challenger = SocketHandler.notificationer;
                                acceptChallenge ac = new acceptChallenge();
                                ac.execute();
                            }
                        });
                alert = dlgAlert.create();
                alert.show();
                SocketHandler.notificationMode=false;
            }
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }
    private class declineChallenge extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            try {
                printwriter = new PrintWriter(socket.getOutputStream(), true);
                printwriter.println("challenge_decline:"+challenger); // write the message to output stream
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
        }
    }
    public void goInChallenge(String target){
        if(pd!=null){
            pd.dismiss();
        }
        Intent i = new Intent(this, InGame.class);
        i.putExtra("serverReply", target+" ("+challengerNomos+"-"+challengerPerioxi+")"+"-$-"+challengerAvatar);
        startActivity(i);
        finish();
    }
    private class acceptChallenge extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            try {
                printwriter = new PrintWriter(socket.getOutputStream(), true);
                printwriter.println("challenge_accept:"+challenger); // write the message to output stream
                InputStreamReader inputStreamReader = new InputStreamReader(socket.getInputStream());
                bufferedReader = new BufferedReader(inputStreamReader);
                do {
                    try {
                        serverReply = bufferedReader.readLine();
                        if(serverReply!=null) {
                            if (serverReply.contains("challengerperioxi:")) {
                                challengerNomos = serverReply.replace("challengerperioxi:", "").split(Pattern.quote("-$-"))[0].split("666")[0];
                                challengerPerioxi = serverReply.replace("challengerperioxi:", "").split(Pattern.quote("-$-"))[0].split("666")[1];
                                challengerAvatar = serverReply.replace("challengerperioxi:", "").split(Pattern.quote("-$-"))[1];
                            }
                        }
                    }catch(Exception e){
                        runOnUiThread(new Runnable(){

                            @Override
                            public void run() {
                                pd.dismiss();
                                Toast toast = Toast.makeText(getApplicationContext(), "Πρόβλημα σύνδεσης με το διαδίκτυο", Toast.LENGTH_SHORT);
                                toast.show();
                                finish();
                            }
                        });
                        break;
                    }
                }while(serverReply==null || !serverReply.contains("challenge_"));
                if(serverReply.equals("challenge_on")){
                    goInChallenge(challenger);
                }else if(serverReply.equals("challenge_off")){
                    runOnUiThread(new Runnable(){

                        @Override
                        public void run() {
                            pd.dismiss();
                            Toast toast = Toast.makeText(getApplicationContext(), "Ο/Η "+ challenger+" ακύρωσε την πρόκληση.", Toast.LENGTH_SHORT);
                            toast.show();
                            finish();

                        }
                    });
                }
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
        }
    }
    private class confirmFRequest extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            try {
                PrintWriter printwriter = new PrintWriter(socket.getOutputStream(), true);
                printwriter.println("accept_friend:"+adder); // write the message to output stream
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
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        fl.setBackgroundResource(R.drawable.menu);
    }

    @Override
    protected void onStop() {
        super.onStop();
        fl.setBackgroundDrawable(null);
        fl.setBackgroundColor(Color.parseColor("#67d4ff"));
    }
}
