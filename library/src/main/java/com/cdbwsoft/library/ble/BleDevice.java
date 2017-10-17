package com.cdbwsoft.library.ble;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.cdbwsoft.library.AppConfig;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * 蓝牙设备
 * Created by DDL on 2016/5/16.
 */
@SuppressLint("NewApi")
public class BleDevice {
	public final static String          TAG                      = BleDevice.class.getSimpleName();
	public final static int             MSG_CONNECT              = 0x801;
	public final static int             MSG_DISCONNECT           = 0x802;
	public final static int             MSG_DISCOVER_SERVICES    = 0x803;
	public final static int             MSG_GATT_CLOSE           = 0x804;
	public final static int             MSG_SET_NOTIFICATION     = 0x805;
	public final static int             MAX_RETRY_TIMES          = 1;
	public final static int             CONNECT_TIME_OUT         = 6 * 1000;
	public final static int             DISCOVER_TIME_OUT         = 4 * 1000;
	public final static int STATE_IDLE = 0x901;
	public final static int STATE_CONNECTING = 0x902;
	public final static int STATE_CONNECTED = 0x903;
	public final static int STATE_DISCOVERING = 0x905;
	public final static int STATE_DISCOVERED = 0x906;
	public final static int STATE_WRITING_DESCRIPTOR = 0x907;
	public final static int STATE_WROTE_DESCRIPTOR = 0x908;
	private final static Object mLocker = new Object();
	private static      int             mDeviceIndex             = 0;//设备索引计数器
	private BluetoothAdapter            mBluetoothAdapter;//蓝牙适配器
	private BleManager<BleDevice>               mBleManager;//蓝牙管理器
	private String                      mBleName;//蓝牙设备名称
	private String                      mBleAddress;//蓝牙设备地址
	private BluetoothDevice             mDevice;//ble蓝牙对象
	private BluetoothGattCharacteristic mWriteCharacteristic;//ble发送对象
	private BluetoothGattCharacteristic mOtaWriteCharacteristic;//ota ble发送对象
	private OtaListener                 mOtaListener;//ota更新操作监听器
	private DeviceListener<BleDevice>           mDeviceListener;//设备操作监听器
	private BluetoothGatt               mBluetoothGatt;//ble连接的Gatt对象
	private Context                     mContext;//程序对象
	private BleGattCallback             mBleGattCallback;//蓝牙回调对象
	private BleDeviceHandler            mHandler;//主线程对象
	private List<BluetoothGattCharacteristic> mNotifyCharacteristics = new ArrayList<>();//通知特性回调数组
	private int                               mNotifyIndex           = 0;//通知特性回调列表
	private int                               mIndex                 = 0;//设备索引
	private int                               mConnectionState       = BluetoothProfile.STATE_DISCONNECTED;//是否连接
	private boolean                           mOtaUpdating           = false;//是否OTA更新
	private boolean                           mAutoConnect           = true;//是否自动连接
	private boolean                           mReady                 = false;//是否准备就绪
	private boolean                           mDeleted               = false;//设备是否已删除
	private int                               mReconnectTimes        = 0;//重试连接次数
	private int                               mRediscoverTimes        = 0;//重试搜索次数
	private boolean                           mConnecting            = false;//是否正在连接
	private boolean                           mDisconnecting         = false;//是否正在断开连接
	private int mState=STATE_IDLE;
	private Runnable                          mConnectTimeout        = new Runnable() { // 连接设备超时
		@Override
		public void run() {
			close();
		}
	};
	private Runnable                          mDiscoverTimeout        = new Runnable() { // 获取服务超时
		@Override
		public void run() {
			rediscoverServices();
		}
	};

	public BleDevice(BleManager<BleDevice> bleManager, String address, String name) {
		mContext = bleManager.getContext();
		mBleAddress = address;
		mBleName = name;
		mBleGattCallback = new BleGattCallback();
		mBleManager = bleManager;
		mBluetoothAdapter = mBleManager.getBluetoothAdapter();
		mIndex = mDeviceIndex++;
		if (!TextUtils.isEmpty(mBleAddress) && mBluetoothAdapter != null) {
			mDevice = mBluetoothAdapter.getRemoteDevice(mBleAddress);
		}
		mHandler = new BleDeviceHandler(this, mContext.getMainLooper());
	}

	/**
	 * 是否已经连接
	 */
	public boolean isConnected() {
		return mConnectionState == BluetoothProfile.STATE_CONNECTED;
	}

