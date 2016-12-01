package com.example.myselfapp.activity;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.android.core.base.AbsBaseActivity;
import com.example.myselfapp.R;
import com.example.myselfapp.manager.NotifyManager;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by liulei on 2016/5/31.
 */
public class NotifyActivity extends AbsBaseActivity {

    private int flag;
    @Override
    protected int getLayoutResource() {
        return R.layout.activity_notify;
    }

    @Override
    protected void onInitView() {
        setTitle("通知栏");
        toolbar.setNavigationIcon(R.mipmap.abc_ic_ab_back_mtrl_am_alpha);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @OnClick(R.id.simple_notify)
    void simple(){
        flag = 0;
        NotifyManager.showSimpleNotify(getApplication(),flag);
    }

    @OnClick(R.id.progress_notify)
    void progress(){
        flag = 1;
        NotifyManager.showNotifyProgress(getApplication(),flag);
    }

    @OnClick(R.id.full_notify)
    void full(){
        flag = 2;
        NotifyManager.showFullScreen(getApplication(),flag);
    }

    @OnClick(R.id.custom_notify)
    void custom(){
        flag = 3;
        NotifyManager.shwoNotify(getApplication(),flag);
    }

    @OnClick(R.id.hide_notify)
    void hide(){
        NotifyManager.hideNotify(getApplication(),flag);
    }

    @OnClick(R.id.hide_all)
    void hide_all(){
        NotifyManager.hideAllNotify(getApplication());
    }
}
