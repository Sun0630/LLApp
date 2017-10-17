package com.cdbwsoft.library.net;

/**
 * 接口定义
 * Created by DDL on 2015/7/20 0020.
 */
public interface Api {
    /**
     * 获取应用新版本
     */
    String APP_LAST_UPDATE     = "app/lastUpdate";
    /**
     * 获取OTA新版本
     */
    String APP_LAST_OTA_UPDATE = "app/lastOtaUpdate";
    /**
     * 上传日志文件
     */
    String APP_UPLOAD_LOG      = "app/uploadLog";
    /**
     * 绑定设备
     */
    String APP_BIND_DEVICE      = "app/bingDevice";
    /**
     * 活动日志文件
     */
    String APP_RUNNING_LOG      = "app/runningLog";
    /**
     * 停留日志文件
     */
    String APP_STAY_LOG      = "app/stayLog";
    /**
     * 应用安装
     */
    String APP_INSTALLED      = "app/installed";

}
