package com.umeng.soexample.run;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.core.adapter.CommonAdapter;
import com.android.core.adapter.ViewHolder;
import com.android.core.base.AbsBaseActivity;
import com.umeng.soexample.R;
import com.umeng.soexample.run.step.bean.StepData;
import com.umeng.soexample.run.step.utils.DbUtils;

import java.util.List;


/**
 *
 * Created by yuandl on 2016-10-18.
 */

public class HistoryActivity extends AbsBaseActivity {
    private ListView lv;

    private void assignViews() {
        lv = (ListView) findViewById(R.id.lv);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.ac_history;
    }

    @Override
    protected void onInitView() {
        setTitle("历史记录");

        assignViews();

        initData();
    }

    private void initData() {
        setEmptyView(lv);
        if(DbUtils.getLiteOrm()==null){
            DbUtils.createDb(this, "jingzhi");
        }
        List<StepData> stepDatas =DbUtils.getQueryAll(StepData.class);
//        Logger.d("stepDatas="+stepDatas);
        lv.setAdapter(new CommonAdapter<StepData>(this,stepDatas,R.layout.run_history_item) {
            @Override
            public void convert(ViewHolder helper, StepData item) {
                helper.setText(R.id.tv_date,item.getToday());
                helper.setText(R.id.tv_step,item.getStep()+"步");
            }
        });
    }

    protected <T extends View> T setEmptyView(ListView listView) {
        TextView emptyView = new TextView(this);
        emptyView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        emptyView.setText("暂无数据！");
        emptyView.setGravity(Gravity.CENTER);
        emptyView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        emptyView.setVisibility(View.GONE);
        ((ViewGroup) listView.getParent()).addView(emptyView);
        listView.setEmptyView(emptyView);
        return (T) emptyView;
    }
}
