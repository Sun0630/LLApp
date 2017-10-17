package com.cdbwsoft.library.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;

import java.util.Map;
import java.util.Set;

/**
 * 偏好设置工具类
 * Created by DDL on 2016/5/9.
 */
public class SharedPreferenceUtils {

	/**
	 * 保存在手机里面的文件名
	 */
	public static final String FILE_NAME = "common";

	protected String mFileName;
	protected Context mContext;

	public SharedPreferenceUtils(Context context,String fileName) {
		mContext = context;
		mFileName = fileName;
	}

	/**
	 * 保存数据的方法，我们需要拿到保存数据的具体类型，然后根据类型调用不同的保存方法
	 *
	 * @param key     键名
	 * @param object  对象
	 */
	public <T> void put(String key, T object) {
		put(mContext, key, object, mFileName);
	}

	/**
	 * 保存数据的方法，我们需要拿到保存数据的具体类型，然后根据类型调用不同的保存方法
	 *
	 * @param context 程序对象
	 * @param key     键名
	 * @param object  对象
	 */
	public static <T> void put(Context context, String key, T object) {
		put(context, key, object, FILE_NAME);
	}

	/**
	 * 保存数据的方法，我们需要拿到保存数据的具体类型，然后根据类型调用不同的保存方法
	 *
	 * @param context  程序对象
	 * @param key      键名
	 * @param object   对象
	 * @param fileName 文件名
	 */
	public static <T> void put(Context context, String key, T object, String fileName) {
		SharedPreferences sp = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		if (object instanceof String) {
			editor.putString(key, (String) object);
		} else if (object instanceof Integer) {
			editor.putInt(key, (Integer) object);
		} else if (object instanceof Boolean) {
			editor.putBoolean(key, (Boolean) object);
		} else if (object instanceof Float) {
			editor.putFloat(key, (Float) object);
		} else if (object instanceof Long) {
			editor.putLong(key, (Long) object);
		} else {
			editor.putString(key, object.toString());
		}
		editor.apply();
	}

	/**
	 * 保存数据的方法，我们需要拿到保存数据的具体类型，然后根据类型调用不同的保存方法
	 *
	 * @param key     键名
	 * @param object  对象
	 */
	public void put(String key, Set<String> object) {
		put(mContext, key, object, mFileName);
	}

	/**
	 * 保存数据的方法，我们需要拿到保存数据的具体类型，然后根据类型调用不同的保存方法
	 *
	 * @param context 程序对象
	 * @param key     键名
	 * @param object  对象
	 */
	public static void put(Context context, String key, Set<String> object) {
		put(context, key, object, FILE_NAME);
	}

	/**
	 * 保存数据的方法，我们需要拿到保存数据的具体类型，然后根据类型调用不同的保存方法
	 *
	 * @param context 程序对象
	 * @param key     键名
	 * @param object  对象
	 */
	public static void put(Context context, String key, Set<String> object, String fileName) {
		SharedPreferences sp = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putStringSet(key, object);
		editor.apply();
	}

	/**
	 * 得到保存数据的方法，我们根据默认值得到保存的数据的具体类型，然后调用相对于的方法获取值
	 *
	 * @param key     键名
	 * @return 值
	 */
	public String get(String key) {
		return  get(key, (String)null);
	}

	/**
	 * 得到保存数据的方法，我们根据默认值得到保存的数据的具体类型，然后调用相对于的方法获取值
	 *
	 * @param context 程序对象
	 * @param key     键名
	 * @return 值
	 */
	public static String get(Context context, String key) {
		return get(context, key, (String)null);
	}

	/**
	 * 得到保存数据的方法，我们根据默认值得到保存的数据的具体类型，然后调用相对于的方法获取值
	 *
	 * @param key           键名
	 * @param defaultObject 默认对象
	 * @return 值
	 */
	public <T> T get(String key, T defaultObject) {
		return get(mContext, key, defaultObject, mFileName);
	}

