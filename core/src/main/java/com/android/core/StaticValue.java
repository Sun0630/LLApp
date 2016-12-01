package com.android.core;

import android.content.Intent;
import android.view.View;

import com.android.core.base.AbsBaseActivity;


/**
 * 静态数据类，用于保存全局静态变量
 * Created by SmileSB101 on 2016/9/25.
 */
public class StaticValue {

	public static final String KEY_PATTERN_SHA1 = "pref_key_pattern_sha1";
	public static final String DEFAULT_PATTERN_SHA1 = null;

	public static final String IS_PSW_OPEN = "is_psw_open";//是否打开密码图案验证

	//主题色  白天和夜间模式
	public static final String KEY_THEME = "pref_key_theme";
	//状态栏和toolbar的颜色
	public static final String TOOL_COLOR = "tool_color";
	public static int THEME_MODE = 0;//主题模式   默认是正常   1为夜间模式

	/**
	 * 应用默认的颜色主题
	 */
	public static int color = R.color.success_stroke_color;

	//夜间模式颜色
	public static int black_color = R.color.background_material_dark;

	/**
	 * 主活动
	 */
	public static AbsBaseActivity MainActivity;
	/**
	 * 当前活动
	 */
	public static AbsBaseActivity NowActivity;
	/**
	 * 主视野
	 */
	public static View MainView;
	/**
	 * 播放列表是否为空
	 */
	public static boolean IsPlayListNull;
	/**
	 * 屏幕宽
	 */
	public static int ScreenWidth;
	/**
	 * 屏幕高
	 */
	public static int ScreenHeigtht;

	/**
	 * 主活动结果码
	 */
	public static final int MainActivityRequestCode = 0;
	/**
	 * 本地音乐列表活动结果码
	 */
	public static final int localMusicListRequestCode = 1;
	/**
	 * 扫描音乐活动结果码
	 */
	public static final int MusicScanActivityRequestCode = 2;
	/**
	 * 是否正在播放音乐
	 */
	public static boolean Music_IsPlay = false;

	/**
	 * 内容提供器模式字段
	 */
	public static final String SCHEME = "content://";
	/**
	 * 内容提供器的包名
	 */
	public static final String AUTHORITY = "winter.zxb.smilesb101.winterMusic//";

	/**
	 * 数据库表名枚举
	 */
	public enum DB_LIST_CODE{
		PLAY_LIST,SINGLE_SONG_LIST,SINGER_LIST,ALBUM_LIST,FOLDER_LIST
	};
	/**
	 * 数据库名称
	 */
	public static final String DB_NAME = "WinterMusic.db";
	public static Intent MusicServiceIntent;

	/**
	 * 活动名称
	 */
	public class ActivityName
	{
		/**
		 * 主活动名称
		 */
		public static final String MainActivityName = "MainActivity";
		/**
		 * 本地音乐活动名称
		 */
		public static final String LocalMusicActivityName = "LocalMusicActivity";
	}
}
