package com.example.http;

import android.app.Application;
import android.content.Context;
/**
 * 应用主程
 * Created by DDL on 2016/2/5.
 */
public class BaseApplication extends Application{

	private static BaseApplication mApplication;
	private static Context mContext;

	@Override
	public void onCreate() {
		super.onCreate();
		mApplication = this;
		mContext = getApplicationContext();
	}


	public static BaseApplication getInstance() {
		return mApplication;
	}

	public static Context getContext() {
		return mContext;
	}

}