package com.umeng.soexample.listener;

/**
 * 作者：刘磊 on 2016/10/21 17:01
 * 公司：希顿科技
 */

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.Vibrator;
import android.util.Log;

import com.umeng.soexample.IMyAidlInterface;
import com.umeng.soexample.service.AidlService;

import java.util.List;

import static android.content.Context.BIND_AUTO_CREATE;
import static android.content.Context.VIBRATOR_SERVICE;

/**
 * 重力感应类
 */
public class SensonListener implements SensorEventListener {
    private List<Sensor> mSensors;
    private Sensor mSensor;
    private SensorManager mManager;
    private Vibrator mVibrator;
    private SensonChangedListener mListener;
    private IMyAidlInterface myAidlInterface;

    /**
     * 检测的时间间隔
     */
    static final long UPDATE_INTERVAL = 1000;
    /**
     * 上一次检测的时间
     */
    long mLastUpdateTime;

    public SensonListener(Context context) {
        mManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        mVibrator = (Vibrator) context.getSystemService(VIBRATOR_SERVICE);
        mSensors = mManager.getSensorList(Sensor.TYPE_ACCELEROMETER);
        mSensor = mSensors.get(0);
    }

    private ServiceConnection sc = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.e("SensonListener","++start service++");
            myAidlInterface = IMyAidlInterface.Stub.asInterface(service);
//            //通过AIDL远程调用
//            try {
//                myAidlInterface.shark();
//            } catch (RemoteException e) {
//                e.printStackTrace();
//            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.e("SensonListener","++stop service++");
        }
    };

    public void startService(Activity activity){
        //开启服务
        Intent intent = new Intent(activity,AidlService.class);
        activity.startService(intent);
        //连接远程Service和Activity
        Intent bindIntent = new Intent(activity,AidlService.class);
        activity.bindService(bindIntent,sc,BIND_AUTO_CREATE);
    }

    public void stopService(Activity activity){
        //暂停服务
        Intent intent = new Intent(activity, AidlService.class);
        activity.stopService(intent);

        //断开与远程Service的连接
        activity.unbindService(sc);
    }

    /**
     * 打开重力感应
     */
    public void openSensor() {
        try {
            mManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_GAME);
            //第一个参数是监听器
            //第二个参数是传感器类型，Sensor.TYPE_ACCELEROMETER 表示加速度传感器
            //第三个参数是回调的频率，sensorManager.SENSOR_DELAY_NORMAL 表示正常速度
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 关闭sensor  重力感应
     */
    public void closeSensor() {
        mManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        long currentTime = System.currentTimeMillis();
        long diffTime = currentTime - mLastUpdateTime;
        if (diffTime < UPDATE_INTERVAL) {
            return;
        }
        mLastUpdateTime = currentTime;

        if (event.values[1] > 1) {//向右
//            mListener.turnRight();
        } else if (event.values[1] < -1) {//向左
//            mListener.turnLeft();
        } else {
//            mListener.noChange();
        }

        //传感器信息改变时执行该方法
        float[] values = event.values;
        float x = values[0]; // x轴方向的重力加速度，向右为正
        float y = values[1]; // y轴方向的重力加速度，向前为正
        float z = values[2]; // z轴方向的重力加速度，向上为正
//        Log.i("SensonListener", "x轴方向的重力加速度" + x + "；y轴方向的重力加速度" + y + "；z轴方向的重力加速度" + z);
        // 一般在这三个方向的重力加速度达到40就达到了摇晃手机的状态。
        int medumValue = 19;// 三星 i9250怎么晃都不会超过20，没办法，只设置19了
        if (Math.abs(x) > medumValue || Math.abs(y) > medumValue || Math.abs(z) > medumValue) {
            //摇动手机后，再伴随震动提示
            mVibrator.vibrate(500);
            //通过AIDL远程调用
            try {
                myAidlInterface.shark();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
//            mListener.shark();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void setOnSensonChangeListener(SensonChangedListener listener) {
        mListener = listener;
    }

    public interface SensonChangedListener {

        void turnLeft();

        void turnRight();

        void noChange();

        void shark();

    }
}
