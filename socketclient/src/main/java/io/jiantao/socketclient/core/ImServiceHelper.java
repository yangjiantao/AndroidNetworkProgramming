package io.jiantao.socketclient.core;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.SparseArray;

import java.util.Map;

/**
 * TODO 1. 加一个requestId,区分每次请求，对应callback，ImBaseReceiver处理相关逻辑。这里需要服务器支持。
 * 1.1 本地生成reqId，随msg一起传给服务器，server处理后返回携带reqId的响应消息。
 * 1.2 区分普通消息和request响应消息。
 * 1.3
 *
 * 2. 收到消息后，判断主进程存活状态，若挂掉，则发送通知栏消息将其唤醒，正常情况则发广播消息。
 * Created by jiantao on 2017/3/4.
 */

public class ImServiceHelper {

    private static ImServiceHelper mInstance;
    private Context mAppContext;
    private int mReqId;
    private final SparseArray<IResponseCallback> mCallbackMap;

    private ImServiceHelper(Context context) {
        this.mAppContext = context.getApplicationContext();
        mCallbackMap = new SparseArray<>();
    }

    public synchronized static ImServiceHelper getInstance(@NonNull Context appContext) {
        if (mInstance == null) {
            mInstance = new ImServiceHelper(appContext);
        }
        return mInstance;
    }

    public void connect(long startSocketId, String ip, int port) {
        Intent intent = createImServiceIntent(Constants.OP_TYPE_CONNECT);
        intent.putExtra(Constants.KEY_IP, ip);
        intent.putExtra(Constants.KEY_PORT, port);
        intent.putExtra(Constants.KEY_START_SOCKET_ID, startSocketId);
        this.mAppContext.startService(intent);
    }

    public void disConnect() {
        Intent intent = createImServiceIntent(Constants.OP_TYPE_DISCONNECT);
        this.mAppContext.startService(intent);
    }

    /**
     * 重连
     */
    public void reConnect() {
        Intent intent = createImServiceIntent(Constants.OP_TYPE_RECONNECT);
        this.mAppContext.startService(intent);
    }

    /**
     * todo 超时检查
     * @param msg
     */
    public void sendMessage(byte[] msg, IResponseCallback responseCallback) {
        if(responseCallback != null){
            mReqId ++;
            mCallbackMap.put(mReqId, responseCallback);
        }
        Intent intent = createImServiceIntent(Constants.OP_TYPE_SEND);
        intent.putExtra(Constants.KEY_REMOTE_SOCKET_MSG_REQID, mReqId);
        intent.putExtra(Constants.KEY_REMOTE_SOCKET_MSG_DATA, msg);
        mAppContext.startService(intent);
    }

    private Intent createImServiceIntent(int opType) {
        Intent i = new Intent(mAppContext, ImService.class);
        i.putExtra(Constants.KEY_OP_TYPE, opType);
        return i;
    }
}
