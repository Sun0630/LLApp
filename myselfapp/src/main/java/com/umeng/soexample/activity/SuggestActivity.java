package com.umeng.soexample.activity;

import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ProgressBar;

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
    @Bind(R.id.progressbar)
    ProgressBar progressBar;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_suggest;
    }

    @Override
    protected void onInitView() {
        setTitle("意见");
        toolbar.setNavigationIcon(R.mipmap.abc_ic_ab_back_mtrl_am_alpha);
        toolbar.setNavigationOnClickListener(v -> finish());


    }

    @OnClick(R.id.commit)
    void commit() {
        if (suggest_edit.getText().toString().trim().length() <= 0) {
            Animation anim = AnimationUtils.loadAnimation(
                    this, R.anim.myanim);
            suggest_edit.startAnimation(anim);
            ToastUtil.showToast("输入内容不能为空哦！");
        } else {
            progressBar.setVisibility(View.VISIBLE);
            new Handler(getMainLooper()).postDelayed(this::doSuggest, 3000);
        }
    }

    private void doSuggest() {
        progressBar.setVisibility(View.GONE);
        ToastUtil.showToast("您的建议已提交成功,请耐心等待数月，反正也没人理...");
        suggest_edit.setText("");
    }

}
