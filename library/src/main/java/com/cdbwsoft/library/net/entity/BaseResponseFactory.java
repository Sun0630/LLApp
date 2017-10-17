package com.cdbwsoft.library.net.entity;

/**
 * 工厂类
 * Created by DDL on 2016/9/23.
 */
public abstract class BaseResponseFactory<T extends SuperResponse> {

	public abstract T newInstance(int status);

	public abstract T newInstance(int status, String msg);

	public abstract T newInstance(int status, String msg, String data);
}
