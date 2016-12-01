package com.example.myselfapp.activity;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.os.IBinder;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.android.core.base.AbsBaseActivity;
import com.bumptech.glide.Glide;
import com.example.myselfapp.R;
import com.example.myselfapp.floatView.FloatViewService;
import com.example.myselfapp.photoview.PhotoView;
import com.example.myselfapp.task.DownloadImageTask;
import com.heaton.liulei.utils.utils.OtherUtils;
import com.heaton.liulei.utils.utils.SPUtils;
import com.heaton.liulei.utils.utils.ToastUtil;

import java.io.File;

import butterknife.Bind;
import butterknife.OnClick;
import butterknife.OnLongClick;

/**
 * 作者：liulei
 * 公司：希顿科技
 */
public class FloatViewActivity extends AbsBaseActivity {

    private FloatViewService mFloatViewService;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_float;
    }

    @Override
    protected void onInitView() {
        setTitle("悬浮窗");
        toolbar.setNavigationIcon(R.mipmap.abc_ic_ab_back_mtrl_am_alpha);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @OnClick(R.id.start)
    void start(){
        startFloat();
    }

    @OnClick(R.id.close)
    void close(){
        hideFloatingView();
        destroy();
    }

    private void startFloat() {
        //开启服务   全局显示悬浮窗
        Intent intent = new Intent(this, FloatViewService.class);
        startService(intent);
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    /**
     * 连接到悬浮窗Service
     */
    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mFloatViewService = ((FloatViewService.FloatViewServiceBinder) iBinder).getService();
            showFloatingView();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mFloatViewService = null;
        }
    };

    /**
     * 显示悬浮图标
     */
    public void showFloatingView() {
        if (mFloatViewService != null) {
            mFloatViewService.showFloat();
        }
    }

    /**
     * 隐藏悬浮图标
     */
    public void hideFloatingView() {
        if (mFloatViewService != null) {
            mFloatViewService.hideFloat();
        }
    }

    /**
     * 释放PJSDK数据
     */
    public void destroy() {
        try {
            stopService(new Intent(this, FloatViewService.class));
            unbindService(mServiceConnection);
        } catch (Exception e) {
        }
    }

    @Override
    public void onRestart() {
        super.onRestart();
        showFloatingView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        destroy();
    }
}
