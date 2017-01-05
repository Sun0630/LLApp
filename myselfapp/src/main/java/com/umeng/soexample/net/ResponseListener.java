package com.umeng.soexample.net;

import com.android.volley.Response;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * 返回回调
 * Created by DDL on 2015/7/28 0028.
 */
public abstract class ResponseListener<T> implements Response.Listener<T> {
	private final Type type;

	public ResponseListener() {
		Type superClass = this.getClass().getGenericSuperclass();
		Type[] types = ((ParameterizedType) superClass).getActualTypeArguments();
		this.type = types[0];
	}

	public Type getType() {
		return type;
	}
}
