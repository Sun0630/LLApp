package com.heaton.liulei.utils.utils;

/**
 * 作者：刘磊 on 2016/10/31 14:57
 * 公司：希顿科技
 */

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

/**
 * 跟App相关的辅助类
 */
public class AppUtils {
    /**
     * 获取应用程序名称
     */
    public static String getAppName() {
        try {
            PackageManager packageManager = LiuleiUtils.getmContext().getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    LiuleiUtils.getmContext().getPackageName(), 0);
            int labelRes = packageInfo.applicationInfo.labelRes;
            return LiuleiUtils.getmContext().getResources().getString(labelRes);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * [获取应用程序版本名称信息]
     *
     * @return 当前应用的版本名称
     */
    public static String getVersionName() {
        try {
            PackageManager packageManager = LiuleiUtils.getmContext().getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    LiuleiUtils.getmContext().getPackageName(), 0);
            return packageInfo.versionName;

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

}
