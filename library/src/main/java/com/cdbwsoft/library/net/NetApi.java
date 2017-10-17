package com.cdbwsoft.library.net;


import android.text.TextUtils;

import com.cdbwsoft.library.AppConfig;
import com.cdbwsoft.library.BaseApplication;
import com.cdbwsoft.library.net.entity.Response;
import com.cdbwsoft.library.net.entity.ResponseList;
import com.cdbwsoft.library.net.entity.ResponseVo;
import com.cdbwsoft.library.vo.AppVO;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;


/**
 * 网络请求对象
 *
 * @author DDL
 */
public class NetApi implements Api {

	/**
	 * 附加参数
	 *
	 * @param request 请求对象
	 * @param params  请求参数
	 * @param <T>     类型
	 */
	protected static <T> void attachParams(BaseRequest<T> request, String[]... params) {
		request.setAttachParams(params);
	}

	/**
	 * 转换成参数对象
	 *
	 * @param list 参数来源
	 * @return 转换后参数
	 */
	protected static String[][] parseArrayToParams(List<?> list, String key, String[]... params) {
		if (list == null || list.size() == 0) {
			return params;
		}
		List<String[]> listParams = new ArrayList<>();
		if (params != null && params.length > 0) {
			Collections.addAll(listParams, params);
		}
		int i = 0;
		for (Object object : list) {
			String[][] p = parseToParams(object, key + "[" + i++ + "][$key]");
			if (p == null || p.length < 2) {
				continue;
			}
			Collections.addAll(listParams, p);
		}
		return listParams.toArray(new String[listParams.size()][2]);
	}

	/**
	 * 转换成参数对象
	 *
	 * @param object 参数来源
	 * @return 转换后参数
	 */
	protected static String[][] parseToParams(Object object, String[]... params) {
		return parseToParams(object, null, params);
	}

