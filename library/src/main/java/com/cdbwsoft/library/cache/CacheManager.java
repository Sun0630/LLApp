package com.cdbwsoft.library.cache;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;

import com.cdbwsoft.library.AppConfig;
import com.cdbwsoft.library.utils.SharedPreferenceUtils;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 缓存管理器
 * Created by DDL on 2016/5/17.
 */
public class CacheManager extends SharedPreferenceUtils{

	public CacheManager(Context context) {
		super(context,AppConfig.CACHE_FILE);
	}

	public void cacheText(String key, String value) {
		put(key, value);
	}

	public String getCacheText(String key) {
		return get(key);
	}

	public boolean hasCacheText(String key) {
		return contains(key);
	}


	/**
	 * 获取文件名称
	 *
	 * @return 文件名称
	 */
	public static String getRollyName(String fileName) {
		if (fileName.contains("$date$")) {
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(AppConfig.CACHE_DAILY, Locale.getDefault());
			fileName = fileName.replace("$date$", simpleDateFormat.format(new Date()));
		}
		if (fileName.contains("$time$")) {
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(AppConfig.CACHE_TIMELY, Locale.getDefault());
			fileName = fileName.replace("$time$", simpleDateFormat.format(new Date()));
		}
		return fileName;
	}

	/**
	 * 保存缓存文件
	 *
	 * @param fileName 文件名称
	 * @param content  文件内容
	 * @return 是否保存成功
	 */
	public static boolean saveCacheDaily(String fileName, String content) {
		return saveCacheDaily(fileName, content, false);
	}

	/**
	 * 保存缓存文件
	 *
	 * @param fileName 文件名
	 * @param content  内容
	 * @param append   是否追加
	 * @return 是否保存成功
	 */
	public static boolean saveCacheDaily(String fileName, String content, boolean append) {
		return saveCache(getRollyName(fileName), content, append);
	}

	/**
	 * 保存缓存文件
	 *
	 * @param fileName 文件名称
	 * @param content  文件内容
	 * @return 是否保存成功
	 */
	public static boolean saveCache(String fileName, String content) {
		return saveCache(fileName, content, false);
	}

	/**
	 * 保存缓存文件
	 *
	 * @param fileName 文件名
	 * @param content  内容
	 * @param append   是否追加
	 * @return 是否保存成功
	 */
	public static boolean saveCache(String fileName, String content, boolean append) {
		File file = new File(AppConfig.CACHE_PATH, fileName);
		if (!file.exists()) {
			try {
				File path = file.getParentFile();
				if (!path.exists() && !path.mkdirs()) {
					return false;
				}
				if (!file.createNewFile()) {
					return false;
				}
			} catch (IOException e) {
				if (AppConfig.DEBUG) {
					e.printStackTrace();
				}
				return false;
			}
		}
		if (!file.canWrite()) {
			return false;
		}
		try {
			BufferedWriter out = null;
			try {
				out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, append)));
				out.write(content);
				return true;
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
		return false;
	}

	/**
	 * 获取缓存文件
	 *
	 * @param fileName 文件名称
	 * @return 内容
	 */
	public static String getCache(String fileName) {
		File file = new File(AppConfig.CACHE_PATH, fileName);
		if (!file.exists()) {
			return null;
		}
		try {
			BufferedReader in = null;
			try {
				in = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
				String content = "";
				String line;
				do {
					line = in.readLine();
					if (line != null) {
						content += line;
					}
				} while (line != null);
				return content;
			} catch (Exception e) {
				if (AppConfig.DEBUG) {
					e.printStackTrace();
				}
			} finally {
				if (in != null) {
					in.close();
				}
			}
		} catch (IOException e) {
			if (AppConfig.DEBUG) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * 保存缓存文件
	 *
	 * @param name   文件名
	 * @param bitmap 图片
	 * @return 图片绝对路径
	 */
	public String saveBitmapCache(String name, Bitmap bitmap) {
		return saveBitmapCache(name, bitmap, Bitmap.CompressFormat.JPEG);
	}

	/**
	 * 保存缓存文件
	 *
	 * @param name   文件名
	 * @param bitmap 图片
	 * @param format 图片格式
	 * @return 图片绝对路径
	 */
	public String saveBitmapCache(String name, Bitmap bitmap, Bitmap.CompressFormat format) {
		if (TextUtils.isEmpty(name) || bitmap == null) {
			return null;
		}
		File file = new File(mContext.getExternalCacheDir(), name);
		try {
			BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
			bitmap.compress(format, 100, bos);
			bos.flush();
			bos.close();
			return file.getAbsolutePath();
		} catch (IOException e) {
			if (AppConfig.DEBUG) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * 读取缓存图片
	 *
	 * @param name 图片名称
	 * @return 图片对象
	 */
	public Bitmap getBitmapCache(String name) {
		if (TextUtils.isEmpty(name)) {
			return null;
		}
		File file = new File(mContext.getExternalCacheDir(), name);
		if (!file.exists()) {
			return null;
		}
		Bitmap bm = null;
		InputStream stream = null;
		try {
			stream = new FileInputStream(file);
			bm = BitmapFactory.decodeStream(stream);
		} catch (Exception e) {
			if (AppConfig.DEBUG) {
				e.printStackTrace();
			}
		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (IOException e) {
					if (AppConfig.DEBUG) {
						e.printStackTrace();
					}
				}
			}
		}
		return bm;
	}
}
