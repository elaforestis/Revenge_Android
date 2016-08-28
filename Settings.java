package com.example.orestis.myapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;

import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.method.DigitsKeyListener;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;

import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.Map;

public class Settings extends Activity {
ScrollView sv;
    ProgressDialog dialog1;
    byte[] encryptedPass,salt;
    boolean connfailed;
    private String messsage;
    ImageButton chall,chatB,fr,soundButton,changePass,changeEmail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_settings);
        sv = (ScrollView)findViewById(R.id.sv);
        chall = (ImageButton)findViewById(R.id.challengeRequestsButton);
        chatB = (ImageButton)findViewById(R.id.chatButton);
        fr = (ImageButton)findViewById(R.id.friendRequestsButton);
        soundButton = (ImageButton)findViewById(R.id.soundButton);
        changePass = (ImageButton)findViewById(R.id.changePass);
        changeEmail = (ImageButton)findViewById(R.id.changeEmail);

        SharedPreferences prefs = getSharedPreferences("revengePrefs", MODE_PRIVATE);
        boolean challenge;
        boolean chat;
        boolean friendReq;
        challenge = prefs.getBoolean("challengeRequests",true);
        chat = prefs.getBoolean("chatNotifications",true);
        friendReq = prefs.getBoolean("friendRequests",true);

        if(challenge){
            Drawable bg = getResources().getDrawable(android.R.drawable.btn_default);
            bg.setColorFilter(0xFF00FF00, PorterDuff.Mode.MULTIPLY);
            chall.setBackgroundDrawable(bg);
        }else{
            Drawable bg = getResources().getDrawable(android.R.drawable.btn_default);
            bg.setColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY);
            chall.setBackgroundDrawable(bg);
        }
        if(chat){
            Drawable bg = getResources().getDrawable(android.R.drawable.btn_default);
            bg.setColorFilter(0xFF00FF00, PorterDuff.Mode.MULTIPLY);
            chatB.setBackgroundDrawable(bg);
        }else{
            Drawable bg = getResources().getDrawable(android.R.drawable.btn_default);
            bg.setColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY);
            chatB.setBackgroundDrawable(bg);
        }
        if(friendReq){
            Drawable bg = getResources().getDrawable(android.R.drawable.btn_default);
            bg.setColorFilter(0xFF00FF00, PorterDuff.Mode.MULTIPLY);
            fr.setBackgroundDrawable(bg);
        }else{
            Drawable bg = getResources().getDrawable(android.R.drawable.btn_default);
            bg.setColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY);
            fr.setBackgroundDrawable(bg);
        }

        chall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences prefs = getSharedPreferences("revengePrefs", MODE_PRIVATE);
                if(prefs.getBoolean("challengeRequests",true)){
                    Drawable bg = getResources().getDrawable(android.R.drawable.btn_default);
                    bg.setColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY);
                    chall.setBackgroundDrawable(bg);
                    SharedPreferences.Editor editor = getSharedPreferences("revengePrefs", MODE_PRIVATE).edit();
                    editor.putBoolean("challengeRequests", false);
                    editor.commit();
                    Toast.makeText(Settings.this,"Ειδοποιήσεις προκλήσεων απενεργοποιήθηκαν!",Toast.LENGTH_SHORT).show();
                }else{
                    Drawable bg = getResources().getDrawable(android.R.drawable.btn_default);
                    bg.setColorFilter(0xFF00FF00, PorterDuff.Mode.MULTIPLY);
                    chall.setBackgroundDrawable(bg);
                    SharedPreferences.Editor editor = getSharedPreferences("revengePrefs", MODE_PRIVATE).edit();
                    editor.putBoolean("challengeRequests", true);
                    editor.commit();
                    Toast.makeText(Settings.this, "Ειδοποιήσεις προκλήσεων ενεργοποιήθηκαν!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        chatB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences prefs = getSharedPreferences("revengePrefs", MODE_PRIVATE);
                if(prefs.getBoolean("chatNotifications",true)){
                    Drawable bg = getResources().getDrawable(android.R.drawable.btn_default);
                    bg.setColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY);
                    chatB.setBackgroundDrawable(bg);
                    SharedPreferences.Editor editor = getSharedPreferences("revengePrefs", MODE_PRIVATE).edit();
                    editor.putBoolean("chatNotifications", false);
                    editor.commit();
                    Toast.makeText(Settings.this, "Ειδοποιήσεις chat απενεργοποιήθηκαν!", Toast.LENGTH_SHORT).show();
                }else{
                    Drawable bg = getResources().getDrawable(android.R.drawable.btn_default);
                    bg.setColorFilter(0xFF00FF00, PorterDuff.Mode.MULTIPLY);
                    chatB.setBackgroundDrawable(bg);
                    SharedPreferences.Editor editor = getSharedPreferences("revengePrefs", MODE_PRIVATE).edit();
                    editor.putBoolean("chatNotifications", true);
                    editor.commit();
                    Toast.makeText(Settings.this, "Ειδοποιήσεις chat ενεργοποιήθηκαν!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        fr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences prefs = getSharedPreferences("revengePrefs", MODE_PRIVATE);
                if(prefs.getBoolean("friendRequests",true)){
                    Drawable bg = getResources().getDrawable(android.R.drawable.btn_default);
                    bg.setColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY);
                    fr.setBackgroundDrawable(bg);
                    SharedPreferences.Editor editor = getSharedPreferences("revengePrefs", MODE_PRIVATE).edit();
                    editor.putBoolean("friendRequests", false);
                    editor.commit();
                    Toast.makeText(Settings.this, "Ειδοποιήσεις αιτημάτων φιλίας απενεργοποιήθηκαν!", Toast.LENGTH_SHORT).show();
                }else{
                    Drawable bg = getResources().getDrawable(android.R.drawable.btn_default);
                    bg.setColorFilter(0xFF00FF00, PorterDuff.Mode.MULTIPLY);
                    fr.setBackgroundDrawable(bg);
                    SharedPreferences.Editor editor = getSharedPreferences("revengePrefs", MODE_PRIVATE).edit();
                    editor.putBoolean("friendRequests", true);
                    editor.commit();
                    Toast.makeText(Settings.this, "Ειδοποιήσεις αιτημάτων φιλίας ενεργοποιήθηκαν!", Toast.LENGTH_SHORT).show();
                }
            }
        });


        changePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(Settings.this);
                final EditText edittext2= new EditText(Settings.this);
                edittext2.setHint("Παλιό password");
                edittext2.setGravity(Gravity.CENTER);
                edittext2.setInputType(InputType.TYPE_CLASS_TEXT |
                        InputType.TYPE_TEXT_VARIATION_PASSWORD);
                final EditText edittext= new EditText(Settings.this);
                edittext.setHint("Νέο password");
                edittext.setGravity(Gravity.CENTER);
                edittext.setInputType(InputType.TYPE_CLASS_TEXT |
                        InputType.TYPE_TEXT_VARIATION_PASSWORD);
                //edittext.setKeyListener(DigitsKeyListener.getInstance(true,true));
                InputFilter filter = new InputFilter() {
                    public CharSequence filter(CharSequence source, int start, int end,
                                               Spanned dest, int dstart, int dend) {
                        for (int i = start; i < end; i++) {
                            if ("*$".contains(String.valueOf(source.charAt(i)))) {
                                return "";
                            }
                        }
                        return null;
                    }
                };

                edittext.setFilters(new InputFilter[]{filter});
                alert.setTitle("Αλλαγή password");
                LinearLayout ll = new LinearLayout(Settings.this);
                ll.setOrientation(LinearLayout.VERTICAL);
                ll.addView(edittext2);
                ll.addView(edittext);
                alert.setView(ll);

                alert.setPositiveButton("Άκυρο", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        //do nothing
                    }
                });

                alert.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String oldPass = edittext2.getText().toString();
                        SharedPreferences prefs = getSharedPreferences("revengePrefs", MODE_PRIVATE);
                        if (prefs.getString("passwordChat","wrong").equals(oldPass) && !edittext.getText().toString().contains("override-")) {
                            String newPass = edittext.getText().toString();
                            //prefs
                            SharedPreferences.Editor editor = getSharedPreferences("revengePrefs", MODE_PRIVATE).edit();
                            editor.remove("passwordChat");
                            editor.commit();
                            editor.putString("passwordChat",newPass);
                            editor.commit();
                            //
                            //openfire
                            XMPPConnection xmppconn = notificationManager.con;
                            org.jivesoftware.smack.AccountManager am = xmppconn.getAccountManager();
                            try {
                                am.changePassword(newPass);
                            } catch (XMPPException e) {
                                e.printStackTrace();
                            }
                            //
                            //encrypt pass
                            PasswordEncryption pe = new PasswordEncryption();

                            try {
                                salt = pe.generateSalt();
                                encryptedPass = pe.getEncryptedPassword(newPass,salt);

                            } catch (NoSuchAlgorithmException e) {
                                e.printStackTrace();
                            } catch (InvalidKeySpecException e) {
                                e.printStackTrace();
                            }
                            //finish encrypt
                            //server
                            messsage = "changePass";
                            SendMessage sendMessageTask = new SendMessage();
                            sendMessageTask.execute();
                            //
                            dialog1 = new ProgressDialog(Settings.this);
                            dialog1.setMessage("Παρακαλώ περιμένετε...");
                            dialog1.setCancelable(false);
                            dialog1.show();
                        } else {
                            Toast.makeText(Settings.this, "Λανθασμένος κωδικός!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                alert.show();
            }
        });
        changeEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(Settings.this);
                final EditText edittext2= new EditText(Settings.this);
                edittext2.setHint("Password");
                edittext2.setInputType(InputType.TYPE_CLASS_TEXT |
                        InputType.TYPE_TEXT_VARIATION_PASSWORD);
                edittext2.setGravity(Gravity.CENTER);
                final EditText edittext= new EditText(Settings.this);
                edittext.setHint("Νέο e-mail");
                edittext.setGravity(Gravity.CENTER);
                alert.setTitle("Αλλαγή e-mail");
                LinearLayout ll = new LinearLayout(Settings.this);
                ll.setOrientation(LinearLayout.VERTICAL);
                ll.addView(edittext2);
                ll.addView(edittext);
                alert.setView(ll);

                alert.setPositiveButton("Άκυρο", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        //do nothing
                    }
                });

                alert.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                String oldPass = edittext2.getText().toString();
                                SharedPreferences prefs = getSharedPreferences("revengePrefs", MODE_PRIVATE);
                                if (prefs.getString("passwordChat", "wrong").equals(oldPass)) {
                                    String newEmail = edittext.getText().toString();
                                    //server
                                    if (signup.isValidEmail(newEmail)) {
                                        messsage = "changeEmail:" + newEmail;
                                        SendMessage sendMessageTask = new SendMessage();
                                        sendMessageTask.execute();
                                        //
                                        dialog1 = new ProgressDialog(Settings.this);
                                        dialog1.setMessage("Παρακαλώ περιμένετε...");
                                        dialog1.setCancelable(false);
                                        dialog1.show();
                                    } else {
                                        Toast.makeText(Settings.this, "Μη έγκυρο e-mail!", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(Settings.this, "Λανθασμένος κωδικός!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                );
                    alert.show();
                }
            });
    }

    @Override
    protected void onResume() {
        super.onResume();
        sv.setBackgroundResource(R.drawable.menu);
        chall.setImageResource(R.drawable.vs);
        chatB.setImageResource(R.drawable.chat);
        fr.setImageResource(R.drawable.friendrequests);

        soundButton.setImageResource(R.drawable.sound);
        changePass.setImageResource(R.drawable.pass);
        changeEmail.setImageResource(R.drawable.mail);
    }

    @Override
    protected void onStop() {
        super.onStop();
        sv.setBackgroundDrawable(null);
        sv.setBackgroundColor(Color.parseColor("#67d4ff"));
        chall.setImageDrawable(null);
        chatB.setImageDrawable(null);
        fr.setImageDrawable(null);
        soundButton.setImageDrawable(null);
        changePass.setImageDrawable(null);
        changeEmail.setImageDrawable(null);
    }
    private class SendMessage extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            try {
                PrintWriter printwriter = new PrintWriter(SocketHandler.getSocket().getOutputStream(), true);
                printwriter.println(messsage); // write the message to output stream

                if(messsage.equals("changePass")){
                    DataOutputStream dos = new DataOutputStream(SocketHandler.getSocket().getOutputStream());
                    dos.writeInt(encryptedPass.length);
                    dos.write(encryptedPass);
                    //          //
                    //send salt//
                    dos.writeInt(salt.length);
                    dos.write(salt);
                    //          //
                }

            } catch (Exception e) {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        connfailed = true;
                        dialog1.dismiss();
                        Toast toast = Toast.makeText(getApplicationContext(), "Πρόβλημα σύνδεσης με το διαδίκτυο", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                });

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(connfailed){
                connfailed=false;
            }else{
                if(messsage.equals("changePass")){
                    SharedPreferences.Editor editor = getSharedPreferences("revengePrefs", MODE_PRIVATE).edit();
                    editor.remove("password");
                    editor.commit();
                    editor.putString("password", Base64.encodeToString(encryptedPass, Base64.DEFAULT));
                    editor.commit();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog1.dismiss();
                        Toast.makeText(Settings.this,"Αλλαγή επιτυχής!",Toast.LENGTH_LONG).show();
                        //System.exit(0);
                    }
                });
            }
        }
    }
}
