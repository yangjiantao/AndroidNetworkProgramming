package com.jiantao.socketlib.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerSocketHelper {

    public static void main(String[] args) {
        startServive("192.168.137.55");
    }

    static SocketHandler socketHandler;
    public static void startServive(String hostName) {
        ServerSocket serverSocket = null;
        try {
            InetAddress address = InetAddress.getByName(hostName);
            serverSocket = new ServerSocket(0, 10, address);
//            String hostAddress = address.getHostName();
            int port = serverSocket.getLocalPort();
            System.out.println("hostName = " + hostName + "; port = " + port);
        } catch (IOException e) {
            e.printStackTrace();
            serverSocket = null;
        }
        if (serverSocket == null) {
            System.out.println(" serverSocket init failed ");
            return;
        }
        while (true) {
            Socket socket = null;
            try {
                System.out.println(" serverSocket waiting accept ...");
                socket = serverSocket.accept();
                socketHandler = new SocketHandler(socket);
                Thread workThread = new Thread(socketHandler);    //创建线程
                workThread.start();                                    //启动线程
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
        }
    }

    public static void closeLastHandler(){
        if (socketHandler != null) {
            socketHandler.close();
        }
    }
}