	/**
	 * 转换成参数对象
	 *
	 * @param object 参数来源
	 * @return 转换后参数
	 */
	protected static String[][] parseToParams(Object object, String wrapper, String[]... params) {
		if (object == null) {
			return params;
		}
		List<String[]> listParams = new ArrayList<>();
		if (params != null && params.length > 0) {
			Collections.addAll(listParams, params);
		}
		Class<?> cls = object.getClass();
		while (cls != null && !cls.isAssignableFrom(Object.class)) {
			for (Field field : cls.getDeclaredFields()) {
				if ((field.getModifiers() & Modifier.STATIC) == Modifier.STATIC) {
					continue;
				}
				if (!field.isAccessible()) {
					field.setAccessible(true);
				}
				try {
					Object objectValue = field.get(object);
					if (objectValue == null) {
						continue;
					}
					String value;
					if (objectValue instanceof Date) {
						value = String.valueOf(((Date) objectValue).getTime());
					} else {
						value = String.valueOf(objectValue);
					}
					String key = field.getName();
					if (!TextUtils.isEmpty(wrapper)) {
						key = wrapper.replace("$key", key);
					}
					String[] param = new String[]{key, value};
					listParams.add(param);
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
			cls = cls.getSuperclass();
		}
		return listParams.toArray(new String[listParams.size()][2]);
	}

	/**
	 * 执行请求
	 *
	 * @param url      地址
	 * @param listener 回调
	 * @param params   参数
	 */
	public static <T> void executeListRequest(String url, ResponseListener<ResponseList<T>> listener, String[]... params) {
		if (TextUtils.isEmpty(url)) {
			return;
		}
		ListRequest<T> request = new ListRequest<>(url, listener);
		attachParams(request, params);
		BaseApplication.getInstance().getRequestQueue().add(request);
	}

	/**
	 * 执行请求
	 *
	 * @param url      地址
	 * @param listener 回调
	 * @param params   参数
	 */
	public static <T> void executeBeanRequest(String url, ResponseListener<ResponseVo<T>> listener, String[]... params) {
		BeanRequest<T> request = new BeanRequest<>(url, listener);
		attachParams(request, params);
		BaseApplication.getInstance().getRequestQueue().add(request);
	}

	/**
	 * 执行通用请求
	 *
	 * @param url      地址
	 * @param listener 回调
	 * @param params   参数
	 */
	public static <T> void executeCommonRequest(String url, ResponseListener<T> listener, String[]... params) {
		CommonRequest<T> request = new CommonRequest<>(url, listener);
		attachParams(request, params);
		BaseApplication.getInstance().getRequestQueue().add(request);
	}

	/**
	 * 执行请求
	 *
	 * @param url      地址
	 * @param listener 回调
	 * @param params   参数
	 */
	public static void executeSimpleRequest(String url, ResponseListener<Response> listener, String[]... params) {
		SimpleRequest request = new SimpleRequest(url, listener);
		attachParams(request, params);
		BaseApplication.getInstance().getRequestQueue().add(request);
	}

	/**
	 * 执行请求
	 *
	 * @param url      地址
	 * @param listener 回调
	 * @param params   参数
	 */
	public static void executeFileRequest(String url, FileListener listener, String[]... params) {
		FileRequest request = new FileRequest(url, listener);
		attachParams(request, params);
		BaseApplication.getInstance().getRequestQueue().add(request);
	}


	protected static String getUrl(String path) {
		return AppConfig.SERVER + path;
	}

	/**
	 * 应用相关
	 */
	public static class App {

		/**
		 * 获取新版本
		 *
		 * @param listener 回调对象
		 */
		public static <T> void lastUpdate(String packageName, ResponseListener<ResponseVo<T>> listener) {
			executeBeanRequest(getUrl(APP_LAST_UPDATE), listener, new String[][]{{"app_id", packageName}, {"platform", AppConfig.PLATFORM}});
		}

		/**
		 * 获取OTA新版本
		 *
		 * @param listener 回调对象
		 */
		public static <T> void lastOtaUpdate(String packageName, ResponseListener<ResponseVo<T>> listener) {
			executeBeanRequest(getUrl(APP_LAST_OTA_UPDATE), listener, new String[][]{{"app_id", packageName}});
		}

		/**
		 * 上传日志文件
		 *
		 * @param listener 回调对象
		 */
		public static void uploadLog(String packageName, int versionCode, String versionName, String uniqueId, FileListener listener) {
			executeFileRequest(getUrl(APP_UPLOAD_LOG), listener, new String[][]{{"app_package", packageName}, {"app_version_code", String.valueOf(versionCode)}, {"app_version_name", versionName}, {"unique_id", uniqueId}});
		}

		/**
		 * 绑定设备
		 *
		 * @param listener 回调对象
		 */
		public static void bindDevice(String packageName, String uniqueId, String deviceBrand, String deviceModel, String devicePlatform, int deviceVersion, ResponseListener<Response> listener) {
			executeSimpleRequest(getUrl(APP_BIND_DEVICE), listener, new String[][]{{"device_source", packageName}, {"unique_id", uniqueId}, {"device_brand", deviceBrand}, {"device_model", deviceModel}, {"device_platform", devicePlatform}, {"version", String.valueOf(deviceVersion)}});
		}

		/**
		 * 活动日志文件
		 *
		 * @param listener 回调对象
		 */
		public static void runningLog(String uniqueId, String packageName, int versionCode, String versionName, FileListener listener) {
			executeFileRequest(getUrl(APP_RUNNING_LOG), listener, new String[][]{{"unique_id", uniqueId}, {"app_package", packageName}, {"app_version_code", String.valueOf(versionCode)}, {"app_version_name", versionName}});
		}

		/**
		 * 应用安装信息
		 *
		 * @param listener 回调对象
		 */
		public static void appInstalled(String uniqueId, List<AppVO> apps, ResponseListener<Response> listener) {
			executeSimpleRequest(getUrl(APP_INSTALLED), listener, parseArrayToParams(apps, "apps", new String[]{"unique_id", uniqueId}));
		}
	}
}
