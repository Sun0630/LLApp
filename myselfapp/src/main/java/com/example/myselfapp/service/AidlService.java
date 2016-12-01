package com.example.myselfapp.service;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.myselfapp.IMyAidlInterface;
import com.example.myselfapp.MainActivity;
import com.example.myselfapp.R;
import com.heaton.liulei.utils.utils.ToastUtil;

/**
 * 作者：刘磊 on 2016/10/14 15:50
 * 公司：希顿科技
 */

public class AidlService extends Service {

    boolean flag;
    private final static String TAG = "MainService";
    private Activity activity;

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "++MainService onDestroy++");
        flag = false;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "++MainService onCreate++");

//        Notification no = new Notification(R.mipmap.ic_launcher, "有通知到来", System.currentTimeMillis());
//        Intent intent = new Intent(this, MainActivity.class);
//        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, 0);
//        no.setLatestEventInfo(this, "AIDLDemo", "running", pi);
//        startForeground(1, no);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
//        Bundle bundle = intent.getExtras();
//        flag = bundle.getBoolean("flag");
//        System.out.println(flag);
        return ms;
    }

    //这里是进行每个方法实现的地方  最终回调结果
    IMyAidlInterface.Stub ms = new IMyAidlInterface.Stub() {
        @Override
        public void turnLeft() throws RemoteException {
            Log.e(TAG,"向左。。。");
        }

        @Override
        public void turnRight() throws RemoteException {
            Log.e(TAG,"向右。。。");
        }

        @Override
        public void noChange() throws RemoteException {

        }

        @Override
        public void shark() throws RemoteException {
            Log.e(TAG,"摇一摇。。。");
            ToastUtil.showToast("摇到漂亮妹子一枚");
//            new AlertDialog.Builder(this).setCancelable(false)
//                    .setIcon(R.mipmap.touxiang)
//                    .setMessage("摇到漂亮妹子一枚")
//                    .setTitle("摇一摇")
//                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.dismiss();
//                        }
//                    })
//                    .setNegativeButton("我不要", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.dismiss();
//                        }
//                    }).show();
        }
    };

    /**
     *
     * @param activity
     * 初始化MainActivity对象
     */
    public void setMainActivity(MainActivity activity)
    {
        this.activity=activity;
    }


}