	/**
	 * 得到保存数据的方法，我们根据默认值得到保存的数据的具体类型，然后调用相对于的方法获取值
	 *
	 * @param context       程序对象
	 * @param key           键名
	 * @param defaultObject 默认对象
	 * @return 值
	 */
	public static <T> T get(Context context, String key, T defaultObject) {
		return get(context, key, defaultObject, FILE_NAME);
	}

	/**
	 * 得到保存数据的方法，我们根据默认值得到保存的数据的具体类型，然后调用相对于的方法获取值
	 *
	 * @param context       程序对象
	 * @param key           键名
	 * @param defaultObject 默认对象
	 * @return 值
	 */
	@Nullable
	@SuppressWarnings("unchecked")
	public static <T> T get(Context context, String key, T defaultObject, String fileName) {
		SharedPreferences sp = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);

		if (defaultObject == null || defaultObject instanceof String) {
			return (T)sp.getString(key, (String) defaultObject);
		} else if (defaultObject instanceof Integer) {
			return (T)Integer.valueOf(sp.getInt(key, (Integer) defaultObject));
		} else if (defaultObject instanceof Boolean) {
			return (T)Boolean.valueOf(sp.getBoolean(key, (Boolean) defaultObject));
		} else if (defaultObject instanceof Float) {
			return (T)Float.valueOf(sp.getFloat(key, (Float) defaultObject));
		} else if (defaultObject instanceof Long) {
			return (T) Long.valueOf(sp.getLong(key, (Long) defaultObject));
		} else if (defaultObject instanceof Set) {
			return (T)sp.getStringSet(key, (Set<String>) defaultObject);
		}
		return null;
	}
	/**
	 * 移除某个key值已经对应的值
	 *
	 * @param key     键名
	 */
	public void remove(String key) {
		remove(mContext, key, mFileName);
	}

	/**
	 * 移除某个key值已经对应的值
	 *
	 * @param context 程序对象
	 * @param key     键名
	 */
	public static void remove(Context context, String key) {
		remove(context, key, FILE_NAME);
	}

	/**
	 * 移除某个key值已经对应的值
	 *
	 * @param context 程序对象
	 * @param key     键名
	 */
	public static void remove(Context context, String key, String fileName) {
		SharedPreferences sp = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.remove(key);
		editor.apply();
	}

	/**
	 * 清除所有数据
	 *
	 */
	public void clear() {
		clear(mContext, mFileName);
	}

	/**
	 * 清除所有数据
	 *
	 * @param context 程序对象
	 */
	public static void clear(Context context) {
		clear(context, FILE_NAME);
	}

	/**
	 * 清除所有数据
	 *
	 * @param context 程序对象
	 */
	public static void clear(Context context, String fileName) {
		SharedPreferences sp = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.clear();
		editor.apply();
	}

	/**
	 * 查询某个key是否已经存在
	 *
	 * @param key     键名
	 * @return 值
	 */
	public boolean contains(String key) {
		return contains(mContext, key, mFileName);
	}

	/**
	 * 查询某个key是否已经存在
	 *
	 * @param context 程序对象
	 * @param key     键名
	 * @return 值
	 */
	public static boolean contains(Context context, String key) {
		return contains(context, key, FILE_NAME);
	}

	/**
	 * 查询某个key是否已经存在
	 *
	 * @param context 程序对象
	 * @param key     键名
	 * @return 值
	 */
	public static boolean contains(Context context, String key, String fileName) {
		SharedPreferences sp = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
		return sp.contains(key);
	}

	/**
	 * 返回所有的键值对
	 *
	 * @return 值
	 */
	public Map<String, ?> getAll() {
		return getAll(mContext, mFileName);
	}

	/**
	 * 返回所有的键值对
	 *
	 * @param context 程序对象
	 * @return 值
	 */
	public static Map<String, ?> getAll(Context context) {
		return getAll(context, FILE_NAME);
	}

	/**
	 * 返回所有的键值对
	 *
	 * @param context 程序对象
	 * @return 值
	 */
	public static Map<String, ?> getAll(Context context, String fileName) {
		SharedPreferences sp = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
		return sp.getAll();
	}
}
