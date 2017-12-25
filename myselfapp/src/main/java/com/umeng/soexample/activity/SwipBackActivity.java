package com.umeng.soexample.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.android.core.base.AbsBaseSwipeBackActivity;
import com.android.core.widget.SwipeBackLayout;
import com.umeng.soexample.R;

/**
 * @author: liulei
 * @date: 2016-10-26 15:58
 */
public class SwipBackActivity extends AbsBaseSwipeBackActivity {

    public static void start(Context context) {
        Intent intent = new Intent(context, SwipBackActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common);
        setDragEdge(SwipeBackLayout.DragEdge.LEFT);
        setTitle("右滑返回");
        toolbar.setNavigationIcon(R.mipmap.abc_ic_ab_back_mtrl_am_alpha);
        toolbar.setNavigationOnClickListener(v -> finish());
    }
}
