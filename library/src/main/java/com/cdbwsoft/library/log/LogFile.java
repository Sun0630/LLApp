package com.cdbwsoft.library.log;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import com.cdbwsoft.library.AppConfig;
import com.cdbwsoft.library.BaseApplication;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * 日志文件
 * Created by DDL on 2016/5/17.
 */
public class LogFile {
	public static final String TAG = "LogFile";
	private String           mFilePath;
	private File             mFile;
	private String           mFileName;
	private SimpleDateFormat mTimeFormatter;
	private Map<String, String> mDeviceInfo = new HashMap<>();

	public LogFile(String path, String fileName) {
		mFilePath = path;
		mFileName = fileName;
		mTimeFormatter = new SimpleDateFormat(AppConfig.LOG_TIME_FORMAT, Locale.getDefault());
		initInfo();
	}

	public String getFileName() {
		return mFileName;
	}


	/**
	 * 初始化设备信息
	 */
	private void initInfo() {
		mDeviceInfo.put("versionName", BaseApplication.getInstance().getVersionName());
		mDeviceInfo.put("versionCode", String.valueOf(BaseApplication.getInstance().getVersionCode()));
		/**
		 * 获取系统版本信息
		 */
		Field[] fields = Build.class.getDeclaredFields();
		for (Field field : fields) {
			try {
				field.setAccessible(true);
				mDeviceInfo.put(field.getName(), field.get(null).toString());
				if (AppConfig.DEBUG) {
					Log.d(TAG, field.getName() + " : " + field.get(null));
				}
			} catch (Exception e) {
				if (AppConfig.DEBUG) {
					Log.e(TAG, "an error occured when collect crash info", e);
				}
			}
		}
	}

	/**
	 * 写入日志
	 *
	 * @param content 日志内容
	 */
	public void write(String content) {
		try {
			if (!checkWritable()) {
				return;
			}
			BufferedWriter out = null;
			try {
				out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(mFile, true)));
				out.write(mTimeFormatter.format(new Date()) + "：");
				out.write(content);
			} catch (Exception e) {
				if (AppConfig.DEBUG) {
					e.printStackTrace();
				}
			} finally {
				if (out != null) {
					out.close();
				}
			}
		} catch (IOException e) {
			if (AppConfig.DEBUG) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 文件是否存在
	 *
	 * @return 是否存在
	 */
	public boolean exists() {
		return mFile != null && mFile.exists();
	}

	/**
	 * 记录日志
	 *
	 * @param content 日志内容
	 */
	public void write(Map<String, String> content) {
		if (content != null && content.size() > 0) {
			StringBuilder sb = new StringBuilder();
			for (Map.Entry<String, String> entry : content.entrySet()) {
				String key = entry.getKey();
				String value = entry.getValue();
				sb.append(key).append("=").append(value).append("\n");
			}
			write(sb.toString());
		}
	}

	/**
	 * 检查文件
	 *
	 * @return 是否可用
	 * @throws IOException
	 */
	private boolean checkWritable() throws IOException {
		if (mFile == null) {
			if (TextUtils.isEmpty(mFilePath)) {
				return false;
			}
			mFile = new File(mFilePath, mFileName);
		}
		if (!mFile.exists()) {
			if (!mFile.createNewFile()) {
				return false;
			}else{
				write(mDeviceInfo);
				mDeviceInfo.clear();
			}
		}
		return mFile.canWrite();
	}
}
