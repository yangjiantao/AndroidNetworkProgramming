package com.medlinker.androidsocketchat;

import android.app.Application;

import com.medlinker.socketclient.ImManager;

/**
 * Created by jiantao on 2017/4/4.
 */

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
//        Logger.init(new Settings().setShowMethodLink(true).setShowThreadInfo(false).setShowLog(BuildConfig.LOG_DEBUG));
        ImManager.logEnable(BuildConfig.DEBUG);
    }
}
