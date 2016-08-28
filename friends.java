package com.example.orestis.myapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TableRow.LayoutParams;
import android.view.View;
import android.view.WindowManager;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.regex.Pattern;

public class friends extends Activity {
    TableLayout table_layout;
    Socket socket;
    PrintWriter printwriter;
    BufferedReader bufferedReader;
    String serverReply;
    String[] players;
    String friendToBeDeleted;
    int rowToBeDeleted;
    String[] points;
    boolean[] logged;
    EditText useradded;
    Button sendFRequest;
    ProgressDialog pd;
    ScrollView sv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        sv = (ScrollView)findViewById(R.id.scrollView);

        table_layout = (TableLayout) findViewById(R.id.tableLayout1);
        useradded=(EditText)findViewById(R.id.editText2);
        sendFRequest=(Button)findViewById(R.id.send);
        sendFRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager inputMethodManager = (InputMethodManager)  friends.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(friends.this.getCurrentFocus().getWindowToken(), 0);
                boolean f = false;
                for(int i =0;i<players.length;i++){
                    if(useradded.getText().toString().equalsIgnoreCase(players[i])){
                        f = true;
                    }
                }
                if(!f) {
                    if(socket!=null) {
                        try {
                            printwriter = new PrintWriter(socket.getOutputStream(), true);
                            printwriter.println("conn dead?");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    if(printwriter==null || printwriter.checkError() || socket==null){
                        Toast toast = Toast.makeText(getApplicationContext(), "Πρόβλημα σύνδεσης στο διαδίκτυο", Toast.LENGTH_SHORT);
                        toast.show();
                    }else {
                        Toast toast = Toast.makeText(getApplicationContext(), "Αίτημα φιλίας αποστάλθηκε", Toast.LENGTH_SHORT);
                        toast.show();
                        FriendRequest fr = new FriendRequest();
                        fr.execute();
                    }
                }else{
                    Toast toast = Toast.makeText(getApplicationContext(), "Αυτός ο χρήστης είναι ήδη στη λίστα φίλων", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });
        socket = SocketHandler.getSocket();

        pd = ProgressDialog.show(this, "", "Φόρτωση φίλων...");
        requestFriends rf = new requestFriends();
        rf.execute();
    }
    private void BuildTable(int rows, int cols) {

        // outer for loop
        for (int i = -1; i < rows; i++) {

            TableRow row = new TableRow(this);
            row.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                    LayoutParams.WRAP_CONTENT));

            if(i!=-1) {
                for (int j = 0; j < cols; j++) {
                    switch (j) {

                        case 0:
                            TextView tv = new TextView(this);
                            tv.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
                                    LayoutParams.WRAP_CONTENT));
                            tv.setPadding(5, 5, 5, 5);
                            tv.setTextSize(25);
                            tv.setText(players[i]);
                            tv.setGravity(Gravity.CENTER);
                            row.addView(tv);
                            break;
                        case 1:
                            TextView tv2 = new TextView(this);
                            tv2.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
                                    LayoutParams.WRAP_CONTENT));
                            tv2.setPadding(5, 5, 5, 5);
                            tv2.setText(points[i]);
                            tv2.setTextSize(25);
                            tv2.setGravity(Gravity.CENTER);
                            row.addView(tv2);
                            break;
                        case 2:
/*                            CheckBox cb = new CheckBox(this);
                            LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                            lp.gravity = Gravity.CENTER;
                            cb.setLayoutParams(lp);
                            if(logged[i]) {
                                cb.setChecked(true);
                            }else{
                                cb.setChecked(false);
                            }
                            cb.setClickable(false);
                            row.addView(cb);*/
                            ImageView iv = new ImageView(this);
                            LayoutParams lp = new LayoutParams(Math.round((float)23 * getApplicationContext().getResources().getDisplayMetrics().density), Math.round((float)23 * getApplicationContext().getResources().getDisplayMetrics().density));
                            lp.gravity = Gravity.CENTER;
                            iv.setLayoutParams(lp);
                            if(logged[i]) {
                                iv.setImageResource(R.drawable.greendot);
                            }else{
                                iv.setImageResource(R.drawable.reddot);
                            }
                            row.addView(iv);
                            break;
                    }
                }
            }else{
                for (int j = 0; j < cols; j++) {
                    switch (j) {

                        case 0:
                            TextView tv = new TextView(this);
                            tv.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
                                    LayoutParams.WRAP_CONTENT));
                            tv.setPadding(5, 5, 5, 5);
                            tv.setText("Χρήστης");
                            tv.setGravity(Gravity.CENTER);
                            row.addView(tv);
                            break;
                        case 1:
                            TextView tv2 = new TextView(this);
                            tv2.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
                                    LayoutParams.WRAP_CONTENT));
                            tv2.setPadding(5, 5, 5, 5);
                            tv2.setText("Πόντοι");
                            tv2.setGravity(Gravity.CENTER);
                            row.addView(tv2);
                            break;
                        case 2:
                            TextView tv3 = new TextView(this);
                            tv3.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
                                    LayoutParams.WRAP_CONTENT));
                            tv3.setPadding(5, 5, 5, 5);
                            tv3.setText("Ενεργός");
                            tv3.setGravity(Gravity.CENTER);
                            row.addView(tv3);
                            break;
                    }
                }
            }

            if(i!=-1) {
                row.setBackgroundResource(R.drawable.row_border);
            }
            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TableRow tr = (TableRow)v;
                    Intent k = new Intent(friends.this,profile.class);
                    k.putExtra("requestUser",players[(int)tr.getTag()]);
                    startActivity(k);
                }
            });
            row.setOnLongClickListener(new View.OnLongClickListener() {

                @Override
                public boolean onLongClick(View v) {
                    TableRow tr = (TableRow) v;
                    friendToBeDeleted = players[(int) tr.getTag()];
                    rowToBeDeleted = (int) tr.getTag();
                    AlertDialog.Builder dlgAlert = new AlertDialog.Builder(friends.this);
                    dlgAlert.setMessage("Είστε σίγουρος/η οτι θέλετε να διαγράψετε τον/την " + players[(int) tr.getTag()] + " απο τους φίλους σας;");
                    dlgAlert.setTitle("Διαγραφή φίλου");
                    dlgAlert.setCancelable(false);
                    AlertDialog alert;
                    dlgAlert.setPositiveButton("Οχι",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                    dlgAlert.setNegativeButton("Ναι",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    deleteFriend df = new deleteFriend();
                                    df.execute();
                                }
                            });
                    alert = dlgAlert.create();
                    alert.show();
                    return true;
                }
            });
            row.setTag(i);
            table_layout.addView(row);

        }
    }
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }
    private class deleteFriend extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            try {
                printwriter = new PrintWriter(socket.getOutputStream(), true);
                printwriter.println("friend_delete:"+friendToBeDeleted); // write the message to output stream
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
            table_layout.removeViewAt(rowToBeDeleted+1);
        }
    }
    private class FriendRequest extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            try {
                printwriter = new PrintWriter(socket.getOutputStream(), true);
                printwriter.println("add_friend:"+useradded.getText().toString()); // write the message to output stream
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
                printwriter = new PrintWriter(socket.getOutputStream(), true);
                printwriter.println("request_friends"); // write the message to output stream

                InputStreamReader inputStreamReader = new InputStreamReader(socket.getInputStream());
                bufferedReader = new BufferedReader(inputStreamReader);
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
            Log.i("serverReplyFriends",serverReply);
            serverReply = serverReply.replace("friends:","");
            String[] playersPoints;
            if(!serverReply.contains("~%~*")){
                playersPoints = new String[1];
                playersPoints[0] = serverReply;
            }else {
                playersPoints = serverReply.split(Pattern.quote("~%~*"));
            }
            players = new String[playersPoints.length];
            points = new String[playersPoints.length];
            logged = new boolean[playersPoints.length];

            for(int i = 0;i<playersPoints.length;i++){
                players[i] = playersPoints[i].split(Pattern.quote("=========="))[0];
                points[i] = playersPoints[i].split(Pattern.quote("=========="))[1];
                logged[i] = Boolean.parseBoolean(playersPoints[i].split(Pattern.quote("=========="))[2]);
            }

            table_layout.removeAllViews();
            if(!serverReply.equals("==========0==========false")) { //zero friends
                BuildTable(playersPoints.length, 3);
            }
            pd.dismiss();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        sv.setBackgroundResource(R.drawable.menu);
    }

    @Override
    protected void onStop() {
        super.onStop();
        sv.setBackgroundDrawable(null);
        sv.setBackgroundColor(Color.parseColor("#67d4ff"));
    }
}
