package com.example.orestis.myapplication;


import android.app.Activity;

import android.app.ProgressDialog;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;

import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;

import android.text.Html;

import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import java.net.Socket;
import java.net.UnknownHostException;
import java.util.regex.Pattern;

public class profileGeneral extends Activity implements View.OnClickListener {
    public Socket socket;
    private TextView username;
    private TextView profileHTML;
    private PrintWriter printwriter;
    private BufferedReader bufferedReader;
    RelativeLayout rl;
    public static String serverReply;
    Button challenge;
    Button settings;
    Button chat;
    public static  Intent chatIntent;
    ProgressDialog dialog1;

    boolean challengeCancelled = false;
    String challengeTarget;
    ProgressDialog pd;
    public static String requestedUser;
    String targetNomos;
    String targetPerioxi;
    String targetAvatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_profile_general);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        rl=(RelativeLayout)findViewById(R.id.rl);

        challenge = (Button)findViewById(R.id.challenge);
        chat = (Button)findViewById(R.id.chat);
        settings = (Button)findViewById(R.id.settings);
        socket = gameHomeScreen.socket;
        username =(TextView)findViewById(R.id.username);
        Typeface font = Typeface.createFromAsset(getAssets(), "duality.ttf");
        username.setTypeface(font);
        SharedPreferences prefs = getSharedPreferences("revengePrefs", MODE_PRIVATE);
        username.setText(prefs.getString("username", "No username found").substring(0, 1).toUpperCase() + prefs.getString("username", "No username found").substring(1));
        profileHTML = (TextView)findViewById(R.id.profileHtml);
        pd=ProgressDialog.show(this, "", "Φόρτωση προφιλ...");
        if(profile.requestedUser!=null){
            settings.setVisibility(View.INVISIBLE);
            requestedUser = profile.requestedUser;
            username.setText(requestedUser.substring(0, 1).toUpperCase() + requestedUser.substring(1));
            requestUserProfile rup = new requestUserProfile();
            rup.execute();
            challenge.setOnClickListener(this);
            chat.setOnClickListener(this);
            profile.requestedUser=null;
        }else {
            settings.setOnClickListener(this);
            chat.setVisibility(View.INVISIBLE);
            challenge.setVisibility(View.INVISIBLE);
            requestProfile rp = new requestProfile();
            rp.execute();
        }
        //username.setTextSize(TypedValue.COMPLEX_UNIT_PX, 150f);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.challenge:
                PrintWriter printwriter = null;
                try {
                    printwriter = new PrintWriter(SocketHandler.getSocket().getOutputStream(),true);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(SocketHandler.getSocket()!=null) {
                    try {
                        printwriter = new PrintWriter(SocketHandler.getSocket().getOutputStream(), true);
                        printwriter.println("conn dead?");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                if(printwriter==null || printwriter.checkError() || SocketHandler.getSocket()==null){
                    Toast toast = Toast.makeText(getApplicationContext(), "Πρόβλημα σύνδεσης στο διαδίκτυο", Toast.LENGTH_SHORT);
                    toast.show();
                }else {

                    dialog1 = new ProgressDialog(profileGeneral.this);
                    dialog1.setMessage("Αναμονή απάντησης...");
                    dialog1.setCancelable(false);
                    dialog1.setButton(DialogInterface.BUTTON_NEGATIVE, "Ακύρωση", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            cancelChallenge cc = new cancelChallenge();
                            cc.execute();
                            challengeCancelled=true;
                            dialog1.dismiss();
                        }
                    });
                    dialog1.show();
                    challengeTarget =  requestedUser;
                    if(!challengeTarget.equalsIgnoreCase(gameHomeScreen.username)) {
                        challengeRequest cr = new challengeRequest();
                        cr.execute();
                    }else{

                        dialog1.dismiss();
                        Toast toast = Toast.makeText(getApplicationContext(),"Σφάλμα!",Toast.LENGTH_LONG);
                        toast.show();
                    }
                    break;
                }
                break;
            case R.id.chat:
                if(Integer.valueOf(android.os.Build.VERSION.SDK)>=11) {
                    chatIntent = new Intent(this, Chat.class);
                    chatIntent.putExtra("chatUser", username.getText().toString());
                    startActivity(chatIntent);
                }else{
                    Toast.makeText(this,"Requires API 11+",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.settings:
                Intent i = new Intent(this, Settings.class);
                startActivity(i);
                break;
        }
    }

    private class requestUserProfile extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            try {
                printwriter = new PrintWriter(socket.getOutputStream(), true);
                printwriter.println("request_profile:"+requestedUser); // write the message to output stream

                InputStreamReader inputStreamReader = new InputStreamReader(socket.getInputStream());
                bufferedReader = new BufferedReader(inputStreamReader);
                do {
                    serverReply = bufferedReader.readLine();
                }while(serverReply==null || !serverReply.contains("profile:"));
                runOnUiThread(new Runnable(){

                    @Override
                    public void run() {
                        profileHTML.setText(Html.fromHtml(serverReply.split(Pattern.quote("profileDAILY:"))[0].replace("profile:", "")));
                    }
                });



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
            pd.dismiss();
        }
    }
    private class requestProfile extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            try {
                printwriter = new PrintWriter(socket.getOutputStream(), true);
                printwriter.println("request_profile"); // write the message to output stream

                InputStreamReader inputStreamReader = new InputStreamReader(socket.getInputStream());
                bufferedReader = new BufferedReader(inputStreamReader);
                do {
                    serverReply = bufferedReader.readLine();
                }while(serverReply==null || !serverReply.contains("profile:"));
                runOnUiThread(new Runnable(){

                    @Override
                    public void run() {
                        profileHTML.setText(Html.fromHtml(serverReply.split(Pattern.quote("profileDAILY:"))[0].replace("profile:","")));
                    }
                });



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
            pd.dismiss();
        }
    }

    private class cancelChallenge extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            try {
                printwriter = new PrintWriter(socket.getOutputStream(), true);
            } catch (IOException e) {
                e.printStackTrace();
            }
            printwriter.println("challenge_cancelled:"+challengeTarget); // write the message to output stream
            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }
    private class challengeRequest extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            try {
                printwriter = new PrintWriter(socket.getOutputStream(), true);
                printwriter.println("challenge:"+challengeTarget); // write the message to output stream

                InputStreamReader inputStreamReader = new InputStreamReader(socket.getInputStream());
                bufferedReader = new BufferedReader(inputStreamReader);
                do {
                    try {
                        serverReply = bufferedReader.readLine();
                        if(serverReply.contains("challengedperioxi:")){
                            targetNomos = serverReply.replace("challengedperioxi:","").split(Pattern.quote("-$-"))[0].split("666")[0];
                            targetPerioxi = serverReply.replace("challengedperioxi:","").split(Pattern.quote("-$-"))[0].split("666")[1];
                            targetAvatar = serverReply.replace("challengedperioxi:","").split(Pattern.quote("-$-"))[1];
                        }
                    }catch(Exception e){
                        runOnUiThread(new Runnable(){

                            @Override
                            public void run() {
                                dialog1.dismiss();
                                Toast toast = Toast.makeText(getApplicationContext(), "Πρόβλημα σύνδεσης με το διαδίκτυο", Toast.LENGTH_SHORT);
                                toast.show();
                            }
                        });
                        break;
                    }
                }while(!serverReply.contains("challenge_"));
                if(!challengeCancelled) {
                    if (serverReply.equals("challenge_accept")) {
                        printwriter.println("going_in"); // write the message to output stream
                        goInChallenge(challengeTarget);
                    } else if (serverReply.equals("challenge_decline")) {
                        if(dialog1!=null) {
                            dialog1.dismiss();
                        }
                        runOnUiThread(new Runnable(){

                            @Override
                            public void run() {
                                Toast toast = Toast.makeText(getApplicationContext(), "Η πρόκληση σας απορρίφθηκε", Toast.LENGTH_SHORT);
                                toast.show();
                            }
                        });
                    }
                }else{
                    challengeCancelled =false;
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
    public void goInChallenge(String target){

        if(dialog1!=null) {
            dialog1.dismiss();
        }
        if(pd!=null){
            pd.dismiss();
        }
        Intent i = new Intent(this, InGame.class);
        i.putExtra("serverReply",target+" ("+targetNomos+"-"+targetPerioxi+")"+"-$-"+targetAvatar);
        SocketHandler.setSocket(socket);
        startActivity(i);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        rl.setBackgroundResource(R.drawable.menu);
    }

    @Override
    protected void onStop() {
        super.onStop();
        rl.setBackgroundDrawable(null);
        rl.setBackgroundColor(Color.parseColor("#67d4ff"));
    }
}
