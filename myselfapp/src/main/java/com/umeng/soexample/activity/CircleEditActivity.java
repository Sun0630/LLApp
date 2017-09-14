package com.umeng.soexample.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.core.adapter.CommonAdapter;
import com.android.core.adapter.ViewHolder;
import com.android.core.base.AbsBaseActivity;
import com.bumptech.glide.Glide;
import com.umeng.soexample.R;
import com.umeng.soexample.bean.CircleVO;
import com.umeng.soexample.bean.SendEvent;
import com.umeng.soexample.run.step.bean.StepData;

import java.util.ArrayList;

import butterknife.Bind;
import de.greenrobot.event.EventBus;

public class CircleEditActivity extends AbsBaseActivity {

    @Bind(R.id.gv_photo)
    GridView gridView;
    @Bind(R.id.et_content)
    EditText editText;


    private ArrayList<String> mList;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_circle_edit;
    }

    @Override
    protected void onInitView() {

        initTool();

        mList = getIntent().getStringArrayListExtra("photos");

        gridView.setAdapter(new CommonAdapter<String>(this,mList,R.layout.grid_circle_item) {
            @Override
            public void convert(ViewHolder helper, String item) {
                Glide.with(CircleEditActivity.this)
                        .load(item)
                        .into((ImageView) helper.getView(R.id.gv_img));
            }
        });
    }

    private void initTool() {
        setTitle("");
        TextView send = (TextView) toolbar.findViewById(R.id.right_text);
        send.setText("发表");
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CircleVO.Images images = new CircleVO.Images(mList);
                CircleVO circleVO = new CircleVO(editText.getText().toString().trim(),"小艾","",images);
                EventBus.getDefault().post(new SendEvent(circleVO));
                finish();
            }
        });
        toolbar.setNavigationIcon(R.mipmap.abc_ic_ab_back_mtrl_am_alpha);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
