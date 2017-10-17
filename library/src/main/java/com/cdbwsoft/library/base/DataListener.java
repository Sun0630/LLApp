package com.cdbwsoft.library.base;

/**
 * 数据回调接口
 * Created by DDL on 2016/5/16.
 */
public abstract class DataListener {

	protected byte[] onDataRead(byte[] data){
		return data;
	}
	protected byte[] onDataWrite(byte[] data){
		return data;
	}
	protected void onWriteComplete(byte[] data){}
	protected void onReadComplete(byte[] data){}

	public boolean writeData(byte[] data){
		data = onDataWrite(data);
		boolean result = doWriteData(data);
		if(!result){
			return false;
		}
		onWriteComplete(data);
		return true;
	}
	public boolean readData(byte[] data){
		data = onDataRead(data);
		boolean result = doReadData(data);
		if(!result){
			return false;
		}
		onReadComplete(data);
		return true;
	}
	protected abstract boolean doWriteData(byte[] data);
	protected abstract boolean doReadData(byte[] data);

}
