package com.umeng.soexample.base;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.baronzhang.android.router.Router;
import com.umeng.soexample.service.RouterService;

/**
 * Created by LiuLei on 2017/7/18.
 */

public class RouterBaseActivity extends Activity {

    public RouterService routerService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        routerService = new Router(this).create(RouterService.class);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
