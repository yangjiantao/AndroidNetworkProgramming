package com.medlinker.socketclient.core;

import android.text.TextUtils;

import com.qpdstudio.logger.util.LogUtil;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Arrays;

import com.medlinker.socketclient.core.util.DefaultRetryPolicy;
import com.medlinker.socketclient.core.util.RetryPolicy;


/**
 * socket client封装，外部调用必须放到子线程处理。
 * Created by jiantao on 2017/3/4.
 */

class TcpClient implements IClient {

    private static final String TAG = Constants.getIMCoreLogTag(TcpClient.class.getSimpleName());
    private Socket mSocket;
    private DataOutputStream dataOS;
    private DataInputStream dataIS;

    private String ip;
    private int port;
    private SocketIOCallback mCallback;
    private RetryPolicy mRetryPolicy;

    @ClientState
    private int mState;

    TcpClient(SocketIOCallback callback) {
        this.mCallback = callback;
        mRetryPolicy = new DefaultRetryPolicy();
    }

    @Override
    public void connect(String ip, int port) {
        if (isConnected()) {
            disConnect(false);
        }
        mState = STATE_CONNECTING;
        boolean connectState = false;
        //缓存，以便重连。
        this.ip = ip;
        this.port = port;
        try {
            LogUtil.d(TAG, " connecting  ip=%s , port = %d", ip, port);
            while (true) {
                try {
                    mSocket = new Socket();
                    mSocket.setKeepAlive(true);
                    mSocket.setSoTimeout(2 * 3 * 60 * 1000);//inputStream read 超时时间
                    mSocket.setTcpNoDelay(true);
                    mSocket.connect(new InetSocketAddress(ip, port));
                    if (mSocket.isConnected()) {
                        dataIS = new DataInputStream(mSocket.getInputStream());
                        dataOS = new DataOutputStream(mSocket.getOutputStream());
                        connectState = true;
                    }
                    this.mCallback.onConnect(this);
                    break;//connect sucess
                } catch (IOException e) {
                    mRetryPolicy.retry(e);
                    //间隔5秒，重连。
                    Thread.sleep(5000);
                    LogUtil.e(TAG, " connect IOException =%s , and retry count = %d", e.getMessage(), mRetryPolicy.getCurrentRetryCount());
                }
            }
        } catch (Exception e) {
            //重试后，仍然失败了。发消息提示使用者。
            connectState = false;
            e.printStackTrace();
            LogUtil.e(TAG, " connect IOException = " + e.getMessage());
            mCallback.onConnectFailed(e);
        }
        if (!connectState) {
            disConnect(true);
        } else {
            receiveData();
        }
    }

    private void receiveData() {
        mState = STATE_CONNECTED;
        LogUtil.i(TAG, " receiveData ing isConnect = %b  ", isConnected());
        while (isConnected()) {
            try {
                int type = dataIS.readByte();//读取1位
                int length = dataIS.readChar();//读取2位标记第三段数据长度
                byte[] data = new byte[length];
                LogUtil.i(TAG, " receiveData connected receiveData type = %d, ", type);
                dataIS.readFully(data);
                mCallback.onReceive(type, data);
            } catch (SocketTimeoutException e) {
                LogUtil.e(TAG, " receiveData SocketTimeoutException = " + e.getMessage());
                e.printStackTrace();
                break;
            } catch (IOException e) {
                LogUtil.e(TAG, " receiveData IOException = " + e.getMessage());
                e.printStackTrace();
                break;
            }
        }
        disConnect(true);
    }

    @Override
    public void disConnect(boolean needRec) {
        closeInputStream(dataIS);
        closeOutputStream(dataOS);
        if (mSocket != null) {
            try {
                mSocket.shutdownInput();
                mSocket.shutdownOutput();
                mSocket.close();
                mSocket = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (mCallback != null && needRec) {
            mCallback.onDisconnect();
        }
        mState = STATE_DISCONNECT;
    }


    @Override
    public void reConnect() {
        if (!TextUtils.isEmpty(ip) && port > 0) {
            connect(ip, port);
        }
    }

    @Override
    public boolean isConnected() {
        return mSocket != null && mSocket.isConnected() && (mState == STATE_CONNECTED);
    }

    @Override
    public int getConnectState() {
        return mState;
    }

    @Override
    public void send(byte[] bytes, ISendCallBack callback) {
        synchronized (TcpClient.class) {
            if (isConnected()) {
                try {
                    byte type = 1;
                    dataOS.writeByte(type);
                    dataOS.writeChar(bytes.length);
                    dataOS.write(bytes);
                    dataOS.flush();
                    LogUtil.i(TAG, "send success msg : %s", Arrays.toString(bytes));
                } catch (final IOException e) {
                    callback.onFailed(e);
                    disConnect(true);
                    e.printStackTrace();
                }
            } else {
                callback.onFailed(new Exception("socket is not connected"));
                disConnect(true);
                LogUtil.i(TAG, "socket is not connected !");
            }
        }
    }

    private void closeInputStream(InputStream is) {
        if (is != null) {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void closeOutputStream(OutputStream os) {
        if (os != null) {
            try {
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
