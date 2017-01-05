package com.umeng.soexample.net;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.umeng.soexample.net.entity.Response;
import com.umeng.soexample.net.entity.ResponseList;
import com.heaton.liulei.utils.AppConfig;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * 列表请求
 * Created by DDL on 2015/7/20 0020.
 */
public class ListRequest<T> extends BaseRequest<ResponseList<T>> {
    private final ResponseListener<ResponseList<T>> mListener;
    public static final String TAG = "ListRequest";

    /**
     * Creates a new request with the given method.
     *
     * @param method        the request {@link Method} to use
     * @param url           URL to fetch the string at
     * @param listener      Listener to receive the String response
     * @param errorListener Error listener, or null to ignore errors
     */
    public ListRequest(int method, String url, ResponseListener<ResponseList<T>> listener,
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
    public ListRequest(String url, ResponseListener<ResponseList<T>> listener, com.android.volley.Response.ErrorListener errorListener) {
        this(Method.POST, url, listener, errorListener);
    }

    /**
     * Creates a new POST request.
     *
     * @param url      URL to fetch the string at
     * @param listener Listener to receive the String response
     */
    public ListRequest(String url, ResponseListener<ResponseList<T>> listener) {
        this(Method.POST, url, listener, null);
    }

    //get请求
    public ListRequest(int method, String url, ResponseListener<ResponseList<T>> listener) {
        this(method, url, listener, null);
    }


    @Override
    protected void deliverResponse(ResponseList<T> response) {
        if (mListener != null) {
            mListener.onResponse(response);
        }
    }

    @Override
    public void deliverError(VolleyError error) {
        super.deliverError(error);
        if (AppConfig.DEBUG) {
            Log.e(TAG, getUrl() + "，请求失败：" + error.getMessage(), error);
        }
        if (mListener != null) {
            ResponseList<T> parsed = new ResponseList<>();
            parsed.setStatus(Response.NETWORK_ERROR);
            parsed.setData("网络繁忙");
            mListener.onResponse(parsed);
        }
    }

    @Override
    protected com.android.volley.Response<ResponseList<T>> parseNetworkResponse(NetworkResponse response) {
        ResponseList<T> parsed;
        try {
            parsed = JSON.parseObject(response.data, ResponseList.class);
            if (mListener.getType() instanceof Class) {
                parsed.parseList((Class<T>) mListener.getType());
            } else if (mListener.getType() instanceof ParameterizedType) {
                Type[] types = ((ParameterizedType) mListener.getType()).getActualTypeArguments();
                if (types != null && types.length > 0 && types[0] instanceof Class) {
                    parsed.parseList((Class<T>) types[0]);
                }
            }
        } catch (JSONException e) {
            parsed = new ResponseList<>();
            parsed.setStatus(Response.JSON_PARSE_ERROR);
            parsed.setData("数据格式不正确");
        }
        if (AppConfig.DEBUG) {
            Log.d(TAG, "请求地址：" + getUrl());
            Log.d(TAG, "请求参数：" + mParams);
            Log.d(TAG, "返回内容：" + new String(response.data));
        }
        return com.android.volley.Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response));
    }
}
