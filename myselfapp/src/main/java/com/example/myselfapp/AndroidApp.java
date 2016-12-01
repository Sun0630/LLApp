package com.example.myselfapp;

import android.graphics.drawable.Drawable;
import android.widget.TextView;

import com.android.core.MainApp;
import com.android.core.control.crash.AndroidCrash;
import com.android.core.control.crash.HttpReportCallback;
import com.android.core.control.logcat.Logcat;
import com.android.core.model.LogicProxy;
import com.example.myselfapp.music.MusicService;
import com.example.myselfapp.music.Playlist;
import com.heaton.liulei.utils.utils.LiuleiUtils;
import com.umeng.socialize.PlatformConfig;

import org.litepal.LitePalApplication;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;

import io.socket.client.IO;
import io.socket.client.Socket;

/**
 * 作者： liulei
 * 公司：希顿科技
 */
public class AndroidApp extends MainApp {

    private static AndroidApp instance;
    public MusicService mMusicServer;
    public ArrayList<Playlist> musicList;
    public int batteryValue;

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;
        //初始化LiuleiUtils
        LiuleiUtils.init(this);
        LitePalApplication.initialize(this);//litepal的配置
        //Android crash 上传服务器回掉
        HttpReportCallback report = new HttpReportCallback() {
            @Override
            public void uploadException2remote(File file) {
                //可以直接上传文件
            }
        };
        AndroidCrash.getInstance().setCrashReporter(report).init(this);
        if (BuildConfig.DEBUG)
            Logcat.init("com.android.racofix").hideThreadInfo().methodCount(3);
    }

    //各个平台的配置，建议放在全局Application或者程序入口
    {
        //微信    wx12342956d1cab4f9,a5ae111de7d9ea137e88a5e02c07c94d
        PlatformConfig.setWeixin("wx1b4ebbf52692b609", "c7ac8f697f3c7661a3356d11f99a0434");
        //新浪微博
//        PlatformConfig.setSinaWeibo("3921700954", "04b48b094faeb16683c32669824ebdad");
        PlatformConfig.setQQZone("1105331365", "XRvsFLkjIW9cBQ6J");
    }

    private Socket mSocket;
    {
        try {
            mSocket = IO.socket(Constants.CHAT_SERVER_URL);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public Socket getSocket() {
        return mSocket;
    }

    public static AndroidApp getInstance() {
        if (instance == null) {
            instance = new AndroidApp();
        }
        return instance;
    }

    /**
     * 更新电池电量及图标
     *
     * @param value 电量值
     */
    public void updateBatteryAndIcon(int value, TextView view) {
        batteryValue = value;
        Drawable battery = null;
        if (value <= 10) {
            battery = getResources().getDrawable(R.mipmap.power0);
        } else if (value <= 25) {
            battery = getResources().getDrawable(R.mipmap.power1);
        } else if (value <= 50) {
            battery = getResources().getDrawable(R.mipmap.power2);
        } else if (value <= 75) {
            battery = getResources().getDrawable(R.mipmap.power3);
        } else {
            battery = getResources().getDrawable(R.mipmap.power4);
        }
        battery.setBounds(0, 0, battery.getMinimumWidth(), battery.getMinimumHeight());
        view.setCompoundDrawables(battery, null, null, null);
        view.setText(value + "%");
    }

}
