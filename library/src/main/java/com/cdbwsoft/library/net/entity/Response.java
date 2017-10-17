package com.cdbwsoft.library.net.entity;


import com.cdbwsoft.library.AppConfig;

import java.util.Map;

/**
 * 请求结果对象
 *
 * @author DDL
 */
public class Response implements SuperResponse {

	protected String data   = "";
	private   int    status = UNKNOWN_ERROR;
	private   String msg    = "";

	public Response() {}

	public Response(int status) {
		setStatus(status);
	}

	public Response(int status, String msg) {
		setStatus(status);
		setMsg(msg);
	}

	public Response(int status, String msg, String data) {
		setStatus(status);
		setMsg(msg);
		setData(data);
	}

	public Response(Map<String, Object> map) {
		if (map != null && map.size() > 0) {
			try {
				setData(String.valueOf(map.get(AppConfig.RESPONSE_DATA_KEY)));
				setMsg(String.valueOf(map.get(AppConfig.RESPONSE_MSG_KEY)));
				Object s = map.get(AppConfig.RESPONSE_CODE_KEY);
				if (s != null) {
					if (s instanceof Integer) {
						setStatus((int) s);
					} else {
						setStatus(Integer.valueOf(String.valueOf(s)));
					}
				}
			} catch (Exception e) {
				if (AppConfig.DEBUG) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 是否请求成功
	 *
	 * @return 是否成功
	 */
	public boolean isSuccess() {
		return getStatus() == OK;
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
		return new Response(status);
	}

	public static Response result(int status, String msg) {
		return new Response(status, msg);
	}

	public static Response result(int status, String msg, String data) {
		return new Response(status, msg, data);
	}
}
