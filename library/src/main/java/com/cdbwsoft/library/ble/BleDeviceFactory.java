package com.cdbwsoft.library.ble;

import android.content.Context;

/**
 * 设备工厂
 * Created by DDL on 2016/5/16.
 */
public class BleDeviceFactory<T extends BleDevice> {
	private Context mContext;

	public BleDeviceFactory(Context context) {
		mContext = context;
	}

	public T create(BleManager<T> bleManager, String address, String name) throws Exception {
		return (T) new BleDevice((BleManager<BleDevice>) bleManager, address, name);
	}

	public Context getContext() {
		return mContext;
	}
}
