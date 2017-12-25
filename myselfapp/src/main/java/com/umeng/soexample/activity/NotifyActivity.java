package com.umeng.soexample.activity;

import android.view.View;

import com.android.core.base.AbsBaseActivity;
import com.umeng.soexample.R;
import com.umeng.soexample.manager.NotifyManager;

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
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    @OnClick({R.id.simple_notify, R.id.progress_notify, R.id.full_notify,
            R.id.custom_notify, R.id.hide_notify, R.id.hide_all})
    void notifyOnClick(View view){
        switch (view.getId()){
            case R.id.simple_notify:
                flag = 0;
                NotifyManager.showSimpleNotify(getApplication(),flag);
                break;
            case R.id.progress_notify:
                flag = 1;
                NotifyManager.showNotifyProgress(getApplication(),flag);
                break;
            case R.id.full_notify:
                flag = 2;
                NotifyManager.showFullScreen(getApplication(),flag);
                break;
            case R.id.custom_notify:
                flag = 3;
                NotifyManager.shwoNotify(getApplication(),flag);
                break;
            case R.id.hide_notify:
                NotifyManager.hideNotify(getApplication(),flag);
                break;
            case R.id.hide_all:
                NotifyManager.hideAllNotify(getApplication());
                break;
        }
    }
}
