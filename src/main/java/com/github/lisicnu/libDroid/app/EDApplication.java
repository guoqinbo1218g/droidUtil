package com.github.lisicnu.libDroid.app;

import android.app.Application;

import com.github.lisicnu.libDroid.helper.BitmapCacheLoader;
import com.github.lisicnu.log4android.LogManager;

/**
 * <p/>
 * <p/>
 * Author: Eden Lee<p/>
 * Date: 2014/11/24 <p/>
 * Email: checkway@outlook.com <p/>
 * Version: 1.0 <p/>
 */
public class EDApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        CrashHandler.getInstance().init(getApplicationContext());

        configLog();
    }

    protected void configLog() {
        LogManager.init(getApplicationContext());
        LogManager.getLogger().setClientID(getApplicationContext().getPackageName());
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();

        BitmapCacheLoader.getInstance().clearCaches();
    }
}
