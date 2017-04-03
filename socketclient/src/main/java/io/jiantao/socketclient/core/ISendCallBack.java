package io.jiantao.socketclient.core;

/**
 * 这里只处理发送失败情况。成功会通过socket返回response
 */
public interface ISendCallBack {

    void onFailed(Exception e);
}