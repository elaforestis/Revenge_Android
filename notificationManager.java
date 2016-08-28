package com.example.orestis.myapplication;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.util.Log;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.util.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;


public class notificationManager extends IntentService{
    public static final String HOST = gameHomeScreen.IP;
    public static final int PORT = 5222;
    public static final String SERVICE = "themonster";
    public static String USERNAME;
    public static String PASSWORD;
    public static XMPPConnection con;
    static SharedPreferences prefs;

    public notificationManager(){
        super("notificationManager");
    }
    String ns = Context.NOTIFICATION_SERVICE;
    static Context context;
    static Context baseContext;
    static SharedPreferences.Editor editor;
    static NotificationManager mNotificationManager;

    @Override
    protected void onHandleIntent(Intent intent) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String nReply = null;
                prefs = getSharedPreferences("revengePrefs", MODE_PRIVATE);
                editor = getSharedPreferences("revengePrefs", MODE_PRIVATE).edit();
                mNotificationManager = (NotificationManager) getSystemService(ns);
                context = getApplicationContext();
                baseContext = getBaseContext();
                USERNAME = prefs.getString("username", "No username found");//"No name defined" is the default value.
                PASSWORD = prefs.getString("passwordChat", "No password found"); //0 is the default value.
                chatServerConnect();
                while (true) {
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    InputStreamReader inputStreamReader;
                    try {
                        if(gameHomeScreen.nSocket!=null) {
                            inputStreamReader = new InputStreamReader(gameHomeScreen.nSocket.getInputStream());
                            BufferedReader br = new BufferedReader(inputStreamReader);
                            nReply = br.readLine();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if(nReply!=null) {
                        if (nReply.contains("NOTIFY:FREQUEST:") && prefs.getBoolean("friendRequests",true)) {
                            String ns = Context.NOTIFICATION_SERVICE;
                            NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);

                            int icon = R.drawable.icon;
                            CharSequence tickerText = "Νέο αίτημα φιλίας"; // ticker-text
                            long when = System.currentTimeMillis();
                            Context context = getApplicationContext();
                            CharSequence contentTitle = "Νέο αίτημα φιλίας";
                            CharSequence contentText = "Αίτημα φιλίας απο τον/την: " + nReply.replace("NOTIFY:FREQUEST:", "");
                            Intent notificationIntent = new Intent(context, onNotificationClick.class);
                            notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                                    Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            SocketHandler.notificationMode = true;
                            SocketHandler.notificationCode = 1;
                            SocketHandler.notificationer = nReply.replace("NOTIFY:FREQUEST:", "");
                            PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
                            Notification notification = new Notification(icon, tickerText, when);
                            notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
                            notification.sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                            notification.flags = Notification.FLAG_AUTO_CANCEL;
                            // and this
                            int HELLO_ID = (int) System.currentTimeMillis();
                            mNotificationManager.notify(HELLO_ID, notification);

                        } else if (nReply.contains("NOTIFY:CHALLENGE:") && prefs.getBoolean("challengeRequests",true)) {//
                            String ns = Context.NOTIFICATION_SERVICE;
                            NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);

                            int icon = R.drawable.icon;
                            CharSequence tickerText = "Νέα πρόκληση"; // ticker-text
                            long when = System.currentTimeMillis();
                            Context context = getApplicationContext();
                            CharSequence contentTitle = "Νέα πρόκληση";
                            CharSequence contentText = "Πρόκληση απο τον/την: " + nReply.replace("NOTIFY:CHALLENGE:", "");
                            Intent notificationIntent = new Intent(context, onNotificationClick.class);
                            notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                                    Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            SocketHandler.notificationMode = true;
                            SocketHandler.notificationCode = 2;
                            SocketHandler.notificationer = nReply.replace("NOTIFY:CHALLENGE:", "");
                            PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
                            Notification notification = new Notification(icon, tickerText, when);
                            notification.flags = Notification.FLAG_AUTO_CANCEL;
                            notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
                            notification.sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                            // and this
                            int HELLO_ID = (int) System.currentTimeMillis();
                            mNotificationManager.notify(HELLO_ID, notification);
                        }
                        nReply=null;
                    }
                }
            }
        }).start();
    }

    public static void chatServerConnect() {

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                // Create a connection
                ConnectionConfiguration connConfig = new ConnectionConfiguration(HOST, PORT, SERVICE);
                XMPPConnection connection = new XMPPConnection(connConfig);
                try {
                    connection.connect();
                    Log.i("XMPPChatDemoActivity", "[SettingsDialog] Connected to " + connection.getHost());
                } catch (XMPPException ex) {
                    Log.e("XMPPChatDemoActivity",  "[SettingsDialog] Failed to connect to "+ connection.getHost());
                    Log.e("XMPPChatDemoActivity", ex.toString());
                    setConnection(null);
                }
                try {
                    connection.login(USERNAME,PASSWORD);
                    Log.i("XMPPChatDemoActivity",  "Logged in as" + connection.getUser());

                    // Set the status to available
                    Presence presence = new Presence(Presence.Type.available);
                    connection.sendPacket(presence);
                    setConnection(connection);

                    Roster roster = connection.getRoster();
                    Collection<RosterEntry> entries = roster.getEntries();
                    for (RosterEntry entry : entries) {

                        Log.d("XMPPChatDemoActivity",  "--------------------------------------");
                        Log.d("XMPPChatDemoActivity", "RosterEntry " + entry);
                        Log.d("XMPPChatDemoActivity", "User: " + entry.getUser());
                        Log.d("XMPPChatDemoActivity", "Name: " + entry.getName());
                        Log.d("XMPPChatDemoActivity", "Status: " + entry.getStatus());
                        Log.d("XMPPChatDemoActivity", "Type: " + entry.getType());
                        Presence entryPresence = roster.getPresence(entry.getUser());

                        Log.d("XMPPChatDemoActivity", "Presence Status: "+ entryPresence.getStatus());
                        Log.d("XMPPChatDemoActivity", "Presence Type: " + entryPresence.getType());

                        Presence.Type type = entryPresence.getType();
                        if (type == Presence.Type.available)
                            Log.d("XMPPChatDemoActivity", "Presence AVIALABLE");
                        Log.d("XMPPChatDemoActivity", "Presence : " + entryPresence);
                    }
                } catch (Exception ex) {
                    Log.e("XMPPChatDemoActivity", "Failed to log in");
                    ex.printStackTrace();
                    setConnection(null);
                }
            }
        });
        t.start();
    }

    public static void setConnection(XMPPConnection connection) {
        Log.i("test1","hi");
        con = connection;
        if (connection != null) {
            // Add a packet listener to get messages sent to us
            PacketFilter filter = new MessageTypeFilter(Message.Type.chat);
            connection.addPacketListener(new PacketListener() {
                @Override
                public void processPacket(Packet packet) {
                    Message message = (Message) packet;
                    if (message.getBody() != null) {
                            String fromName = StringUtils.parseBareAddress(message.getFrom());
                            Log.i("XMPPChatDemoActivity ", " Text Recieved " + message.getBody() + " from " + fromName);
                            String currentDateandTime = new SimpleDateFormat("dd/MM HH:mm").format(new Date());
                            String msgReceived = fromName.split(Pattern.quote("@"))[0] + " (" + currentDateandTime + "):\n" + message.getBody();

                        if (!Chat.chatActive && prefs.getBoolean("chatNotifications",true)){
                            Log.i("test","hi1");


                            int icon = R.drawable.icon;
                            CharSequence tickerText = "Chat-"+fromName.split(Pattern.quote("@"))[0]; // ticker-text
                            long when = System.currentTimeMillis();

                            CharSequence contentTitle = "Chat-"+fromName.split(Pattern.quote("@"))[0];
                            CharSequence contentText = message.getBody();
                            Intent notificationIntent = new Intent(context, Chat.class);
                            notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                                    Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            notificationIntent.putExtra("chatUser",fromName.split(Pattern.quote("@"))[0].substring(0,1).toUpperCase()+fromName.split(Pattern.quote("@"))[0].substring(1));
                            notificationIntent.putExtra("msgReceived",msgReceived);
                            //SocketHandler.notificationer = nReply.replace("NOTIFY:FREQUEST:", "");
                            PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                            Notification notification = new Notification(icon, tickerText, when);
                            notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
                            notification.sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                            notification.flags = Notification.FLAG_AUTO_CANCEL;
                            // and this
                            int HELLO_ID = (int) System.currentTimeMillis();
                            mNotificationManager.notify(HELLO_ID, notification);

                        }else if(!Chat.chatActive && !prefs.getBoolean("chatNotifications",true) && Integer.valueOf(android.os.Build.VERSION.SDK)>=11){
                            Set<String> set = prefs.getStringSet((fromName.split(Pattern.quote("@"))[0].substring(0,1).toUpperCase()+fromName.split(Pattern.quote("@"))[0].substring(1)).toLowerCase(),null);
                            int numMessages = prefs.getInt((fromName.split(Pattern.quote("@"))[0].substring(0,1).toUpperCase()+fromName.split(Pattern.quote("@"))[0].substring(1)).toLowerCase() + "num", 0);
                            ArrayList<String> messages = new ArrayList<String>();
                            if(set!=null) {
                                ArrayList messages_unordered = new ArrayList(set);
                                for (int k = 0;k<numMessages;k++) {
                                    for (int j = 0; j < messages_unordered.size(); j++) {
                                        if(Integer.parseInt(((String) messages_unordered.get(j)).split(Pattern.quote("-$-&&-$yolo-"))[1])==k){
                                            messages.add(((String) messages_unordered.get(j)).split(Pattern.quote("-$-&&-$yolo-"))[0]);
                                        }
                                    }
                                }
                                messages.add(msgReceived);

                                for(int i = 0;i<messages.size(); i++) {
                                    messages.set(i,messages.get(i)+"-$-&&-$yolo-"+i);
                                }

                                Set<String> setToSave = new HashSet<String>();
                                setToSave.addAll(messages);
                                editor.putStringSet((fromName.split(Pattern.quote("@"))[0].substring(0, 1).toUpperCase() + fromName.split(Pattern.quote("@"))[0].substring(1)).toLowerCase(), setToSave);
                                editor.putInt((fromName.split(Pattern.quote("@"))[0].substring(0, 1).toUpperCase() + fromName.split(Pattern.quote("@"))[0].substring(1)).toLowerCase() + "num", numMessages + 1);
                                editor.commit();
                            }
                            Log.i("test","hi2");
                        }else{
                            if(Chat.chatUser.equals((fromName.split(Pattern.quote("@"))[0].substring(0,1).toUpperCase()+fromName.split(Pattern.quote("@"))[0].substring(1)).toLowerCase())) {
                                if(!Chat.chatInBackground) {
                                    Intent broadcastIntent = new Intent();
                                    broadcastIntent.setAction("incoming_chat");
                                    broadcastIntent.putExtra("msgReceived", msgReceived);
                                    baseContext.sendBroadcast(broadcastIntent);
                                    Log.i("test", "hi3");
                                }else{
                                    Intent broadcastIntent = new Intent();
                                    broadcastIntent.setAction("incoming_chat");
                                    broadcastIntent.putExtra("msgReceived", msgReceived);
                                    baseContext.sendBroadcast(broadcastIntent);
                                    Log.i("test", "hi4");
                                    int icon = R.drawable.icon;
                                    CharSequence tickerText = "Chat-"+fromName.split(Pattern.quote("@"))[0]; // ticker-text
                                    long when = System.currentTimeMillis();
                                    CharSequence contentTitle = "Chat-"+fromName.split(Pattern.quote("@"))[0];
                                    CharSequence contentText = message.getBody();
                                    Intent notificationIntent = new Intent(context, Chat.class);
                                    notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                                            Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                    PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                                    Notification notification = new Notification(icon, tickerText, when);
                                    notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
                                    notification.sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                                    notification.flags = Notification.FLAG_AUTO_CANCEL;
                                    // and this
                                    int HELLO_ID = (int) System.currentTimeMillis();
                                    mNotificationManager.notify(HELLO_ID, notification);
                                }
                            }else if(prefs.getBoolean("chatNotifications",true)){

                                int icon = R.drawable.icon;
                                CharSequence tickerText = "Chat-"+fromName.split(Pattern.quote("@"))[0]; // ticker-text
                                long when = System.currentTimeMillis();
                                CharSequence contentTitle = "Chat-"+fromName.split(Pattern.quote("@"))[0];
                                CharSequence contentText = message.getBody();
                                Intent notificationIntent = new Intent(context, Chat.class);
                                notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                                        Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                notificationIntent.putExtra("chatUser",fromName.split(Pattern.quote("@"))[0].substring(0,1).toUpperCase()+fromName.split(Pattern.quote("@"))[0].substring(1));
                                notificationIntent.putExtra("msgReceived",msgReceived);
                                //SocketHandler.notificationer = nReply.replace("NOTIFY:FREQUEST:", "");
                                PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                                Notification notification = new Notification(icon, tickerText, when);
                                notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
                                notification.sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                                notification.flags = Notification.FLAG_AUTO_CANCEL;
                                // and this
                                int HELLO_ID = (int) System.currentTimeMillis();
                                mNotificationManager.notify(HELLO_ID, notification);
                                Log.i("test", "hi5");
                            }
                        }
                    }
                }
            }, filter);
        }
    }
}
