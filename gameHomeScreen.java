package com.example.orestis.myapplication;

import android.app.Activity;

import android.app.AlertDialog;

import android.app.ProgressDialog;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;

import android.graphics.Bitmap;
import android.graphics.Color;

import android.graphics.Point;
import android.os.AsyncTask;

import android.os.Bundle;

import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;



import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;

import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import java.nio.charset.Charset;
import java.util.regex.Pattern;




public class gameHomeScreen extends Activity implements View.OnTouchListener {

    //public static final String[] NOMOILOWERCASE = {"Αιτωλοακαρνανίας","Αργολίδας","Αρκαδίας","Άρτας","Αττικής","Αχαΐας","Βοιωτίας","Γρεβενών","Δράμας","Δωδεκανήσου","Εβοίας","Έβρου","Ευρυτανίας","Ζακύνθου","Ηλείας","Ημαθίας","Ηρακλείου","Θεσπρωτίας","Θεσσαλονίκης","Ιωαννίνων","Καβάλας","Καρδίτσας","Καστοριάς","Κέρκυρας","Κεφαλληνίας","Κιλκίς","Κοζάνης","Κορινθίας","Κυκλάδων","Λακωνίας","Λάρισας","Λασιθίου","Λέσβου","Λευκάδας","Μαγνησίας","Μεσσηνίας","Ξάνθης","Πέλλας","Πιερίας","Πρέβεζας","Ρεθύμνου","Ροδόπης","Σάμου","Σερρών","Τρικάλων","Φθιώτιδας","Φλώρινας","Φωκίδας","Χαλκιδικής","Χανίων","Χίου"};
    //public static final String[] NOMOI = {"ΑΙΤΩΛΟΑΚΑΡΝΑΝΙΑΣ", "ΑΡΓΟΛΙΔΑΣ", "ΑΡΚΑΔΙΑΣ", "ΑΡΤΑΣ", "ΑΤΤΙΚΗΣ", "ΑΧΑΙΑΣ", "ΒΟΙΩΤΙΑΣ", "ΓΡΕΒΕΝΩΝ", "ΔΡΑΜΑΣ", "ΔΩΔΕΚΑΝΗΣΟΥ", "ΕΒΟΙΑΣ", "ΕΒΡΟΥ", "ΕΥΡΥΤΑΝΙΑΣ", "ΖΑΚΥΝΘΟΥ", "ΗΛΕΙΑΣ", "ΗΜΑΘΙΑΣ", "ΗΡΑΚΛΕΙΟΥ", "ΘΕΣΠΡΩΤΙΑΣ", "ΘΕΣΣΑΛΟΝΙΚΗΣ", "ΙΩΑΝΝΙΝΩΝ", "ΚΑΒΑΛΑΣ", "ΚΑΡΔΙΤΣΑΣ", "ΚΑΣΤΟΡΙΑΣ", "ΚΕΡΚΥΡΑΣ", "ΚΕΦΑΛΛΗΝΙΑΣ", "ΚΙΛΚΙΣ", "ΚΟΖΑΝΗΣ", "ΚΟΡΙΝΘΙΑΣ", "ΚΥΚΛΑΔΩΝ", "ΛΑΚΩΝΙΑΣ", "ΛΑΡΙΣΑΣ", "ΛΑΣΙΘΙΟΥ", "ΛΕΣΒΟΥ", "ΛΕΥΚΑΔΑΣ", "ΜΑΓΝΗΣΙΑΣ", "ΜΕΣΣΗΝΙΑΣ", "ΞΑΝΘΗΣ", "ΠΕΛΛΑΣ", "ΠΙΕΡΙΑΣ", "ΠΡΕΒΕΖΑΣ", "ΡΕΘΥΜΝΟΥ", "ΡΟΔΟΠΗΣ", "ΣΑΜΟΥ", "ΣΕΡΡΩΝ", "ΤΡΙΚΑΛΩΝ", "ΦΘΙΩΤΙΔΑΣ", "ΦΛΩΡΙΝΑΣ", "ΦΩΚΙΔΑΣ", "ΧΑΛΚΙΔΙΚΗΣ", "ΧΑΝΙΩΝ", "ΧΙΟΥ"};
    public static Socket socket;
    //public static SSLSocket socket;
    public static Socket nSocket;
    public static String username;
    public static String perioxi;
    public static String nomos;
    public static byte[] password;


    //


    ImageView image,imagemap;
    FrameLayout fl;


    public String serverLogInMessage;
    ProgressDialog dialog1;
    private PrintWriter printwriter;
    private BufferedReader bufferedReader;
    String serverReply = "";
    SendMessage sm;
    challengeRequest cr;

    ProgressDialog dialog;
    ProgressDialog pd;
    //boolean queueCancelled = false;
    boolean attempt1 = true;
    boolean challengeCancelled = false;
    public static final String IP = "89.210.0.121";
    String challengeTarget;

