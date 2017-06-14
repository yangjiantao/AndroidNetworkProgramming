package com.medlinker.androidsocketchat;

import android.content.Context;

import com.qpdstudio.logger.util.LogUtil;

import java.nio.charset.Charset;
import java.util.Random;

import com.medlinker.socketclient.ImManager;
import com.medlinker.socketclient.core.Constants;
import com.medlinker.socketclient.core.ImBaseReceiver;

/**
 * Created by jiantao on 2017/4/3.
 * test code
 */

public class MedImReceiver extends ImBaseReceiver {
    private static final String TAG = Constants.getIMCoreLogTag(MedImReceiver.class.getSimpleName());

    static final Random randomTest = new Random();

    @Override
    protected void onReceiveSocketLocalMsg(Context context, int msgWhat) {
        LogUtil.i(TAG, " onReceiveSocketLocalMsg msgWhat = %d", msgWhat);
        switch (msgWhat) {
            case Constants.LOCAL_MSG_CONNECT:
                // socket连接完成
                break;

            case Constants.LOCAL_MSG_DISCONNECTED:
            case Constants.LOCAL_MSG_CONNECT_FAILED:
                //内部连接失败，或者通知断开连接，这里统一重连
                //延迟5s重连 , 这里不判断网络状态，就不用监听网络变化。
//                Observable.timer(5, TimeUnit.SECONDS)
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .subscribe(new Action1<Long>() {
//                            @Override
//                            public void call(Long aLong) {
//                            }
//                        });
//                                ImManager.init(context,-1);
                break;
        }
    }

    @Override
    protected void onReceiveSocketRemoteMsg(Context context, int msgType, byte[] byteArrayExtra) {
        final String msg = new String(byteArrayExtra, Charset.defaultCharset());
        LogUtil.i(TAG, "msgType = %d, msg = %s",msgType, msg);
        //消息回执
        System.out.println("client <<< 收到服务器消息: \""+msg+"\"");
        ImManager.sendMessage(context, (" client 发送de 消息回执 ").getBytes());
        ImManager.sendMessage(context, (" client 发送新消息  id = "+ randomTest.nextInt(100)).getBytes());
    }
}
