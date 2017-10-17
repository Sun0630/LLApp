package com.cdbwsoft.library;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cdbwsoft.library.widget.AlertDialog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 应用更新管理器
 * Created by DDL on 2016/2/5.
 */
public class UpdateManager {


	public interface DownloadListener {
		void onPreDownload(String url);

		void onDownloading(int progress);

		void onDownloadComplete();

		void onDownloadFailed();

		void onInstall(File file);
	}

	private Context     mContext;
	private AlertDialog mDownloadDialog;
	private int         mTheme;
	private ProgressBar mProgress;

	private static final int     DOWN_UPDATE   = 1;
	private static final int     DOWN_OVER     = 2;
	private static final int     DOWN_FAIL     = 3;
	private static final int     DOWN_BEFORE   = 4;
	private              boolean interceptFlag = false;
	private DownloadListener          mDownloadListener;
	private RequestPermissionListener mRequestPermissionListener;
	private boolean mApk = true;
	private TextView    mTvTotal;
	private float       mSize;
	private AlertDialog mDialog;
	private boolean     mShowDialog;


	private MessageHandler mHandler = new MessageHandler(this);

	private static class MessageHandler extends Handler {
		private WeakReference<UpdateManager> weakReference;

		public MessageHandler(UpdateManager updateManager) {
			weakReference = new WeakReference<>(updateManager);
		}

