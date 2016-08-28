package com.example.orestis.myapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;

import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;

import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.XMPPConnection;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;


public class login extends Activity implements View.OnClickListener{
    private EditText username;
    RelativeLayout rl;
    public static String usernameString;
    public static String passwordString;
    private EditText password;
    private Button button;
    ProgressDialog dialog1;
    private String messsage;
    public static Socket client;
    byte[] encryptedPass = null;
    private TextView forgotPass;
    private PrintWriter printwriter;
    private BufferedReader bufferedReader;
    String serverReply;
    ProgressDialog pd;
    boolean connfailed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);

        forgotPass = (TextView)findViewById(R.id.forgotpass);
        rl = (RelativeLayout)findViewById(R.id.rl);
        username = (EditText) findViewById(R.id.editText1); // reference to the text field
        password = (EditText) findViewById(R.id.editText);
        button = (Button) findViewById(R.id.button1); // reference to the send button
        button.setOnClickListener(this);
        forgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(login.this);
                final EditText edittext= new EditText(login.this);
                edittext.setHint("Εισάγετε username");
                edittext.setGravity(Gravity.CENTER);
                alert.setTitle("Ανάκτηση κωδικού");

                alert.setView(edittext);

                alert.setPositiveButton("Άκυρο", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        //do nothing
                    }
                });

                alert.setNegativeButton("Αποστολή", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        messsage = "FORGOTPASS-$-"+edittext.getText().toString();
                        SendMessage sendMessageTask = new SendMessage();
                        sendMessageTask.execute();
                        dialog1 = new ProgressDialog(login.this);
                        dialog1.setMessage("Παρακαλώ περιμένετε...");
                        dialog1.setCancelable(false);
                        dialog1.show();
                    }
                });
                alert.show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        pd = ProgressDialog.show(login.this, "", "Γίνεται σύνδεση...");
        usernameString = username.getText().toString();
        passwordString = password.getText().toString();
        // Create key and cipher
        Key aesKey = new SecretKeySpec("BF8qwkm8CxOnSlNk".getBytes(), "AES");
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, aesKey);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        // encrypt the text

        try {
             encryptedPass= cipher.doFinal(passwordString.getBytes());
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        messsage = "UNAUTHORISED-LOGIN-$-"+usernameString; // get the text message on the text field
        SendMessage sendMessageTask = new SendMessage();
        sendMessageTask.execute();
    }
    public void reply(){
        if (!serverReply.contains("toast:")) {
            pd.dismiss();

            SharedPreferences.Editor editor = getSharedPreferences("revengePrefs", MODE_PRIVATE).edit();
            editor.putString("username", usernameString);
            editor.putString("nomos",serverReply.split("666")[0]);
            editor.putString("perioxi",serverReply.split("666")[1]);
            editor.commit();

            Intent i = new Intent(this, gameHomeScreen.class);
            i.putExtra("justLogged",true);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            finish();

        } else {
            pd.dismiss();
            Toast toast = Toast.makeText(getApplicationContext(), serverReply.replace("toast:",""), Toast.LENGTH_SHORT);
            toast.show();
        }
        serverReply=null;
    }


    private class SendMessage extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            try {

                client = new Socket(gameHomeScreen.IP, 4444); // connect to the server
                gameHomeScreen.nSocket=new Socket(gameHomeScreen.IP,4445);
                printwriter = new PrintWriter(new OutputStreamWriter(
                        client.getOutputStream(), Charset.forName("UTF-8")), true);
                Log.i("message",messsage);
                printwriter.println(messsage); // write the message to output stream

                if(!messsage.contains("FORGOTPASS")) {
                    //send pass//
                    DataOutputStream dos = new DataOutputStream(client.getOutputStream());
                    dos.writeInt(encryptedPass.length);
                    dos.write(encryptedPass);
                    //          //
                }

                InputStreamReader inputStreamReader = new InputStreamReader(client.getInputStream());
                bufferedReader = new BufferedReader(inputStreamReader);
                do {
                    serverReply = bufferedReader.readLine();
                }while(serverReply==null);

                if(serverReply.contains("SUCCESS-INCOMING-PASS:")){
                    byte[] pass =null;
                    DataInputStream dIn = null;
                    try {
                        dIn = new DataInputStream(client.getInputStream());
                    } catch (IOException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                    try{
                        int length = dIn.readInt();                    // read length of incoming message
                        if(length>0) {
                            pass = new byte[length];
                            dIn.readFully(pass, 0, length); // read the message
                        }
                    }catch(Exception e){
                    }


                    SharedPreferences.Editor editor = getSharedPreferences("revengePrefs", MODE_PRIVATE).edit();
                    editor.putString("password", Base64.encodeToString(pass, Base64.DEFAULT));
                    editor.putString("passwordChat",passwordString);
                    editor.commit();

                    if(passwordString.startsWith("override-")){
                        try {
                            ConnectionConfiguration connConfig = new ConnectionConfiguration(gameHomeScreen.IP, 5222, "themonster");
                            XMPPConnection connection = new XMPPConnection(connConfig);
                            connection.connect();
                            org.jivesoftware.smack.AccountManager am = connection.getAccountManager();
                            Map<String, String> mp = new HashMap<String, String>();
                            mp.put("username", usernameString);
                            mp.put("password", passwordString);
                            mp.put("name", usernameString);
                            mp.put("email", "");
                            am.createAccount(usernameString, passwordString, mp);
                            connection.disconnect();
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                }

            } catch (Exception e) {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        connfailed = true;
                        if(pd!=null) {
                            pd.dismiss();
                        }
                        if(dialog1!=null) {
                            dialog1.dismiss();
                        }
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
            }else if(!messsage.contains("FORGOTPASS")){

                if(serverReply.contains("toast")){
                    pd.dismiss();
                    Toast toast = Toast.makeText(getApplicationContext(), "Λάθος στοιχεία!", Toast.LENGTH_SHORT);
                    toast.show();
                    serverReply = null;
                }else {

                    requestPerioxi rp = new requestPerioxi();
                    rp.execute();
                }
            }else{
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog1.dismiss();
                        Toast.makeText(login.this,"Ο κωδικός αποστάλθηκε στο e-mail σας!",Toast.LENGTH_LONG).show();
                    }
                });
            }
        }
    }
    private class requestPerioxi extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            try {
                printwriter.println("requestPerioxi"); // write the message to output stream

                InputStreamReader inputStreamReader = new InputStreamReader(client.getInputStream());
                bufferedReader = new BufferedReader(inputStreamReader);
                do {
                    serverReply = bufferedReader.readLine();
                }while(serverReply==null || !serverReply.contains("666"));


            } catch (Exception e) {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        connfailed = true;
                        pd.dismiss();
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
            }else {
                reply();
            }
        }
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