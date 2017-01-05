package com.umeng.soexample.net;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.umeng.soexample.net.entity.Response;
import com.umeng.soexample.net.entity.ResponseVo;
import com.heaton.liulei.utils.AppConfig;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * 列表请求
 * Created by DDL on 2015/7/20 0020.
 */
public class BeanRequest<T> extends BaseRequest<ResponseVo<T>> {
    private final ResponseListener<ResponseVo<T>> mListener;
    private final Type type;
    public static final String TAG = "BeanRequest";

    /**
     * Creates a new request with the given method.
     *
     * @param method        the request {@link Method} to use
     * @param url           URL to fetch the string at
     * @param listener      Listener to receive the String response
     * @param errorListener Error listener, or null to ignore errors
     */
    public BeanRequest(int method, String url, ResponseListener<ResponseVo<T>> listener,
                       com.android.volley.Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        mListener = listener;
        Type superClass = this.getClass().getGenericSuperclass();
        this.type = ((ParameterizedType) superClass).getActualTypeArguments()[0];
    }

    /**
     * Creates a new POST request.
     *
     * @param url           URL to fetch the string at
     * @param listener      Listener to receive the String response
     * @param errorListener Error listener, or null to ignore errors
     */
    public BeanRequest(String url, ResponseListener<ResponseVo<T>> listener, com.android.volley.Response.ErrorListener errorListener) {
        this(Method.POST, url, listener, errorListener);
    }

    /**
     * Creates a new POST request.
     *
     * @param url      URL to fetch the string at
     * @param listener Listener to receive the String response
     */
    public BeanRequest(String url, ResponseListener<ResponseVo<T>> listener) {
        this(Method.POST, url, listener, null);
    }


    @Override
    protected void deliverResponse(ResponseVo<T> response) {
        if (mListener != null) {
            mListener.onResponse(response);
        }
    }

    @Override
    public void deliverError(VolleyError error) {
        super.deliverError(error);
        if (mListener != null) {
            ResponseVo<T> parsed = new ResponseVo<>();
            parsed.setStatus(Response.NETWORK_ERROR);
            parsed.setData("网络繁忙");
            mListener.onResponse(parsed);
        }
    }

    @Override
    protected com.android.volley.Response<ResponseVo<T>> parseNetworkResponse(NetworkResponse response) {
        ResponseVo<T> parsed;
        try {
            parsed = JSON.parseObject(response.data, ResponseVo.class);
            if (mListener.getType() instanceof Class) {
                parsed.parseVo((Class<T>) mListener.getType());
            } else if (mListener.getType() instanceof ParameterizedType) {
                Type[] types = ((ParameterizedType) mListener.getType()).getActualTypeArguments();
                if (types != null && types.length > 0 && types[0] instanceof Class) {
                    parsed.parseVo((Class<T>) types[0]);
                }
            }
        } catch (JSONException e) {
            parsed = new ResponseVo<>();
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
