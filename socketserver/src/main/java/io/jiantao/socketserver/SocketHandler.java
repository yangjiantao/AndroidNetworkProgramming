package io.jiantao.socketserver;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.charset.Charset;

class SocketHandler implements Runnable{
    private Socket socket;
    private DataOutputStream dataOutputStream;
    public SocketHandler(Socket socket){
        try {
            this.socket=socket;
            dataOutputStream = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void run(){
        try{
            System.out.println("新连接:"+socket.getInetAddress()+":"+socket.getPort());
            Thread.sleep(5000);
            startHeartbeat();
            receiveMessage(socket.getInputStream());
        }catch(Exception e){e.printStackTrace();}finally{
            try{
                System.out.println("关闭连接:"+socket.getInetAddress()+":"+socket.getPort());
                if(socket!=null)socket.close();
            }catch(IOException e){
                e.printStackTrace();
            }
        }
    }

    private void startHeartbeat() {
        // 新开线程处理心跳
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(isConnected()){
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    byte[] heartbeat = " server 发动心跳包 ".getBytes();
                    sendMessage(heartbeat);
                }
            }
        }).start();
    }

    private void sendMessage(byte[] bytes) {
        System.out.println("server >>> 发送到客服端消息: \" "+new String(bytes)+"\"");
        if(dataOutputStream != null){
            try {
                byte type = 1;
                dataOutputStream.writeByte(type);
                dataOutputStream.writeChar(bytes.length);
                dataOutputStream.write(bytes);
                dataOutputStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private void receiveMessage(InputStream inputStream) {
        final DataInputStream dataInputStream = new DataInputStream(inputStream);
        while(isConnected()){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                // 数据结构：第一位为数据类型，紧接着后两位为数据payload长度，后面为payload
                int type = dataInputStream.readByte();//读取1位
                int length = dataInputStream.readChar();//读取2位标记第三段数据长度
                byte[] data = new byte[length];
                dataInputStream.readFully(data);
                System.out.println("server <<< 收到客户端消息: \""+ new String(data, Charset.defaultCharset())+"\"");

            } catch (SocketTimeoutException e) {
                e.printStackTrace();
                break;
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }
    }

    private boolean isConnected() {
        return socket != null && socket.isConnected() && !socket.isClosed();
    }
}