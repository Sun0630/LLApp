package com.umeng.soexample.activity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.android.core.base.AbsBaseActivity;
import com.heaton.liulei.utils.utils.LogUtils;
import com.heaton.liulei.utils.utils.ToastUtil;
import com.umeng.soexample.R;
import com.umeng.soexample.custom.TemperatureView;

import java.math.BigDecimal;
import java.util.List;

import butterknife.Bind;

public class TemperatureActivity extends AbsBaseActivity {

    @Bind(R.id.temperature_view)
    TemperatureView temperatureView;
    @Bind(R.id.humidity_view)
    TemperatureView humidityView;

    private SensorManager mSensorManager;
    private Sensor mTemperaSensor;
    private Sensor mHumiditySensor;
    private TempListener tempListener;
    private HumidityListener humidityListener;

    @Override
    protected int getLayoutResource() {
        isShowTool(false);
        return R.layout.activity_temperature;
    }

    @Override
    protected void onInitView() {

        if(humidityView != null){
            humidityView.setPanelText("当前湿度");
            humidityView.setUnit(" %");
            humidityView.setTikeCount(40);
        }

        //获取传感器
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        //获取温度传感器
        mTemperaSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        //获取湿度传感器
        mHumiditySensor = mSensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);

        List<Sensor> deviceSensors = mSensorManager.getSensorList(Sensor.TYPE_ALL);
        for (Sensor sensor : deviceSensors) {
            Log.i("sensor", "------------------");
            Log.i("sensor", sensor.getName());
            Log.i("sensor", sensor.getVendor());
            Log.i("sensor", Integer.toString(sensor.getType()));
            Log.i("sensor", "------------------");
        }

        if(mTemperaSensor == null){
            ToastUtil.showToast("您的设备暂不支持温度传感器功能");
        }

        if(mHumiditySensor == null){
            ToastUtil.showToast("您的设备暂不支持湿度传感器功能");
        }

        //给传感器注册一个时间监听器     监听传感器的数据的变化
        tempListener = new TempListener();
        humidityListener = new HumidityListener();

        mSensorManager.registerListener(tempListener,mTemperaSensor,SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(humidityListener,mHumiditySensor,SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this.tempListener);
        mSensorManager.unregisterListener(this.humidityListener);
    }

    private class TempListener implements SensorEventListener{

        @Override
        public void onSensorChanged(SensorEvent event) {
            float temperatureValue = event.values[0];
            BigDecimal bd = new BigDecimal(temperatureValue);
            double temperature = bd.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
            temperatureView.setCurrentTemp((float) temperature);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            LogUtils.e(this,"onAccuracyChanged");
        }
    }

    private class HumidityListener implements SensorEventListener{

        @Override
        public void onSensorChanged(SensorEvent event) {
            float humidityValue = event.values[0];    // 利用这些数据执行一些工作
            BigDecimal bd = new BigDecimal(humidityValue);
            double humidity = bd.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
            temperatureView.setCurrentTemp((float) humidity);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            LogUtils.e(this,"onAccuracyChanged");
        }
    }
}
