package com.umeng.soexample.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.baronzhang.android.router.RouterInjector;
import com.umeng.soexample.App;
import com.umeng.soexample.R;
import com.umeng.soexample.base.RouterBaseActivity;
import com.umeng.soexample.task.ThreadTask;

public class RouterActivity extends RouterBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_router);
        RouterInjector.inject(this);

        findViewById(R.id.share).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //路由
                routerService.startShareActivity("RouterActivity","哈哈哈哈");
            }
        });

        findViewById(R.id.execute).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ThreadTask.executeTask();
            }
        });
    }
}
