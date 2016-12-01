package com.example.myselfapp.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.android.core.StaticValue;
import com.bumptech.glide.Glide;
import com.example.myselfapp.MainActivity;
import com.example.myselfapp.R;
import com.heaton.liulei.utils.utils.OtherUtils;
import com.heaton.liulei.utils.utils.SPUtils;


public class SplashActivity extends AppCompatActivity {

    private static final int SHOW_TIME = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //隐藏华为等手机底部虚拟按键
        OtherUtils.hideSystemUI(this);
        setContentView(R.layout.activity_splash);

        ImageView gifImg = (ImageView) findViewById(R.id.gif);
        String url = "http://img2.selfimg.com.cn/vogueminiParagaph/2015/06/05/1433497686_zR0tbN.gif";

        Glide.with(this).load(url).asGif().into(gifImg);
        new AsyncTask<Void, Void, Integer>() {
            @Override
            protected Integer doInBackground(Void... params) {
                int result;
                long startTime = System.currentTimeMillis();
                result = loadingCache();
                long loadingTime = System.currentTimeMillis() - startTime;
                if (loadingTime < SHOW_TIME) {
                    try {
                        Thread.sleep(SHOW_TIME - loadingTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                return result;
            }
            @Override
            protected void onPostExecute(Integer result) {
                Intent intent;
                //是否打开图案密码验证
                boolean is_open_psw = SPUtils.get(getBaseContext(), StaticValue.IS_PSW_OPEN,false);
                if(is_open_psw){
                    intent = new Intent(SplashActivity.this,SetPatternActivity.class).putExtra("flag",1);
                }else {
                    intent = new Intent(SplashActivity.this,LoginActivity.class);
                }
//                intent = new Intent(SplashActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }.execute(new Void[]{});

    }

    private int loadingCache() {
        return 0;
    }
}
