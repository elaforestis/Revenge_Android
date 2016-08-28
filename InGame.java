package com.example.orestis.myapplication;


import android.app.Activity;
import android.app.AlertDialog;

import android.app.ProgressDialog;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.SystemClock;
import android.text.Html;
import android.util.DisplayMetrics;

import android.view.View;
import android.view.WindowManager;
import android.widget.AbsoluteLayout;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class InGame extends Activity implements View.OnClickListener {
    RelativeLayout rl;
    private final int turns = 1;
    ImageView target;
    int height;
    int width;
    Chronometer time;
    long timeStarted,timeNow;
    int clicks;
    int finalScore;
    public static Socket socket;
    TextView clicksText;
    TextView scoreText;
    private PrintWriter printwriter;
    BufferedReader bufferedReader;
    public static String serverReply ="";
    ProgressDialog pd;
    CountDownTimer cdt;
    TextView countdownText;
    TextView timer;
    int widthPixels;
    int heightPixels;
    CounterClass theTimer;
    boolean gamefinished = false;
    public static AbsoluteLayout layout;
    AbsoluteLayout.LayoutParams params;
    double phoneInches;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_in_game);
        rl = (RelativeLayout)findViewById(R.id.rl);
        Intent i = getIntent();
        int avatars = Integer.parseInt(i.getExtras().getString("serverReply").split(Pattern.quote("-$-"))[1]);
        switch(avatars){
            case 11:
                rl.setBackgroundResource(R.drawable.mvm_day);
                break;
            case 12:
                rl.setBackgroundResource(R.drawable.mvf_day);
                break;
            case 21:
                rl.setBackgroundResource(R.drawable.fvm_day);
                break;
            case 22:
                rl.setBackgroundResource(R.drawable.fvf_day);
                break;
        }
        layout = (AbsoluteLayout) findViewById(R.id.al);
        socket = SocketHandler.getSocket();

        clicks=0;

        DisplayMetrics display  =this.getResources().getDisplayMetrics();
        height = display.heightPixels;
        width = display.widthPixels;


        clicksText = (TextView) findViewById(R.id.clicks);
        countdownText = (TextView) findViewById(R.id.countdownText);
        countdownText.setText(Html.fromHtml("<font color=green>"+gameHomeScreen.username+" ("+gameHomeScreen.nomos+"-"+gameHomeScreen.perioxi+")<br>"+"</font><font color = red> VS </font><br><font color = green>"+i.getExtras().getString("serverReply").split(Pattern.quote("-$-"))[0]+"</font>"));
        countdownText.setTypeface(null, Typeface.BOLD);
        timer = (TextView) findViewById(R.id.timer);
        timer.setText("4");
        theTimer = new CounterClass(4000,100);
        scoreText = (TextView) findViewById(R.id.score);
        //find inches
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        widthPixels = metrics.widthPixels;
        heightPixels = metrics.heightPixels;
        float widthDpi = metrics.xdpi;
        float heightDpi = metrics.ydpi;
        float widthInches = widthPixels / widthDpi;
        float heightInches = heightPixels / heightDpi;
        phoneInches = Math.sqrt((widthInches * widthInches) + (heightInches * heightInches));
        //

        //initial randomised position
        Random r = new Random();
