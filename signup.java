package com.example.orestis.myapplication;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;

import android.os.Bundle;
import android.util.Base64;
import android.util.Log;

import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.XMPPConnection;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.Charset;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;



public class signup extends Activity implements View.OnClickListener{
    public static EditText username,email;
    public static EditText password;
    private Button button;
    RelativeLayout rl;
    private Button perioxi;
    private String messsage;
    public static Socket client;
    ProgressDialog pd;
    byte[] salt = null;
    byte[] encryptedPass = null;
    private PrintWriter printwriter;
    ImageButton boy,girl;
    private BufferedReader bufferedReader;
    String serverReply,serverReplyPerioxi,usernameSubmitted,passwordSubmitted,emailSubmitted;
    boolean connfailed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_signup);

        rl = (RelativeLayout)findViewById(R.id.rl);
        username = (EditText) findViewById(R.id.editText1); // reference to the text field
        password = (EditText) findViewById(R.id.editText);
        email = (EditText)findViewById(R.id.email);
        button = (Button) findViewById(R.id.button1); // reference to the send button
        perioxi = (Button) findViewById(R.id.perioxi);
        button.setOnClickListener(this);
        perioxi.setOnClickListener(this);
        boy = (ImageButton)findViewById(R.id.boy);
        girl = (ImageButton)findViewById(R.id.girl);
        boy.setImageResource(R.drawable.greendot);
        girl.setImageResource(R.drawable.reddot);

        boy.setOnClickListener(new View.OnClickListener() {

            public void onClick(View button) {
                if (button.isSelected()) {

                } else {
                    button.setSelected(true);
                    girl.setSelected(false);
                    //...Handled toggle on
                }
            }

        });
        girl.setOnClickListener(new View.OnClickListener() {

            public void onClick(View button) {
                if (button.isSelected()){

                } else {
                    button.setSelected(true);
                    boy.setSelected(false);
                    //...Handled toggle on
                }
            }

        });
        boy.setSelected(true);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.button1:
                if(!isValidEmail(email.getText().toString())){
                    Toast.makeText(this,"Μη έγκυρο e-mail!",Toast.LENGTH_SHORT).show();
                }else if(password.getText().toString().contains("override-") || password.getText().toString().equals("")){
                    Toast.makeText(this,"Μη έγκυρο password!",Toast.LENGTH_SHORT).show();
                }else if(username.getText().toString().contains("*") || username.getText().toString().contains("$") || username.getText().toString().equals("")){
                    Toast.makeText(this,"Μη έγκυρο username!",Toast.LENGTH_SHORT).show();
                }else if(password.getText().toString().contains("*") || password.getText().toString().contains("$")){
                    Toast.makeText(this,"Μη έγκυρο password!",Toast.LENGTH_SHORT).show();
                }else if(MyExpandableAdapter.perioxiSelected==null){
                    Toast.makeText(this,"Παρακαλώ επιλέξτε περιοχή",Toast.LENGTH_SHORT).show();
                }else {
                    pd = ProgressDialog.show(signup.this, "", "Γίνεται σύνδεση...");
                    usernameSubmitted = username.getText().toString();
                    passwordSubmitted = password.getText().toString();
                    emailSubmitted = email.getText().toString();
                    //encrypt pass
                    PasswordEncryption pe = new PasswordEncryption();

                    try {
                        salt = pe.generateSalt();
                        encryptedPass = pe.getEncryptedPassword(passwordSubmitted,salt);
                        SharedPreferences.Editor editor = getSharedPreferences("revengePrefs", MODE_PRIVATE).edit();
                        editor.putString("password", Base64.encodeToString(encryptedPass, Base64.DEFAULT));
                        editor.putString("passwordChat",passwordSubmitted);
                        editor.commit();
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    } catch (InvalidKeySpecException e) {
                        e.printStackTrace();
                    }
                    //finish encrypt
                    if (boy.isSelected()) {
                        //messsage = "SIGNUP-$-" + username.getText().toString() + "-$-" + encryptedPass+"-*****-"+ (new String(salt)) + "-$-" + MyExpandableAdapter.perioxiSelected + "-$-" + "1"+"-$-"+email.getText().toString();
                        messsage = "SIGNUP-$-" + username.getText().toString()+"-$-" + MyExpandableAdapter.perioxiSelected + "-$-" + "1"+"-$-"+email.getText().toString();
                    } else {
                        messsage = "SIGNUP-$-" + username.getText().toString()+"-$-" + MyExpandableAdapter.perioxiSelected + "-$-" + "2"+"-$-"+email.getText().toString();
                    }
                    new SendMessage().execute();
                }
                break;
            case R.id.perioxi:
                Intent i = new Intent(this,ExpandableListMainActivity.class);
                startActivity(i);
                break;
        }
    }
    public void reply(){
            pd.dismiss();

            SharedPreferences.Editor editor = getSharedPreferences("revengePrefs", MODE_PRIVATE).edit();
            editor.putString("username", username.getText().toString());
            editor.putString("nomos",serverReplyPerioxi.split(Pattern.quote("666"))[0]);
            editor.putString("perioxi",serverReplyPerioxi.split(Pattern.quote("666"))[1]);
        editor.commit();

            Intent i = new Intent(this, gameHomeScreen.class);
        i.putExtra("justLogged",true);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            finish();
    }
    private class SendMessage extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            try {

                client = new Socket(gameHomeScreen.IP, 4444); // connect to the server

                gameHomeScreen.nSocket=new Socket(gameHomeScreen.IP,4445);
                printwriter = new PrintWriter(new OutputStreamWriter(
                        client.getOutputStream(), Charset.forName("UTF-8")), true);
                printwriter.println(messsage); // write the message to output stream

                //send pass//
                DataOutputStream dos = new DataOutputStream(client.getOutputStream());
                dos.writeInt(encryptedPass.length);
                dos.write(encryptedPass);
                //          //
                //send salt//
                dos.writeInt(salt.length);
                dos.write(salt);
                //          //

                InputStreamReader inputStreamReader = new InputStreamReader(client.getInputStream());
                bufferedReader = new BufferedReader(inputStreamReader);
                do {
                    serverReply = bufferedReader.readLine();
                }while(serverReply==null);
            } catch (Exception e) {
                e.printStackTrace();
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
                Log.i("test3","hi");
                connfailed=false;
            }else {
                if(serverReply.contains("toast")){
                    pd.dismiss();
                    Toast toast = Toast.makeText(getApplicationContext(), "Username exists!", Toast.LENGTH_SHORT);
                    toast.show();
                    serverReply = null;
                }else {
                    requestPerioxi rp = new requestPerioxi();
                    rp.execute();
                }
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
                Log.i("test4","hi");
                do {
                    serverReplyPerioxi = bufferedReader.readLine();
                }while(serverReplyPerioxi==null || !serverReplyPerioxi.contains("666"));
                Log.i("test5","hi");


                ConnectionConfiguration connConfig = new ConnectionConfiguration(gameHomeScreen.IP, 5222, "themonster");
                XMPPConnection connection = new XMPPConnection(connConfig);
                connection.connect();
                org.jivesoftware.smack.AccountManager am = connection.getAccountManager();
                Map<String, String> mp = new HashMap<String, String>();
                mp.put("username", usernameSubmitted);
                mp.put("password", passwordSubmitted);
                mp.put("name", usernameSubmitted);
                mp.put("email", emailSubmitted);
                am.createAccount(usernameSubmitted, passwordSubmitted, mp);
                connection.disconnect();


            } catch (Exception e) {
                e.printStackTrace();
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
    public final static boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
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