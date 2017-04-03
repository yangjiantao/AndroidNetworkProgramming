package com.medlinker.socketclient;

import android.content.Context;

import com.medlinker.socketclient.core.ImServiceHelper;
import com.qpdstudio.logger.Logger;
import com.qpdstudio.logger.Settings;
import com.qpdstudio.logger.util.LogUtil;



/**
 * 外部调用封装。初始化、重连、断开连接、发送消息。
 * Created by jiantao on 2017/3/9.
 */

public class ImManager {

    private static final String TAG = ImManager.class.getSimpleName();

    private static final long DEFAULT_START_ID = 1188L;

    /**
     * 初始化，创建socket连接。
     */
    public static void init(Context context, long startId) {
        LogUtil.d(TAG, " initIM ");
        String ip = "127.0.0.1";
        int port = 6666;
        ImServiceHelper.getInstance(context).connect(startId <= 0 ? DEFAULT_START_ID : startId, ip, port);
    }

    /**
     * 请求gate服务器失败后重连
     */
    private static void reconnect() {
    }

    public static void connect(long startSocketId, String ip, int port) {
    }

    public static void disConnect() {
    }

    /**
     * @param msg
     */
    public static void sendMessage(Context context, byte[] msg) {
        ImServiceHelper.getInstance(context).sendMessage(msg, null);
    }

    public static void logEnable(boolean enable) {
        Logger.init(new Settings().setShowMethodLink(true).setShowThreadInfo(false).setShowLog(enable));
    }
}
