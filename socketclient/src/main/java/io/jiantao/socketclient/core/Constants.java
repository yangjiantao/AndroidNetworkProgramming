package io.jiantao.socketclient.core;

/**
 * Created by jiantao on 2017/3/4.
 */

public class Constants {
    public static final boolean DEBUG = true;

    /**
     * 广播action
     */
    public static final String IM_RECEIVER_ACTION = "android.intent.action.im.receiver_action";

    /**
     * 自定义权限
     */
    public static final String RECEIVER_PERMISSION = "android.intent.permission.im.receiver_permission";

    /**
     * LOG 前缀
     */
    public static final String LOG_PREFIX = "im_core";

    public static final String KEY_IP = "key_str_ip";
    public static final String KEY_PORT = "key_str_port";

    public static final String KEY_REMOTE_SOCKET_MSG_REQID = "key_remote_socket_msg_reqId";
    public static final String KEY_REMOTE_SOCKET_MSG_DATA = "key_remote_socket_msg_data";
    public static final String KEY_REMOTE_SOCKET_MSG_TYPE = "key_remote_socket_msg_type";
    public static final String KEY_MESSAGE_TYPE = "key_message_type";
    public static final String KEY_LOCAL_SOCKET_MSG = "key_local_socket_msg";


    public static final String getIMCoreLogTag(String tag){
        return LOG_PREFIX.concat(tag);
    }

    /**
     * 操作service类型key
     */
    public static final String KEY_OP_TYPE = "KEY_OP_TYPE";


    /**
     * 启动socket服务的用户id
     */
    public static final String KEY_START_SOCKET_ID= "KEY_START_SOCKET_ID";


//    操作类type
    /**
     * socket连接
     */
    public static final int OP_TYPE_CONNECT = 1;


    /**
     * socket 断开连接
     */
    public static final int OP_TYPE_DISCONNECT = 2;


    /**
     * socket 重新连接
     */
    public static final int OP_TYPE_RECONNECT = 3;


    /**
     * 发送消息
     */
    public static final int OP_TYPE_SEND = 4;


    /**
     * service 初始化
     */
    public static final int OP_TYPE_INIT = 10;



    /**
     * 远程socket消息
     */
    public static final int MESSAGE_TYPE_REMOTE = 20;

    /**
     * 本地socket消息。主要是连接相关消息。
     */
    public static final int MESSAGE_TYPE_LOCAL = 21;


    public static final int LOCAL_MSG_CONNECT = 30;
    public static final int LOCAL_MSG_DISCONNECT = 31;
    public static final int LOCAL_MSG_CONNECT_FAILED = 32;
}
