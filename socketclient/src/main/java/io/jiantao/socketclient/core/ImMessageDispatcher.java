package io.jiantao.socketclient.core;

import android.content.Context;
import android.content.Intent;
import android.os.Process;

import com.qpdstudio.logger.util.LogUtil;

import java.util.Arrays;

/**
 * 消息收发。local+remote
 * Created by jiantao on 2017/3/7.
 */

class ImMessageDispatcher {

    private static final String TAG = Constants.getIMCoreLogTag(ImMessageDispatcher.class.getSimpleName());

    /**
     * 发送服务器返回消息
     * @param contexts
     * @param bytes
     */
    static void broadRemoteMessage(Context contexts, int type, byte[] bytes){
        Intent intent = new Intent(Constants.IM_RECEIVER_ACTION);
        intent.putExtra(Constants.KEY_REMOTE_SOCKET_MSG_DATA, bytes);
        intent.putExtra(Constants.KEY_REMOTE_SOCKET_MSG_TYPE, type);
        intent.putExtra(Constants.KEY_MESSAGE_TYPE, Constants.MESSAGE_TYPE_REMOTE);
        LogUtil.i(TAG, "sendRemoteMessage intent dataArray=%s \n, dataStr = %s timeStamp=%d, pid=%d", Arrays.toString(bytes), new String(bytes), System.currentTimeMillis(), Process.myPid());
        contexts.sendBroadcast(intent, Constants.RECEIVER_PERMISSION);
    }

    /**
     * 发送本地socket连接相关消息
     * @param contexts
     */
    static void broadLocalMessage(Context contexts, int msgWhat){
        Intent intent = new Intent(Constants.IM_RECEIVER_ACTION);
        intent.putExtra(Constants.KEY_LOCAL_SOCKET_MSG, msgWhat);
        intent.putExtra(Constants.KEY_MESSAGE_TYPE, Constants.MESSAGE_TYPE_LOCAL);
        LogUtil.i(TAG, "sendLocalMessage intent msgWhat=%d , timeStamp=%d, pid=%d", msgWhat, System.currentTimeMillis(), Process.myPid());
        contexts.sendBroadcast(intent, Constants.RECEIVER_PERMISSION);
    }

}
