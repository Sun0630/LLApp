package com.umeng.soexample.net.entity;


import com.umeng.soexample.net.ErrorStatus;

/**
 * 请求结果对象
 *
 * @author DDL
 */
public class Response implements ErrorStatus {

	protected String data  = "";
	private   int    status = -1;
	private   String msg   = "";

	/**
	 * 是否请求成功
	 *
	 * @return 是否成功
	 */
	public boolean isSuccess() {
		return status == OK;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public static Response result(int status) {
		Response res = new Response();
		res.status = status;
		return res;
	}

	public static Response result(int status, String msg) {
		Response res = new Response();
		res.status = status;
		res.msg = msg;
		return res;
	}
}
