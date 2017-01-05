package com.umeng.soexample.net.entity;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.heaton.liulei.utils.AppConfig;

public class ResponseVo<T> extends Response {

	private T mVo;

	public T getVo() {
		return mVo;
	}

	public void parseVo(Class<T> cls) {
		if(TextUtils.isEmpty(data)){
			return;
		}
		try {
			mVo = JSON.parseObject(data, cls);
		} catch (JSONException e) {
			if (AppConfig.DEBUG) {
				e.printStackTrace();
			}
		}
	}
}
