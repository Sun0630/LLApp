package com.cdbwsoft.library.utils;

import android.os.Build;
import android.text.TextUtils;

import com.cdbwsoft.library.BaseApplication;
import com.cdbwsoft.library.cache.CacheManager;

import java.util.UUID;

/**
 * 扩展工具
 * Created by DDL on 2016/7/27.
 */
public class ToolUtils {

	public static final String UNIQUE_ID   = "unique_id";
	public static final String UNIQUE_FILE = "unique_id";

	//获得独一无二的Psuedo ID
	public static String getUniqueID() {
		String uniqueID = SharedPreferenceUtils.get(BaseApplication.getInstance(), UNIQUE_ID);
		if (!TextUtils.isEmpty(uniqueID)) {
			return uniqueID;
		}
		uniqueID = CacheManager.getCache(UNIQUE_FILE);
		if (!TextUtils.isEmpty(uniqueID)) {
			return uniqueID;
		}

		String serial;
		String m_szDevIDShort = "35" + Build.BOARD.length() % 10 + Build.BRAND.length() % 10 + Build.CPU_ABI.length() % 10 + Build.DEVICE.length() % 10 + Build.DISPLAY.length() % 10 + Build.HOST.length() % 10 + Build.ID.length() % 10 + Build.MANUFACTURER.length() % 10 + Build.MODEL.length() % 10 + Build.PRODUCT.length() % 10 + Build.TAGS.length() % 10 + Build.TYPE.length() % 10 + Build.USER.length() % 10; //13 位
		try {
			serial = android.os.Build.class.getField("SERIAL").get(null).toString();
			//API>=9 使用serial号
			uniqueID = new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
		} catch (Exception exception) {
			//serial需要一个初始化
			serial = "serial"; // 随便一个初始化
		}
		if (uniqueID == null) {
			//使用硬件信息拼凑出来的15位号码
			uniqueID = new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
		}
		SharedPreferenceUtils.put(BaseApplication.getInstance(), UNIQUE_ID, uniqueID);
		CacheManager.saveCache(UNIQUE_FILE, uniqueID);
		return uniqueID;
	}
}
