package com.cdbwsoft.library.net.entity;

import com.cdbwsoft.library.net.ErrorStatus;

/**
 * 返回接口
 * Created by DDL on 2016/9/23.
 */

public interface SuperResponse extends ErrorStatus {
	public boolean isSuccess();

	public int getStatus();

	public void setStatus(int status);

	public String getMsg();

	public void setMsg(String msg);

	public String getData();

	public void setData(String data);
}
