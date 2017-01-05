//package com.example.myselfapp.net;
//
//import android.util.Log;
//
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONException;
//import com.android.volley.AuthFailureError;
//import com.android.volley.NetworkResponse;
//import com.android.volley.VolleyError;
//import com.android.volley.toolbox.HttpHeaderParser;
//import com.example.myselfapp.net.entity.ProgressFileBody;
//import com.example.myselfapp.net.entity.Response;
//import com.heaton.liulei.utils.AppConfig;
//
//import java.io.IOException;
//import java.io.OutputStream;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//
///**
// * 上传文件请求
// * Created by DDL on 2015/7/20 0020.
// */
//public class FileRequest extends BaseRequest<Response> implements ProgressFileBody.ProgressListener {
//	private final FileListener mListener;
//	private             List<ProgressFileBody> mFiles       = new ArrayList<>();
//	private             String                 boundary     = "----AndroidFormBoundary7MA4YWxkTrZu0gW"; //数据分隔线
//	public static final String                 TAG          = "FileRequest";
//	private             long                   mTotalAmount = 0;
//
//	/**
//	 * Creates a new request with the given method.
//	 *
//	 * @param method        the request {@link Method} to use
//	 * @param url           URL to fetch the string at
//	 * @param listener      Listener to receive the String response
//	 * @param errorListener Error listener, or null to ignore errors
//	 */
//	public FileRequest(int method, String url, FileListener listener,
//	                   com.android.volley.Response.ErrorListener errorListener) {
//		super(method, url, errorListener);
//		mListener = listener;
//		initFileBody();
//	}
//
//	/**
//	 * Creates a new POST request.
//	 *
//	 * @param url           URL to fetch the string at
//	 * @param listener      Listener to receive the String response
//	 * @param errorListener Error listener, or null to ignore errors
//	 */
//	public FileRequest(String url, FileListener listener, com.android.volley.Response.ErrorListener errorListener) {
//		this(Method.POST, url, listener, errorListener);
//	}
//
//	/**
//	 * Creates a new POST request.
//	 *
//	 * @param url      URL to fetch the string at
//	 * @param listener Listener to receive the String response
//	 */
//	public FileRequest(String url, FileListener listener) {
//		this(Method.POST, url, listener, null);
//	}
//
//	@Override
//	protected void deliverResponse(Response response) {
//		if (mListener != null) {
//			mListener.onResponse(response);
//		}
//	}
//
//	/**
//	 * 初始化文件体
//	 */
//	private void initFileBody() {
//		if (mListener != null) {
//			List<ProgressFileBody> files = mListener.getFiles();
//			if (files != null) {
//				for (int i = 0; i < files.size(); i++) {
//					ProgressFileBody fileBody = files.get(i);
//					fileBody.setProgressListener(this);
//					fileBody.setIndex(i);
//					mFiles.add(fileBody);
//					mTotalAmount += fileBody.getContentLength();
//				}
//			}
//		}
//	}
//
//	@Override
//	public void deliverError(VolleyError error) {
//		super.deliverError(error);
//		if (mListener != null) {
//			mListener.onResponse(Response.result(Response.NETWORK_ERROR, "网络繁忙"));
//		}
//	}
//
//	@Override
//	public boolean hasBody() {
//		return (mParams != null && mParams.size() > 0) || (mFiles != null && mFiles.size() > 0);
//	}
//
//	@Override
//	public void update(long current, long total, int index) {
//		if (mListener != null && mFiles != null) {
//			current = 0;
//			for (ProgressFileBody fileBody : mFiles) {
//				current += Math.min(fileBody.getCurrent(), fileBody.getTotal());
//			}
//			mListener.onProgress(current, mTotalAmount);
//		}
//	}
//
//	@Override
//	public void writeBody(OutputStream out) throws AuthFailureError, IOException {
//		StringBuilder sb = new StringBuilder();
//		Map<String, String> params = getParams();
//		if (params != null && params.size() > 0) {
//			if (mFiles != null && mFiles.size() > 0) {
//				for (String key : params.keySet()) {
//
//					String value = params.get(key);
//					sb.append("--");
//					sb.append(boundary);
//					sb.append("\r\n");
//					//key
//					sb.append("Content-Disposition: form-data;name=\"");
//					sb.append(key);
//					sb.append("\"");
//					sb.append("\r\n");
//
//					//换行
//					sb.append("\r\n");
//
//					//value
//					sb.append(value);
//					sb.append("\r\n");
//
//				}
//				if (sb.length() > 0) {
//					out.write(sb.toString().getBytes());
//				}
//			} else {
//				out.write(super.getBody());
//			}
//		}
//
//		if (mFiles != null && mFiles.size() > 0) {
//			for (ProgressFileBody fileBody : mFiles) {
//				out.write("--".getBytes());
//				out.write(boundary.getBytes());
//				out.write("\r\n".getBytes());
//				StringBuilder fileNameSb = new StringBuilder();
//				fileNameSb.append("Content-Disposition: form-data; name=");
//				fileNameSb.append("\"").append(fileBody.getKeyName()).append("\"");
//				fileNameSb.append("; filename=\"").append(fileBody.getFilename()).append("\"\r\n");
//				fileNameSb.append("Content-Type: ").append(fileBody.getMimeType()).append("\r\n\r\n");
//				out.write(fileNameSb.toString().getBytes());
//
//				fileBody.writeTo(out);
//
//				out.write("\r\n".getBytes());
////				out.writeChars(boundary);
////				out.writeChars("\r\n");
////				out.writeChars("Content-Disposition: form-data; name=\"" + fileBody.getKeyName() + "\"; filename=\"" + fileBody.getFilename() + "\"");
////				out.writeChars("\r\n");
////				out.writeChars("Content-Type: " + fileBody.getMimeType());
////				out.writeChars("\r\n");
////				out.writeChars("\r\n");
////				fileBody.writeTo(out);
//			}
//			out.write(("--" + boundary + "--\r\n").getBytes());
//		}
//		out.flush();
//	}
//
//	@Override
//	public String getBodyContentType() {
//		if (mFiles != null && mFiles.size() > 0) {
//			return "multipart/form-data; boundary=" + boundary;
//		} else {
//			return super.getBodyContentType();
//		}
//	}
//
//	@Override
//	protected com.android.volley.Response<Response> parseNetworkResponse(NetworkResponse response) {
//		Response parsed;
//		try {
//			parsed = JSON.parseObject(response.data, Response.class);
//		} catch (JSONException e) {
//			parsed = Response.result(Response.JSON_PARSE_ERROR, "数据格式不正确");
//		}
//		if (AppConfig.DEBUG) {
//			Log.d(TAG, "请求地址：" + getUrl());
//			Log.d(TAG, "请求参数：" + mParams);
//			String s = new String(response.data);
//			Log.d(TAG, "返回内容：" + new String(response.data));
//		}
//		return com.android.volley.Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response));
//	}
//}
