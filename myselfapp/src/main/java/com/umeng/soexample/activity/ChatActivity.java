package com.umeng.soexample.activity;

import android.os.Build;
import android.support.design.widget.AppBarLayout;
import android.view.View;

import com.android.core.base.AbsBaseActivity;
import com.heaton.liulei.utils.utils.ScreenUtils;
import com.umeng.soexample.R;

import butterknife.Bind;

/**
 * 作者：刘磊 on 2016/10/26 14:04
 * 公司：希顿科技
 */

public class ChatActivity extends AbsBaseActivity {

    @Bind(R.id.appbar_chat)
    AppBarLayout mAppbarSetting;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_chat;
    }

    @Override
    protected void onInitView() {
        setTitle("聊天");
        toolbar.setNavigationIcon(R.mipmap.abc_ic_ab_back_mtrl_am_alpha);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
