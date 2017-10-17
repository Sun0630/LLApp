package com.cdbwsoft.library.ble;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.cdbwsoft.library.AppConfig;
import com.cdbwsoft.library.statistics.Running;

import java.lang.ref.WeakReference;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 蓝牙管理器
 * Created by DDL on 2016/5/16.
 */
@SuppressLint("NewApi")
public abstract class BleManager<T extends BleDevice> implements BleDevice.DeviceListener<T> {
	private static final String TAG = BleManager.class.getSimpleName();

	private static final int MSG_ON_DEVICE_WRITE        = 0x901;
	private static final int MSG_ON_DEVICE_READ         = 0x902;
	private static final int MSG_ON_DEVICE_READY        = 0x903;
	private static final int MSG_ON_DEVICE_CHANGED      = 0x904;
	private static final int MSG_ON_SERVICES_DISCOVERED = 0x905;
	private static final int MSG_ON_CONNECTION_CHANGED  = 0x906;
	private static final int MSG_ON_CHANGE              = 0x907;
	private static final int MSG_ON_START               = 0x908;
	private static final int MSG_ON_STOP                = 0x909;
	private static final int MSG_SCAN_START             = 0x90a;
	private static final int MSG_SCAN_STOP              = 0x90b;
	public static final  int MSG_ON_CONNECT_MAX         = 0x90c;


	public final static String UUID_SERVICE_TEXT        = "0000fee9-0000-1000-8000-00805f9b34fb";//服务UUID字符串
	public final static String UUID_CHARACTERISTIC_TEXT = "d44bc439-abfd-45a2-b575-925416129600";//特性UUID字符串
	public final static String UUID_DESCRIPTOR_TEXT     = "00002902-0000-1000-8000-00805f9b34fb";//描述UUID字符串
	public              UUID   UUID_SERVICE             = UUID.fromString(UUID_SERVICE_TEXT);//服务UUID字符串
	public              UUID   UUID_CHARACTERISTIC      = UUID.fromString(UUID_CHARACTERISTIC_TEXT);//特性UUID字符串
	public              UUID   UUID_DESCRIPTOR          = UUID.fromString(UUID_DESCRIPTOR_TEXT);//描述UUID字符串


	private static final int SCAN_TIME             = 10000;//扫描蓝牙时间(ms)
	private static final int STATE_STOPPED         = 0;
	private static final int STATE_SCANNING        = 1;
	private static final int MAX_CONNECTING_DEVICE = 4;

	private final Handler mMessageHandler;//消息对象
	private final List<T> mDevices = new ArrayList<>();
	private Context          mContext;
	private BluetoothManager mBluetoothManager;//蓝牙管理服务
	private BluetoothAdapter mBluetoothAdapter;//蓝牙适配器
	private List<BleListener> mBleListeners = new ArrayList<>();
	private BleDeviceFactory<T> mBleDeviceFactory;
	private       BleReceiver mBleReceiver       = new BleReceiver();
	private       List<T>     mConnectingDevices = new ArrayList<>();
	private       List<T>     mCurrentDevices    = new ArrayList<>();
	private       List<T>     mConnectedDevices  = new ArrayList<>();
	private final Object      mLocker            = new Object();
	private final Class<T> mDeviceClass;
	private       int      mState;

	public BleManager(Context context) {
		this(context, null, null);
	}

	public BleManager(Context context, BleListener<T> bleListener) {
		this(context, bleListener, null);
	}

	public BleManager(Context context, BleDeviceFactory<T> factory) {
		this(context, null, factory);
	}

