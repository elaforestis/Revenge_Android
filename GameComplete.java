package com.example.orestis.myapplication;


import android.app.Activity;

import android.app.ProgressDialog;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;

import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.UnknownHostException;
import java.util.regex.Pattern;


public class GameComplete extends Activity implements View.OnClickListener {

    Button replay;
    Button frequest;
    String serverReply;
    String[] players;
    String opponentUser;
    boolean replayRequested = false;
    ProgressDialog pd;
    boolean opponentRequestedReplay = false;
    replayChecker rc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_game_complete);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        opponentUser = getIntent().getStringExtra("opponentUsername").split(Pattern.quote("("))[0].trim();
        frequest = (Button)findViewById(R.id.friendRequest);
        replay = (Button)findViewById(R.id.replay);
        TextView gameCompleteText1 = (TextView)findViewById(R.id.textView);
        TextView winLoss = (TextView)findViewById(R.id.winLoss);
        String serverReply = getIntent().getStringExtra("Server reply").replace("gameresult:","");
        if(serverReply.contains("Νίκη! ")){
            winLoss.setText("Νίκη!");
            gameCompleteText1.setText(Html.fromHtml(serverReply.replace("Νίκη! ","")));
        }else if(serverReply.contains("Ήττα... ")){
            winLoss.setText("Ήττα...");
            gameCompleteText1.setText(Html.fromHtml(serverReply.replace("Ήττα... ","")));
        }else if(serverReply.contains("Ισοπαλία! ")){
            winLoss.setText("Ισοπαλία!");
            gameCompleteText1.setText(Html.fromHtml(serverReply.replace("Ισοπαλία! ","")));
        }
        Button button  = (Button)findViewById(R.id.button2);
        button.setOnClickListener(this);
        frequest.setOnClickListener(this);
        replay.setOnClickListener(this);
        rc = new replayChecker();
        rc.execute();

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private class FriendRequest extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            try {
                PrintWriter printwriter = new PrintWriter(SocketHandler.getSocket().getOutputStream(), true);
                printwriter.println("add_friend:"+getIntent().getStringExtra("opponentUsername")); // write the message to output stream
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
            Toast toast = Toast.makeText(getApplicationContext(), "Αίτημα φιλίας αποστάλθηκε", Toast.LENGTH_SHORT);
            toast.show();
        }
    }
    public void onBackPressed() {
        replayDecline rd = new replayDecline();
        rd.execute();
        moveTaskToBack(true);

    }
    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.button2:
                replayDecline rd = new replayDecline();
                rd.execute();
                Intent i = new Intent(this, gameHomeScreen.class);
                startActivity(i);
                finish();
                break;
            case R.id.friendRequest:

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
                        requestFriends rf = new requestFriends();
                        rf.execute();
                        pd = ProgressDialog.show(this, "", "Παρακαλώ περιμένετε...");
                    }

                break;
            case R.id.replay:
                PrintWriter printwriter2 = null;
                try {
                    printwriter2 = new PrintWriter(SocketHandler.getSocket().getOutputStream(),true);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(SocketHandler.getSocket()!=null) {
                    try {
                        printwriter2 = new PrintWriter(SocketHandler.getSocket().getOutputStream(), true);
                        printwriter2.println("conn dead?");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                if(!opponentRequestedReplay) {

                    if(printwriter2==null || printwriter2.checkError() || SocketHandler.getSocket()==null){
                        Toast toast = Toast.makeText(getApplicationContext(), "Πρόβλημα σύνδεσης στο διαδίκτυο", Toast.LENGTH_SHORT);
                        toast.show();
                    }else {
                        replayGame rg = new replayGame();
                        rg.execute();
                    }

                }else{


                    if(printwriter2==null || printwriter2.checkError() || SocketHandler.getSocket()==null){
                        Toast toast = Toast.makeText(getApplicationContext(), "Πρόβλημα σύνδεσης στο διαδίκτυο", Toast.LENGTH_SHORT);
                        toast.show();
                    }else {
                        replayAccept ra = new replayAccept();
                        ra.execute();
                        Intent k = new Intent(GameComplete.this, InGame.class);
                        k.putExtra("serverReply", getIntent().getStringExtra("serverReply"));
                        startActivity(k);
                        finish();
                    }
                }
                break;
        }
    }
    private class replayGame extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            try {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        replay.setText("Αναμονη...");
                        replay.getBackground().setColorFilter(0xFF00FF00, PorterDuff.Mode.MULTIPLY);
                    }
                });

                PrintWriter printwriter = new PrintWriter(SocketHandler.getSocket().getOutputStream(), true);
                printwriter.println("request_replay"); // write the message to output stream
                replayRequested=true;
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
    private class requestFriends extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            try {
                PrintWriter printwriter = new PrintWriter(SocketHandler.getSocket().getOutputStream(), true);
                printwriter.println("request_friends"); // write the message to output stream

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
    private class replayChecker extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            try {
                InputStreamReader inputStreamReader = new InputStreamReader(SocketHandler.getSocket().getInputStream());
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                do {
                    serverReply = bufferedReader.readLine();
                }while(serverReply==null || !(serverReply.contains("replay") || serverReply.contains("friends") || serverReply.equals("666")));
                if (serverReply.equals("replay"))
                    opponentRequestedReplay = true;

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
            if (serverReply != null) {
                if (serverReply.equals("replay")) {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            Drawable bg = getResources().getDrawable(android.R.drawable.btn_default);
                            bg.setColorFilter(0xFF00FF00, PorterDuff.Mode.MULTIPLY);
                            replay.setBackgroundDrawable(bg);
                        }
                    });
                    if(replayRequested){
                        Log.i("test1","yep im here");
                        replayBothAccept ra = new replayBothAccept();
                        ra.execute();
                    }
                } else if (serverReply.equals("replay_accepted")) {
                    replayAccepted ra = new replayAccepted();
                    ra.execute();
                    Intent i = new Intent(GameComplete.this, InGame.class);
                    i.putExtra("serverReply", getIntent().getStringExtra("serverReply"));
                    startActivity(i);
                    finish();
                } else if (serverReply.equals("replay_declined")) {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            if (opponentRequestedReplay) {
                                replay.setText("ΑΠΠΟΡΙΦΘΗΚΕ");
                            }
                            Drawable bg = getResources().getDrawable(android.R.drawable.btn_default);
                            bg.setColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY);
                            replay.setBackgroundDrawable(bg);
                            replay.setOnClickListener(null);
                        }
                    });

                }else if(serverReply.contains("friends:")){
                    serverReply = serverReply.replace("friends:","");
                    String[] playersPoints;
                    if(!serverReply.contains("~%~*")){
                        playersPoints = new String[1];
                        playersPoints[0] = serverReply;
                    }else {
                        playersPoints = serverReply.split(Pattern.quote("~%~*"));
                    }
                    players = new String[playersPoints.length];

                    for(int i = 0;i<playersPoints.length;i++){
                        players[i] = playersPoints[i].split(Pattern.quote("=========="))[0];
                    }

                    boolean alreadyFriend=false;
                    for(int k =0;k<players.length;k++){
                        if(opponentUser.equalsIgnoreCase(players[k])){
                            pd.dismiss();
                            Toast toast = Toast.makeText(getApplicationContext(), "Αυτός ο παίκτης είναι ήδη στους φίλους σας", Toast.LENGTH_SHORT);
                            toast.show();
                            alreadyFriend=true;
                        }
                    }
                    if(!alreadyFriend) {
                        FriendRequest fr = new FriendRequest();
                        fr.execute();
                    }
                    rc = new replayChecker();
                    rc.execute();
                }
            }
        }
    }
    private class replayAccept extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            try {
                PrintWriter printwriter = new PrintWriter(SocketHandler.getSocket().getOutputStream(), true);
                printwriter.println("replay_accept"); // write the message to output stream


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
    private class replayBothAccept extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            try {
                PrintWriter printwriter = new PrintWriter(SocketHandler.getSocket().getOutputStream(), true);
                printwriter.println("replay_both_accept"); // write the message to output stream


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
            Intent k = new Intent(GameComplete.this, InGame.class);
            k.putExtra("serverReply", getIntent().getStringExtra("serverReply"));
            startActivity(k);
            finish();
        }
    }
    private class replayAccepted extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            try {
                PrintWriter printwriter = new PrintWriter(SocketHandler.getSocket().getOutputStream(), true);
                printwriter.println("replay_accepted"); // write the message to output stream


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
    private class replayDecline extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            try {
                PrintWriter printwriter = new PrintWriter(SocketHandler.getSocket().getOutputStream(), true);
                printwriter.println("replay_decline"); // write the message to output stream

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
}
