package com.cdbwsoft.library.setting;

import android.content.Context;

import com.cdbwsoft.library.AppConfig;
import com.cdbwsoft.library.utils.SharedPreferenceUtils;

/**
 * 设置管理器
 * Created by DDL on 2016/5/17.
 */
public class SettingManager extends SharedPreferenceUtils{

	public SettingManager(Context context){
		super(context,AppConfig.SETTING_FILE);
	}

}
