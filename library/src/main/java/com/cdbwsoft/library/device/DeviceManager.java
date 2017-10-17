package com.cdbwsoft.library.device;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import com.cdbwsoft.library.AppConfig;
import com.cdbwsoft.library.BaseApplication;
import com.cdbwsoft.library.net.FileListener;
import com.cdbwsoft.library.net.NetApi;
import com.cdbwsoft.library.net.ResponseListener;
import com.cdbwsoft.library.net.entity.ProgressFileBody;
import com.cdbwsoft.library.net.entity.Response;
import com.cdbwsoft.library.net.entity.SuperResponse;
import com.cdbwsoft.library.statistics.Running;
import com.cdbwsoft.library.utils.SharedPreferenceUtils;
import com.cdbwsoft.library.utils.ToolUtils;
import com.cdbwsoft.library.vo.AppVO;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 设备管理器
 * Created by DDL on 2016/7/27.
 */
public class DeviceManager {

	public static final String TAG  = "DeviceManager";
	public static final String UUID = "uuid";
	public static String uuid;
	public static boolean syncData      = false;
	public static boolean syncInstalled = false;

	/**
	 * 初始化设备ID
	 */
	public static void init() {
		if (!AppConfig.BIND_DEVICE || !TextUtils.isEmpty(DeviceManager.uuid)) {
			return;
		}
		String uuid = SharedPreferenceUtils.get(BaseApplication.getInstance(), UUID);
		if (!TextUtils.isEmpty(uuid)) {
			DeviceManager.uuid = uuid;
			return;
		}
		String uniqueId = ToolUtils.getUniqueID();
		if (TextUtils.isEmpty(uniqueId)) {
			return;
		}
		NetApi.App.bindDevice(BaseApplication.getInstance().getPackageName(), uniqueId, Build.BRAND, Build.MODEL, AppConfig.PLATFORM, Build.VERSION.SDK_INT, new ResponseListener<Response>() {
			@Override
			public void onResponse(Response response) {
				if (response.isSuccess()) {
					DeviceManager.uuid = response.getData();
					SharedPreferenceUtils.put(BaseApplication.getInstance(), UUID, DeviceManager.uuid);
					try {
						syncData();
						syncInstall();
					} catch (Exception e) {
						if (AppConfig.DEBUG) {
							e.printStackTrace();
						}
					}
				}
			}
		});
	}

	/**
	 * 同步安装应用
	 */
	public static void syncInstall() {
		if (!AppConfig.UPLOAD_APP_INSTALLS || !BaseApplication.getInstance().isWifi()) {
			return;
		}
		String uniqueId = ToolUtils.getUniqueID();
		if (TextUtils.isEmpty(uniqueId) || syncInstalled) {
			return;
		}
		ArrayList<AppVO> appList = new ArrayList<>(); //用来存储获取的应用信息数据
		PackageManager packageManager = BaseApplication.getInstance().getPackageManager();
		if (packageManager == null) {
			return;
		}
		if (AppConfig.DEBUG) {
			Log.i(TAG, "App installed getting");
		}
		List<PackageInfo> packages = packageManager.getInstalledPackages(0);
		for (int i = 0; i < packages.size(); i++) {
			PackageInfo packageInfo = packages.get(i);
			if (packageInfo == null || TextUtils.isEmpty(packageInfo.packageName)) {
				return;
			}
			AppVO appVO = new AppVO();
			appVO.app_name = packageInfo.applicationInfo.loadLabel(packageManager).toString();
			appVO.app_package = packageInfo.packageName;
			appVO.app_version_name = packageInfo.versionName;
			appVO.app_version_code = packageInfo.versionCode;
			appVO.install_time = packageInfo.firstInstallTime;
			appVO.update_time = packageInfo.lastUpdateTime;
			if (packageInfo.applicationInfo.metaData != null) {
				appVO.app_channel = packageInfo.applicationInfo.metaData.getString(AppConfig.META_CHANNEL);
			}
			if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
				appList.add(appVO);
			}
		}
		if (AppConfig.DEBUG) {
			Log.i(TAG, "App installed uploading");
		}
		syncInstalled = true;
		NetApi.App.appInstalled(uniqueId, appList, new ResponseListener<Response>() {
			@Override
			public void onResponse(Response response) {
				if (response.isSuccess()) {
					if (AppConfig.DEBUG) {
						Log.i(TAG, "App installed uploaded");
					}
				}
			}
		});

	}

	/**
	 * 同步数据
	 */
	public static void syncData() {
		String uniqueId = ToolUtils.getUniqueID();
		if (!AppConfig.UPLOAD_RUNNING_LOGS || TextUtils.isEmpty(uniqueId) || syncData) {
			return;
		}
		if (!BaseApplication.getInstance().isWifi()) {
			return;
		}
		syncData = true;
		NetApi.App.runningLog(uniqueId, BaseApplication.getInstance().getPackageName(), BaseApplication.getInstance().getVersionCode(), BaseApplication.getInstance().getVersionName(), new FileListener() {
			@Override
			public List<ProgressFileBody> getFiles() {
				List<ProgressFileBody> postFiles = new ArrayList<>();
				/**
				 * 运行日志文件列表
				 */
				List<File> files = Running.getFiles();
				int pos = 0;
				if (files != null && files.size() > 0) {
					for (File file : files) {
						if (file.exists()) {
							postFiles.add(new ProgressFileBody(file, "file" + pos++));
						}
					}
				}
				return postFiles;
			}

			@Override
			public void onResponse(SuperResponse response) {
				if (response.isSuccess()) {
					for (ProgressFileBody fileBody : getFileRequest().getFiles()) {
						fileBody.getFile().delete();
					}
				}
			}
		});
	}
}
