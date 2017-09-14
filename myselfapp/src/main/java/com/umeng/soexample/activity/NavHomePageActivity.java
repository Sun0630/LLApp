package com.umeng.soexample.activity;

import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.WindowManager;

import com.android.core.StaticValue;
import com.umeng.soexample.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by LiuLei on 2017/2/23.
 */

public class NavHomePageActivity extends AppCompatActivity {
    @Bind(R.id.nav_toolbar_layout)
    CollapsingToolbarLayout toolbarLayout;
    @Bind(R.id.home_page_toolbar)
    Toolbar toolbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        setContentView(R.layout.activity_nav_home_page);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        toolbarLayout.setTitle(getString(R.string.app_name));
        toolbarLayout.setContentScrimColor(StaticValue.color);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

}
