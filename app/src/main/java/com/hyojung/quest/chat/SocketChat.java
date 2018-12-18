package com.hyojung.quest.chat;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter.Listener;

public class SocketChat {
    // static variables
    private static SocketChat instance = new SocketChat();
    private static String address = ServerConfig.ADDRESS;
    private static int port = ServerConfig.PORT;

    //constructor
    private SocketChat() {

    }

    //private static methods
    /**
     *
     * @return instance
     */
    private static SocketChat getInstance() {
        return SocketChat.instance;
    }

    //public static methods
    public static void init(String address, int port) {
        getInstance().connect(address, port);
    }

    /**
     * init socket connection
     */
    public static void init() {
        System.out.println("connect for : "+SocketChat.address+":"+SocketChat.port);
        init(SocketChat.address, SocketChat.port);
    }

    /**
     * Listener add for : Socket.EVENT_CONNECT
     * @param onConnected
     */
    public static void setOnConnectedListener(Listener onConnected) {
        getInstance().mSocket.on(Socket.EVENT_CONNECT, onConnected);
    }

    /**
     * Listener add for : Socket.EVENT_DISCONNECT
     * @param onDisconnected
     */
    public static void setOnDisconnectedListener(Listener onDisconnected) {
        getInstance().mSocket.on(Socket.EVENT_DISCONNECT, onDisconnected);
    }

    /**
     * Listener add for : Socket.EVENT_ERROR
     * @param onError
     */
    public static void setOnErrorListener(Listener onError) {
        getInstance().mSocket.on(Socket.EVENT_ERROR, onError);
    }

    /**
     * Listener add for : other events(can manualy added)
     * @param onMessage
     */
    public static void setOnMessageListener(Listener onMessage) {
        getInstance().mSocket.on("receive message", onMessage);
    }

    /**
     * Listener add for : other events(can manualy added)
     * @param onNotification
     */
    public static void setListenerFor(String event, Listener onNotification) {
        getInstance().mSocket.on(event, onNotification);
    }

    /**
     * Get Params : user name, user message
     * @param name
     * @param message
     */
    public static void sendMessage(String name, String message) {
        getInstance().mSocket.emit("send message", name, message);
    }
    // private instance variables
    private Socket mSocket;

    // private methods
    private boolean connect(String address, int port) {
        try {
            mSocket = IO.socket(address+":"+port);
            mSocket.connect();
            return true;
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return false;
        }
    }
}
