package com.jiantao.socketlib.client;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/** @author Created by jiantaoyang on 2018/11/30. */
public class SocketHelper {

  public static void main(String[] args) {
    SocketHelper helper = new SocketHelper();
    String host = "127.0.0.1";
    int port = 59724;
    helper.connectServer(host, port);
  }

  private static final String TAG = "SocketHelper";
  DataInputStream dataIS;
  DataOutputStream dataOS;
  BufferedOutputStream bufferedOutputStream;

  Executor executor;
  Socket mSocket;

  public SocketHelper() {
    executor = Executors.newFixedThreadPool(2);
  }

  public void connectServer(String host, int port) {
    try {
      mSocket = new Socket();
      mSocket.setKeepAlive(true);
      mSocket.setSoTimeout(2 * 3 * 60 * 1000);
      mSocket.setTcpNoDelay(true);
      mSocket.connect(new InetSocketAddress(host, port));
      if (mSocket.isConnected()) {
        dataIS = new DataInputStream(mSocket.getInputStream());
        dataOS = new DataOutputStream(mSocket.getOutputStream());
        bufferedOutputStream = new BufferedOutputStream(mSocket.getOutputStream());

        executor.execute(
            new Runnable() {
              @Override
              public void run() {
                // listening
                receiveData();
              }
            });
        // send a msg
        sendData();
        //sendDataWithDataOutputStream();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void close() {
    if (mSocket != null) {
      try {
        mSocket.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  private String msgStr =
      "djalkgjdaoiugaodkgakljdklajgdkljlJglkdjglLgjdal国家大力就搞定了金刚狼的韩国辣酱过来打几个打开了国际爱劳动工具阿卡丽伽伽个"
          + "都快放假埃里克的结果拉卡宫颈癌UI噢哟打暑假工康迪克了解阿里的结果拉gj打开房间爱老公奇偶奥的国际噶断开逻辑奥克兰的价格jdlagjalkdgjioudioajgkldjgilahgialjlg;djoiauyeojad"
          + "adgjkladgjiaougkjlgdilaujgaljmgiouyaopjgkladjgialyhgialjga"
          + "asdjklgjkalgjoiajgnajkghasdkgj"
          + "dlakjfoiaduajglkajpoj结构胶价格拉激怒我高压我偶尔鸡鸣狗盗卡了几个我欧元区破健康管理对面那看来韩国卡拉多几个垃圾狗jd.com";

  private void sendData() {
    try {
      long millis = System.currentTimeMillis();
      // 1s中发送多少条数据
      long count = 0;
      while (System.currentTimeMillis() < millis + 2000) {
        String data = msgStr.concat("millis").concat(String.valueOf(millis));
        byte[] bytes = data.getBytes();
        byte type = 1;
        int prefixLen = 3;
        int msgDataLen = bytes.length;
        byte[] msg = new byte[msgDataLen + prefixLen];
        // 消息类型
        msg[0] = type;
        msg[1] = (byte) ((msgDataLen >>> 8) & 0xFF);
        msg[2] = (byte) ((msgDataLen >>> 0) & 0xFF);
        System.arraycopy(bytes, 0, msg, prefixLen, msgDataLen);
        bufferedOutputStream.write(msg);
        count++;
        System.out.println(
            "send to server ,  new !!! msg.length = " + msg.length + "; count " + count);
      }
    } catch (final IOException e) {
      e.printStackTrace();
    }
  }

  private void sendDataWithDataOutputStream() {
    try {
      long millis = System.currentTimeMillis();
      // 1s中发送多少条数据
      long count = 0;
      while (System.currentTimeMillis() < millis + 2000) {
        String data = msgStr.concat("millis").concat(String.valueOf(millis));
        byte[] bytes = data.getBytes();
        byte type = 1;
        dataOS.writeByte(type);
        dataOS.writeChar(bytes.length);
        dataOS.write(bytes);
        dataOS.flush();
        count++;
        System.out.println(
            "send to server ,  DataOutputStream !!! msg.length = " + bytes.length + "; count " + count);
      }
    } catch (final IOException e) {
      e.printStackTrace();
    }
  }

  private void receiveData() {
    while (true) {
      try {
        // test 单条数据不超多4096字节
        // byte[] buffer = new byte[4096];
        int type = dataIS.readByte(); // 读取1位
        int length = dataIS.readChar(); // 读取2位标记第三段数据长度
        byte[] data = new byte[length];
        dataIS.readFully(data);
        System.out.println(
            " receiveData connected receiveData type = "
                + type
                + "; data = "
                + new String(data));
      } catch (SocketTimeoutException e) {
        e.printStackTrace();
        break;
      } catch (IOException e) {
        System.out.println(" receiveData IOException = " + e.getMessage());
        e.printStackTrace();
        break;
      }
    }
  }
}