	/**
	 * 是否正在连接
	 */
	public boolean isConnecting() {
		return mConnecting;
	}

	/**
	 * 获取设备连接状态
	 *
	 * @return 设备状态
	 */
	public int getConnectionState() {
		return mConnectionState;
	}

	/**
	 * 获取设备索引
	 *
	 * @return 索引
	 */
	public int getIndex() {
		return mIndex;
	}

	/**
	 * 设置设备索引
	 *
	 * @param index 设备索引
	 */
	public void setIndex(int index) {
		mIndex = index;
	}

	/**
	 * 设置自动连接
	 *
	 * @param autoConnect 自动连接
	 */
	public void setAutoConnect(boolean autoConnect) {
		mAutoConnect = autoConnect;
	}

	/**
	 * 获取自动连接
	 *
	 * @return 自动连接
	 */
	public boolean getAutoConnect() {
		return mAutoConnect;
	}

	/**
	 * 是否准备就绪
	 *
	 * @return 准备就绪
	 */
	public boolean getReady() {
		return mReady;
	}

	/**
	 * 删除设备
	 *
	 * @param deleted 是否删除
	 */
	public void setDeleted(boolean deleted) {
		if (!mDeleted && deleted) {
			mDeleted = true;
			if (mConnecting || mDisconnecting) {
				return;
			}
			if (isConnected()) {
				disconnect();
			} else {
				mBleManager.removeDevice(this);
			}
		}
	}

	/**
	 * 连接蓝牙设备
	 *
	 * @return 连接结果
	 */
	public boolean connect() {
		if (mConnecting || mDeleted || mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
			return false;
		}
		if (mConnectionState == BluetoothGatt.STATE_CONNECTED || mConnectionState == BluetoothGatt.STATE_CONNECTING) {
			return true;
		}
		if (mHandler != null) {
			if(!mBleManager.addConnectingDevice(this)){
				return false;
			}
			mConnecting = true;
			mAutoConnect = true;
			mState = STATE_CONNECTING;
			mHandler.obtainMessage(MSG_CONNECT).sendToTarget();
			return true;
		}
		return false;
	}

