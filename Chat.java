package com.example.orestis.myapplication;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import java.util.Date;

import java.util.HashSet;

import java.util.Set;
import java.util.regex.Pattern;


import org.jivesoftware.smack.packet.Message;

import android.app.Activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;


public class Chat extends Activity {





    private static ArrayList<String> messages = new ArrayList<String>();

    LinearLayout ll;
    private EditText textMessage;
    private static ListView listview;
    public static boolean chatInBackground;
    public TextView tvUser;
    IntentFilter intf;
    static SharedPreferences prefs;
    public static boolean chatActive =false;
    public static String chatUser;
    int numMessages;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_game_complete);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_chat);

        ll= (LinearLayout)findViewById(R.id.ll);
        tvUser =(TextView)findViewById(R.id.chatUser);
        textMessage = (EditText) this.findViewById(R.id.chatET);
        listview = (ListView) this.findViewById(R.id.listMessages);


        Intent i = getIntent();
        chatUser = i.getExtras().getString("chatUser");
        tvUser.setText("Chat: "+chatUser);
        chatUser = chatUser.toLowerCase();


        // Set a listener to send a chat text message
        Button send = (Button) this.findViewById(R.id.sendBtn);
        send.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String to = chatUser;
                String text = textMessage.getText().toString();
                Log.i("XMPPChatDemoActivity ", "Sending text " + text + " to " + to);
                Message msg = new Message(to + "@themonster", Message.Type.chat);
                msg.setBody(text);
                if (notificationManager.con != null) {
                    notificationManager.con.sendPacket(msg);
                    String currentDateandTime = new SimpleDateFormat("dd/MM HH:mm").format(new Date());
                    messages.add(notificationManager.con.getUser().split(Pattern.quote("@"))[0] + " (" + currentDateandTime + "):\n" + text);
                    setListAdapter();
                    numMessages++;
                }
                textMessage.setText("");
            }
        });

        prefs = getSharedPreferences("revengePrefs", MODE_PRIVATE);
        Set<String> set = prefs.getStringSet(chatUser,null);
        if(set!=null) {
            messages = new ArrayList<String>();

            numMessages = prefs.getInt(chatUser+"num",0);

            ArrayList messages_unordered = new ArrayList(set);
            for (int k = 0;k<numMessages;k++) {
                for (int j = 0; j < messages_unordered.size(); j++) {
                    if(Integer.parseInt(((String) messages_unordered.get(j)).split(Pattern.quote("-$-&&-$yolo-"))[1])==k){
                        messages.add(((String) messages_unordered.get(j)).split(Pattern.quote("-$-&&-$yolo-"))[0]);
                    }
                }
            }
            setListAdapter();

        }else{
            SharedPreferences.Editor editor = getSharedPreferences("revengePrefs", MODE_PRIVATE).edit();
            editor.putInt(chatUser+"num",0);
            editor.commit();
            messages = new ArrayList<String>();
            numMessages=0;
        }
        if(i.getStringExtra("msgReceived")!=null){
            Log.i("msgindeedreceived", "yep");
            numMessages++;
            messages.add(i.getStringExtra("msgReceived"));
            setListAdapter();
        }
    }

    /**
     * Called by Settings dialog when a connection is establised with
     * the XMPP server
     * @param msgReceived
     */


    public void messageArrived(String msgReceived){
        messages.add(msgReceived);
        setListAdapter();
    }
    public void  setListAdapter() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.listitem, messages);
        listview.setAdapter(adapter);
    }



    @Override
    protected void onResume() {
        super.onResume();
        ll.setBackgroundResource(R.drawable.menu_op);
        chatActive = true;
        intf = new IntentFilter();
        intf.addAction("incoming_chat");
        registerReceiver(intentReceiver, intf);
        Intent i = getIntent();
        chatInBackground=false;
    }

    private BroadcastReceiver intentReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            numMessages++;
            messageArrived(intent.getExtras().getString("msgReceived"));
        }
    };



    @Override
    protected void onStop() {
        super.onStop();
        ll.setBackgroundDrawable(null);
        ll.setBackgroundColor(Color.parseColor("#67d4ff"));
        chatInBackground = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        chatActive = false;
        unregisterReceiver(intentReceiver);

        for(int i = 0;i<messages.size(); i++) {
            messages.set(i,messages.get(i)+"-$-&&-$yolo-"+i);
        }
        SharedPreferences.Editor editor = getSharedPreferences("revengePrefs", MODE_PRIVATE).edit();
        Set<String> setToSave = new HashSet<String>();
        setToSave.addAll(messages);
        editor.putStringSet(chatUser, setToSave);
        editor.putInt(chatUser+"num",numMessages);
        Log.i("nummesgs",String.valueOf(numMessages));
        editor.commit();
    }
}