package com.umeng.soexample.activity;

import android.view.View;

import com.android.core.base.AbsBaseActivity;
import com.umeng.soexample.R;

/**
 * Created by admin on 2017/2/20.
 */

public class SetActivity extends AbsBaseActivity {

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_set;
    }

    @Override
    protected void onInitView() {
        toolbar.setNavigationIcon(R.mipmap.abc_ic_ab_back_mtrl_am_alpha);
        toolbar.setNavigationOnClickListener(view -> finish());
    }
}
