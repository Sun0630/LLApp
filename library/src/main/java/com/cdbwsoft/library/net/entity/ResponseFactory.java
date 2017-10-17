package com.cdbwsoft.library.net.entity;

/**
 * 工厂类
 * Created by DDL on 2016/9/23.
 */
public class ResponseFactory extends BaseResponseFactory<Response> {
	@Override
	public Response newInstance(int status) {
		return Response.result(status);
	}

	@Override
	public Response newInstance(int status, String msg) {
		return Response.result(status, msg);
	}

	@Override
	public Response newInstance(int status, String msg, String data) {
		return Response.result(status, msg, data);
	}
}
