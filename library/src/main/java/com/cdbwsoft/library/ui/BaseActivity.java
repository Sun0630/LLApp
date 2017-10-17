package com.cdbwsoft.library.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseArray;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.cdbwsoft.library.R;
import com.cdbwsoft.library.RequestPermissionListener;
import com.cdbwsoft.library.statistics.Running;

/**
 * 界面基类
 * Created by DDL on 2016/5/16.
 */
public class BaseActivity extends AppCompatActivity implements RequestPermissionListener {


	private AlertDialog mAlertDialog; //对话框对象
	private int                   mPermissionIdx = 0x10;//请求权限索引
	private SparseArray<Runnable> mPermissions   = new SparseArray<>();//请求权限运行列表
	private   ProgressDialog mProgressDialog;//进度对话框
	private   Toast          mToast;//提示消息对象
	protected Handler        mHandler;
	protected boolean mImmersive      = true;
	protected boolean mFullScreen     = true;
	protected boolean mHideNavigation = true;


	private Runnable mHideBarRunnable = new Runnable() {
		@SuppressLint("InlinedApi")
		@Override
		public void run() {
//			int visibility = getWindow().getDecorView().getSystemUiVisibility();
//			int newValue = getWindowVisibility();
//			if(visibility != newValue) {
//				getWindow().getDecorView().setSystemUiVisibility(visibility);
//			}
		}
	};

	protected int getWindowVisibility(){
		int visibility = getWindow().getDecorView().getSystemUiVisibility();
		visibility |= View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
		if (mImmersive) {
			visibility |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
		}
//		if (mFullScreen) {
//			visibility |= View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
//			visibility |= View.SYSTEM_UI_FLAG_FULLSCREEN;
//		}
		if (mHideNavigation) {
			visibility |= View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
			visibility |= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
		}
		return visibility;
	}

	@SuppressLint("Override")
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		Runnable runnable = mPermissions.get(requestCode);
		if (runnable != null) {
			mPermissions.remove(requestCode);
		}
		if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
			runOnUiThread(runnable);
		}
	}

	@Override
	public void requestPermission(String[] permissions, String reason, Runnable runnable) {
		if (Build.VERSION.SDK_INT < 23 || permissions == null || permissions.length == 0) {
			runOnUiThread(runnable);
			return;
		}
		final int requestCode = mPermissionIdx++;
		mPermissions.put(requestCode, runnable);

		/*
			是否需要请求权限
		 */
		boolean granted = true;
		for (String permission : permissions) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
				granted = granted && checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
			}
		}

		if (granted) {
			runOnUiThread(runnable);
			return;
		}

		/*
			是否需要请求弹出窗
		 */
		boolean request = true;
		for (String permission : permissions) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
				request = request && !shouldShowRequestPermissionRationale(permission);
			}
		}

		if (!request) {
			final String[] permissionTemp = permissions;
			AlertDialog dialog = new AlertDialog.Builder(this)
					.setMessage(reason)
					.setPositiveButton(R.string.btn_sure, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
								requestPermissions(permissionTemp, requestCode);
							}
						}
					})
					.setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					}).create();
			dialog.show();
		} else {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
				requestPermissions(permissions, requestCode);
			}
		}
	}

	protected int getContentView(){
		return 0;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		int layout = getContentView();
		if(layout > 0){
			setContentView(layout);
		}
		mHandler = new Handler();

		if (mImmersive) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//				getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
//				getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			}
//			mHandler.post(mHideBarRunnable);
			getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
				@Override
				public void onSystemUiVisibilityChange(int visibility) {
					getWindow().getDecorView().setSystemUiVisibility(getWindowVisibility());
				}
			});
		}

//		int visibility = getWindow().getDecorView().getSystemUiVisibility();
//		visibility |= View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
//			visibility |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
////			visibility |= View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
////			visibility |= View.SYSTEM_UI_FLAG_FULLSCREEN;
//			visibility |= View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
//			visibility |= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
		getWindow().getDecorView().setSystemUiVisibility(getWindowVisibility());
	}


	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if (mImmersive) {
			if (hasFocus) {
//				mHandler.post(mHideBarRunnable);
			}
		}
	}


	@Override
	public Activity getContext() {
		return this;
	}

	/**
	 * 隐藏进度条
	 */
	protected void hideProgress() {
		if (mProgressDialog != null) {
			mProgressDialog.dismiss();
		}
	}

	/**
	 * 显示进度条
	 *
	 * @param title   标题
	 * @param message 消息
	 * @return 进度条对象
	 */
	protected ProgressDialog showProgress(String title, String message) {
		if (mProgressDialog == null) {
			mProgressDialog = new ProgressDialog(this);
			mProgressDialog.setCanceledOnTouchOutside(false);
			mProgressDialog.setIndeterminate(true);
		}
		mProgressDialog.setTitle(title);
		mProgressDialog.setMessage(message);
		mProgressDialog.show();
		return mProgressDialog;
	}

	/**
	 * 显示提示消息
	 *
	 * @param message 消息内容
	 */
	protected void showToast(String message) {
		showToast(message, -1);
	}

	/**
	 * 显示提示消息
	 *
	 * @param message  消息内容
	 * @param duration 显示时间
	 */
	protected void showToast(String message, int duration) {
		if (mToast == null) {
			mToast = Toast.makeText(this, message, Toast.LENGTH_LONG);
		}
		if (duration > -1) {
			mToast.setDuration(duration);
		}
		mToast.setText(message);
		mToast.show();
	}

	/**
	 * 显示对话框
	 *
	 * @param title   标题
	 * @param message 消息
	 * @return 对话框对象
	 */
	protected AlertDialog showDialog(String title, String message) {
		return showDialog(title, message, getResources().getString(R.string.btn_sure), null, null, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
	}

	/**
	 * 显示对话框
	 *
	 * @param title   标题
	 * @param message 消息
	 * @param btnSure 确定按钮
	 * @return 对话框对象
	 */
	protected AlertDialog showDialog(String title, String message, String btnSure) {
		return showDialog(title, message, btnSure, null, null, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
	}

	/**
	 * 显示对话框
	 *
	 * @param title           标题
	 * @param message         内容
	 * @param btnSure         确定按钮
	 * @param btnCancel       取消按钮
	 * @param onClickListener 点击事件
	 * @return 对话框对象
	 */
	protected AlertDialog showDialog(String title, String message, String btnSure, String btnCancel, DialogInterface.OnClickListener onClickListener) {
		return showDialog(title, message, btnSure, btnCancel, null, onClickListener);
	}

	/**
	 * 显示对话框
	 *
	 * @param title           标题
	 * @param message         消息内容
	 * @param btnSure         确定按钮
	 * @param btnCancel       取消按钮
	 * @param btnCenter       中间按钮
	 * @param onClickListener 点击事件
	 * @return 对话框对象
	 */
	protected AlertDialog showDialog(String title, String message, String btnSure, String btnCancel, String btnCenter, DialogInterface.OnClickListener onClickListener) {
		if (mAlertDialog == null) {
			mAlertDialog = new AlertDialog.Builder(this).create();
		}
		mAlertDialog.setCanceledOnTouchOutside(false);
		mAlertDialog.setTitle(title);
		mAlertDialog.setMessage(message);
		mAlertDialog.setButton(AlertDialog.BUTTON_POSITIVE, btnSure, onClickListener);
		mAlertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, btnCancel, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		mAlertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, btnCenter, onClickListener);
		mAlertDialog.show();
		return mAlertDialog;
	}
}
