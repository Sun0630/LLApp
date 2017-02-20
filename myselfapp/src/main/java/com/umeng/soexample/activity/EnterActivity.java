package com.umeng.soexample.activity;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.core.base.AbsBaseActivity;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.soexample.MainActivity;
import com.umeng.soexample.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by admin on 2016/12/15.
 */

public class EnterActivity extends AbsBaseActivity {

    @Bind(R.id.enter_bg)
    RelativeLayout bg;
    private List<Integer> list = new ArrayList<>();
    private Integer i;

    @Override
    protected int getLayoutResource() {
        isTransparentSystem(true);
        isShowTool(false);
        return R.layout.activity_enter;
    }

    @Override
    protected void onInitView() {
        bg.setBackgroundResource(getRes());
    }

    private Integer getRes() {
        list.add(R.mipmap.b_1);
        list.add(R.mipmap.b_4);
        list.add(R.mipmap.b_1);
        list.add(R.mipmap.b_4);
        i = list.get(new Random().nextInt(4));
        return i;
    }

    @OnClick(R.id.wechat)
    void wechat(){
        UMShareAPI.get(getApplication()).doOauthVerify(EnterActivity.this, SHARE_MEDIA.WEIXIN,authListener);
    }

    @OnClick(R.id.qq)
    void qq() {
//        startActivity(new Intent(this, WXEntryActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
//        finish();
        UMShareAPI.get(getApplication()).doOauthVerify(EnterActivity.this, SHARE_MEDIA.QQ,authListener);
    }

    @OnClick(R.id.sina)
    void sina() {
//        startActivity(new Intent(this, WXEntryActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
//        finish();
        UMShareAPI.get(getApplication()).doOauthVerify(EnterActivity.this, SHARE_MEDIA.SINA,authListener);
    }

    @OnClick(R.id.number_login)
    void num(){
        startActivity(LoginActivity.class);
        finish();
    }

    UMAuthListener authListener = new UMAuthListener() {
        @Override
        public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {
//            Toast.makeText(LoginActivity.this,"成功了",Toast.LENGTH_LONG).show();
            startActivity(new Intent(EnterActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            finish();
        }

        @Override
        public void onError(SHARE_MEDIA platform, int action, Throwable t) {
            Toast.makeText(EnterActivity.this,"失败："+t.getMessage(),Toast.LENGTH_LONG).show();

        }

        @Override
        public void onCancel(SHARE_MEDIA platform, int action) {
            Toast.makeText(EnterActivity.this,"取消了",Toast.LENGTH_LONG).show();

        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode,resultCode,data);
    }

}
