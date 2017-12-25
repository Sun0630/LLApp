package com.umeng.soexample.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.core.StaticValue;
import com.heaton.liulei.utils.utils.SPUtils;
import com.heaton.liulei.utils.utils.ToastUtil;
import com.umeng.soexample.MainActivity;
import com.umeng.soexample.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscriber;
import rx.Subscription;
import zwh.com.lib.FPerException;
import zwh.com.lib.FingerPrinterView;
import zwh.com.lib.RxFingerPrinter;

/**
 * Created by LiuLei on 2017/5/5.
 * 指纹验证对话框
 */

public class FingerDialog extends Activity {

    public static final String TAG = "FingerDialog";

    @Bind(R.id.fpv)
    FingerPrinterView fingerPrinterView;
    @Bind(R.id.cv)
    CardView cv;
    @Bind(R.id.finger_result)
    ImageView finger_result;
    private int type = StaticValue.SET_FINGER;// 默认手势设置   1指纹验证
    private int fingerErrorNum = 0; // 指纹错误次数
    private RxFingerPrinter rxfingerPrinter;
    private Subscription subscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.finger_dialog);

        ButterKnife.bind(this);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        getWindow().setGravity(Gravity.CENTER);
        initView();
    }

    private void initView() {

        type = getIntent().getIntExtra(StaticValue.FINGER,0);

        fingerPrinterView.setOnStateChangedListener(state -> {
            if (state == FingerPrinterView.STATE_CORRECT_PWD) {
                if(type == 1){
                    fingerErrorNum = 0;
                    cv.setVisibility(View.GONE);
                    finger_result.setImageResource(R.mipmap.finger_sucess);
                    finger_result.setVisibility(View.VISIBLE);
                    new Handler().postDelayed(() -> {
                        startActivity(new Intent(FingerDialog.this,MainActivity.class));
                        finish();
                    },1500);
                }
            }
            if (state == FingerPrinterView.STATE_WRONG_PWD) {
                if(fingerErrorNum >= 3){
                    finish();
                }
                if(type == 1){
                    ToastUtil.showToast("指纹识别失败，还剩"+(3 - fingerErrorNum) + "次机会");
                    cv.setVisibility(View.GONE);
                    finger_result.setImageResource(R.mipmap.finger_error);
                    finger_result.setVisibility(View.VISIBLE);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            cv.setVisibility(View.VISIBLE);
                            finger_result.setVisibility(View.GONE);
                        }
                    },2000);
                }
                fingerPrinterView.setState(FingerPrinterView.STATE_NO_SCANING);
            }
        });
        rxfingerPrinter = new RxFingerPrinter(this);
        fingerErrorNum = 0;
        rxfingerPrinter.unSubscribe(this);
        subscription = rxfingerPrinter.begin().subscribe(new Subscriber<Boolean>() {
            @Override
            public void onStart() {
                super.onStart();
                if (fingerPrinterView.getState() == FingerPrinterView.STATE_SCANING) {
                    Log.e(TAG,"STATE_SCANING");
                    return;
                } else if (fingerPrinterView.getState() == FingerPrinterView.STATE_CORRECT_PWD
                        || fingerPrinterView.getState() == FingerPrinterView.STATE_WRONG_PWD) {
                    fingerPrinterView.setState(FingerPrinterView.STATE_NO_SCANING);
                    Log.e(TAG,"STATE_NO_SCANING");
                } else {
                    fingerPrinterView.setState(FingerPrinterView.STATE_SCANING);
                    Log.e(TAG,"========STATE_SCANING");
                }
            }

            @Override
            public void onCompleted() {
                Log.e(TAG,"========onCompleted");
            }

            @Override
            public void onError(Throwable e) {
                if(e instanceof FPerException){
                    ToastUtil.showToast(((FPerException) e).getDisplayMessage());
                }
            }

            @Override
            public void onNext(Boolean aBoolean) {
                if(aBoolean){
                    fingerPrinterView.setState(FingerPrinterView.STATE_CORRECT_PWD);
                    if(type == 0){
                        SPUtils.put(FingerDialog.this, StaticValue.PSW_TYPE,1);
                        ToastUtil.showToast("设置指纹成功");
                        Log.e(TAG,"STATE_CORRECT_PWD");
                        finish();
                    }
                }else{
                    if(type == 1){
                        fingerErrorNum++;
                    }
                    fingerPrinterView.setState(FingerPrinterView.STATE_WRONG_PWD);
                    Log.e(TAG,"STATE_WRONG_PWD");
                }
            }
        });
        rxfingerPrinter.addSubscription(this,subscription);

    }

    @OnClick(R.id.close)
    void close(){
        closeApp();
    }

    private void closeApp() {
        if(rxfingerPrinter != null && subscription != null){
            rxfingerPrinter.unSubscribe(this);
            ToastUtil.showToast("指纹验证取消");
            finish();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
            closeApp();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
