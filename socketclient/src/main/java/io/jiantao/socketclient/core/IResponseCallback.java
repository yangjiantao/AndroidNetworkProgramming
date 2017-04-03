package io.jiantao.socketclient.core;

/**
 * 发送消息回调(外部调用进程使用)
 * Created by jiantao on 2017/3/29.
 */

public interface IResponseCallback {

    /**
     * 发送成功。
     * @param callback FIXME callback为服务器响应返回数据，类型待确认
     */
    void onResponse(Object callback);

    /**
     * 发送异常。
     * @param e 包括socket IO异常、TimeoutException
     */
    void onError(Exception e);
}
