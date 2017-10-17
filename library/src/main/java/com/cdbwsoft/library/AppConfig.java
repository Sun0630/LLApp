package com.cdbwsoft.library;

import android.content.Context;
import android.os.Environment;

import com.cdbwsoft.library.net.entity.BaseResponseFactory;
import com.cdbwsoft.library.net.entity.Response;
import com.cdbwsoft.library.net.entity.ResponseFactory;

import java.io.File;
import java.util.UUID;

/**
 * 应用配置
 * Created by DDL on 2016/2/5.
 */
public class AppConfig {

	public static String PATH_ROOT;                    //扩展存储路径
	public static String PATH_LOGS;                   //日志文件路径
	public static String CACHE_PATH;                  //缓存文件路径
	public static       boolean DEBUG                          = BuildConfig.DEBUG;//是否处于调试模式
	public static final String  META_CHANNEL                   = "HEATON_CHANNEL";//渠道标识
	public static final String  PLATFORM                       = "Android";//平台标识
	public static       String  SERVER                         = "http://api.e-toys.cn/api/";//服务器路径
	public static final String  SETTING_FILE                   = "setting";//设置文件名
	public static final String  CACHE_FILE                     = "cache";//缓存文件名
	public static       String  PATH_NAME                      = "heaton";//扩展存储文件夹
	public static       String  CACHE_DAILY                    = "yyyy-MM-dd";//缓存日期文件名格式
	public static       String  CACHE_TIMELY                   = "HH:mm:ss";//缓存时间文件名格式
	public static       String  LOG_FILE_NAME                  = "yyyy-MM-dd";//日志文件名格式
	public static       String  LOG_EXT                        = ".log";//日志文件扩展名
	public static       String  LOG_TIME_FORMAT                = "yyyy-MM-dd HH:mm:ss";//日志记录时间格式
	public static       int     OTA_DIALOG_LAYOUT              = R.layout.update_layout;//OTA升级对话框样式
	public static       int     UPDATE_DIALOG_LAYOUT           = R.layout.update_layout;//应用升级对话框样式
	public static       int     UPDATE_DIALOG_THEME            = 0;//应用对话框主题
	public static       int     UPDATE_DIALOG_TITLE            = R.string.update_title;//更新标题
	public static       int     UPDATE_DIALOG_CANCEL           = R.string.update_cancel;//更新取消按钮
	public static       int     UPDATE_DIALOG_SURE             = R.string.update_sure;//更新确定按钮
	public static       int     UPDATE_DIALOG_AFTER            = R.string.update_after;//更新稍后按钮
	public static       int     UPDATE_DIALOG_RETRY            = R.string.update_retry;//更新重试按钮
	public static       int     UPDATE_DIALOG_URL_INVALID      = R.string.download_url_invalid;//更新下载地址不正确
	public static       int     UPDATE_DIALOG_WRITE_PERMISSION = R.string.need_write_permission;//写入权限
	public static       int     UPDATE_DIALOG_NOT_AMOUNT       = R.string.not_mount;//未挂载存储
	public static       int     UPDATE_DIALOG_NO_PERMISSION    = R.string.no_permission;//没有权限
	public static       int     UPDATE_DIALOG_DOWNLOAD_ERROR   = R.string.download_error;//下载失败
	public static       int     UPDATE_DIALOG_TOTAL_SIZE       = R.string.total_size;//下载大小字符串
	public static       int     MAX_CONNECTED_DEVICE           = 6;//最多连接设备
	public static       boolean CENTER_SINGLE_BUTTON           = false;//居中显示按钮
	public static       boolean DIALOG_BUTTON_REVERSAL         = false;//按钮反转
	public static       String  RESPONSE_CODE_KEY              = "status";// 返回的错误码字段
	public static       int     RESPONSE_SUCCESS_CODE          = 0;// 返回的成功错误码
	public static       String  RESPONSE_DATA_KEY              = "data";// 返回的数据字段
	public static       String  RESPONSE_MSG_KEY               = "msg";// 返回的消息字段

	public final static String UUID_BLE_SERVICE_TEXT        = "0000fee9-0000-1000-8000-00805f9b34fb";//ble服务UUID字符串
	public final static String UUID_BLE_CHARACTERISTIC_TEXT = "d44bc439-abfd-45a2-b575-925416129600";//ble特性UUID字符串
	public final static String UUID_BLE_DESCRIPTOR_TEXT     = "00002902-0000-1000-8000-00805f9b34fb";//ble描述UUID字符串

	public final static String                 UUID_SECURE_TEXT    = "00001101-0000-1000-8000-00805F9B34FB";//spp加密服务UUID字符串
	public final static String                 UUID_INSECURE_TEXT  = "00001101-0000-1000-8000-00805F9B34FB";//spp未加密服务UUID字符串
	public final static UUID                   UUID_SECURE         = UUID.fromString(UUID_SECURE_TEXT);//spp加密服务UUID
	public final static UUID                   UUID_INSECURE       = UUID.fromString(UUID_INSECURE_TEXT);//spp未加密服务UUID
	public static       Class<?>               RESPONSE_CLASS      = Response.class;// 数据返回对象类
	public static       BaseResponseFactory<?> RESPONSE_FACTORY    = new ResponseFactory();// 数据返回工厂类
	public static       boolean                BIND_DEVICE         = true;//是否绑定设备ID
	public static       boolean                UPLOAD_ERROR_LOGS   = true;//是否上传错误日志
	public static       boolean                UPLOAD_RUNNING_LOGS = true;//是否上传运行日志
	public static       boolean                UPLOAD_APP_INSTALLS = true;//是否上传相关应用信息

	public static void init(Context context) {
		PATH_ROOT = Environment.getExternalStorageDirectory().getPath() + File.separator + PATH_NAME + File.separator;//扩展存储路径
		PATH_LOGS = PATH_ROOT + context.getPackageName() + File.separator + "logs" + File.separator;//日志文件路径
		CACHE_PATH = PATH_ROOT + context.getPackageName() + File.separator + "cache" + File.separator;//缓存文件路径
	}
}
