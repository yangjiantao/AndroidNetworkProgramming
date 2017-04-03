package io.jiantao.socketclient.core;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.IBinder;
import android.os.Process;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.qpdstudio.logger.util.LogUtil;

import io.jiantao.socketclient.aidl.ImAIDLService;
import io.jiantao.socketclient.core.util.AsyncTaskExecutor;
import io.jiantao.socketclient.core.util.ImExceptionHandler;
import io.jiantao.socketclient.core.util.RunnablePool;

/**
 * 通过service控制socket创建、断开、发送。
 * 保活：1. bind一个可见的activity 2. 灰色保活。
 * 跨进程IM service，通过broadcast bundle 实现IPC
 * Created by jiantao on 2017/3/6.
 */

public class ImService extends Service {

    private static final String TAG = Constants.getIMCoreLogTag(ImService.class.getSimpleName());

    //灰色保活service id
    private static final int GRAY_SERVICE_ID = 0xff;
    private IClient mIClient;
    private RunnablePool.IRunnbleExecutor mRunnableExecutorImpl;
    private long mStartSocketId;//启动id

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.i(TAG, "onCreate pid=%d", Process.myPid());
        mIClient = new TcpClient(new SocketIOCallback() {
            @Override
            public void onConnect(IClient transceiver) {
                ImMessageDispatcher.broadLocalMessage(getBaseContext(), Constants.LOCAL_MSG_CONNECT);
            }

            @Override
            public void onDisconnect() {
                ImMessageDispatcher.broadLocalMessage(getBaseContext(), Constants.LOCAL_MSG_DISCONNECT);
            }

            @Override
            public void onConnectFailed(Exception ex) {
                ImMessageDispatcher.broadLocalMessage(getBaseContext(), Constants.LOCAL_MSG_CONNECT_FAILED);
            }

            @Override
            public void onReceive(int type, byte[] data) {
                ImMessageDispatcher.broadRemoteMessage(getBaseContext(), type, data);
            }
        });

        mRunnableExecutorImpl = new RunnablePool.IRunnbleExecutor() {
            @Override
            public void execute(int what, Object... params) {
                switch (what) {
                    case Constants.OP_TYPE_CONNECT:
                        mIClient.connect(((String) params[0]), ((int) params[1]));
                        break;
                    case Constants.OP_TYPE_SEND:
                        mIClient.send(((byte[]) params[0]), ((ISendCallBack) params[1]));
                        break;
                    case Constants.OP_TYPE_DISCONNECT:
                        //手动断开，不需要重连。
                        mIClient.disConnect(false);
                        break;

                    case Constants.OP_TYPE_RECONNECT:
                        mIClient.reConnect();
                        break;
                }
            }
        };
        //初始化异常处理
        ImExceptionHandler.getInstance().initialize(getApplicationContext());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            LogUtil.i(TAG, "onStartCommand intent == null , pid=%d , mIClient = %s", Process.myPid(), mIClient);
            if (mIClient != null) {
                AsyncTaskExecutor.execute(RunnablePool.obtain(mRunnableExecutorImpl, Constants.OP_TYPE_RECONNECT));
            }
            return START_STICKY;
        }

        if (Build.VERSION.SDK_INT < 18) {
            startForeground(GRAY_SERVICE_ID, new Notification());
        } else if (Build.VERSION.SDK_INT <= 24) {
            Intent innerIntent = new Intent(this, GrayInnerService.class);
            startService(innerIntent);
            startForeground(GRAY_SERVICE_ID, new Notification());
        }
        int opType = intent.getIntExtra(Constants.KEY_OP_TYPE, -1);

        LogUtil.i(TAG, "onStartCommand intent: %s , flags=%d, startId=%d , pid=%d , opType = %d, isConnected = %b, connectStatus = %d", intent.toString(), flags, startId, Process.myPid(), opType, mIClient.isConnected(), mIClient.getConnectState());
        switch (opType) {
            case Constants.OP_TYPE_INIT:

                break;

            case Constants.OP_TYPE_CONNECT:
                long newStartSocketId = intent.getLongExtra(Constants.KEY_START_SOCKET_ID, -1L);
                if (newStartSocketId <= 0 || (newStartSocketId == mStartSocketId && (mIClient.getConnectState() == IClient.STATE_CONNECTING || mIClient.getConnectState() == IClient.STATE_CONNECTED))) {
                    //userid 不合法 或者 socket已经连接、或者正在连接
                    LogUtil.e(TAG, "onStartCommand intent op connect , intercept newstartUserId = %d, startUserid = %d, socket connectState = %d", newStartSocketId, mStartSocketId, mIClient.getConnectState());
                    break;
                }
                mStartSocketId = newStartSocketId;
                connectSocket(intent);
                break;

            case Constants.OP_TYPE_DISCONNECT:

                AsyncTaskExecutor.execute(RunnablePool.obtain(mRunnableExecutorImpl, Constants.OP_TYPE_DISCONNECT));
                break;

            case Constants.OP_TYPE_RECONNECT:
                if (!mIClient.isConnected()) {
                    AsyncTaskExecutor.execute(RunnablePool.obtain(mRunnableExecutorImpl, Constants.OP_TYPE_RECONNECT));
                }
                break;

            case Constants.OP_TYPE_SEND:
                sendMessage(intent);
                break;
        }
        return START_STICKY;//返回sticky，系统回收后会自动重启service，此时intent为null。
    }

    private void sendMessage(Intent intent) {
        byte[] bytes = intent.getByteArrayExtra(Constants.KEY_REMOTE_SOCKET_MSG_DATA);
        int reqId =  intent.getIntExtra(Constants.KEY_REMOTE_SOCKET_MSG_REQID, -1);
        AsyncTaskExecutor.execute(RunnablePool.obtain(mRunnableExecutorImpl, Constants.OP_TYPE_SEND, bytes, new SendCallBackImpl(reqId)));
    }

    private void connectSocket(Intent intent) {
        String ip = intent.getStringExtra(Constants.KEY_IP);
        int port = intent.getIntExtra(Constants.KEY_PORT, -1);
        if (TextUtils.isEmpty(ip) || port < 0) {
            return;
        }
        AsyncTaskExecutor.execute(RunnablePool.obtain(mRunnableExecutorImpl, Constants.OP_TYPE_CONNECT, ip, port));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtil.i(TAG, "onDestroy pid=%d", Process.myPid());
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        LogUtil.i(TAG, "onBind intent=%s, result binder : %s", intent.toString(), mBinder.toString());
        return mBinder;
    }

    @Override
    public void unbindService(ServiceConnection conn) {
        super.unbindService(conn);
        LogUtil.i(TAG, "unbindService conn=%s ", conn.toString());
    }

    ImAIDLService.Stub mBinder = new ImAIDLService.Stub() {
        @Override
        public boolean isSocketConnected() throws RemoteException {
            return mIClient != null && mIClient.isConnected();
        }
    };

    /**
     * 测试结果：API<=24可行。25/7.1.1版本，通知栏正常状态下看不到icon，但滑下来就看得到icon。
     * 给 API >= 18 的平台上用的灰色保活手段
     */
    public static class GrayInnerService extends Service {

        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            startForeground(GRAY_SERVICE_ID, new Notification());
//            stopForeground(true);
            stopSelf();
            return super.onStartCommand(intent, flags, startId);
        }

        @Nullable
        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }

    }

    private class SendCallBackImpl implements ISendCallBack{
        private int reqId;

        public SendCallBackImpl(int reqId) {
            this.reqId = reqId;
        }

        @Override
        public void onFailed(Exception e) {
            // TODO: 2017/4/2 广播通知到外部

        }
    }
}
