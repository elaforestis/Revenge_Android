package com.example.orestis.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;

import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class profileSearch extends Activity {

    RelativeLayout rl;
    EditText user;
    Button search;
    String serverReply;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_search);

        rl=(RelativeLayout)findViewById(R.id.rl);
        user = (EditText)findViewById(R.id.username);
        search=(Button)findViewById(R.id.search);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager inputMethodManager = (InputMethodManager)  profileSearch.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(profileSearch.this.getCurrentFocus().getWindowToken(), 0);
                existsPlayer ep = new existsPlayer();
                ep.execute();
            }
        });
    }
    private class existsPlayer extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            try {
                PrintWriter printwriter = new PrintWriter(SocketHandler.getSocket().getOutputStream(), true);
                printwriter.println("exists_player:"+user.getText().toString()); // write the message to output stream

                InputStreamReader inputStreamReader = new InputStreamReader(SocketHandler.getSocket().getInputStream());
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                do{
                    serverReply=bufferedReader.readLine();
                }while(!serverReply.contains("exists_player:"));

            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(serverReply.split(":")[2].equals("true")) {

                Intent k = new Intent(profileSearch.this, profile.class);
                k.putExtra("requestUser", user.getText().toString());
                startActivity(k);
            }else{
                Toast toast = Toast.makeText(getApplicationContext(), "Δεν υπάρχει παίχτης με αυτό το username", Toast.LENGTH_SHORT);
                toast.show();
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