/*        int x = r.nextInt(width-(int)((13*phoneInches*getResources().getDisplayMetrics().density)+0.5));
        int y = r.nextInt(height-(int)((13*phoneInches*getResources().getDisplayMetrics().density)+0.5));
        params = new AbsoluteLayout.LayoutParams((int)((13*phoneInches*getResources().getDisplayMetrics().density)+0.5), (int)((13*phoneInches*getResources().getDisplayMetrics().density)+0.5), x, y);*/
        int x = r.nextInt(width-(int)((widthPixels*0.2)+0.5));
        int y = r.nextInt(height-(int)((heightPixels*0.2)+0.5));
        params = new AbsoluteLayout.LayoutParams((int)((widthPixels*0.2)+0.5), (int)((widthPixels*0.2)+0.5), x, y);
        //initial randomised position
        time = (Chronometer) findViewById(R.id.chronometer);
        theTimer.start();

        target = new ImageView(this);
        target.setImageResource(R.drawable.cross);
        target.setLayoutParams(params);
        target.setBackgroundColor(Color.TRANSPARENT);


    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

    }
    public void onBackPressed() {
        //do nothing
    }


    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        if(!gamefinished) {
            Toast toast = Toast.makeText(getApplicationContext(), "Εγκατάλειψη παιχνιδιού σε 10 δευτερόλεπτα!", Toast.LENGTH_SHORT);
            toast.show();
            cdt = new CountDownTimer(10000, 1000) {

                public void onTick(long millisUntilFinished) {
                    //
                }

                public void onFinish() {
                    finalScore = -1;
                    SendMessage sm = new SendMessage();
                    sm.execute();
                    finish();
                    System.exit(0);
                }
            }.start();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(cdt!=null) {
            cdt.cancel();
        }
    }

    @Override
    public void onClick(View v) {
        clicks++;
        if (clicks<turns) {
            clicksText.setText("Clicks: "+clicks);
            Random r = new Random();
/*            int x = r.nextInt(width - (int)((13*phoneInches*getResources().getDisplayMetrics().density)+0.5));
            int y = r.nextInt(height - (int)((13*phoneInches*getResources().getDisplayMetrics().density)+0.5));
            AbsoluteLayout.LayoutParams params2 = new AbsoluteLayout.LayoutParams((int)((13*phoneInches*getResources().getDisplayMetrics().density)+0.5),(int)((13*phoneInches*getResources().getDisplayMetrics().density) + 0.5), x, y);*/
            int x = r.nextInt(width-(int)((widthPixels*0.2)+0.5));
            int y = r.nextInt(height-(int)((heightPixels*0.2)+0.5));
            AbsoluteLayout.LayoutParams params2 = new AbsoluteLayout.LayoutParams((int)((widthPixels*0.2)+0.5), (int)((widthPixels*0.2)+0.5), x, y);
            target.setLayoutParams(params2);
            scoreText.bringToFront();
            clicksText.bringToFront();
        }else{
            time.stop();
            clicksText.setText("Στόχοι: " + clicks);
            target.setVisibility(View.INVISIBLE);
            finalScore = Integer.parseInt(((String) scoreText.getText()).replace("Σκορ: ", "").replace("ms", ""));
                pd = ProgressDialog.show(InGame.this, "", "Περιμέντε να τελειώσει ο αντίπαλός σας...");
                SendMessage sm = new SendMessage();
                sm.execute();
        }
    }



    public void goGameComplete(){
        pd.dismiss();
        Intent i = new Intent(this, GameComplete.class);
        Intent j = getIntent();
        i.putExtra("Server reply",serverReply);
        i.putExtra("serverReply",j.getStringExtra("serverReply"));
        i.putExtra("opponentUsername",j.getExtras().getString("serverReply").split(Pattern.quote("-$-"))[0]);
        startActivity(i);
        finish();
    }


    private class SendMessage extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            try {
                printwriter = new PrintWriter(socket.getOutputStream(), true);
                printwriter.println(finalScore); // write the message to output stream
                InputStreamReader inputStreamReader = new InputStreamReader(socket.getInputStream(),"UTF8");
                bufferedReader = new BufferedReader(inputStreamReader);
                gamefinished = true;
                do {
                    try {
                        serverReply = bufferedReader.readLine();
                    }catch(Exception e){
                        try {
                            pd.dismiss();
                        }catch(NullPointerException npe){
                            npe.printStackTrace();
                        }
                        runOnUiThread(new Runnable(){

                            @Override
                            public void run() {
                                final AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(InGame.this);
                                dlgAlert.setMessage("Πρόβλημα σύνδεσης στο διαδίκτυο. Η εφαρμογή θα κλείσει.");
                                dlgAlert.setTitle("Αποτυχία σύνδεσης");
                                dlgAlert.setCancelable(false);
                                AlertDialog alert = null;
                                final AlertDialog finalAlert = alert;
                                dlgAlert.setPositiveButton("Ok",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                //finalAlert.dismiss();
                                                System.exit(0);
                                            }
                                        });
                                alert = dlgAlert.create();
                                alert.show();
                            }
                        });
                        serverReply=null;
                        break;
                    }
                    if(serverReply==null && finalScore!=-1){
                        try {
                            pd.dismiss();
                        }catch (NullPointerException npe){
                            npe.printStackTrace();
                        }
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                final AlertDialog.Builder dlgAlert = new AlertDialog.Builder(InGame.this);
                                dlgAlert.setMessage("Πρόβλημα σύνδεσης στο διαδίκτυο. Η εφαρμογή θα κλείσει.");
                                dlgAlert.setTitle("Αποτυχία σύνδεσης");
                                dlgAlert.setCancelable(false);
                                AlertDialog alert = null;
                                final AlertDialog finalAlert = alert;
                                dlgAlert.setPositiveButton("Ok",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                //finalAlert.dismiss();
                                                System.exit(0);
                                            }
                                        });
                                alert = dlgAlert.create();
                                alert.show();
                            }
                        });
                        break;
                    }
                }while(serverReply!=null && !serverReply.contains("gameresult") && finalScore!=-1);
                if(serverReply!=null && finalScore!=-1) {
                    goGameComplete();
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

    public class CounterClass extends CountDownTimer{
        boolean flag = true;
        public CounterClass(long millisInFuture, long countDownInterval){
            super(millisInFuture,countDownInterval);
        }

        public void onTick(long millisUntilFinished){
            String hms = String.valueOf(TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished)-TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)));
            if(Integer.parseInt(hms)==0 && flag){
                timer.setVisibility(View.INVISIBLE);
                timer.setText("0");
                countdownText.setVisibility(View.INVISIBLE);
                rapidFire();
                flag =false;
            }else
            timer.setText(hms);
        }

        @Override
        public void onFinish() {

        }
    }
    public void rapidFire() {
        layout.addView(target);
        target.setOnClickListener(InGame.this);
        time.setBase(SystemClock.elapsedRealtime());
        time.start();
        timeStarted = System.currentTimeMillis();
        final Handler h = new Handler();
        Runnable updater = new Runnable(){
            @Override
            public void run() {
               if (clicks<turns) {
                   timeNow = System.currentTimeMillis();
                   scoreText.setText("Σκορ: " + (timeNow - timeStarted) + "ms");
                   if(timeNow-timeStarted>=60000){
                       gamefinished = true;
                       time.stop();
                       Toast.makeText(InGame.this,"Υπερβήκατε το χρονικό όριο των 60 δευτερολέπτων, χάσατε 200 πόντους.",Toast.LENGTH_LONG).show();
                       finalScore = -1;
                       SendMessage sm = new SendMessage();
                       sm.execute();
                       Intent i = new Intent(InGame.this, gameHomeScreen.class);
                       i.putExtra("justAutoLogged",true);
                       startActivity(i);
                       h.removeCallbacksAndMessages(null);
                       finish();
                       return;
                   }
                   h.postDelayed(this,30);
               }
            }
        };
        h.post(updater);
    }

    @Override
    protected void onStop() {
        super.onStop();
        rl.setBackgroundDrawable(null);
        rl.setBackgroundColor(Color.parseColor("#67d4ff"));
    }
}
