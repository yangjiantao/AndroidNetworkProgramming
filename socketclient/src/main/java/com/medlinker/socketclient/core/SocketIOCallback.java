package com.medlinker.socketclient.core;

/**
 * socket 连接状态
 * Created by jiantao on 2017/3/4.
 */
public interface SocketIOCallback {

    void onConnect(IClient transceiver);

    void onDisconnect();

    void onConnectFailed(Exception ex);

    void onReceive(int type, byte[] data);
}
