package io.jiantao.socketclient.core;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Process;

import com.qpdstudio.logger.util.LogUtil;

/**
 * 注册时需声明权限：Constants.RECEIVER_PERMISSION
 * Created by jiantao on 2017/3/5.
 */

public class ImBaseReceiver extends BroadcastReceiver {
    private static final String TAG = Constants.getIMCoreLogTag(ImBaseReceiver.class.getSimpleName());
    @Override
    public void onReceive(Context context, Intent intent) {

        if(intent != null && intent.getAction().equalsIgnoreCase(Constants.IM_RECEIVER_ACTION)){
            final int msgType = intent.getIntExtra(Constants.KEY_MESSAGE_TYPE, -1);
            LogUtil.i(TAG, "onReceive with permission action=%s,  pid=%d , timeStamp=%d ,type = %d",
                    intent.getAction(), Process.myPid(), System.currentTimeMillis(), msgType);
            if(msgType == Constants.MESSAGE_TYPE_REMOTE){
                final int type = intent.getIntExtra(Constants.KEY_REMOTE_SOCKET_MSG_TYPE, -999);
                final byte[] data = intent.getByteArrayExtra(Constants.KEY_REMOTE_SOCKET_MSG_DATA);
                onReceiveSocketRemoteMsg(context, type, data);
            }else if(msgType == Constants.MESSAGE_TYPE_LOCAL){
                final int msgWhat = intent.getIntExtra(Constants.KEY_LOCAL_SOCKET_MSG, -1);
                onReceiveSocketLocalMsg(context, msgWhat);
            }

        }
    }

    /**
     * @param context
     * @param msgWhat 本地socket连接相关信息
     */
    protected void onReceiveSocketLocalMsg(Context context, int msgWhat){}

    /**
     * 接收server返回消息
     * @param context
     * @param msgType  消息类型
     * @param byteArrayExtra 消息数据
     */
    protected void onReceiveSocketRemoteMsg(Context context, int msgType, byte[] byteArrayExtra){}
}
