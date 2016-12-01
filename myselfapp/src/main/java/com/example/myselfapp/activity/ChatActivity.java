package com.example.myselfapp.activity;

import android.view.View;

import com.android.core.base.AbsBaseActivity;
import com.example.myselfapp.R;

/**
 * 作者：刘磊 on 2016/10/26 14:04
 * 公司：希顿科技
 */

public class ChatActivity extends AbsBaseActivity {

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
