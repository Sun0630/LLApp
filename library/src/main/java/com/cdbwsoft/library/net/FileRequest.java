package com.cdbwsoft.library.net;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.cdbwsoft.library.AppConfig;
import com.cdbwsoft.library.net.entity.ProgressFileBody;
import com.cdbwsoft.library.net.entity.Response;
import com.cdbwsoft.library.net.entity.SuperResponse;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 上传文件请求
 * Created by DDL on 2015/7/20 0020.
 */
public class FileRequest extends BaseRequest<SuperResponse> implements ProgressFileBody.ProgressListener, Handler.Callback {
	public static final int MSG_PROGRESS = 0x1001;
	private final FileListener mListener;
	private             List<ProgressFileBody> mFiles       = new ArrayList<>();
	private             String                 boundary     = "----AndroidFormBoundary7MA4YWxkTrZu0gW"; //数据分隔线
	public static final String                 TAG          = "FileRequest";
	private             long                   mTotalAmount = 0;
	private             Handler                mHandler     = new Handler(Looper.getMainLooper(), this);

	/**
	 * Creates a new request with the given method.
	 *
	 * @param method        the request {@link Method} to use
	 * @param url           URL to fetch the string at
	 * @param listener      Listener to receive the String response
	 * @param errorListener Error listener, or null to ignore errors
	 */
	public FileRequest(int method, String url, FileListener listener,
	                   com.android.volley.Response.ErrorListener errorListener) {
		super(method, url, errorListener);
		mListener = listener;
		mListener.setFileRequest(this);
		initFileBody();
	}

	/**
	 * Creates a new POST request.
	 *
	 * @param url           URL to fetch the string at
	 * @param listener      Listener to receive the String response
	 * @param errorListener Error listener, or null to ignore errors
	 */
	public FileRequest(String url, FileListener listener, com.android.volley.Response.ErrorListener errorListener) {
		this(Method.POST, url, listener, errorListener);
	}

	/**
	 * Creates a new POST request.
	 *
	 * @param url      URL to fetch the string at
	 * @param listener Listener to receive the String response
	 */
	public FileRequest(String url, FileListener listener) {
		this(Method.POST, url, listener, null);
	}

	@Override
	protected void deliverResponse(SuperResponse response) {
		if (mListener != null) {
			mListener.onResponse(response);
		}
	}

	@Override
	public boolean handleMessage(Message msg) {
		if (msg == null) {
			return false;
		}
		switch (msg.what) {
			case MSG_PROGRESS:
				if (msg.obj == null) {
					return false;
				}
				long current = (Long) msg.obj;
				if (mListener != null && mFiles != null) {
					current = 0;
					for (ProgressFileBody fileBody : mFiles) {
						current += Math.min(fileBody.getCurrent(), fileBody.getTotal());
					}
					mListener.onProgress(current, mTotalAmount);
				}
				return true;
		}
		return false;
	}

	/**
	 * 初始化文件体
	 */
	private void initFileBody() {
		if (mListener != null) {
			List<ProgressFileBody> files = mListener.getFiles();
			if (files != null) {
				for (int i = 0; i < files.size(); i++) {
					ProgressFileBody fileBody = files.get(i);
					fileBody.setProgressListener(this);
					fileBody.setIndex(i);
					mFiles.add(fileBody);
					mTotalAmount += fileBody.getContentLength();
				}
			}
		}
	}

	@Override
	public void deliverError(VolleyError error) {
		super.deliverError(error);
		if (mListener != null) {
			mListener.onResponse(Response.result(Response.NETWORK_ERROR, "网络繁忙"));
		}
		if (AppConfig.DEBUG) {
			Log.d(TAG, "请求地址：" + getUrl());
			Log.d(TAG, "请求参数：" + mParams);
			Log.d(TAG, "附加参数：" + JSON.toJSONString(getAttachParams()));
			Log.d(TAG, "返回内容：" + (Response.NETWORK_ERROR + "网络繁忙"));
		}
	}

	@Override
	public boolean hasBody() {
		return super.hasBody() || (mFiles != null && mFiles.size() > 0);
	}

