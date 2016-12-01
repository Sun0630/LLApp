package com.android.core;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;


import com.android.core.manager.SystemBarTintManager;

import java.util.List;

import static android.content.Context.ACTIVITY_SERVICE;

/**
 * 帮助类
 * Created by SmileSB101 on 2016/10/12.
 */

public class Help {
	/**
	 * 检查应用程序是否在前台
	 */
	public static boolean isBackGround(Context context)
	{
		ActivityManager activity = (ActivityManager)StaticValue.MainActivity.getSystemService(ACTIVITY_SERVICE);
		List<ActivityManager.RunningAppProcessInfo> appProcesses = activity.getRunningAppProcesses();
		for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
			if (appProcess.processName.equals(context.getPackageName())) {
				if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_BACKGROUND) {
					Log.i("后台", appProcess.processName);
					return true;
				}else{
					Log.i("前台", appProcess.processName);
					return false;
				}
			}
		}
		return false;
	}
	public static void getScreenWindow(Activity activity)
	{
		WindowManager windowManager = activity.getWindowManager();
		DisplayMetrics metrics = new DisplayMetrics();
		windowManager.getDefaultDisplay().getMetrics(metrics);
		StaticValue.ScreenWidth = metrics.widthPixels;
		StaticValue.ScreenHeigtht = metrics.heightPixels;
	}
	/**
	 *创建播放栏，通过悬浮窗的方式，不过需要解决在桌面任然显示的问题
	 * @param Layout_Id 需要添加的视野的布局
	 */
	public static void ShowPlayBar_FloatWindow(int Layout_Id)
	{
		if(StaticValue.MainActivity != null) {
			View playbarView = LayoutInflater.from(StaticValue.MainActivity).inflate(Layout_Id,null);WindowManager.LayoutParams params = new WindowManager.LayoutParams(WindowManager.LayoutParams.TYPE_TOAST);//TYPE_TOAST 是关键  这样就不需要悬浮窗权限了
			params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;//禁止获得焦点，不然下面的界面无法接收到触摸点击事件  无法接收onBack
			params.width = WindowManager.LayoutParams.MATCH_PARENT;
			params.height = WindowManager.LayoutParams.WRAP_CONTENT;
			params.gravity = Gravity.BOTTOM;//对齐到底部
			WindowManager windowManager = (WindowManager)StaticValue.MainActivity.getApplication().getSystemService(StaticValue.MainActivity.getApplication().WINDOW_SERVICE);
			windowManager.addView(playbarView, params);
		}
		else
		{
			throw new Error("同步上下文没有得到赋值！");
		}
	}


	/**
	 * 更新系统状态栏颜色
	 * @param activity 需要同步颜色的活动
	 * @param status_color_ID 颜色的ID
	 */
	public static void initSystemBar(Activity activity,int status_color_ID) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			setTranslucentStatus(activity, true);
		}
		SystemBarTintManager tintManager = new SystemBarTintManager(activity);
		tintManager.setStatusBarTintEnabled(true);
        // 使用颜色资源
		tintManager.setStatusBarTintResource(status_color_ID);

	}

	/**
	 * 改变状态栏颜色
	 * @param activity
	 * @param boo
	 */
	@TargetApi(19)
	private static void setTranslucentStatus(Activity activity,boolean boo)
	{
		Window win = activity.getWindow();
		WindowManager.LayoutParams winParams = win.getAttributes();
		final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
		if (boo) {
			winParams.flags |= bits;
		} else {
			winParams.flags &= ~bits;
		}
		win.setAttributes(winParams);
	}


}
