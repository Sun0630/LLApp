package com.cdbwsoft.library;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Looper;
import android.widget.Toast;

import java.io.IOException;

/**
 * 异常捕获
 * Created by DDL on 2016/2/5.
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {
	public static final String TAG = "CrashHandler";
	//程序的Context对象
	private Context                         mContext;
	//系统默认的UncaughtException处理类
	private Thread.UncaughtExceptionHandler mDefaultHandler;

	public CrashHandler(Context context) {
		mContext = context;
		//获取系统默认的UncaughtException处理器
		mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
		//设置该CrashHandler为程序的默认处理器
		Thread.setDefaultUncaughtExceptionHandler(this);
	}

	/**
	 * 当UncaughtException发生时会转入该函数来处理
	 */
	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		try {
			if(AppConfig.DEBUG){
				ex.printStackTrace();
			}
			BaseApplication.getInstance().getLogManager().log(ex);
			if (thread.getId() != Looper.getMainLooper().getThread().getId()) {
				mDefaultHandler.uncaughtException(thread, ex);
				return;
			}
			Toast.makeText(mContext,"抱歉，程序由于出错而终止！",Toast.LENGTH_LONG).show();
//			new AlertDialog.Builder(mContext).setTitle("程序异常").setMessage("抱歉，程序由于出错而终止！").setPositiveButton(R.string.btn_sure, new DialogInterface.OnClickListener() {
//				@Override
//				public void onClick(DialogInterface dialog, int which) {
//					dialog.dismiss();
//					android.os.Process.killProcess(android.os.Process.myPid());
//					System.exit(1);
//				}
//			}).setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
//				@Override
//				public void onClick(DialogInterface dialog, int which) {
//					dialog.dismiss();
//				}
//			}).create().show();

			if (mDefaultHandler != null) {
				mDefaultHandler.uncaughtException(thread, ex);
			}
		} catch (Exception e) {
			if (AppConfig.DEBUG) {
				e.printStackTrace();
			}
		}
	}
	public static void onNativeCrashed() {
		new RuntimeException("crashed here (native trace should follow after the Java trace)").printStackTrace();
		try {

			Process process = Runtime.getRuntime().exec(new String[]{"logcat","-d","-v","threadtime"});

//			log = screaming.readAllOf(process.getInputStream());

		} catch (IOException e) {

			e.printStackTrace();

//			Toast.makeText(CrashHandler.this, e.toString(), Toast.LENGTH_LONG).show();

		}
	}
}