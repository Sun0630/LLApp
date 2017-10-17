package com.cdbwsoft.library.net;

import android.text.TextUtils;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.cdbwsoft.library.AppConfig;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * 公共请求
 * Created by DDL on 2015/7/20 0020.
 */
public abstract class BaseRequest<T> extends Request<T> {
	public static final String              TAG           = "BaseRequest";
	protected final     Map<String, String> mParams       = new HashMap<>();
	protected           String[][]          mAttachParams = null;

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

	/**
	 * 设置附加参数
	 *
	 * @param params 参数对象
	 */
	public void setAttachParams(String[][] params) {
		mAttachParams = params;
	}

	/**
	 * 获取附加的参数
	 *
	 * @return 参数对象
	 */
	public String[][] getAttachParams() {
		return mAttachParams;
	}

	public void deliverError(VolleyError error) {
		super.deliverError(error);
		if (AppConfig.DEBUG) {
			Log.e(TAG, getUrl() + "，请求失败：" + error.getMessage(), error);
			if (error.networkResponse != null) {
				String content = new String(error.networkResponse.data);
				if (!TextUtils.isEmpty(content)) {
					for (String line : content.split("\n")) {
						Log.e(TAG, line);
					}
				}
			}
		}
	}

	@Override
	protected Map<String, String> getParams() throws AuthFailureError {
		return mParams;
	}

	public boolean hasBody() {
		return mParams.size() != 0 || (mAttachParams != null && mAttachParams.length > 0);
	}

	public byte[] getBody() throws AuthFailureError {
		Map<String, String> params = getParams();
		if((params == null || params.size() == 0) && (mAttachParams == null || mAttachParams.length ==0 )){
			return null;
		}
		return encodeParameters(params, getParamsEncoding());
	}

	/**
	 * 解析参数
	 *
	 * @param params         参数对象
	 * @param paramsEncoding 编码
	 * @return 解析后的数据
	 */
	private byte[] encodeParameters(Map<String, String> params, String paramsEncoding) {
		StringBuilder encodedParams = new StringBuilder();
		try {
			if(params != null) {
				for (Map.Entry<String, String> entry : params.entrySet()) {
					encodedParams.append(URLEncoder.encode(entry.getKey(), paramsEncoding));
					encodedParams.append('=');
					encodedParams.append(URLEncoder.encode(entry.getValue(), paramsEncoding));
					encodedParams.append('&');
				}
			}
			if (mAttachParams != null && mAttachParams.length > 0) {
				for (String[] param : mAttachParams) {
					if (param == null || param.length < 2 || TextUtils.isEmpty(param[0]) || TextUtils.isEmpty(param[1])) {
						continue;
					}
					encodedParams.append(URLEncoder.encode(param[0], paramsEncoding));
					encodedParams.append('=');
					encodedParams.append(URLEncoder.encode(param[1], paramsEncoding));
					encodedParams.append('&');
				}
			}
			return encodedParams.toString().getBytes(paramsEncoding);
		} catch (UnsupportedEncodingException uee) {
			throw new RuntimeException("Encoding not supported: " + paramsEncoding, uee);
		}
	}


	public void writeBody(OutputStream out) throws AuthFailureError, IOException {
		byte[] body = getBody();
		if (body != null) {
			out.write(body);
			out.flush();
		}
	}
}
