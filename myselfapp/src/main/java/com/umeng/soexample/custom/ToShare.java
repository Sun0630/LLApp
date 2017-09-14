package com.umeng.soexample.custom;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.baronzhang.android.router.RouterInjector;
import com.baronzhang.android.router.annotation.inject.InjectUriParam;
import com.heaton.liulei.utils.utils.ToastUtil;
import com.umeng.soexample.R;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMVideo;
import com.umeng.socialize.media.UMusic;
import com.umeng.soexample.base.RouterBaseActivity;

import butterknife.Bind;
import butterknife.ButterKnife;


public class ToShare extends RouterBaseActivity {

    @Bind(R.id.sl_title)
    TextView slTitle;
//    @Bind(R.id.sl_cancel)
//    TextView slCancel;
    @Bind(R.id.wechat_img)
    ImageView wechatImg;
    @Bind(R.id.sl_weixin)
    RelativeLayout slWeixin;
    @Bind(R.id.wechat_space_img)
    ImageView wechatSpaceImg;
    @Bind(R.id.sl_weixin_space)
    RelativeLayout slWeixinSpace;
   /* @Bind(R.id.weibo_img)
    ImageView weiboImg;
    @Bind(R.id.sl_weibo)
    RelativeLayout slWeibo;*/
    @Bind(R.id.qq_img)
    ImageView qqImg;
    @Bind(R.id.sl_qq)
    RelativeLayout slQq;
    @Bind(R.id.qq_space_img)
    ImageView qqSpaceImg;
    @Bind(R.id.qq_space_text)
    TextView qqSpaceText;
    @Bind(R.id.sl_qq_space)
    RelativeLayout slQqSpace;
    @Bind(R.id.sms_img)
    ImageView smsImg;
    @Bind(R.id.sl_sms)
    RelativeLayout slSms;
    UMImage image;
    UMusic music;
    UMVideo video;
    String url;

    @InjectUriParam
    String preActivity;

    @SuppressLint("SetTextI18n")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.share_layout);
        ButterKnife.bind(this);
        RouterInjector.inject(this);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        getWindow().setGravity(Gravity.BOTTOM);
        initView();
    }

    public void initView() {

        ToastUtil.showToast(preActivity);
        //Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.info_icon_1);
        //UMImage image = new UMImage(ShareActivity.this,bitmap);
        UMusic music = new UMusic("http://music.huoxing.com/upload/20130330/1364651263157_1085.mp3");
/*        music = new UMusic("http://y.qq.com/#type=song&mid=002I7CmS01UAIH&tpl=yqq_song_detail");
        music.setTitle("This is music title");
        music.setThumb("http://www.umeng.com/images/pic/social/chart_1.png");

        music.setDescription("my description");*/
        video = new UMVideo("http://video.sina.com.cn/p/sports/cba/v/2013-10-22/144463050817.html");
//        video.setThumb("http://www.adiumxtras.com/images/thumbs/dango_menu_bar_icon_set_11_19047_6240_thumb.png");
        url = "http://www.umeng.com";

        slWeixin.setOnClickListener(new ClickListener());
        slWeixinSpace.setOnClickListener(new ClickListener());
//        slWeibo.setOnClickListener(new ClickListener());
        slQq.setOnClickListener(new ClickListener());
        slQqSpace.setOnClickListener(new ClickListener());
        slSms.setOnClickListener(new ClickListener());
//        slCancel.setOnClickListener(new ClickListener());
    }

    public class ClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.sl_weixin:
                    shareToChat();
                    break;
          /*  case R.id.to_qq_zone:
                shareToQqZone();
				break;*/
                case R.id.sl_weixin_space:
                    shareToChatCircle();
                    break;
//                case R.id.sl_weibo:
//                    shareToSina();
//                    break;
                case R.id.sl_qq:
                    shareToQQ();
                    break;
                case R.id.sl_qq_space:
                    shareToQqSpace();
                    break;
                case R.id.sl_sms:
//                    ShareToSms();
                    shareToSina();
                    break;
//                case R.id.sl_cancel:
//                    ToShare.this.finish();
//                    break;
                default:
                    break;
            }
        }
    }

    private void shareToQqSpace() {
        ShareAction action = new ShareAction(this).setPlatform(SHARE_MEDIA.QZONE).setCallback(umShareListener);
        action.withText("myselfapp");
        action.withTitle("myselfapp");
        if(video!=null){
            action.withMedia(video);
        }else {
            action.withMedia(image);
        }
//                .withMedia(video)
        action.share();
    }

    private void shareToQQ() {
        ShareAction action = new ShareAction(this).setPlatform(SHARE_MEDIA.QQ).setCallback(umShareListener);
        action.withTitle("myselfapp");
        action.withText("myselfapp");
        if(video!=null){
            action.withMedia(video);
        }else {
            action.withMedia(image);
        }
//                .withMedia(video)
//                .withTargetUrl(url)
                            //.withTitle("qqshare")
        action.share();

    }

    private void ShareToSms() {
        new ShareAction(this).setPlatform(SHARE_MEDIA.SMS).setCallback(umShareListener)
                .withText("myselfapp")
//                .withMedia(image)
                .withTitle("myselfapp")
                .withTargetUrl(url)
                .share();
    }

    private void shareToChatCircle() {
        ShareAction action = new ShareAction(this).setPlatform(SHARE_MEDIA.WEIXIN_CIRCLE).setCallback(umShareListener);
        if(video!=null){
            action.withMedia(video);
        }else {
            action.withMedia(image);
        }
        action.withText("myselfapp");
        action.share();
    }

    private void shareToChat() {
        ShareAction action = new ShareAction(this).setPlatform(SHARE_MEDIA.WEIXIN).setCallback(umShareListener);
        if(video!=null){
            action.withMedia(video);
        }else {
            action.withMedia(image);
        }
                        //.withMedia(new UMEmoji(ShareActivity.this,"http://img.newyx.net/news_img/201306/20/1371714170_1812223777.gif"))
        action.withText("myselfapp");
//                .withMedia(video)
        action.withTargetUrl(url);
        action.share();
    }

    private void shareToSina() {
        new ShareAction(this).setPlatform(SHARE_MEDIA.SINA).setCallback(umShareListener)
                .withText("Umeng Share")
                .withTitle("this is title")
                .withMedia(new UMImage(this,R.drawable.ic_launcher))
//                .withExtra(new UMImage(ToShare.this, R.drawable.ic_launcher))
                .withTargetUrl(url)
                .share();
    }
    private UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onResult(SHARE_MEDIA platform) {
            Log.d("plat", "platform" + platform);
            if(platform.name().equals("WEIXIN_FAVORITE")){
                Toast.makeText(ToShare.this, platform + " 收藏成功啦", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(ToShare.this, platform + " 分享成功啦", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            Toast.makeText(ToShare.this,platform + " 分享失败啦", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
            Toast.makeText(ToShare.this,platform + " 分享取消了", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /** attention to this below ,must add this**/
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
        Log.d("result","onActivityResult");
    }

}
