package com.umeng.soexample.net;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.heaton.liulei.utils.AppConfig;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * 公共请求
 * Created by DDL on 2015/7/20 0020.
 */
public abstract class BaseRequest<T> extends Request<T> {
	public static final String              TAG     = "BaseRequest";
	protected final     Map<String, String> mParams = new HashMap<>();

	/**
	 * Creates a new request with the given method.
	 *
	 * @param method        the request {@link Method} to use
	 * @param url           URL to fetch the string at
	 * @param errorListener Error listener, or null to ignore errors
	 */
	public BaseRequest(int method, String url, com.android.volley.Response.ErrorListener errorListener) {
		super(method, url, errorListener);
		setRetryPolicy(new DefaultRetryPolicy(15000, 0, 0));
		addParam("user_id","1");
	}

	public void addParam(String key, String value) {
		if (key == null || "".equals(key = key.trim())) {
			return;
		}
		mParams.put(key, value);
	}

	public void addParams(Map<String, String> params) {
		if (params == null || params.size() == 0) {
			return;
		}
		mParams.putAll(params);
	}

	public void deliverError(VolleyError error) {
		super.deliverError(error);
		if (AppConfig.DEBUG) {
			Log.e(TAG, getUrl() + "，请求失败：" + error.getMessage(), error);
			if(error.networkResponse != null) {
				String s = new String(error.networkResponse.data);
				Log.e(TAG, "返回内容：" + new String(error.networkResponse.data), error);
			}
		}
	}

	@Override
	protected Map<String, String> getParams() throws AuthFailureError {
		return mParams;
	}

	public boolean hasBody() {
		return mParams.size() != 0;
	}

	public void writeBody(OutputStream out) throws AuthFailureError, IOException {
		byte[] body = getBody();
		if (body != null) {
			out.write(body);
			out.flush();
		}
	}
}
