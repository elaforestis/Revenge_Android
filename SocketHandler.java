package com.example.orestis.myapplication;

import java.net.Socket;

public class SocketHandler {
    private static Socket socket;
    public static String notificationer;
    public static int notificationCode;
    public static boolean notificationMode;


    public static synchronized Socket getSocket(){
        return socket;
    }

    public static synchronized void setSocket(Socket socket){
        SocketHandler.socket = socket;
    }
}