package com.cdbwsoft.library.net;

import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.cdbwsoft.library.AppConfig;
import com.cdbwsoft.library.net.entity.SuperResponse;

/**
 * 对象请求
 * Created by DDL on 2015/7/20 0020.
 */
public class CommonRequest<T> extends BaseRequest<T> {
	private final ResponseListener<T> mListener;
	public static final String TAG = "CommonRequest";
	private SuperResponse mResponse;

	/**
	 * Creates a new request with the given method.
	 *
	 * @param method        the request {@link Method} to use
	 * @param url           URL to fetch the string at
	 * @param listener      Listener to receive the String response
	 * @param errorListener Error listener, or null to ignore errors
	 */
	public CommonRequest(int method, String url, ResponseListener<T> listener,
	                     com.android.volley.Response.ErrorListener errorListener) {
		super(method, url, errorListener);
		mListener = listener;
	}

	/**
	 * Creates a new POST request.
	 *
	 * @param url           URL to fetch the string at
	 * @param listener      Listener to receive the String response
	 * @param errorListener Error listener, or null to ignore errors
	 */
	public CommonRequest(String url, ResponseListener<T> listener, com.android.volley.Response.ErrorListener errorListener) {
		this(Method.POST, url, listener, errorListener);
	}

	/**
	 * Creates a new POST request.
	 *
	 * @param url      URL to fetch the string at
	 * @param listener Listener to receive the String response
	 */
	public CommonRequest(String url, ResponseListener<T> listener) {
		this(Method.POST, url, listener, null);
	}


	@Override
	protected void deliverResponse(T response) {
		if (mListener != null) {
			mListener.onResponse(mResponse, response);
		}
	}

	@Override
	public void deliverError(VolleyError error) {
		super.deliverError(error);
		if (mListener != null) {
			mListener.onResponse(AppConfig.RESPONSE_FACTORY.newInstance(SuperResponse.NETWORK_ERROR, "网络繁忙"), null);
		}
		if (AppConfig.DEBUG) {
			Log.d(TAG, "请求地址：" + getUrl());
			Log.d(TAG, "请求参数：" + mParams);
			Log.d(TAG, "附加参数：" + JSON.toJSONString(getAttachParams()));
			Log.d(TAG, "返回内容：" + (SuperResponse.NETWORK_ERROR + "网络繁忙"));
		}
	}

	@Override
	protected com.android.volley.Response<T> parseNetworkResponse(NetworkResponse response) {
		T parsed = null;

		try {
			mResponse = JSON.parseObject(response.data, AppConfig.RESPONSE_CLASS);
			if (mListener != null) {
				parsed = JSON.parseObject(mResponse.getData(), mListener.getType());
			}
		} catch (JSONException e) {
			if (mResponse == null) {
				mResponse = AppConfig.RESPONSE_FACTORY.newInstance(SuperResponse.JSON_PARSE_ERROR, "数据格式不正确");
			}
		}

		if (AppConfig.DEBUG) {
			Log.d(TAG, "请求地址：" + getUrl());
			Log.d(TAG, "请求参数：" + mParams);
			Log.d(TAG, "附加参数：" + JSON.toJSONString(getAttachParams()));
			Log.d(TAG, "返回内容：");
			String content = new String(response.data);
			if (!TextUtils.isEmpty(content)) {
				for (String line : content.split("\n")) {
					Log.e(TAG, line);
				}
			}
		}

		return com.android.volley.Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response));
	}
}
