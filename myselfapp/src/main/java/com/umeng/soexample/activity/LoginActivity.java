package com.umeng.soexample.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.core.Help;
import com.android.core.base.AbsBaseActivity;
import com.example.http.ProgressSubscriber;
import com.example.http.listenser.SubscriberOnNextListener;
import com.heaton.liulei.utils.utils.SPUtils;
import com.umeng.soexample.Constants;
import com.umeng.soexample.MainActivity;
import com.umeng.soexample.R;
import com.umeng.soexample.api.http.RetrofitUtil;
import com.umeng.soexample.bean.CategoryVO;
import com.umeng.soexample.task.TaskExecutor;
import com.umeng.soexample.utils.EditTextClearTools;
import com.heaton.liulei.utils.utils.BlurUtils;
import com.heaton.liulei.utils.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 作者：刘磊 on 2016/10/31 11:31
 * 公司：希顿科技
 */

public class LoginActivity extends AbsBaseActivity implements View.OnTouchListener {

    @Bind(R.id.main_bg)
    FrameLayout main_bg;
    @Bind(R.id.bg)
    ScrollView bg;
    @Bind(R.id.portrait)
    ImageView portrait;
    @Bind(R.id.phonenumber)
    EditText number;
    @Bind(R.id.password)
    EditText psw;
    @Bind(R.id.del_phonenumber)
    ImageView del_num;
    @Bind(R.id.del_password)
    ImageView del_psw;
    @Bind(R.id.login_help_view)
    View mTipsViewRoot;
    @Bind(R.id.pulldoor_close_tips)
    TextView mTipsTextView;
    private Animation mTipsAnimation;

    private Bitmap bitmap;
    private List<Bitmap> list = new ArrayList<>();
    private Handler mHandler = new Handler();
    private String num;
    private String pass;

    @Override
    protected int getLayoutResource() {
        isShowTool(false);
        return R.layout.activity_user_login;
    }

    private void getData() {
        Resources resources = getResources();
        list.add(BitmapFactory.decodeResource(resources, R.mipmap.b_1));
        list.add(BitmapFactory.decodeResource(resources, R.mipmap.b_4));
        list.add(BitmapFactory.decodeResource(resources, R.mipmap.b_1));
        list.add(BitmapFactory.decodeResource(resources, R.mipmap.b_4));
        bitmap = list.get(new Random().nextInt(4));
    }

    private void initHeaderBg() {
//        new Thread(() -> {
//            if (bitmap != null) {
//                //高斯模糊
//                final Drawable drawable = BlurUtils.BoxBlurFilter(bitmap,2);
//                runOnUiThread(() -> setBg(drawable));//使用lamdba表达式
////                runOnUiThread(() ->Glide.with(this).load(R.mipmap.b_1).bitmapTransform(new BlurTransformation(this,25)).crossFade(1000).into(target));
//            }
//        }).start();
        TaskExecutor.executeTask(() -> {
            if (bitmap != null) {
                //高斯模糊
                final Drawable drawable = BlurUtils.BoxBlurFilter(bitmap, 2);
                runOnUiThread(() -> setBg(drawable));//使用lamdba表达式
            }
        });
    }

    private void setBg(Drawable drawable) {
        if (drawable != null)
            main_bg.setBackground(drawable);//崩溃
    }

    @Override
    protected void onInitView() {
        init();
        getData();
        initHeaderBg();
    }

    private void init() {
        setTitle("登录");
        mTipsAnimation = AnimationUtils.loadAnimation(this, R.anim.connection);
        Help.initSystemBar(this, R.color.transparent);//这个对所有的都适合
        Toolbar toolbar = (Toolbar) findViewById(R.id.login_tool);
        toolbar.setBackgroundColor(getResources().getColor(R.color.transparent));
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.WHITE);//设置ToolBar的titl颜色
        //添加清除监听器
        EditTextClearTools.addclerListener(number, del_num);
        EditTextClearTools.addclerListener(psw, del_psw);
        number.setOnTouchListener(this);
        psw.setOnTouchListener(this);

    }

    @OnClick({R.id.rl_bg, R.id.loginButton})
    void loginOnClick(View view) {
        switch (view.getId()){
            case R.id.rl_bg:
                hideSoftKeyboard();
                break;
            case R.id.loginButton:
                num = number.getText().toString().trim();
                pass = psw.getText().toString().trim();
                if (TextUtils.isEmpty(num)) {
                    number.setError("请输入手机号");
                    return;
                }
                if (TextUtils.isEmpty(pass)) {
                    psw.setError("请输入密码");
                    return;
                }
                if (!num.equals("18682176281") || !pass.equals("123")) {
                    ToastUtil.showToast("用户名或密码错误");
                    return;
                }
                final ProgressDialog dialog = new ProgressDialog(this);
                dialog.setMessage("登录中,请稍后...");
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
                hideSoftKeyboard();
                mHandler.postDelayed(() -> startAct(dialog), 2000);
                break;
        }
    }

    private void startAct(ProgressDialog dialog) {
        dialog.dismiss();
        //保存账号密码到内存中   下次直接登录
        SPUtils.put(getBaseContext(), Constants.APP_COUNT, num);
        SPUtils.put(getBaseContext(), Constants.APP_PSW, pass);
        startActivity(new Intent(LoginActivity.this, MainActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        finish();
    }

    @OnClick(R.id.other)
    void other() {
        startActivity(new Intent(this, MainActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mTipsViewRoot.setVisibility(View.VISIBLE);
        if (mTipsTextView != null && mTipsAnimation != null)
            mTipsTextView.startAnimation(mTipsAnimation);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mTipsTextView != null && mTipsAnimation != null)
            mTipsTextView.clearAnimation();
    }

    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (v.getId()) {
            case R.id.phonenumber:
                changeScrollView();
                break;
            case R.id.password:
                changeScrollView();
                break;
        }
        return false;
    }

    /**
     * 使ScrollView指向底部
     */
    private void changeScrollView() {
        new Handler().postDelayed(() -> bg.scrollTo(0, bg.getHeight()), 300);
    }

    private void start() {
        AnimationSet animationSet = new AnimationSet(true);
        ScaleAnimation scaleAnimation = new ScaleAnimation(
                1, 0.1f, 1, 0.1f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setDuration(500);
        animationSet.addAnimation(scaleAnimation);
        animationSet.setFillAfter(true);
        animationSet.setFillBefore(false);
        animationSet.setRepeatCount(0);//设置重复次数
        portrait.startAnimation(scaleAnimation);
        new Handler().postDelayed(
                () -> portrait.setVisibility(View.GONE)
                , 500);
    }

}
