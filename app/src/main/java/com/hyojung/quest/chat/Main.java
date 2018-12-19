package com.hyojung.quest.chat;

import java.util.Scanner;

import io.socket.emitter.Emitter.Listener;
/**
 *
 * @author OrehOnyah
 * SocketChat 클래스를 사용할 시 필요한 내용
 *
 * https://github.com/socketio/socket.io-client-java#gradle
 * (gradle 부분 참고)
 *
 */
public class Main {
    public static void main(String[] args) {
        SocketChat.init();
        //connected Listener
        SocketChat.setOnConnectedListener(new Listener() {
            public void call(Object... arg0) {
                System.out.println("socket Connected");
            }
        });
        //disconnected Listener
        SocketChat.setOnDisconnectedListener(new Listener() {
            public void call(Object... arg0) {
                System.out.println("socket Disconnected");
            }
        });
        //error listener
        SocketChat.setOnErrorListener(new Listener() {
            public void call(Object... arg0) {
                System.out.println("socket Error");
            }
        });
        //message listener
        SocketChat.setOnMessageListener(new Listener() {
            public void call(Object... arg0) {
                System.out.println("socket Message received:"+ arg0[0].toString());
            }
        });

        //message send
        Scanner sc = new Scanner(System.in);
        while(true) {
            SocketChat.sendMessage("Socketchat Test Console", sc.nextLine());
        }
    }
}
