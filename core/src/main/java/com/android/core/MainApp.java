package com.android.core;

import android.app.Application;
import android.content.Context;


/**
 * @作者: liulei
 * @公司：希顿科技
 */
public class MainApp extends Application {

    private static MainApp ourInstance = new MainApp();
    private static Context mContext;

    public static MainApp getInstance() {
        return ourInstance;
    }

    public static Context getContext() {
        return mContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ourInstance = this;
        mContext = getApplicationContext();

    }
}
