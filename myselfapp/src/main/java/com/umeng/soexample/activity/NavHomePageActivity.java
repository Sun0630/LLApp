package com.umeng.soexample.activity;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ViewGroup;

import com.android.core.control.statusbar.StatusBarUtil;
import com.umeng.soexample.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by admin on 2017/2/23.
 */

public class NavHomePageActivity extends AppCompatActivity {
    @Bind(R.id.toolbar_layout)
    CollapsingToolbarLayout toolbarLayout;
    @Bind(R.id.home_page_toolbar)
    Toolbar toolbar;
//    @Override
//    protected int getLayoutResource() {
//        isShowTool(false);
//        isTransparentSystem(true);
//        return R.layout.activity_nav_home_page;
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav_home_page);
        ButterKnife.bind(this);

        toolbarLayout.setTitle(getString(R.string.app_name));
        initTranslucentBar();
    }

//    @Override
//    protected void onInitView() {
//
//    }

    private void initTranslucentBar() {
        StatusBarUtil.setTranslucentForImageView(this, 0, toolbarLayout);
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) toolbarLayout.getLayoutParams();
        layoutParams.setMargins(0, -StatusBarUtil.getStatusBarHeight(this), 0, 0);
        ViewGroup.MarginLayoutParams layoutParams2 = (ViewGroup.MarginLayoutParams) toolbar.getLayoutParams();
        layoutParams2.setMargins(0, StatusBarUtil.getStatusBarHeight(this), 0, 0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

}