    boolean queued = false;
    boolean challenging = false;
    String targetNomos;
    String targetPerioxi;
    String targetAvatar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_game_home_screen);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        image = (ImageView)findViewById(R.id.image);
        imagemap=(ImageView)findViewById(R.id.image_areas);
        fl = (FrameLayout)findViewById(R.id.my_frame);
        image.setOnTouchListener(this);



        final Intent i  = getIntent();
        SharedPreferences prefs = getSharedPreferences("revengePrefs", MODE_PRIVATE);
        if(i.getBooleanExtra("justAutoLogged",false)){
                    username = prefs.getString("username", "invalid");
                    password = Base64.decode(prefs.getString("password", "invalid"), Base64.DEFAULT);
                    nomos = prefs.getString("nomos", "σφαλμα");
                    perioxi = prefs.getString("perioxi", "σφαλμα");
                    login(username);
        }else if(i.getBooleanExtra("justLogged",false)){
                    if (login.client != null) {
                        socket = login.client;/////////////////////////////////////
                        SocketHandler.setSocket(socket);
                        username = login.usernameString;
                        password = Base64.decode(prefs.getString("password", "invalid"), Base64.DEFAULT);
                        nomos = prefs.getString("nomos","σφαλμα");
                        perioxi = prefs.getString("perioxi","σφαλμα");
                    } else if (signup.client != null) {
                        socket = signup.client;//////////////////////////////
                        SocketHandler.setSocket(socket);
                        username = signup.username.getText().toString();
                        password = Base64.decode(prefs.getString("password", "invalid"), Base64.DEFAULT);
                        nomos = prefs.getString("nomos", "σφαλμα");
                        perioxi = prefs.getString("perioxi", "σφαλμα");
                    }
        }else{
            socket = SocketHandler.getSocket();//////////////////////////////////////////
            try {
                printwriter = new PrintWriter(socket.getOutputStream(), true);
            } catch (IOException e) {
                e.printStackTrace();
            }
            username = prefs.getString("username","invalid");
            password = Base64.decode(prefs.getString("password", "invalid"), Base64.DEFAULT);
            nomos = prefs.getString("nomos", "σφαλμα");
            perioxi = prefs.getString("perioxi", "σφαλμα");

        }

        startService(new Intent(this, notificationManager.class));


    }


    public void login(String username){
        pd = ProgressDialog.show(gameHomeScreen.this, "", "Γίνεται σύνδεση...");
        serverLogInMessage = "LOGIN-$-"+username;
        connServer cs = new connServer();
        cs.execute();
    }



    @Override
    protected void onDestroy() {
        stopService(new Intent(this, notificationManager.class));
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        if(queued) {
            cancelQueue cq = new cancelQueue();
            cq.execute();
            //queueCancelled = true;
            if(dialog!=null) {
                dialog.dismiss();
            }
            sm.cancel(true);
        }
        if(challenging){
            cancelChallenge cc = new cancelChallenge();
            cc.execute();
            challengeCancelled=true;
            if(dialog1!=null){
                dialog1.dismiss();
            }
        }
        super.onPause();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    public void onBackPressed() {
        moveTaskToBack(true);
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
                    if (ct.closeMatch(Color.RED, touchColor, tolerance)) {  //queue

                        try {
                            printwriter = new PrintWriter(socket.getOutputStream(), true);
                            printwriter.println("conn dead?");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                        if(printwriter==null || printwriter.checkError() || socket==null){
                            attempt1=true;
                            Toast toast = Toast.makeText(getApplicationContext(), "Επανασύνδεση...", Toast.LENGTH_SHORT);
                            toast.show();
                            serverLogInMessage = "LOGIN-$-" + username;
                            reconAttempt ra = new reconAttempt();
                            ra.execute();
                        }else {
                            queued=true;
                            dialog = new ProgressDialog(gameHomeScreen.this);
                            dialog.setMessage("Γίνεται εύρεση αντιπάλου...");
                            dialog.setCancelable(false);
                            dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Ακύρωση", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    cancelQueue cq = new cancelQueue();
                                    cq.execute();
                                    //queueCancelled=true;
                                    dialog.dismiss();
                                    sm.cancel(true);
                                }
                            });
                            dialog.show();
                            sm = new SendMessage();
                            sm.execute();
                        }
                    } else if (ct.closeMatch(Color.YELLOW, touchColor, tolerance)) {    //hs

                        try {
                            printwriter = new PrintWriter(socket.getOutputStream(), true);
                            printwriter.println("conn dead?");

                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                        if(printwriter==null || printwriter.checkError() || socket==null){
                            Toast toast = Toast.makeText(getApplicationContext(), "Επανασύνδεση...", Toast.LENGTH_SHORT);
                            toast.show();
                            serverLogInMessage = "LOGIN-$-" + username;
                            reconAttempt ra = new reconAttempt();
                            ra.execute();
                        }else {
                            SocketHandler.setSocket(socket);
                            Intent hs = new Intent(this, selectHighscores.class);
                            startActivity(hs);
                        }
                    }else if(ct.closeMatch(Color.BLACK,touchColor,tolerance)){  //challenge
                        try {
                            printwriter = new PrintWriter(socket.getOutputStream(), true);
                            printwriter.println("conn dead?");

                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                        if(printwriter==null || printwriter.checkError() || socket==null){
                            Toast toast = Toast.makeText(getApplicationContext(), "Επανασύνδεση...", Toast.LENGTH_SHORT);
                            toast.show();
                            serverLogInMessage = "LOGIN-$-" + username;
                            reconAttempt ra = new reconAttempt();
                            ra.execute();
                        }else {
                            AlertDialog.Builder alert = new AlertDialog.Builder(this);
                            final EditText edittext= new EditText(this);
                            edittext.setHint("Εισάγετε username");
                            edittext.setGravity(Gravity.CENTER);
                            alert.setTitle("Πρόκληση παίκτη");

                            alert.setView(edittext);

                            alert.setPositiveButton("Άκυρο", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    //do nothing
                                }
                            });

                            alert.setNegativeButton("Πρόκληση", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    challenging=true;
                                    dialog1 = new ProgressDialog(gameHomeScreen.this);
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
                                    challengeTarget =  edittext.getText().toString();
                                    if(!challengeTarget.equalsIgnoreCase(username)) {
                                        cr = new challengeRequest();
                                        cr.execute();
                                    }else{
                                        challenging=false;
                                        dialog1.dismiss();
                                        Toast toast = Toast.makeText(getApplicationContext(),"Σφάλμα!",Toast.LENGTH_LONG);
                                        toast.show();
                                    }
                                }
                            });
                            alert.show();
                        }
                    }else if(ct.closeMatch(Color.WHITE,touchColor,tolerance)){  //friends
                        try {
                            printwriter = new PrintWriter(socket.getOutputStream(), true);
                            printwriter.println("conn dead?");

                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                        if(printwriter==null || printwriter.checkError() || socket==null){
                            Toast toast = Toast.makeText(getApplicationContext(), "Επανασύνδεση...", Toast.LENGTH_SHORT);
                            toast.show();
                            serverLogInMessage = "LOGIN-$-" + username;
                            reconAttempt ra = new reconAttempt();
                            ra.execute();
                        }else {
                            SocketHandler.setSocket(socket);
                            Intent fr = new Intent(this, friends.class);
                            startActivity(fr);
                        }
                    }else if(ct.closeMatch(Color.BLUE,touchColor,tolerance)){   //profile
                        try {
                            printwriter = new PrintWriter(socket.getOutputStream(), true);
                            printwriter.println("conn dead?");

                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                        if(printwriter==null || printwriter.checkError() || socket==null){
                            Toast toast = Toast.makeText(getApplicationContext(), "Επανασύνδεση...", Toast.LENGTH_SHORT);
                            toast.show();
                            serverLogInMessage = "LOGIN-$-" + username;
                            reconAttempt ra = new reconAttempt();
                            ra.execute();
                        }else {
                            Intent p = new Intent(this,profile.class);
                            startActivity(p);
                        }
                    }else if(ct.closeMatch(Color.GRAY,touchColor,tolerance)){   //about
                        Intent k  = new Intent(this,about.class);
                        startActivity(k);
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

    private class cancelQueue extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            try {
                printwriter = new PrintWriter(socket.getOutputStream(), true);
            } catch (IOException e) {
                e.printStackTrace();
            }
            printwriter.println("cancel_queue"); // write the message to output stream
            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            queued=false;
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
                                dialog.dismiss();
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
                        dialog1.dismiss();
                        runOnUiThread(new Runnable(){

                            @Override
                            public void run() {
                                Toast toast = Toast.makeText(getApplicationContext(), "Η πρόκληση σας απορρίφθηκε", Toast.LENGTH_SHORT);
                                toast.show();
                            }
                        });
                    }
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
    public void goInGame(){
        queued=false;
        dialog.dismiss();
        Intent i = new Intent(this, InGame.class);
        i.putExtra("serverReply",serverReply.split(":")[1]);
        SocketHandler.setSocket(socket);
        startActivity(i);
        finish();
    }
    public void goInChallenge(String target){
        challenging=false;
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
    private class reconAttempt extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            try {

                socket = new Socket(IP, 4444); // connect to the server/////////////////////////////////////////////////////////
                nSocket=new Socket(IP,4445);
                printwriter = new PrintWriter(new OutputStreamWriter(
                        socket.getOutputStream(), Charset.forName("UTF-8")), true);
                printwriter.println(serverLogInMessage); // write the message to output stream
                //send pass//
                DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                dos.writeInt(password.length);
                dos.write(password);
                //          //
                InputStreamReader inputStreamReader = new InputStreamReader(socket.getInputStream());
                bufferedReader = new BufferedReader(inputStreamReader);
                do {
                    serverReply = bufferedReader.readLine();
                }while(serverReply==null || !serverReply.equals("SUCCESS"));

            }catch(UnknownHostException e){
                e.printStackTrace();
                serverReply="";
            }catch(IOException e){
                e.printStackTrace();
                serverReply="";
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (serverReply.contains("toast:")) {
                Toast toast = Toast.makeText(getApplicationContext(), serverReply.replace("toast:",""), Toast.LENGTH_SHORT);
                toast.show();
            }else if(serverReply.equals("")){
                Toast toast = Toast.makeText(getApplicationContext(), "Προσπάθεια επανασύνδεσης ανεπιτυχής", Toast.LENGTH_SHORT);
                toast.show();
            }else{
                Toast toast = Toast.makeText(getApplicationContext(), "Προσπάθεια επανασύνδεσης επιτυχής!", Toast.LENGTH_SHORT);
                //queueCancelled=false;
                toast.show();
                SocketHandler.setSocket(socket);
            }
            notificationManager.chatServerConnect();
        }
    }
    private class connServer extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
                 try {

                    socket = new Socket(IP, 4444); // connect to the server
                     nSocket=new Socket(IP,4445);
                    printwriter = new PrintWriter(new OutputStreamWriter(
                            socket.getOutputStream(), Charset.forName("UTF-8")), true);
                    printwriter.println(serverLogInMessage); // write the message to output stream

                     //send pass//
                     DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                     dos.writeInt(password.length);
                     dos.write(password);
                     //          //

                    InputStreamReader inputStreamReader = new InputStreamReader(socket.getInputStream());
                    bufferedReader = new BufferedReader(inputStreamReader);
                     do {
                         serverReply = bufferedReader.readLine();
                     }while(serverReply==null || !(serverReply.equals("SUCCESS") || serverReply.contains("toast")));

                }catch(UnknownHostException e){
                    e.printStackTrace();
                     serverReply="";
                }catch(IOException e){
                    e.printStackTrace();
                     serverReply="";
                }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(pd!=null) {
                pd.dismiss();
            }
            if (serverReply.contains("toast:")) {
                Toast toast = Toast.makeText(getApplicationContext(), serverReply.replace("toast:",""), Toast.LENGTH_SHORT);
                toast.show();
                finish();
            }else if(serverReply.equals("")){
                Toast toast = Toast.makeText(getApplicationContext(), "No internet connection!", Toast.LENGTH_SHORT);
                toast.show();
               // finish();
            }else{
                SocketHandler.setSocket(socket);
            }
        }
    }

    private class SendMessage extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            try {
                printwriter = new PrintWriter(socket.getOutputStream(), true);
                printwriter.println("game_queue"); // write the message to output stream

                InputStreamReader inputStreamReader = new InputStreamReader(socket.getInputStream());
                bufferedReader = new BufferedReader(inputStreamReader);
                boolean dced = false;
                do {
                    try {
                        serverReply = bufferedReader.readLine();
                    }catch(Exception e){
                        dced=true;
                        runOnUiThread(new Runnable(){

                            @Override
                            public void run() {
                                dialog.dismiss();
                                Toast toast = Toast.makeText(getApplicationContext(), "Πρόβλημα σύνδεσης με το διαδίκτυο", Toast.LENGTH_SHORT);
                                toast.show();
                            }
                        });
                        break;
                    }
                }while(!serverReply.contains("game_queue_"));
                Log.i("serverReply1",serverReply);
                if(!dced) {
                    if (serverReply.contains("ok_cancelled")) {
                        Log.i("serverReply2",serverReply);
                        //queueCancelled=false;
                    } else if(serverReply.contains("queue_found:")){
                        printwriter.println("ok");
                        goInGame();
                    }
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

    @Override
    protected void onResume() {
        super.onResume();
        Display display = getWindowManager().getDefaultDisplay();
        int width = display.getWidth();  // deprecated
        int height = display.getHeight();  // deprecated
        if(height/(float)width>=1.6 && height/(float)width<=1.8) {
            imagemap.setImageResource(R.drawable.mainmenumap16x9);
            image.setImageResource(R.drawable.mainmenu16x9);
        }else{
            imagemap.setImageResource(R.drawable.mainmenumap);
            image.setImageResource(R.drawable.mainmenu);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        image.setImageDrawable(null);
        imagemap.setImageDrawable(null);
        fl.setBackgroundColor(Color.parseColor("#67d4ff"));
    }
}
