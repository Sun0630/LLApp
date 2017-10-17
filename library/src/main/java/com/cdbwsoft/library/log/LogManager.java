package com.cdbwsoft.library.log;

import android.content.Context;
import android.text.TextUtils;

import com.cdbwsoft.library.AppConfig;
import com.cdbwsoft.library.BaseApplication;
import com.cdbwsoft.library.net.FileListener;
import com.cdbwsoft.library.net.NetApi;
import com.cdbwsoft.library.net.entity.ProgressFileBody;
import com.cdbwsoft.library.net.entity.Response;
import com.cdbwsoft.library.net.entity.SuperResponse;
import com.cdbwsoft.library.utils.ToolUtils;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * 日志管理器
 * Created by DDL on 2016/5/17.
 */
public class LogManager {
	public static final String TAG = "LogManager";
	private Context mContext;
	private LogFile mLogFile;
	private String  mLogPath;

	public LogManager(Context context) {
		mContext = context;
		mLogPath = AppConfig.PATH_LOGS;
	}


	/**
	 * 获取日志文件
	 *
	 * @return 日志文件对象
	 */
	private LogFile getLogFile() {
		// 生成文件名
		String fileName = getFileName();
		if (mLogFile != null) {
			if (fileName.equalsIgnoreCase(mLogFile.getFileName())) {
				return mLogFile;
			}
		}
		File file = new File(mLogPath);
		if (!file.exists()) {
			if (file.mkdirs()) {
				return null;
			}
		}
		if (!file.exists()) {
			return null;
		}
		mLogFile = new LogFile(mLogPath, fileName);
		return mLogFile;
	}

	/**
	 * 获取文件名
	 *
	 * @return 文件名
	 */
	private String getFileName() {
		DateFormat formatter = new SimpleDateFormat(AppConfig.LOG_FILE_NAME, Locale.getDefault());
		return formatter.format(new Date()) + AppConfig.LOG_EXT;
	}

	/**
	 * 记录日志
	 *
	 * @param content 日志内容
	 */
	public void log(String content) {
		LogFile logFile = getLogFile();
		if (logFile != null) {
			logFile.write(content);
		}
	}

	/**
	 * 记录日志
	 *
	 * @param content 日志内容
	 */
	public void log(Map<String, String> content) {
		if (content != null && content.size() > 0) {
			StringBuilder sb = new StringBuilder();
			for (Map.Entry<String, String> entry : content.entrySet()) {
				String key = entry.getKey();
				String value = entry.getValue();
				sb.append(key).append("=").append(value).append("\n");
			}
			log(sb.toString());
		}
	}

	/**
	 * 记录异常信息
	 *
	 * @param ex 异常对象
	 */
	public void log(Throwable ex) {
		if (ex == null) {
			return;
		}
		Writer writer = new StringWriter();
		PrintWriter printWriter = new PrintWriter(writer);
		ex.printStackTrace(printWriter);
		Throwable cause = ex.getCause();
		while (cause != null) {
			cause.printStackTrace(printWriter);
			cause = cause.getCause();
		}
		printWriter.close();
		log(writer.toString());
	}

	/**
	 * 检测是否有日志需要上传
	 */
	public void checkAndUpload() {
		if (!AppConfig.UPLOAD_ERROR_LOGS || TextUtils.isEmpty(mLogPath)) {
			return;
		}
		/**
		 * 检测日志目录
		 */
		File filePath = new File(mLogPath);
		if (!filePath.exists()) {
			return;
		}
		/**
		 * 检测日志文件
		 */
		File[] listFiles = filePath.listFiles();
		if (listFiles == null || listFiles.length == 0) {
			return;
		}
		/**
		 * 创建上传文件列表
		 */
		final List<ProgressFileBody> listUpload = new ArrayList<>();
		Calendar nowCalendar = Calendar.getInstance();
		int days = nowCalendar.get(Calendar.DAY_OF_YEAR);
		for (int i = 0, listFilesLength = listFiles.length; i < listFilesLength; i++) {
			File file = listFiles[i];
			// 过滤目录
			if (file.isDirectory()) {
				continue;
			}
			// 过滤非日志文件
			String fileName = file.getName();
			if (!fileName.endsWith(AppConfig.LOG_EXT)) {
				continue;
			}
			// 过滤当天日志文件
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(file.lastModified());
			if (days == calendar.get(Calendar.DAY_OF_YEAR)) {
				continue;
			}
			// 过滤已上传文件
			if (BaseApplication.getInstance().getCacheManager().hasCacheText(fileName)) {
				continue;
			}
			ProgressFileBody fileBody = new ProgressFileBody(file, "file" + i);
			listUpload.add(fileBody);
		}
		if (listUpload.size() == 0) {
			return;
		}
		String uniqueId = ToolUtils.getUniqueID();
		if (TextUtils.isEmpty(uniqueId)) {
			uniqueId = "";
		}
		NetApi.App.uploadLog(mContext.getPackageName(), BaseApplication.getInstance().getVersionCode(), BaseApplication.getInstance().getVersionName(), uniqueId, new FileListener() {
			@Override
			public List<ProgressFileBody> getFiles() {
				return new ArrayList<>(listUpload);
			}

			@Override
			public void onResponse(SuperResponse response) {
				if (response.isSuccess()) {
					for (ProgressFileBody fileBody : listUpload) {
						BaseApplication.getInstance().getCacheManager().cacheText(fileBody.getFile().getName(), "true");
						fileBody.getFile().delete();
					}
				}
			}
		});
	}
}
