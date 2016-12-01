package com.example.myselfapp.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.android.core.Help;
import com.example.myselfapp.MainActivity;
import com.example.myselfapp.R;
import com.example.myselfapp.utils.EditTextClearTools;
import com.heaton.liulei.utils.utils.BlurUtils;
import com.heaton.liulei.utils.utils.BmpUtils;
import com.heaton.liulei.utils.utils.DensityUtils;
import com.heaton.liulei.utils.utils.OtherUtils;
import com.heaton.liulei.utils.utils.ScreenUtils;
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

public class LoginActivity extends AppCompatActivity implements View.OnTouchListener {

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

    private Bitmap bitmap;
    private List<String> list = new ArrayList<>();
    private String URL;
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);
        ButterKnife.bind(this);
        onInitView();
        getData();
        initHeaderBg();
    }

    private void getData() {
        list.add("http://www.qnsb.com/fzepaper/site1/qnsb/res/1/1/2008-11/05/A03/res05_attpic_brief.jpg");
        list.add("http://pic22.nipic.com/20120708/10386452_163619514180_2.jpg");
        list.add("http://g.hiphotos.baidu.com/zhidao/pic/item/d6ca7bcb0a46f21f20aecd84f6246b600d33ae4b.jpg");
        list.add("http://imgsrc.baidu.com/forum/w%3D580/sign=ec38110a9922720e7bcee2f24bca0a3a/fbf2b2119313b07e2f4fe4600dd7912396dd8cec.jpg");
        list.add("http://www.sinaimg.cn/dy/slidenews/2_img/2011_23/808_385367_588363.jpg");
        URL = list.get(new Random().nextInt(5));
    }

    private void initHeaderBg() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                bitmap = BmpUtils.returnBitMap(URL);
                if (bitmap != null) {
                    //高斯模糊
                    final Drawable drawable = BlurUtils.BoxBlurFilter(bitmap);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (drawable != null) {
                                bg.setBackground(drawable);//崩溃
                            }
                        }
                    });
                }
            }
        }).start();
    }

    protected void onInitView() {
        setTitle("登录");
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
    @OnClick(R.id.rl_bg)
    void rl_bg(){
        Log.e("lOGIN","点击全屏背景");
        hideSoftKeyboard();
    }

    @OnClick(R.id.loginButton)
    void login() {
        String num = number.getText().toString().trim();
        String pass = psw.getText().toString().trim();
        if (TextUtils.isEmpty(num) || TextUtils.isEmpty(pass)) {
            ToastUtil.showToast("用户名或密码不可为空");
            return;
        }
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("登录中,请稍后...");
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        hideSoftKeyboard();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();
                startActivity(new Intent(LoginActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                finish();
            }
        }, 2000);
    }

    @OnClick(R.id.other)
    void other() {
        startActivity(new Intent(this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        changeScrollView();
//        start();
        return false;
    }

    /**
     * 使ScrollView指向底部
     */
    private void changeScrollView() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                bg.scrollTo(0, bg.getHeight());
            }
        }, 300);
    }

    private void start(){
        AnimationSet animationSet=new AnimationSet(true);
        ScaleAnimation scaleAnimation=new ScaleAnimation(
                1,0.1f,1,0.1f,
                Animation.RELATIVE_TO_SELF,0.5f,
                Animation.RELATIVE_TO_SELF,0.5f);
        scaleAnimation.setDuration(500);
        animationSet.addAnimation(scaleAnimation);
        animationSet.setFillAfter(true);
        animationSet.setFillBefore(false);
        animationSet.setRepeatCount(0);//设置重复次数
        portrait.startAnimation(scaleAnimation);
        new Handler().postDelayed(new Runnable(){
            @Override
                    public void run(){
                portrait.setVisibility(View.GONE);
            }
        },500);
    }

    /**
     *菜单、返回键响应
     */
//    @Override
//    public boolean onKeyDown(int keyCode,KeyEvent event){
////TODOAuto-generatedmethodstub
//        if(keyCode==KeyEvent.KEYCODE_BACK){
//            if(portrait.getVisibility()==View.GONE){
//                AnimationSet animationSet=new AnimationSet(true);
//                ScaleAnimation scaleAnimation=new ScaleAnimation(
//                        0.1f,1f,0.1f,1f,
//                        Animation.RELATIVE_TO_SELF,0.5f,
//                        Animation.RELATIVE_TO_SELF,0.5f);
//                scaleAnimation.setDuration(500);
//                animationSet.addAnimation(scaleAnimation);
//                animationSet.setFillAfter(true);
//                animationSet.setFillBefore(false);
//                portrait.startAnimation(scaleAnimation);
//
//                portrait.setVisibility(View.VISIBLE);
//
//            }else{
//                finish();
//            }
//
//        }
//        return false;
//    }
    /**
     * hide inputMethod
     */
    public void hideSoftKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            View localView = getCurrentFocus();
            if (localView != null && localView.getWindowToken() != null) {
                IBinder windowToken = localView.getWindowToken();
                inputMethodManager.hideSoftInputFromWindow(windowToken, 0);
            }
        }
    }

    /**
     * show inputMethod
     */
    public void showSoftKeyboard(final EditText editText) {
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
                           public void run() {
                               InputMethodManager inputManager =
                                       (InputMethodManager) editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                               inputManager.showSoftInput(editText, 0);
                           }
                       },
                400);
    }

}