	@Override
	public void update(long current, long total, int index) {
		mHandler.obtainMessage(MSG_PROGRESS, current).sendToTarget();
	}

	@Override
	public void writeBody(OutputStream out) throws AuthFailureError, IOException {
		if (mFiles != null && mFiles.size() > 0) {
			StringBuilder sb = new StringBuilder();
			Map<String, String> params = getParams();
			if (params != null && params.size() > 0) {
				for (String key : params.keySet()) {

					String value = params.get(key);
					sb.append("--");
					sb.append(boundary);
					sb.append("\r\n");
					//key
					sb.append("Content-Disposition: form-data;name=\"");
					sb.append(key);
					sb.append("\"");
					sb.append("\r\n");

					//换行
					sb.append("\r\n");

					//value
					sb.append(value);
					sb.append("\r\n");
				}
			}

			String[][] attachParams = getAttachParams();
			if (attachParams != null && attachParams.length > 0) {
				for (String[] param : attachParams) {
					if (param == null || param.length < 2 || TextUtils.isEmpty(param[0]) || TextUtils.isEmpty(param[1])) {
						continue;
					}
					String value = param[1];
					sb.append("--");
					sb.append(boundary);
					sb.append("\r\n");
					//key
					sb.append("Content-Disposition: form-data;name=\"");
					sb.append(param[0]);
					sb.append("\"");
					sb.append("\r\n");

					//换行
					sb.append("\r\n");

					//value
					sb.append(value);
					sb.append("\r\n");
				}
			}
			if (sb.length() > 0) {
				out.write(sb.toString().getBytes());
			}
		} else {
			out.write(super.getBody());
		}

		if (mFiles != null && mFiles.size() > 0) {
			for (ProgressFileBody fileBody : mFiles) {
				out.write("--".getBytes());
				out.write(boundary.getBytes());
				out.write("\r\n".getBytes());
				StringBuilder fileNameSb = new StringBuilder();
				fileNameSb.append("Content-Disposition: form-data; name=");
				fileNameSb.append("\"").append(fileBody.getKeyName()).append("\"");
				fileNameSb.append("; filename=\"").append(fileBody.getFilename()).append("\"\r\n");
				fileNameSb.append("Content-Type: ").append(fileBody.getMimeType()).append("\r\n\r\n");
				out.write(fileNameSb.toString().getBytes());

				fileBody.writeTo(out);

				out.write("\r\n".getBytes());
//				out.writeChars(boundary);
//				out.writeChars("\r\n");
//				out.writeChars("Content-Disposition: form-data; name=\"" + fileBody.getKeyName() + "\"; filename=\"" + fileBody.getFilename() + "\"");
//				out.writeChars("\r\n");
//				out.writeChars("Content-Type: " + fileBody.getMimeType());
//				out.writeChars("\r\n");
//				out.writeChars("\r\n");
//				fileBody.writeTo(out);
			}
			out.write(("--" + boundary + "--\r\n").getBytes());
		}
		out.flush();
	}

	@Override
	public String getBodyContentType() {
		if (mFiles != null && mFiles.size() > 0) {
			return "multipart/form-data; boundary=" + boundary;
		} else {
			return super.getBodyContentType();
		}
	}

	public List<ProgressFileBody> getFiles() {
		return mFiles;
	}


	@Override
	protected com.android.volley.Response<SuperResponse> parseNetworkResponse(NetworkResponse response) {
		SuperResponse parsed;
		try {
			parsed = JSON.parseObject(response.data, AppConfig.RESPONSE_CLASS);
		} catch (JSONException e) {
			parsed = Response.result(Response.JSON_PARSE_ERROR, "数据格式不正确");
		}
		if (AppConfig.DEBUG) {
			Log.d(TAG, "请求地址：" + getUrl());
			Log.d(TAG, "请求参数：" + mParams);
			Log.d(TAG, "附加参数：" + JSON.toJSONString(getAttachParams()));
			Log.d(TAG, "返回内容：");
			String content = new String(response.data);
			if (!TextUtils.isEmpty(content)) {
				for (String line : content.split("\n")) {
					Log.d(TAG, line);
				}
			}
		}
		return com.android.volley.Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response));
	}
}
