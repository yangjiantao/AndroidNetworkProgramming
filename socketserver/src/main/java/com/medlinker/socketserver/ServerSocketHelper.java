package com.medlinker.socketserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerSocketHelper {

    public static void main(String[] args) {
        service();
    }

    public static void start(){
        service();
    }
    private static void service() {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(6666);
            String hostAddress = serverSocket.getInetAddress().getHostAddress();
            String hostName = serverSocket.getInetAddress().getHostName();
            System.out.println("hostAddress = "+hostAddress+"; hostName = "+hostName);
        } catch (IOException e) {
            e.printStackTrace();
            serverSocket = null;
        }
        if(serverSocket == null){
            System.out.println(" serverSocket init failed ");
            return;
        }
        while(true){
            Socket socket=null;
            try{
                System.out.println(" serverSocket waiting accept ...");
                socket=serverSocket.accept();                        //主线程获取客户端连接
                Thread workThread=new Thread(new SocketHandler(socket));    //创建线程
                workThread.start();                                    //启动线程
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }
}
