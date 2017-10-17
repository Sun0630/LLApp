package com.cdbwsoft.library.statistics;

import com.cdbwsoft.library.AppConfig;
import com.cdbwsoft.library.cache.CacheManager;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

/**
 * 运行统计
 * Created by DDL on 2016/7/27.
 */
public class Running implements Runnable {

	private static final String ACTIVITY_PATH         = "running" + File.separator;
	private static final String ACTIVITY_FILE_NAME    = "$date$.log";
	private static final String ACTIVITY_FILE         = ACTIVITY_PATH + ACTIVITY_FILE_NAME;
	public static final  int    ACTION_APP_ENTER      = 1;
	public static final  int    ACTION_APP_EXIT       = 2;
	public static final  int    ACTION_CONNECT_DEVICE = 3;
	public static final  int    ACTION_ACTIVE_APP     = 4;
	public static final  int    ACTION_LOGIN_APP      = 5;

	public Running() {
	}

	@Override
	public void run() {
	}

	/**
	 * 记录活动数据
	 *
	 * @param action 动作
	 * @param name   名称
	 * @param data   数据
	 */
	public static void app(int action, String name, String data) {
		StringBuilder builder = new StringBuilder();
		builder.append(System.currentTimeMillis());
		builder.append(",");
		builder.append(name);
		builder.append(",");
		builder.append(action);
		builder.append(",");
		builder.append(data);
		builder.append("\n");
		CacheManager.saveCacheDaily(ACTIVITY_FILE, builder.toString(), true);
	}

	/**
	 * 获取缓存文件
	 *
	 * @return 文件类型
	 */
	public static List<File> getFiles() {
		File path = new File(AppConfig.CACHE_PATH, ACTIVITY_PATH);
		if (path.exists() && path.isDirectory()) {
			File[] files = path.listFiles(new FileFilter() {
				@Override
				public boolean accept(File pathname) {
					/**
					 * 只要不为当天文件，就为上传文件
					 */
					Calendar calendar = Calendar.getInstance();
					int today = calendar.get(Calendar.DAY_OF_YEAR);
					calendar.setTimeInMillis(pathname.lastModified());
					int fileDay = calendar.get(Calendar.DAY_OF_YEAR);
					return today != fileDay;
				}
			});
			return Arrays.asList(files);
		}
		return null;
	}
}