	private static boolean connectInner(BleDevice bleDevice) {
		synchronized (mLocker) {
			if (bleDevice == null || bleDevice.mBluetoothAdapter == null || bleDevice.mDevice == null || TextUtils.isEmpty(bleDevice.mBleAddress) || !bleDevice.mBluetoothAdapter.isEnabled()) {
				if (AppConfig.DEBUG) {
					Log.w(TAG, (bleDevice != null ? bleDevice.mBleAddress : "") + " -- BluetoothAdapter not initialized or unspecified address.");
				}
				if(bleDevice != null){
					bleDevice.mConnecting = false;
				}
				return false;
			}
			if (bleDevice.mConnectionState != BluetoothProfile.STATE_DISCONNECTED || bleDevice.mDeleted) {
				if (AppConfig.DEBUG) {
					Log.w(TAG, "connectInner: device is connected or deleted");
				}
				bleDevice.mConnecting = false;
				return false;
			}

			BluetoothManager bluetoothManager = bleDevice.mBleManager.getBluetoothManager();
			if (bluetoothManager == null) {
				bleDevice.mConnecting = false;
				return false;
			}
			int state = bluetoothManager.getConnectionState(bleDevice.mDevice, BluetoothProfile.GATT);
			if (state != BluetoothProfile.STATE_DISCONNECTED) {
				if (AppConfig.DEBUG) {
					Log.w(TAG, "connectInner:remote device is not disconnected");
				}
				bleDevice.mConnecting = false;
				return false;
			}

			bleDevice.mConnectionState = BluetoothProfile.STATE_CONNECTING;
			if (AppConfig.DEBUG) {
				Log.i(TAG, bleDevice.mBleAddress + " -- Device Connecting");
			}
			try {
				if (bleDevice.mBluetoothGatt != null) {
					bleDevice.mBluetoothGatt.close();
					bleDevice.mBluetoothGatt = null;
				}
			} catch (Exception e) {
				if (AppConfig.DEBUG) {
					e.printStackTrace();
				}
			}
			bleDevice.mBluetoothGatt = bleDevice.mDevice.connectGatt(bleDevice.mContext, false, bleDevice.mBleGattCallback);
			if (bleDevice.mBluetoothGatt == null) {
				if (AppConfig.DEBUG) {
					Log.w(TAG, "connectInner:bluetoothGatt connect null");
				}
				bleDevice.mConnecting = false;
				return false;
			}
			try {
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
					bleDevice.mBluetoothGatt.requestConnectionPriority(BluetoothGatt.CONNECTION_PRIORITY_HIGH);
				}
			} catch (Exception e) {
				if (AppConfig.DEBUG) {
					Log.w(TAG, "connectInner:requestConnectionPriority fail");
				}
				if (AppConfig.DEBUG) {
					e.printStackTrace();
				}
			}
			return true;
		}
	}

	/**
	 * 重新连接蓝牙设备
	 *
	 * @return 连接结果
	 */
	public boolean reconnect() {
		if (mConnecting || mDeleted || mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled() || mReconnectTimes >= MAX_RETRY_TIMES) {
			return false;
		}
		if (mConnectionState == BluetoothGatt.STATE_CONNECTED || mConnectionState == BluetoothGatt.STATE_CONNECTING) {
			return true;
		}
		if (mHandler != null) {
			if(!mBleManager.addConnectingDevice(this)){
				return false;
			}
			mConnecting = true;
			mState = STATE_CONNECTING;
			mReconnectTimes++;
			mHandler.obtainMessage(MSG_CONNECT).sendToTarget();
			return true;
		}
		return false;
	}
	/**
	 * 断开连接
	 */
	public void disconnect() {
		mAutoConnect = false;
		if (mDisconnecting) {
			return;
		}
		if (mHandler != null) {
			mDisconnecting = true;
			mHandler.obtainMessage(MSG_DISCONNECT).sendToTarget();
			if (mDeviceListener != null) {
				mDeviceListener.onConnectionChanged(this);
			}
		}
	}

	private static void disconnectInner(BleDevice bleDevice) {
		synchronized (mLocker) {
			if (bleDevice == null || bleDevice.mConnectionState != BluetoothProfile.STATE_CONNECTED) {
				if (AppConfig.DEBUG) {
					Log.w(TAG, "disconnectInner: device is null or not connected");
				}
				return;
			}
			if (bleDevice.mBluetoothAdapter == null || bleDevice.mBluetoothGatt == null) {
				if (AppConfig.DEBUG) {
					Log.w(TAG, bleDevice.mBleAddress + " -- BluetoothAdapter not initialized");
				}
				return;
			}
			bleDevice.mConnectionState = BluetoothProfile.STATE_DISCONNECTING;
			bleDevice.mBluetoothGatt.disconnect();
		}
	}

	/**
	 * 是否活动连接
	 *
	 * @return 是否
	 */
	public boolean isActive() {
		return mConnecting || mDisconnecting;
	}

	/**
	 * 获取设备状态
	 * @return 状态
	 */
	public int getState(){
		return mState;
	}

	/**
	 * 是否准备就绪
	 * @return 准备就绪
	 */
	public boolean isReady() {
		return mReady;
	}

	public BluetoothGatt getBluetoothGatt(){
		return mBluetoothGatt;
	}

	/**
	 * 搜索服务
	 */
	public void discoverServices() {
		if (mDeleted || mState >= STATE_DISCOVERING  || !isConnected()) {
			return;
		}
		if (mHandler != null) {
			mState = STATE_DISCOVERING;
			mHandler.obtainMessage(MSG_DISCOVER_SERVICES).sendToTarget();
		}
	}
	public void rediscoverServices() {
		if (mDeleted || !isConnected() || mRediscoverTimes >= MAX_RETRY_TIMES) {
			return;
		}
		if (mHandler != null) {
			mState = STATE_DISCOVERING;
			mRediscoverTimes++;
			mHandler.obtainMessage(MSG_DISCOVER_SERVICES).sendToTarget();
		}
	}

	/**
	 * 查找设备服务
	 * @param bleDevice 设备对象
	 */
	private static void discoverServicesInner(BleDevice bleDevice) {
		synchronized (mLocker) {
			if (bleDevice == null || !bleDevice.isConnected() || bleDevice.mDeleted) {
				if (AppConfig.DEBUG) {
					Log.w(TAG, "discoverServicesInner: device is null or deleted");
				}
				return;
			}
			if (AppConfig.DEBUG) {
				Log.w(TAG, "discoverServicesInner:"+bleDevice.getBleAddress());
			}
			if (bleDevice.mBluetoothGatt != null) {
				bleDevice.mHandler.removeCallbacks(bleDevice.mDiscoverTimeout);
				bleDevice.mHandler.postDelayed(bleDevice.mDiscoverTimeout, DISCOVER_TIME_OUT);
				bleDevice.mBluetoothGatt.discoverServices();
			}
		}
	}

	/**
	 * 关闭连接
	 */
	public void close() {
		if (mHandler != null) {
			mConnectionState = BluetoothProfile.STATE_DISCONNECTING;
			mHandler.obtainMessage(MSG_GATT_CLOSE).sendToTarget();
		}
	}

	/**
	 * 关闭连接
	 */
	private static void closeInner(BleDevice bleDevice) {
		synchronized (mLocker) {
			if (bleDevice == null) {
				if (AppConfig.DEBUG) {
					Log.w(TAG, "closeInner:device is null");
				}
				return;
			}
			if (bleDevice.mDeleted) {
				bleDevice.mBleManager.removeDevice(bleDevice);
			}
			if (bleDevice.mBluetoothGatt != null) {
				bleDevice.mBluetoothGatt.close();
				bleDevice.mBluetoothGatt = null;
			}
			if (AppConfig.DEBUG) {
				Log.w(TAG, "closeInner:"+bleDevice.getBleAddress());
			}
			bleDevice.mHandler.removeCallbacks(bleDevice.mConnectTimeout);
			bleDevice.mHandler.removeCallbacks(bleDevice.mDiscoverTimeout);
			bleDevice.mWriteCharacteristic = null;
			bleDevice.mOtaWriteCharacteristic = null;
			bleDevice.mNotifyIndex = 0;
			bleDevice.mNotifyCharacteristics.clear();
			bleDevice.mConnecting = bleDevice.mDisconnecting = false;
			bleDevice.mConnectionState = BluetoothProfile.STATE_DISCONNECTED;
			bleDevice.mState = STATE_IDLE;
			if(bleDevice.mDeviceListener != null) {
				bleDevice.mDeviceListener.onConnectionChanged(bleDevice);
			}
		}
	}

	/**
	 * 开始设置通知
	 */
	public void setEnableCharacteristicNotification() {
		if(mState >= STATE_WRITING_DESCRIPTOR){
			return;
		}
		if (mHandler != null) {
			//开始设置通知特性
			if (mNotifyCharacteristics != null && mNotifyCharacteristics.size() > 0) {
				setCharacteristicNotification(mNotifyCharacteristics.get(mNotifyIndex++), true);
			}
		}
	}
	/**
	 * 设置通知属性状态
	 *
	 * @param characteristic 特性对象
	 * @param enabled        是否启用
	 */
	public void setCharacteristicNotification(BluetoothGattCharacteristic characteristic, boolean enabled) {
		if (mHandler != null) {
			mState = STATE_WRITING_DESCRIPTOR;
			mHandler.obtainMessage(MSG_SET_NOTIFICATION,enabled ? 1:0,0,characteristic).sendToTarget();
		}
	}

	/**
	 * 设置通知属性状态
	 *
	 * @param characteristic 特性对象
	 * @param enabled        是否启用
	 */
	public static void setCharacteristicNotificationInner(BleDevice bleDevice, BluetoothGattCharacteristic characteristic, boolean enabled) {
		synchronized (mLocker) {
			if (bleDevice.mBluetoothAdapter == null || bleDevice.mBluetoothGatt == null) {
				if (AppConfig.DEBUG) {
					Log.w(TAG, bleDevice.mBleAddress + " -- BluetoothAdapter not initialized");
				}
				return;
			}
			try {
				bleDevice.mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);
				BluetoothGattDescriptor descriptor = characteristic.getDescriptor(bleDevice.mBleManager.getUuidDescriptor());
				if (descriptor != null) {
					descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
					writeDescriptor(bleDevice.mBluetoothGatt, descriptor);
				}
			} catch (Exception e) {
				if (AppConfig.DEBUG) {
					e.printStackTrace();
				}
				bleDevice.close();
			}
		}
	}


	/**
	 * 发送数据
	 *
	 * @param value 数据对象
	 * @return 发送结果
	 */
	public boolean writeCharacteristic(byte[] value) {
		if (mBluetoothAdapter == null || mBluetoothGatt == null) {
			if (AppConfig.DEBUG) {
				Log.w(TAG, mBleAddress + " -- BluetoothAdapter not initialized");
			}
			return false;
		}
		try {
			if (mWriteCharacteristic != null && mBleManager.getUuidCharacteristic().equals(mWriteCharacteristic.getUuid())) {
				mWriteCharacteristic.setValue(onEncrypt(value));
				boolean result = writeCharacteristic(mBluetoothGatt, mWriteCharacteristic);
				if (AppConfig.DEBUG) {
					Log.d(TAG, mBleAddress + " -- write data:" + Arrays.toString(value));
					Log.d(TAG, mBleAddress + " -- write result:" + result);
				}
				return result;
			}
		}catch(Exception e ){
			if(AppConfig.DEBUG){
				e.printStackTrace();
			}
			close();
			return false;
		}
		return true;
	}

	/**
	 * 发送数据
	 *
	 * @param value 数据对象
	 * @return 发送结果
	 */
	public boolean writeOtaData(byte[] value) {
		if (mBluetoothAdapter == null || mBluetoothGatt == null) {
			if (AppConfig.DEBUG) {
				Log.w(TAG, mBleAddress + " -- BluetoothAdapter not initialized");
			}
			return false;
		}
		try {
			if (mOtaWriteCharacteristic == null) {
				mOtaUpdating = true;
				BluetoothGattService bluetoothGattService = this.mBluetoothGatt.getService(BleGlobalVariables.UUID_QUINTIC_OTA_SERVICE);
				if (bluetoothGattService == null) {
					return false;
				} else {
					BluetoothGattCharacteristic mOtaNotifyCharacteristic = bluetoothGattService.getCharacteristic(BleGlobalVariables.UUID_OTA_NOTIFY_CHARACTERISTIC);
					if (mOtaNotifyCharacteristic != null) {
						this.mBluetoothGatt.setCharacteristicNotification(mOtaNotifyCharacteristic, true);
					}
					mOtaWriteCharacteristic = bluetoothGattService.getCharacteristic(BleGlobalVariables.UUID_OTA_WRITE_CHARACTERISTIC);
				}

			}
			if (mOtaWriteCharacteristic != null && BleGlobalVariables.UUID_OTA_WRITE_CHARACTERISTIC.equals(mOtaWriteCharacteristic.getUuid())) {
				mOtaWriteCharacteristic.setValue(value);
				boolean result = writeCharacteristic(mBluetoothGatt, mOtaWriteCharacteristic);
				if (AppConfig.DEBUG) {
					Log.d(TAG, mBleAddress + " -- write data:" + Arrays.toString(value));
					Log.d(TAG, mBleAddress + " -- write result:" + result);
				}
				return result;
			}
			return true;
		} catch (Exception e) {
			if (AppConfig.DEBUG) {
				e.printStackTrace();
			}
			close();
			return false;
		}
	}

	/**
	 * 更新完成
	 */
	public void otaUpdateComplete() {
		mOtaUpdating = false;
	}

	/**
	 * 设置是否ota更新
	 *
	 * @param updating 更新状态
	 */
	public void setOtaUpdating(boolean updating) {
		mOtaUpdating = updating;
	}

	/**
	 * OTA设置监听器
	 *
	 * @param otaListener 监听器对象
	 */
	public void setOtaListener(OtaListener otaListener) {
		mOtaListener = otaListener;
	}

	/**
	 * 设置设备监听器
	 *
	 * @param deviceListener 监听器对象
	 */
	public void setDeviceListener(DeviceListener deviceListener) {
		mDeviceListener = deviceListener;
	}

	/**
	 * 发送字符串
	 *
	 * @param value 字符串对象
	 * @return 发送结果
	 */
	public boolean writeCharacteristic(String value) {
		if (mBluetoothAdapter == null || mBluetoothGatt == null) {
			if (AppConfig.DEBUG) {
				Log.w(TAG, mBleAddress + " -- BluetoothAdapter not initialized");
			}
			return false;
		}
		try {
			if (mWriteCharacteristic != null && mBleManager.getUuidCharacteristic().equals(mWriteCharacteristic.getUuid())) {
				mWriteCharacteristic.setValue(value != null ? onEncrypt(value.getBytes()) : null);
				boolean result = writeCharacteristic(mBluetoothGatt, mWriteCharacteristic);
				if (AppConfig.DEBUG) {
					Log.d(TAG, mBleAddress + " -- write data:" + result);
				}
				return result;
			}
			return true;
		} catch (Exception e) {
			if (AppConfig.DEBUG) {
				e.printStackTrace();
			}
			close();
			return false;
		}
	}

	/**
	 * 写入数据操作
	 *
	 * @param gatt           服务对象
	 * @param characteristic 特性对象
	 * @return 写入结果
	 */
	public static boolean writeCharacteristic(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
		synchronized (mLocker) {
			if (gatt == null || characteristic == null) {
				return false;
			}
			return gatt.writeCharacteristic(characteristic);
		}
	}

	/**
	 * 写入数据操作
	 *
	 * @param gatt       服务对象
	 * @param descriptor 描述对象
	 * @return 写入结果
	 */
	public static boolean writeDescriptor(BluetoothGatt gatt, BluetoothGattDescriptor descriptor) {
		synchronized (mLocker) {
			if (gatt == null || descriptor == null) {
				return false;
			}
			return gatt.writeDescriptor(descriptor);
		}
	}

	/**
	 * 获取支持的gatt服务
	 *
	 * @return 服务列表
	 */
	public List<BluetoothGattService> getSupportedGattServices() {
		if (mBluetoothGatt == null) return null;
		return mBluetoothGatt.getServices();
	}

	/**
	 * 获取蓝牙设备对象
	 *
	 * @return 设备对象
	 */
	public BluetoothDevice getDevice() {
		return mDevice;
	}

	/**
	 * 获取蓝牙设备地址
	 *
	 * @return 设备地址
	 */
	public String getBleAddress() {
		return mBleAddress;
	}

	/**
	 * 获取蓝牙设备名称
	 *
	 * @return 设备名称
	 */
	public String getBleName() {
		return mBleName;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof BleDevice) {
			if (mBleAddress.equalsIgnoreCase(((BleDevice) o).mBleAddress)) {
				return true;
			}
		} else if (o instanceof String) {
			if (mBleAddress.equalsIgnoreCase((String) o)) {
				return true;
			}
		}
		return super.equals(o);
	}

	/**
	 * 已连接
	 */
	protected void onConnected() {}

	/**
	 * 已断开
	 */
	protected void onDisconnected() {}

	/**
	 * 正在断开
	 */
	protected void onDisconnecting() {}

	/**
	 * 连接状态变更
	 */
	protected void onConnectionChanged() {}

	/**
	 * 准备就绪
	 */
	protected void onReady() {}
	/**
	 * 服务准备就绪
	 */
	protected void onServiceReady() {}

	/**
	 * 连接失败
	 */
	protected void onFailed() {}

	/**
	 * 写入数据
	 */
	protected void onWrite() {}

	/**
	 * 数据变更
	 */
	protected void onChange(byte[] data) {}

	/**
	 * 加密数据
	 * @param data 数据
	 * @return 加密后的数据
	 */
	protected byte[] onEncrypt(byte[] data){
		return data;
	}

	/**
	 * 解密数据
	 * @param data 数据
	 * @return 解密后的数据
	 */
	protected byte[] onDecrypt(byte[] data){
		return data;
	}
	/**
	 * 蓝牙回调
	 */
	class BleGattCallback extends BluetoothGattCallback {
		@Override
		public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
			super.onConnectionStateChange(gatt, status, newState);
			synchronized (mLocker) {
				if (AppConfig.DEBUG) {
					Log.i(TAG, mBleAddress + " -- onConnectionStateChange|status:" + status + "|newState:" + newState);
				}
				mHandler.removeCallbacks(mConnectTimeout);
				mConnecting = mDisconnecting = false;
				if (mBluetoothGatt == null) {
					mBluetoothGatt = gatt;
				}
				mReady = false;
				if (status == BluetoothGatt.GATT_SUCCESS) {
					//连接状态变更
					mConnectionState = newState;
					if (newState == BluetoothProfile.STATE_CONNECTED) {
						mReconnectTimes = 0;
						if (AppConfig.DEBUG) {
							Log.i(TAG, mBleAddress + " -- Connected to GATT server.");
						}
//					int state = mBleManager.getBluetoothManager().getConnectionState(mDevice, BluetoothProfile.GATT);
//					if (state == BluetoothProfile.STATE_CONNECTED) {
						onConnected();

//						mHandler.removeCallbacks(mDiscoverTimeout);
//						mHandler.postDelayed(mDiscoverTimeout, DISCOVER_TIME_OUT);
//						discoverServices();

//					} else {
//						close();
//					}
						mState = STATE_CONNECTED;
						if (mDeleted) {
							disconnect();
						}
					} else if (newState == BluetoothProfile.STATE_DISCONNECTING) {
						onDisconnecting();
					} else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
						if (AppConfig.DEBUG) {
							Log.i(TAG, mBleAddress + " -- Disconnected from GATT server.");
						}
						mState = STATE_IDLE;
						onDisconnected();
						if (mAutoConnect && !mDeleted && mBluetoothAdapter.isEnabled() && mReconnectTimes++ < MAX_RETRY_TIMES) {
							if (!connect()) {
								close();
							}
						} else {
							try {
								close();
							} catch (Exception e) {
								if (AppConfig.DEBUG) {
									e.printStackTrace();
								}
							}
						}
						if (mDeleted) {
							mBleManager.removeDevice(BleDevice.this);
						}
					}
				} else {
					try {
						close();
					} catch (Exception e) {
						if (AppConfig.DEBUG) {
							e.printStackTrace();
						}
					}
					mConnectionState = BluetoothProfile.STATE_DISCONNECTED;
					if (mAutoConnect && !mDeleted && mReconnectTimes++ < MAX_RETRY_TIMES) {
						if (!connect()) {
							close();
						}
					}
				}
				onConnectionChanged();
				if (mDeviceListener != null) {
					mDeviceListener.onConnectionChanged(BleDevice.this);
				}
			}
		}

		@Override
		public void onServicesDiscovered(BluetoothGatt gatt, int status) {
			super.onServicesDiscovered(gatt, status);
			mHandler.removeCallbacks(mDiscoverTimeout);
			mRediscoverTimes = 0;
			synchronized (mLocker) {
				if (AppConfig.DEBUG) {
					Log.i(TAG, mBleAddress + " -- onServicesDiscovered|status:" + status);
				}
				mState = STATE_DISCOVERED;
				if (status == BluetoothGatt.GATT_SUCCESS) {
					if (mBluetoothGatt == null) {
						mBluetoothGatt = gatt;
					}
					mReady = false;
					//清空通知特性列表
					mNotifyCharacteristics.clear();
					mNotifyIndex = 0;
					for (BluetoothGattService service : getSupportedGattServices()) {
						if (AppConfig.DEBUG) {
							Log.i(TAG, mBleAddress + " -- service UUID:" + service.getUuid());
						}
						if (mBleManager.getUuidService().equals(service.getUuid())) {
							for (BluetoothGattCharacteristic gattCharacteristic : service.getCharacteristics()) {
								if (AppConfig.DEBUG) {
									Log.i(TAG, mBleAddress + " -- Characteristic UUID:" + gattCharacteristic.getUuid());
								}
								//发送特性
								if (mBleManager.getUuidCharacteristic().equals(gattCharacteristic.getUuid())) {
									mWriteCharacteristic = gattCharacteristic;
									//通知特性
								} else if (gattCharacteristic.getProperties() == BluetoothGattCharacteristic.PROPERTY_NOTIFY) {
									mNotifyCharacteristics.add(gattCharacteristic);
								}
							}
						}
					}
					if (mDeviceListener != null) {
						mDeviceListener.onServicesDiscovered(BleDevice.this);
					}
					onServiceReady();
					if (mDeviceListener != null) {
						mDeviceListener.onServiceReady(BleDevice.this);
					}
					//开始设置通知特性
					if (mNotifyCharacteristics == null || mNotifyCharacteristics.size() == 0) {
						mReady = true;
						onReady();
						if (mDeviceListener != null) {
							mDeviceListener.onReady(BleDevice.this);
						}
					}
				} else {
					mConnectionState = BluetoothProfile.STATE_DISCONNECTED;
					close();
					if (AppConfig.DEBUG) {
						Log.w(TAG, mBleAddress + " -- onServicesDiscovered received: " + status);
					}
				}
			}
		}

		@Override
		public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
			super.onCharacteristicRead(gatt, characteristic, status);
			if (AppConfig.DEBUG) {
				Log.i(TAG, mBleAddress + " -- onCharacteristicRead: " + status);
			}
			if (mDeviceListener != null) {
				mDeviceListener.onRead(BleDevice.this);
			}
		}

		@Override
		public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
			super.onCharacteristicWrite(gatt, characteristic, status);
			synchronized (mLocker) {
				if (AppConfig.DEBUG) {
					Log.i(TAG, mBleAddress + " -- onCharacteristicWrite: " + status);
				}
				if (status == BluetoothGatt.GATT_SUCCESS) {
					if (BleGlobalVariables.UUID_OTA_WRITE_CHARACTERISTIC.equals(characteristic.getUuid())) {
						if (mOtaListener != null) {
							mOtaListener.onWrite(BleDevice.this);
						}
						return;
					}
					onWrite();
					if (mDeviceListener != null) {
						mDeviceListener.onWrite(BleDevice.this);
					}
				}
			}
		}

		@Override
		public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
			super.onCharacteristicChanged(gatt, characteristic);
			synchronized (mLocker) {
				if (AppConfig.DEBUG) {
					Log.i(TAG, mBleAddress + " -- onCharacteristicChanged: " + (characteristic.getValue() != null ? Arrays.toString(characteristic.getValue()) : ""));
				}
				if (BleGlobalVariables.UUID_OTA_WRITE_CHARACTERISTIC.equals(characteristic.getUuid()) || BleGlobalVariables.UUID_OTA_NOTIFY_CHARACTERISTIC.equals(characteristic.getUuid())) {
					if (mOtaListener != null) {
						mOtaListener.onChange(BleDevice.this, characteristic.getValue());
					}
					return;
				}
				byte[] data = onDecrypt(characteristic.getValue());
				onChange(data);
				if (mDeviceListener != null) {
					mDeviceListener.onChanged(BleDevice.this, data);
				}
			}
		}

		@Override
		public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
			super.onDescriptorRead(gatt, descriptor, status);
		}

		@Override
		public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
			super.onDescriptorWrite(gatt, descriptor, status);
			synchronized (mLocker) {
				if (AppConfig.DEBUG) {
					Log.i(TAG, mBleAddress + " -- onDescriptorWrite: " + status);
				}
				if (status == BluetoothGatt.GATT_SUCCESS) {
					if (mNotifyCharacteristics != null && mNotifyCharacteristics.size() > 0 && mNotifyIndex < mNotifyCharacteristics.size()) {
						setCharacteristicNotification(mNotifyCharacteristics.get(mNotifyIndex++), true);
					} else {
						mState = STATE_WROTE_DESCRIPTOR;
						mReady = true;
						onReady();
						if (mDeviceListener != null) {
							mDeviceListener.onReady(BleDevice.this);
						}
					}
				}
			}
		}

		public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
			super.onReliableWriteCompleted(gatt, status);
			if (AppConfig.DEBUG) {
				Log.i(TAG, mBleAddress + " -- onReliableWriteCompleted: " + status);
			}
		}

		public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
			super.onReadRemoteRssi(gatt, rssi, status);
			if (AppConfig.DEBUG) {
				Log.i(TAG, mBleAddress + " -- onReadRemoteRssi: " + status + ",rssi:" + rssi);
			}
		}

		public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
			super.onMtuChanged(gatt, mtu, status);
			if (AppConfig.DEBUG) {
				Log.i(TAG, mBleAddress + " -- onMtuChanged: " + status + ",mtu:" + mtu);
			}
		}
	}

	public interface OtaListener {
		void onWrite(BleDevice device);

		void onChange(BleDevice device, byte[] data);
	}

	public interface DeviceListener<T extends BleDevice> {
		void onWrite(T device);

		void onRead(T device);

		void onReady(T device);

		void onChanged(T device, byte[] data);

		void onServicesDiscovered(T device);

		void onConnectionChanged(T device);

		void onServiceReady(T device);
	}

	public static class BleDeviceHandler extends Handler {
		WeakReference<BleDevice> reference;

		BleDeviceHandler(BleDevice bleDevice, Looper looper) {
			super(looper);
			reference = new WeakReference<>(bleDevice);
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (reference == null) {
				return;
			}
			BleDevice bleDevice = reference.get();
			if (bleDevice == null) {
				return;
			}
			switch (msg.what) {
				case MSG_CONNECT:
					boolean result = connectInner(bleDevice);
					if (AppConfig.DEBUG) {
						Log.i(TAG, bleDevice.mBleAddress + " -- connectInner:" + result);
					}
					if(!result){
						bleDevice.mState = STATE_IDLE;
					}
					if (result) {
						bleDevice.mHandler.removeCallbacks(bleDevice.mConnectTimeout);
						bleDevice.mHandler.postDelayed(bleDevice.mConnectTimeout, CONNECT_TIME_OUT);
					}
					break;
				case MSG_DISCONNECT:
					disconnectInner(bleDevice);
					break;
				case MSG_DISCOVER_SERVICES:
					discoverServicesInner(bleDevice);
					break;
				case MSG_GATT_CLOSE:
					closeInner(bleDevice);
					break;
				case MSG_SET_NOTIFICATION:
					setCharacteristicNotificationInner(bleDevice, (BluetoothGattCharacteristic) msg.obj, msg.arg1 == 1);
					break;
			}

		}
	}
}