	public BleManager(Context context, BleListener<T> bleListener, BleDeviceFactory<T> factory) {
		if (bleListener != null) {
			bleListener.setBleManager(this);
			mBleListeners.add(bleListener);
		}
		mBleDeviceFactory = factory;
		mContext = context;
		mMessageHandler = new MessageHandler<>(this, context.getMainLooper());

		Type superClass = getClass().getGenericSuperclass();
		Type type = ((ParameterizedType) superClass).getActualTypeArguments()[0];
		mDeviceClass = getClass(type, 0);

		try {
			initialize();
		} catch (Exception e) {
			if (AppConfig.DEBUG) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 设置服务UUID字符串
	 *
	 * @param uuidServiceText 服务UUID文本
	 */
	public void setUuidServiceText(String uuidServiceText) {
		if (TextUtils.isEmpty(uuidServiceText)) {
			return;
		}
		UUID_SERVICE = UUID.fromString(uuidServiceText);
	}

	/**
	 * 设置特性UUID字符串
	 *
	 * @param uuidCharacteristicText 特性UUID文本
	 */
	public void setUuidCharacteristicText(String uuidCharacteristicText) {
		if (TextUtils.isEmpty(uuidCharacteristicText)) {
			return;
		}
		UUID_CHARACTERISTIC = UUID.fromString(uuidCharacteristicText);
	}

	/**
	 * 设置描述UUID字符串
	 *
	 * @param uuidDescriptorText 描述UUID文本
	 */
	public void setUuidDescriptorText(String uuidDescriptorText) {
		if (TextUtils.isEmpty(uuidDescriptorText)) {
			return;
		}
		UUID_DESCRIPTOR = UUID.fromString(uuidDescriptorText);
	}

	/**
	 * 获取服务UUID
	 *
	 * @return 服务UUID
	 */
	public UUID getUuidService() {
		return UUID_SERVICE;
	}

	/**
	 * 获取特性UUID
	 *
	 * @return 特性UUID
	 */
	public UUID getUuidCharacteristic() {
		return UUID_CHARACTERISTIC;
	}

	/**
	 * 获取描述UUID
	 *
	 * @return 描述UUID
	 */
	public UUID getUuidDescriptor() {
		return UUID_DESCRIPTOR;
	}

	/**
	 * 获取类对象
	 *
	 * @param type 类型
	 * @param i    位置
	 * @return 类对象
	 */
	private static Class getClass(Type type, int i) {
		if (type instanceof ParameterizedType) { // 处理泛型类型
			return getGenericClass((ParameterizedType) type, i);
		} else if (type instanceof TypeVariable) {
			return getClass(((TypeVariable) type).getBounds()[0], 0); // 处理泛型擦拭对象
		} else {// class本身也是type，强制转型
			return (Class) type;
		}
	}

	private static Class getGenericClass(ParameterizedType parameterizedType, int i) {
		Object genericClass = parameterizedType.getActualTypeArguments()[i];
		if (genericClass instanceof ParameterizedType) { // 处理多级泛型
			return (Class) ((ParameterizedType) genericClass).getRawType();
		} else if (genericClass instanceof GenericArrayType) { // 处理数组泛型
			return (Class) ((GenericArrayType) genericClass).getGenericComponentType();
		} else if (genericClass instanceof TypeVariable) { // 处理泛型擦拭对象
			return getClass(((TypeVariable) genericClass).getBounds()[0], 0);
		} else {
			return (Class) genericClass;
		}
	}

	/**
	 * 初始化蓝牙适配器
	 *
	 * @return 是否初始化成功
	 */
	public boolean initialize() {
		if (mBluetoothManager == null) {
			mBluetoothManager = (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
			if (mBluetoothManager == null) {
				Log.e(TAG, "Unable to initialize BluetoothManager.");
				return false;
			}
		}
		mBluetoothAdapter = mBluetoothManager.getAdapter();
		if (mBluetoothAdapter == null) {
			Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
			return false;
		}
		if (mBleDeviceFactory == null) {
			mBleDeviceFactory = new BleDeviceFactory<>(mContext);
		}

		return true;
	}


	public Class<T> getDeviceClass() {
		return mDeviceClass;
	}

	/**
	 * 暂停视图
	 */
	public void onPause() {
		try {
			if (mContext != null) {
				mContext.unregisterReceiver(mBleReceiver);
			}
		} catch (Exception e) {
			if (AppConfig.DEBUG) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 恢复视图
	 */
	public void onResume() {
		try {
			if (mContext != null) {
				mContext.registerReceiver(mBleReceiver, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
			}
		} catch (Exception e) {
			if (AppConfig.DEBUG) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 获取监听器
	 *
	 * @return 监听器对象
	 */
	public List<BleListener> getBleListeners() {
		return mBleListeners;
	}

	/**
	 * 获取蓝牙适配器
	 *
	 * @return 适配器对象
	 */
	public BluetoothAdapter getBluetoothAdapter() {
		return mBluetoothAdapter;
	}

	/**
	 * 获取设备工厂类
	 *
	 * @return 工厂对象
	 */
	public BleDeviceFactory<T> getBleDeviceFactory() {
		return mBleDeviceFactory;
	}

	/**
	 * 获取当前扫描状态
	 *
	 * @return 状态
	 */
	public int getState() {
		return mState;
	}

	/**
	 * 添加设备
	 *
	 * @param device 设备对象
	 */
	public void addDevice(T device) {
		if (device == null || mDevices.contains(device)) {
			return;
		}
		device.setDeviceListener(this);
		mDevices.add(device);
		mMessageHandler.obtainMessage(MSG_ON_CHANGE).sendToTarget();
	}

	/**
	 * 添加多个设备
	 *
	 * @param deviceList 设备对象列表
	 */
	public void addDevice(List<T> deviceList) {
		if (deviceList == null || deviceList.size() == 0) {
			return;
		}
		synchronized (mDevices) {
			for (T device : deviceList) {
				if (mDevices.contains(device)) {
					continue;
				}
				mDevices.add(device);
			}
		}
		mMessageHandler.obtainMessage(MSG_ON_CHANGE).sendToTarget();
	}


	/**
	 * 添加正在连接的设备
	 *
	 * @param device 设备对象
	 * @return 是否可连接
	 */
	public boolean addConnectingDevice(T device) {
		if (device == null || !mDevices.contains(device)) {
			return false;
		}
		synchronized (mLocker) {
			if (mCurrentDevices.contains(device)) {
				return true;
			}
//			if (mConnectingDevices.size() + mConnectedDevices.size() < AppConfig.MAX_CONNECTED_DEVICE) {
//				if (!mConnectingDevices.contains(device)) {
//					mConnectingDevices.add(device);
//				}
//			}
			if (!mConnectingDevices.contains(device)) {
				return false;
			}
			if (mCurrentDevices.size() >= MAX_CONNECTING_DEVICE) {
				return false;
			}
			mCurrentDevices.add(device);
			return true;
		}
	}

	/**
	 * 获取当前设备列表
	 *
	 * @return 设备列表
	 */
	public List<T> getDevices() {
		return mDevices;
	}

	/**
	 * 添加设备
	 *
	 * @param device 设备对象
	 */
	public void addDevice(BluetoothDevice device) throws Exception {
		if (device == null) {
			return;
		}
		if (contains(device)) {
			return;
		}
		T bleDevice = mBleDeviceFactory.create(this, device.getAddress(), device.getName());
		addDevice(bleDevice);
	}

	/**
	 * 是否包含设备
	 *
	 * @param device 设备对象
	 * @return 包含设备
	 */
	public boolean contains(BluetoothDevice device) {
		if (device == null) {
			return false;
		}
		for (T bleDevice : mDevices) {
			if (bleDevice.equals(device.getAddress())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 获取已有的设备
	 *
	 * @param device 蓝牙设备对象
	 * @return 设备对象
	 */
	public T getDevice(BluetoothDevice device) {
		if (device == null) {
			return null;
		}
		for (T bleDevice : mDevices) {
			if (bleDevice.equals(device.getAddress())) {
				return bleDevice;
			}
		}
		return null;
	}

	/**
	 * 根据地址获取设备
	 *
	 * @param address 设备地址
	 * @return 设备对象
	 */
	public T getDevice(String address) {
		if (TextUtils.isEmpty(address)) {
			return null;
		}
		for (T bleDevice : mDevices) {
			if (bleDevice.equals(address)) {
				return bleDevice;
			}
		}
		return null;
	}

	/**
	 * 是否包含设备
	 *
	 * @param address 设备地址
	 * @return 包含设备
	 */
	public boolean contains(String address) {
		if (TextUtils.isEmpty(address)) {
			return false;
		}
		for (T bleDevice : mDevices) {
			if (bleDevice.equals(address)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 查找设备
	 *
	 * @param idx 设备ID
	 * @return 设备对象
	 */
	public T getBleDevice(int idx) {
		if (idx < 0) {
			return null;
		}
		for (T bleDevice : mDevices) {
			if (idx == bleDevice.getIndex()) {
				return bleDevice;
			}
		}
		return null;
	}

	/**
	 * 获取系统蓝牙管理器
	 *
	 * @return 管理器对象
	 */
	public BluetoothManager getBluetoothManager() {
		if (mBluetoothManager == null) {
			mBluetoothManager = (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
		}
		return mBluetoothManager;
	}

	/**
	 * 获取正在连接的设备
	 *
	 * @return 列表
	 */
	public List<T> getConnectingDevices() {
		return mConnectingDevices;
	}

	/**
	 * 获取已连接设备
	 *
	 * @return 列表
	 */
	public List<T> getConnectedDevices() {
		return mConnectedDevices;
	}

	/**
	 * 获取程序对象
	 *
	 * @return 程序对象
	 */
	public Context getContext() {
		return mContext;
	}

	@Override
	public void onWrite(T device) {
		mMessageHandler.obtainMessage(MSG_ON_DEVICE_WRITE, device.getIndex(), 0).sendToTarget();
	}

	@Override
	public void onRead(T device) {
		mMessageHandler.obtainMessage(MSG_ON_DEVICE_READ, device.getIndex(), 0).sendToTarget();
	}

	@Override
	public void onReady(T device) {
		checkDeviceStep(device);
		mMessageHandler.obtainMessage(MSG_ON_DEVICE_READY, device.getIndex(), 0).sendToTarget();
	}

	@Override
	public void onServiceReady(T device) {
		checkDeviceStep(device);
	}

	@Override
	public void onChanged(T device, byte[] data) {
		mMessageHandler.obtainMessage(MSG_ON_DEVICE_CHANGED, device.getIndex(), 0, data).sendToTarget();
	}

	@Override
	public void onServicesDiscovered(T device) {
		checkDeviceStep(device);
		mMessageHandler.obtainMessage(MSG_ON_SERVICES_DISCOVERED, device.getIndex(), 0).sendToTarget();
	}

	private void checkDeviceStep(T device) {
		if (device == null || !mDevices.contains(device)) {
			return;
		}
		synchronized (mLocker) {
			switch (device.getState()) {
				case BleDevice.STATE_IDLE:
					mConnectingDevices.remove(device);
					mConnectedDevices.remove(device);
					if (mCurrentDevices.contains(device)) {
						mCurrentDevices.remove(device);
						if (mConnectingDevices.size() > 0) {
							mConnectingDevices.get(0).connect();
						}
					}
					break;
				case BleDevice.STATE_CONNECTING:
					if (mCurrentDevices.contains(device)) {
						if (mConnectingDevices.contains(device)) {
							mConnectingDevices.remove(device);
						}
						mCurrentDevices.remove(device);
					}
					if (mConnectingDevices.size() > 0) {
						BleDevice bleDevice = mConnectingDevices.get(0);
						bleDevice.connect();
					}
					break;
				case BleDevice.STATE_CONNECTED:
//					if (!mConnectedDevices.contains(device)) {
//						mConnectedDevices.add(device);
//					}
					device.discoverServices();
					break;
				case BleDevice.STATE_DISCOVERING:
					break;
				case BleDevice.STATE_DISCOVERED:
					device.setEnableCharacteristicNotification();
					break;
				case BleDevice.STATE_WRITING_DESCRIPTOR:
					break;
				case BleDevice.STATE_WROTE_DESCRIPTOR:
					if (mCurrentDevices.contains(device)) {
						mConnectedDevices.add(device);
						mConnectingDevices.remove(device);
						mCurrentDevices.remove(device);
					}
					if (mConnectingDevices.size() > 0) {
						BleDevice bleDevice = mConnectingDevices.get(0);
						bleDevice.connect();
					}
					/**
					 * 记录设备连接
					 */
					try {
						Running.app(Running.ACTION_CONNECT_DEVICE, "connect", device.getBleName() + "," + device.getBleAddress());
					} catch (Exception e) {
						if (AppConfig.DEBUG) {
							e.printStackTrace();
						}
					}
					break;
			}
		}
	}

	@Override
	public void onConnectionChanged(T device) {
		checkDeviceStep(device);
		mMessageHandler.obtainMessage(MSG_ON_CONNECTION_CHANGED, device.getIndex(), 0).sendToTarget();

	}


	/**
	 * 删除设备
	 *
	 * @param device 设备对象
	 */
	public void deleteDevice(T device) {
		if (device == null) {
			return;
		}
		for (T bleDevice : mDevices) {
			if (bleDevice == device || bleDevice.equals(device)) {
				device.setDeleted(true);
				return;
			}
		}
	}

	/**
	 * 移除设备
	 *
	 * @param device 设备对象
	 */
	public void removeDevice(T device) {
		if (device == null) {
			return;
		}
		synchronized (mLocker) {
			if (mDevices.remove(device)) {
				mConnectingDevices.remove(device);
				mConnectedDevices.remove(device);
				mCurrentDevices.remove(device);
				mMessageHandler.obtainMessage(MSG_ON_CHANGE).sendToTarget();
			}
		}
	}

	/**
	 * 移除设备
	 *
	 * @param address 设备对象
	 */
	private void removeDevice(String address) {
		if (TextUtils.isEmpty(address)) {
			return;
		}
		for (T bleDevice : mDevices) {
			if (bleDevice.equals(address)) {
				removeDevice(bleDevice);
			}
		}
	}

	/**
	 * 注册监听事件
	 *
	 * @param bleListener 监听器
	 */
	public void registerBleListener(BleListener<T> bleListener) {
		if (mBleListeners.contains(bleListener)) {
			return;
		}
		mBleListeners.add(bleListener);
		if (bleListener.getBleManager() == null) {
			bleListener.setBleManager(this);
		}
	}

	/**
	 * 获取锁
	 */
	public Object getLocker() {
		return mLocker;
	}

	/**
	 * 取消注册事件
	 *
	 * @param bleListener 监听器
	 */
	public void unRegisterBleListener(BleListener bleListener) {
		if (bleListener == null) {
			return;
		}
		if (mBleListeners.contains(bleListener)) {
			mBleListeners.remove(bleListener);
		}
	}

	/**
	 * 扫描设备
	 */
	public void scanDevice() {
		scanDevice(null);
	}

	/**
	 * 扫描设备
	 *
	 * @param serviceUuids 服务过滤器
	 */
	public void scanDevice(UUID[] serviceUuids) {
		if (mBluetoothAdapter == null) {
			return;
		}
		if (!mBluetoothAdapter.isEnabled()) {
			mBluetoothAdapter.enable();
		} else {
			mMessageHandler.obtainMessage(MSG_SCAN_START, serviceUuids).sendToTarget();
		}
	}

	public void stopScan() {
		if (mState == STATE_STOPPED) {
			return;
		}
		mMessageHandler.obtainMessage(MSG_SCAN_STOP).sendToTarget();
	}

	public boolean writeData(byte[] data) {
		if (mConnectedDevices != null && mConnectedDevices.size() > 0) {
			for (T bleDevice : mConnectedDevices) {
				try {
					bleDevice.writeCharacteristic(data);
				} catch (Exception e) {
					if (AppConfig.DEBUG) {
						e.printStackTrace();
					}
				}
			}
			return true;
		}
		return false;
	}

	public boolean connect(T... devices) {
		if (devices == null || devices.length == 0) {
			return false;
		}
		synchronized (mLocker) {
			boolean maxConnect = false;
			List<T> list = new ArrayList<>();
			for (T device : devices) {
				if (!mDevices.contains(device)) {
					mDevices.add(device);
				}
				if (!mConnectingDevices.contains(device) && device.getState() == BleDevice.STATE_IDLE) {
					if (mConnectingDevices.size() + mConnectedDevices.size() < AppConfig.MAX_CONNECTED_DEVICE) {
						mConnectingDevices.add(device);
						if (mCurrentDevices.size() < MAX_CONNECTING_DEVICE && !mCurrentDevices.contains(device)) {
							mCurrentDevices.add(device);
							list.add(device);
						}
					} else {
						maxConnect = true;
					}
				}
			}
			if (maxConnect) {
				mMessageHandler.obtainMessage(MSG_ON_CONNECT_MAX).sendToTarget();
			}
			if (list.size() > 0) {
				for (T device : list) {
					Log.i(TAG, "Connect Current Device:" + device.getBleAddress());
					device.connect();
				}
			}
		}
		return false;
	}

	public boolean connectAll() {
		if (mDevices.size() == 0) {
			return false;
		}
		synchronized (mLocker) {
			boolean maxConnect = false;
			List<T> list = new ArrayList<>();
			for (T device : mDevices) {
				if (!mConnectingDevices.contains(device) && device.getState() == BleDevice.STATE_IDLE) {
					if (mConnectingDevices.size() + mConnectedDevices.size() < AppConfig.MAX_CONNECTED_DEVICE) {
						mConnectingDevices.add(device);
						if (mCurrentDevices.size() < MAX_CONNECTING_DEVICE && !mCurrentDevices.contains(device)) {
							mCurrentDevices.add(device);
							list.add(device);
						}
					} else {
						maxConnect = true;
					}
				}
			}
			if (maxConnect) {
				mMessageHandler.obtainMessage(MSG_ON_CONNECT_MAX).sendToTarget();
			}
			if (list.size() > 0) {
				for (T device : list) {
					Log.i(TAG, "Connect Current Device:" + device.getBleAddress());
					device.connect();
				}
			}
		}
		return false;
	}

	public void release() {
		try {
			mContext.unregisterReceiver(mBleReceiver);
		} catch (Exception e) {
			if (AppConfig.DEBUG) {
				e.printStackTrace();
			}
		}
		mBleReceiver = null;
		clear();
	}

	public void clear() {
		synchronized (mLocker) {
			for (T bleDevice : mDevices) {
				bleDevice.disconnect();
				bleDevice.close();
			}
			mConnectedDevices.clear();
			mConnectingDevices.clear();
			mCurrentDevices.clear();
			mDevices.clear();
		}
	}

	public void setDevices(List<T> devices) {
		clear();
		if (devices != null && devices.size() > 0) {
			synchronized (mDevices) {
				mDevices.addAll(devices);
				for (T device : mDevices) {
					device.setDeviceListener(this);
				}
			}
		}
	}

	private BluetoothAdapter.LeScanCallback mLeScanCallback =
			new BluetoothAdapter.LeScanCallback() {
				@Override
				public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
					if (AppConfig.DEBUG) {
						Log.i(TAG, "onLeScan:" + device.getName() + "," + device.getAddress() + "," + rssi);
					}
					boolean continued = false;
					for (BleListener bleListener : mBleListeners) {
						continued = continued || bleListener.filterDevice(device, rssi, scanRecord);
					}
					if (continued) {
						return;
					}
//					try {
//						addDevice(device);
//					} catch (Exception e) {
//						if(AppConfig.DEBUG) {
//							e.printStackTrace();
//						}
//					}
				}
			};


	private Runnable mStopRunnable = new Runnable() {
		@Override
		public void run() {
			if (mState == STATE_STOPPED) {
				return;
			}
			if (mState == STATE_SCANNING) {
				mState = STATE_STOPPED;
				mMessageHandler.obtainMessage(MSG_ON_STOP).sendToTarget();
			}
			if (mBluetoothAdapter.getState() == BluetoothAdapter.STATE_ON) {
				mBluetoothAdapter.stopLeScan(mLeScanCallback);
			}
		}
	};

	public static class MessageHandler<T extends BleDevice> extends Handler {
		private WeakReference<BleManager<T>> mReference;

		MessageHandler(BleManager<T> manager, Looper looper) {
			super(looper);
			mReference = new WeakReference<>(manager);
		}

		@Override
		public void dispatchMessage(Message msg) {
			BleManager<T> manager = mReference.get();
			if (manager == null) {
				return;
			}
			switch (msg.what) {
				case MSG_ON_DEVICE_WRITE:
					for (BleListener bleListener : manager.mBleListeners) {
						bleListener.onWrite(manager.getBleDevice(msg.arg1));
					}
					break;
				case MSG_ON_DEVICE_READ:
					for (BleListener bleListener : manager.mBleListeners) {
						bleListener.onRead(manager.getBleDevice(msg.arg1));
					}
					break;
				case MSG_ON_DEVICE_READY:
					for (BleListener bleListener : manager.mBleListeners) {
						bleListener.onReady(manager.getBleDevice(msg.arg1));
					}
					break;
				case MSG_ON_DEVICE_CHANGED:
					for (BleListener bleListener : manager.mBleListeners) {
						bleListener.onChanged(manager.getBleDevice(msg.arg1), (byte[]) msg.obj);
					}
					break;
				case MSG_ON_SERVICES_DISCOVERED:
					for (BleListener bleListener : manager.mBleListeners) {
						bleListener.onServicesDiscovered(manager.getBleDevice(msg.arg1));
					}
					break;
				case MSG_ON_CONNECTION_CHANGED:
					for (BleListener bleListener : manager.mBleListeners) {
						bleListener.onConnectionChanged(manager.getBleDevice(msg.arg1));
					}
					break;
				case MSG_ON_CHANGE:
					for (BleListener bleListener : manager.mBleListeners) {
						bleListener.onChange();
					}
					break;
				case MSG_ON_START:
					for (BleListener bleListener : manager.mBleListeners) {
						bleListener.onStart();
					}
					break;
				case MSG_ON_STOP:
					for (BleListener bleListener : manager.mBleListeners) {
						bleListener.onStop();
					}
					break;
				case MSG_SCAN_START:
					manager.mMessageHandler.removeCallbacks(manager.mStopRunnable);
					if (manager.mState != STATE_SCANNING) {
						manager.mBluetoothAdapter.startLeScan((UUID[]) msg.obj, manager.mLeScanCallback);
					}
					manager.mState = STATE_SCANNING;
					manager.mMessageHandler.obtainMessage(MSG_ON_START).sendToTarget();
					manager.mMessageHandler.postDelayed(manager.mStopRunnable, SCAN_TIME);
					break;
				case MSG_SCAN_STOP:
					manager.mMessageHandler.removeCallbacks(manager.mStopRunnable);
					manager.mStopRunnable.run();
					break;
				case MSG_ON_CONNECT_MAX:
					for (BleListener bleListener : manager.mBleListeners) {
						bleListener.onError(MSG_ON_CONNECT_MAX);
					}
					break;
				default:
					super.dispatchMessage(msg);
			}
		}
	}

	class BleReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(intent.getAction())) {
				int status = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1);
				if (status == BluetoothAdapter.STATE_ON) {
					scanDevice();
				}
			}
		}
	}
}