package io.jiantao.socketclient.core;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * client 行为接口
 * Created by jiantao on 2017/3/4.
 */

public interface IClient {

    /**
     * 创建socket连接，并准备接收数据。
     * @param ip
     * @param port
     */
    void connect(String ip, int port);

    /**
     * 断开连接。
     * @param needReconnect 是否需要通知外部，重连。
     */
    void disConnect(boolean needReconnect);

    void reConnect();

    boolean isConnected();

    void send(byte[] bytes, ISendCallBack callback);

    /**
     * @return 返回socket连接状态
     */
    @ClientState int getConnectState();

    /**
     * 正在连接
     */
    int STATE_CONNECTING = 0x0001;

    /**
     * 已连接
     */
    int STATE_CONNECTED = 0x0002;
    /**
     * 连接失败
     */
    int STATE_CONNECT_FAILED = 0x0003;
    /**
     * 已断开连接
     */
    int STATE_DISCONNECT = 0x0004;


    @IntDef({STATE_CONNECTING, STATE_CONNECTED, STATE_CONNECT_FAILED, STATE_DISCONNECT})
    @Retention(RetentionPolicy.SOURCE)
    @interface ClientState{}
}