		public void dispatchMessage(Message msg) {
			final UpdateManager updateManager = weakReference.get();
			if (updateManager == null) {
				return;
			}
			switch (msg.what) {
				case DOWN_BEFORE:
					if (updateManager.mDownloadListener != null) {
						updateManager.mDownloadListener.onPreDownload((String) msg.obj);
					}
					break;
				case DOWN_UPDATE:
					if (updateManager.mProgress != null) {
						updateManager.mProgress.setProgress(msg.arg1);
					}
					if (updateManager.mDownloadListener != null) {
						updateManager.mDownloadListener.onDownloading(msg.arg1);
					}
					break;
				case DOWN_OVER:
					if (updateManager.mDownloadListener != null) {
						updateManager.mDownloadListener.onDownloadComplete();
					}
					updateManager.install((File) msg.obj);
					break;
				case DOWN_FAIL:
					if (updateManager.mDownloadDialog != null) {
						updateManager.mDownloadDialog.dismiss();

						AlertDialog noticeDialog = updateManager.mDialog;
						noticeDialog.setTitle(AppConfig.UPDATE_DIALOG_TITLE);
						noticeDialog.setMessage(AppConfig.UPDATE_DIALOG_DOWNLOAD_ERROR);
						final String url = (String) msg.obj;
						final float size = updateManager.mSize;
						noticeDialog.setPositiveButton(AppConfig.UPDATE_DIALOG_RETRY, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								dialog.dismiss();
								updateManager.downloadFile(url, size, updateManager.mShowDialog);
							}
						});
						noticeDialog.setNegativeButton(AppConfig.UPDATE_DIALOG_CANCEL, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								dialog.dismiss();
							}
						});
						noticeDialog.show();
					}
					if (updateManager.mDownloadListener != null) {
						updateManager.mDownloadListener.onDownloadFailed();
					}
					break;
				default:
					super.dispatchMessage(msg);
			}
		}
	}

	public UpdateManager(RequestPermissionListener listener) {
		this(listener, 0);
	}

	public UpdateManager(RequestPermissionListener listener, int theme) {
		this.mRequestPermissionListener = listener;
		this.mContext = listener.getContext();
		this.mTheme = theme;
	}

	public void showNoticeDialog(String updateMsg, String downloadUrl, float size) {
		showNoticeDialog(updateMsg, downloadUrl, size, true);
	}

	public void showNoticeDialog(String updateMsg, String downloadUrl, float size, boolean showDialog) {
		AlertDialog dialog;
		if (mTheme > 0) {
			dialog = new AlertDialog(mContext, mTheme);
		} else if (AppConfig.UPDATE_DIALOG_THEME > 0) {
			dialog = new AlertDialog(mContext, AppConfig.UPDATE_DIALOG_THEME);
		} else {
			dialog = new AlertDialog(mContext);
		}
		dialog.setTitle(AppConfig.UPDATE_DIALOG_TITLE);
		dialog.setMessage(updateMsg);
		final String url = downloadUrl;
		final float s = size;
		final boolean show = showDialog;
		dialog.setPositiveButton(AppConfig.UPDATE_DIALOG_SURE, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				mRequestPermissionListener.requestPermission(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, mContext.getString(AppConfig.UPDATE_DIALOG_WRITE_PERMISSION), new Runnable() {
					@Override
					public void run() {
						downloadFile(url, s, show);
					}
				});
			}
		});
		dialog.setNegativeButton(AppConfig.UPDATE_DIALOG_AFTER, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		dialog.show();
		mDialog = dialog;
	}

	public void showNoticeDialog(AlertDialog dialog, String downloadUrl, float size) {
		showNoticeDialog(dialog, downloadUrl, size, true);
	}

	public void showNoticeDialog(AlertDialog dialog, String downloadUrl, float size, boolean showDialog) {
		if (dialog == null) {
			if (mDialog == null) {
				return;
			}
			dialog = mDialog;
		}
		dialog.setTitle(AppConfig.UPDATE_DIALOG_TITLE);
		final String url = downloadUrl;
		final float s = size;
		final boolean show = showDialog;
		dialog.setPositiveButton(AppConfig.UPDATE_DIALOG_SURE, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				mRequestPermissionListener.requestPermission(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, mContext.getString(AppConfig.UPDATE_DIALOG_WRITE_PERMISSION), new Runnable() {
					@Override
					public void run() {
						downloadFile(url, s, show);
					}
				});
			}
		});
		dialog.setNegativeButton(AppConfig.UPDATE_DIALOG_AFTER, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		dialog.show();
		mDialog = dialog;
	}

	public void setDownloadDialog(AlertDialog downloadDialog) {
		if (downloadDialog != mDownloadDialog) {
			mDownloadDialog = downloadDialog;
		}
	}

	public void downloadFile(String downloadUrl, float size, boolean showDialog) {
		if (TextUtils.isEmpty(downloadUrl)) {
			Toast.makeText(mContext, AppConfig.UPDATE_DIALOG_URL_INVALID, Toast.LENGTH_LONG).show();
			return;
		}
		File path = Environment.getExternalStorageDirectory();
		File dirPath = new File(path, "download");

		if (!dirPath.exists()) {
			if (dirPath.mkdir()) {
				Toast.makeText(mContext, AppConfig.UPDATE_DIALOG_NO_PERMISSION, Toast.LENGTH_LONG).show();
				return;
			}
		}

		String fileName = downloadUrl.substring(downloadUrl.lastIndexOf("/"));
		File saveFile = new File(dirPath, fileName);

		if (saveFile.exists()) {
			install(saveFile);
			return;
		}

		try {

			if (mDownloadDialog == null) {
				if (mTheme > 0) {
					mDownloadDialog = new AlertDialog(mContext, mTheme);
				} else if (AppConfig.UPDATE_DIALOG_THEME > 0) {
					mDownloadDialog = new AlertDialog(mContext, AppConfig.UPDATE_DIALOG_THEME);
				} else {
					mDownloadDialog = new AlertDialog(mContext);
				}
				mDownloadDialog.setView(AppConfig.UPDATE_DIALOG_LAYOUT);
			}
			mDownloadDialog.setTitle(AppConfig.UPDATE_DIALOG_TITLE);
			mSize = size;


			mDownloadDialog.setNegativeButton(AppConfig.UPDATE_DIALOG_CANCEL, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					mDownloadThread.interrupt();
					interceptFlag = true;
				}
			});
			mDownloadDialog.show();
			View progressView = mDownloadDialog.findViewById(R.id.progress);
			if (progressView != null && progressView instanceof ProgressBar) {
				mProgress = (ProgressBar) progressView;
			}
			View totalView = mDownloadDialog.findViewById(R.id.tv_total);
			if (totalView != null && totalView instanceof TextView) {
				mTvTotal = (TextView) totalView;
				mTvTotal.setText(mContext.getString(AppConfig.UPDATE_DIALOG_TOTAL_SIZE, mSize / 1024));
			}

			mShowDialog = showDialog;
			saveFile = new File(dirPath, fileName + ".tmp");
			downloadApk(saveFile, downloadUrl);


		} catch (Exception e) {
			if (AppConfig.DEBUG) {
				e.printStackTrace();
			}
		}
	}

	public void showDownloadDialog(String downloadUrl, float size) {
		downloadFile(downloadUrl, size, true);
	}

	private DownloadThread mDownloadThread;

	private class DownloadThread extends Thread {
		String downloadUrl;
		File   saveFile;

		DownloadThread(File saveFile, String downloadUrl) {
			this.saveFile = saveFile;
			this.downloadUrl = downloadUrl;
		}

		@Override
		public void run() {
			try {
				URL url = new URL(downloadUrl);
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.connect();
				int length = conn.getContentLength();
				InputStream is = conn.getInputStream();
				FileOutputStream fos = new FileOutputStream(saveFile);
				mHandler.obtainMessage(DOWN_BEFORE, downloadUrl).sendToTarget();
				int count = 0;
				byte buf[] = new byte[1024];
				do {
					int numRead = is.read(buf);
					count += numRead;
					int progress = (int) (((float) count / length) * 100);
					//更新进度
					mHandler.obtainMessage(DOWN_UPDATE, progress, 0).sendToTarget();
					if (numRead <= 0) {
						File file = new File(saveFile.getCanonicalPath().replace(".tmp", ""));
						saveFile.renameTo(file);
						//下载完成通知安装
						mHandler.obtainMessage(DOWN_OVER, file).sendToTarget();
						break;
					}
					fos.write(buf, 0, numRead);
				} while (!interceptFlag);//点击取消就停止下载.
				fos.close();
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
				mHandler.obtainMessage(DOWN_FAIL, downloadUrl).sendToTarget();
			}
		}
	}

	public DownloadListener getDownloadListener() {
		return mDownloadListener;
	}

	public void setDownloadListener(DownloadListener downloadListener) {
		this.mDownloadListener = downloadListener;
	}

	/**
	 * 是否是安装文件
	 *
	 * @return 是安装文件
	 */
	public boolean isApk() {
		return mApk;
	}

	/**
	 * 设置是否是安装文件
	 *
	 * @param apk 是安装文件
	 */
	public void setApk(boolean apk) {
		this.mApk = apk;
	}

	/**
	 * 下载apk
	 */
	private void downloadApk(File saveFile, String downloadUrl) {
		if (mDownloadThread != null) {
			if (mDownloadThread.isAlive()) {
				return;
			}
		}
		mDownloadThread = new DownloadThread(saveFile, downloadUrl);
		mDownloadThread.start();
	}

	/**
	 * 安装
	 *
	 * @param file 文件位置
	 */
	private void install(File file) {
		if (!file.exists()) {
			return;
		}
		if (mDownloadDialog != null) {
			mDownloadDialog.dismiss();
		}
		if (mDownloadListener != null) {
			mDownloadListener.onInstall(file);
		}
		if (mApk) {
			Intent i = new Intent(Intent.ACTION_VIEW);
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			i.setDataAndType(Uri.parse("file://" + file.toString()), "application/vnd.android.package-archive");
			mContext.startActivity(i);
			android.os.Process.killProcess(android.os.Process.myPid());
		}
	}
}