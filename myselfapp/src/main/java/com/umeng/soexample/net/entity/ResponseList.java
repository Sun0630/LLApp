package com.umeng.soexample.net.entity;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.heaton.liulei.utils.AppConfig;

import java.util.List;


public class ResponseList<T> extends Response {

	private List<T> mList;

	public List<T> getList() {
		return mList;
	}

	public void parseList(Class<T> cls) {
		if(TextUtils.isEmpty(data)){
			return;
		}
		try {
			mList = JSON.parseArray(data, cls);
		} catch (JSONException e) {
			if (AppConfig.DEBUG) {
				e.printStackTrace();
			}
		}
	}
}
