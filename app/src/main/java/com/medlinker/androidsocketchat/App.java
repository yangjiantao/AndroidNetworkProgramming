package com.medlinker.androidsocketchat;

import android.app.Application;

import com.medlinker.socketclient.ImManager;
import com.qpdstudio.logger.Logger;
import com.qpdstudio.logger.Settings;

/**
 * Created by jiantao on 2017/4/4.
 */

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Logger.init(new Settings().setShowMethodLink(true).setShowThreadInfo(false).setShowLog(BuildConfig.DEBUG));
        ImManager.logEnable(BuildConfig.DEBUG);
    }
}
