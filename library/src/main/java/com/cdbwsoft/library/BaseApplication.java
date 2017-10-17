package com.cdbwsoft.library;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.cdbwsoft.library.ble.BleDevice;
import com.cdbwsoft.library.ble.BleDeviceFactory;
import com.cdbwsoft.library.ble.BleListener;
import com.cdbwsoft.library.ble.BleManager;
import com.cdbwsoft.library.cache.CacheManager;
import com.cdbwsoft.library.device.DeviceManager;
import com.cdbwsoft.library.log.LogManager;
import com.cdbwsoft.library.net.HurlStack;
import com.cdbwsoft.library.setting.SettingManager;
import com.cdbwsoft.library.statistics.Running;

/**
 * 应用主程
 * Created by DDL on 2016/2/5.
 */
public class BaseApplication extends Application {

	private static final String TAG = "BaseApplication";
	private        RequestQueue                    mRequestQueue;
	private static BaseApplication                 mApplication;
	private        CacheManager                    mCacheManager;
	private        SettingManager                  mSettingManager;
	private        LogManager                      mLogManager;
	private        String                          mVersionName;
	private        int                             mVersionCode;
	private        BleManager<? extends BleDevice> mBleManager;

	public boolean isDebug() {
		return BuildConfig.DEBUG;
	}

	@Override
	public void onCreate() {
		mApplication = this;
		super.onCreate();
		AppConfig.init(this);

		/**
		 * 多进程判断
		 */
		String name = getProcessName();
		if (!TextUtils.isEmpty(name) && name.indexOf(":") > 0) {
			return;
		}

		/**
		 * 获取当前应用版本号
		 */
		try {
			PackageManager pm = getPackageManager();
			PackageInfo pi = pm.getPackageInfo(getPackageName(), PackageManager.GET_CONFIGURATIONS);
			if (pi != null) {
				mVersionName = pi.versionName == null ? "null" : pi.versionName;
				mVersionCode = pi.versionCode;
			}
		} catch (PackageManager.NameNotFoundException e) {
			if (AppConfig.DEBUG) {
				Log.e(TAG, "an error occured when collect package info", e);
			}
		}

		AppConfig.DEBUG = isDebug();

		try {
			/**
			 * 初始化参数
			 */
			mCacheManager = new CacheManager(this);
			mSettingManager = new SettingManager(this);
			mLogManager = new LogManager(this);
			new CrashHandler(this);

			/**
			 * 检测日志
			 */
			if (isWifi()) {
				mLogManager.checkAndUpload();
			}
			/**
			 * 设备管理
			 */
			DeviceManager.init();
			/**
			 * 同步数据
			 */
			DeviceManager.syncData();
			/**
			 * 同步安装
			 */
			DeviceManager.syncInstall();
			/**
			 * 记录进入
			 */
			Running.app(Running.ACTION_APP_ENTER, "enter", String.valueOf(System.currentTimeMillis()));
			/**
			 * 添加退出Hook
			 */
			Runtime.getRuntime().addShutdownHook(new Thread() {
				public void run() {
					/**
					 * 记录退出
					 */
					Running.app(Running.ACTION_APP_EXIT, "exit", String.valueOf(System.currentTimeMillis()));
				}
			});
		} catch (Exception e) {
			if (AppConfig.DEBUG) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 获取当前进程名称
	 *
	 * @return 进程名称
	 */
	private String getProcessName() {
		int pid = android.os.Process.myPid();
		ActivityManager mActivityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		for (ActivityManager.RunningAppProcessInfo appProcess : mActivityManager.getRunningAppProcesses()) {
			if (appProcess.pid == pid) {
				return appProcess.processName;
			}
		}
		return null;
	}


	/**
	 * 获取版本名
	 *
	 * @return 版本名
	 */
	public String getVersionName() {
		return mVersionName;
	}

	/**
	 * 获取版本号
	 *
	 * @return 版本号
	 */
	public int getVersionCode() {
		return mVersionCode;
	}

	/**
	 * 获取缓存管理器
	 *
	 * @return 缓存管理器
	 */
	public CacheManager getCacheManager() {
		return mCacheManager;
	}

	/**
	 * 获取设置管理器
	 *
	 * @return 设置管理器
	 */
	public SettingManager getSettingManager() {
		return mSettingManager;
	}

	/**
	 * 获取日志管理器
	 *
	 * @return 日志管理器
	 */
	public LogManager getLogManager() {
		return mLogManager;
	}

	/**
	 * 获取程序对象
	 *
	 * @return 程序对象
	 */
	public static BaseApplication getInstance() {
		return mApplication;
	}

	/**
	 * 获取请求队列
	 *
	 * @return 请求队列
	 */
	public RequestQueue getRequestQueue() {
		if (mRequestQueue == null) {
			mRequestQueue = Volley.newRequestQueue(this, new HurlStack());
			mRequestQueue.start();
		}
		return mRequestQueue;
	}

	/**
	 * 是否是wifi环境
	 *
	 * @return 当前在wifi环境下
	 */
	public boolean isWifi() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
		return activeNetInfo != null && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI;
	}

	/**
	 * 获取设备管理器
	 *
	 * @param cls         设备类
	 * @param bleListener 设备监听器
	 * @param <T>         设备类对象
	 * @return 设备管理器
	 */
	public <T extends BleDevice> BleManager<T> getBleManager(Class<T> cls, BleListener<T> bleListener, BleDeviceFactory<T> bleDeviceFactory) {
		if (mBleManager != null && mBleManager.getDeviceClass() == cls) {
			BleManager<T> bleManager = (BleManager<T>) mBleManager;
			if (bleListener != null) {
				bleListener.setBleManager(bleManager);
				bleManager.registerBleListener(bleListener);
			}
			return bleManager;
		} else if (mBleManager != null) {
			mBleManager.release();
		}
		BleManager<T> bleManager = new BleManager<T>(this, bleListener, bleDeviceFactory) {};
		mBleManager = bleManager;
		return bleManager;
	}

	/**
	 * 判断是否是主线程
	 *
	 * @return 是否是主线程
	 */
	public static boolean isInMainThread() {
		return Looper.myLooper() == Looper.getMainLooper();
	}
}