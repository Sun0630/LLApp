package com.umeng.soexample.activity;

import android.view.View;

import com.android.core.base.AbsBaseActivity;
import com.umeng.soexample.R;
import com.umeng.soexample.custom.ToShare;

import butterknife.OnClick;

/**
 * Created by liulei on 2016/5/31.
 */
public class ShareActivity extends AbsBaseActivity {

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_share;
    }

    @Override
    protected void onInitView() {
        setTitle("分享");
        toolbar.setNavigationIcon(R.mipmap.abc_ic_ab_back_mtrl_am_alpha);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @OnClick(R.id.share)
    void share(){
        startActivity(ToShare.class);
    }


}
