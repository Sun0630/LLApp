package com.cdbwsoft.library.net.entity;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.cdbwsoft.library.AppConfig;

public class ResponseVo<T> extends Response {

	private T mVo;

	public T getVo() {
		return mVo;
	}

	public void parseVo(Class<T> cls) {
		if(TextUtils.isEmpty(getData())){
			return;
		}
		try {
			mVo = JSON.parseObject(getData(), cls);
		} catch (JSONException e) {
			if (AppConfig.DEBUG) {
				e.printStackTrace();
			}
		}
	}
}
