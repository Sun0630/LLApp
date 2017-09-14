package com.umeng.soexample.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.android.core.StaticValue;
import com.bumptech.glide.Glide;
import com.umeng.soexample.Constants;
import com.umeng.soexample.MainActivity;
import com.umeng.soexample.R;
import com.heaton.liulei.utils.utils.OtherUtils;
import com.heaton.liulei.utils.utils.SPUtils;
import com.umeng.soexample.custom.ad.AdCountView;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;


public class SplashActivity extends AppCompatActivity implements View.OnClickListener, AdCountView.OnStatusChangeListener {

    private static final int SHOW_TIME = 4000;
    private AdCountView adCountView;
    private Subscription subscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //隐藏华为等手机底部虚拟按键
        OtherUtils.hideSystemUI(this);
        setContentView(R.layout.activity_splash);

        adCountView = (AdCountView) findViewById(R.id.ad_time);

        initAd();

        //使用RXJava代码实现
        //这里的RxJava使用了两个操作符：一个是timer操作符，它的意思是延迟执行某个操作；一个是map操作符，它的意思是转换某个执行结果。
        subscription = Observable.timer(4, TimeUnit.SECONDS, AndroidSchedulers.mainThread()).map(l -> {
            junpActivity();
            return null;
        }).subscribe();

//        ImageView gifImg = (ImageView) findViewById(R.id.gif);
//        String url = "http://img2.selfimg.com.cn/vogueminiParagaph/2015/06/05/1433497686_zR0tbN.gif";
//        gifImg.setImageResource(R.mipmap.);
//        Glide.with(this).load(url).asGif().into(gifImg);

//        new AsyncTask<Void, Void, Integer>() {
//            @Override
//            protected Integer doInBackground(Void... params) {
//                int result;
//                long startTime = System.currentTimeMillis();
//                result = loadingCache();
//                long loadingTime = System.currentTimeMillis() - startTime;
//                if (loadingTime < SHOW_TIME) {
//                    try {
//                        Thread.sleep(SHOW_TIME - loadingTime);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//                return result;
//            }
//            @Override
//            protected void onPostExecute(Integer result) {
//                boolean isFirstRun = SPUtils.get(getApplication(), Constants.IS_FIRST_RUN,true);
//                if(isFirstRun){
//                    SPUtils.put(getApplication(),Constants.IS_FIRST_RUN,false);
//                    startActivity(new Intent(SplashActivity.this,GuideActivity.class));
//                }else {
//                    Intent intent;
//                    //是否打开图案密码验证
//                    boolean is_open_psw = SPUtils.get(getBaseContext(), StaticValue.IS_PSW_OPEN,false);
//                    if(is_open_psw){
//                        intent = new Intent(SplashActivity.this,SetPatternActivity.class).putExtra("flag",1);
//                    }else {
//                        intent = new Intent(SplashActivity.this,EnterActivity.class);
//                    }
//                    startActivity(intent);
//                }
//                finish();
//            }
//        }.execute(new Void[]{});
//
//    }
//
//    private int loadingCache() {
//        return 0;
    }

    private void junpActivity() {
        boolean isFirstRun = SPUtils.get(getApplication(), Constants.IS_FIRST_RUN, true);
        if (isFirstRun) {
            SPUtils.put(getApplication(), Constants.IS_FIRST_RUN, false);
            startActivity(new Intent(SplashActivity.this, GuideActivity.class));
        } else {
            Intent intent;
            //是否打开图案密码验证
            int psw_type = SPUtils.get(getBaseContext(), StaticValue.PSW_TYPE, 0);
            if(psw_type == 1){//指纹验证
                intent = new Intent(SplashActivity.this,FingerDialog.class).putExtra(StaticValue.FINGER,1);
            }
            else if (psw_type == 2) {//图案验证
                intent = new Intent(SplashActivity.this, SetPatternActivity.class).putExtra("flag", 1);
            } else {
                String  count = SPUtils.get(getBaseContext(),Constants.APP_COUNT,null);
                String  psw = SPUtils.get(getBaseContext(),Constants.APP_PSW,null);
                if(count!=null && psw!=null){
                    intent = new Intent(SplashActivity.this, MainActivity.class);
                }else {
                    intent = new Intent(SplashActivity.this, EnterActivity.class);
                }
            }
            startActivity(intent);
        }
        finish();
    }

    private void initAd() {
        adCountView.setBackgroundColor(getResources().getColor(R.color.translucent01));
        adCountView.setOnClickListener(this);
        adCountView.setOnStatusChangeListener(this);
        adCountView.setOutRingColor(StaticValue.color);
        adCountView.setTextColor(getResources().getColor(R.color.white));
        adCountView.setText("跳过");
        adCountView.setTextSize(13);
        adCountView.setInverseAnim(true);
        adCountView.setTime(4000);
        adCountView.start();
    }

    @Override
    public void onClick(View v) {
        subscription.unsubscribe();
        junpActivity();
    }

    @Override
    public void onCountViewStart() {

    }

    @Override
    public void onCountViewStop() {

    }
}
