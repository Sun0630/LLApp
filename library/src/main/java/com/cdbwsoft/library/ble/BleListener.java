package com.cdbwsoft.library.ble;

import android.bluetooth.BluetoothDevice;

import com.cdbwsoft.library.base.DataListener;

/**
 * 蓝牙监听器
 * Created by DDL on 2016/5/16.
 */
public abstract class BleListener<T extends BleDevice> extends DataListener {
	private BleManager<T> mBleManager;
	public void onStart(){}
	public void onStop(){}
	public void onChange(){}
	public boolean filterDevice(BluetoothDevice device, int rssi, byte[] scanRecord){return false;}

	public void setBleManager(BleManager<T> bleManager){
		mBleManager = bleManager;
	}

	public BleManager<T> getBleManager(){
		return mBleManager;
	}

	@Override
	protected boolean doWriteData(byte[] data) {
		return mBleManager != null && mBleManager.writeData(data);
	}

	@Override
	protected boolean doReadData(byte[] data) {
		return false;
	}

	public void onWrite(BleDevice device) {}

	public void onRead(BleDevice device) {}

	public void onReady(BleDevice device) {}

	public void onChanged(BleDevice device, byte[] data) {}

	public void onServicesDiscovered(BleDevice device) {}

	public void onConnectionChanged(BleDevice device) {}

	public void onError(int errorCode){}
}
