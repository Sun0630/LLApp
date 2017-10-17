package com.cdbwsoft.library.net;

import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.cdbwsoft.library.AppConfig;
import com.cdbwsoft.library.net.entity.Response;

/**
 * 对象请求
 * Created by DDL on 2015/7/20 0020.
 */
public class SimpleRequest extends BaseRequest<Response> {
    private final com.android.volley.Response.Listener<Response> mListener;
    public static final String TAG = "SimpleRequest";

    /**
     * Creates a new request with the given method.
     *
     * @param method        the request {@link Method} to use
     * @param url           URL to fetch the string at
     * @param listener      Listener to receive the String response
     * @param errorListener Error listener, or null to ignore errors
     */
    public SimpleRequest(int method, String url, com.android.volley.Response.Listener<Response> listener,
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
    public SimpleRequest(String url, com.android.volley.Response.Listener<Response> listener, com.android.volley.Response.ErrorListener errorListener) {
        this(Method.POST, url, listener, errorListener);
    }

    /**
     * Creates a new POST request.
     *
     * @param url      URL to fetch the string at
     * @param listener Listener to receive the String response
     */
    public SimpleRequest(String url, com.android.volley.Response.Listener<Response> listener) {
        this(Method.POST, url, listener, null);
    }


    @Override
    protected void deliverResponse(Response response) {
        if (mListener != null) {
            mListener.onResponse(response);
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
    protected com.android.volley.Response<Response> parseNetworkResponse(NetworkResponse response) {
        Response parsed;

        try {
            parsed = JSON.parseObject(response.data, Response.class);
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
