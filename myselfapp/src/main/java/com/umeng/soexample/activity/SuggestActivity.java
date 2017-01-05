package com.umeng.soexample.activity;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;

import com.android.core.base.AbsBaseActivity;
import com.umeng.soexample.R;
import com.heaton.liulei.utils.utils.ToastUtil;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by admin on 2016/12/8.
 */

public class SuggestActivity extends AbsBaseActivity {

    @Bind(R.id.suggest_edit)
    EditText suggest_edit;
    @Override
    protected int getLayoutResource() {
        return R.layout.activity_suggest;
    }

    @Override
    protected void onInitView() {
        setTitle("意见");
        toolbar.setNavigationIcon(R.mipmap.abc_ic_ab_back_mtrl_am_alpha);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }

    @OnClick(R.id.commit)
    void commit(){
        if(suggest_edit.getText().toString().trim().length()<=0){
            Animation anim = AnimationUtils.loadAnimation(
                    this, R.anim.myanim);
            suggest_edit.startAnimation(anim);
            ToastUtil.showToast("输入内容不能为空哦！");
        }
    }

}
