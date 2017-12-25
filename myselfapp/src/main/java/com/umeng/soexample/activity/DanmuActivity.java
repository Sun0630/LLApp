package com.umeng.soexample.activity;

import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.anbetter.danmuku.DanMuView;
import com.android.core.base.AbsBaseActivity;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.heaton.liulei.utils.utils.ToastUtil;
import com.umeng.soexample.R;
import com.umeng.soexample.danmuku.DanMuHelper;
import com.umeng.soexample.danmuku.DanmakuEntity;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by admin on 2016/12/8.
 */

public class DanmuActivity extends AbsBaseActivity {

//    @Bind(R.id.danmu_bg)
//    ImageView bg;
    @Bind(R.id.danmu_bg)
    ImageView bg;

    private DanMuView mDanMuContainerRoom;
    private DanMuView mDanMuContainerBroadcast;
    private DanMuHelper mDanMuHelper;

    String url = "http://ww2.sinaimg.cn/large/610dc034jw1fa42ktmjh4j20u011hn8g.jpg";

    @Override
    protected int getLayoutResource() {
        isTransparentSystem(true);
        isShowTool(false);
        return R.layout.activity_danmu;
    }

    @Override
    protected void onInitView() {
//        setTitle("弹幕");
//        toolbar.setNavigationIcon(R.mipmap.abc_ic_ab_back_mtrl_am_alpha);
//        toolbar.setNavigationOnClickListener(v -> finish());

        initDanmu();
        bg.setBackgroundResource(R.mipmap.b_1);
//        Glide.with(this).load(url).into(bg);
    }

    private void initDanmu() {
        mDanMuHelper = new DanMuHelper(this);

        // 全站弹幕（广播）
        mDanMuContainerBroadcast = (DanMuView) findViewById(R.id.danmaku_container_broadcast);
        mDanMuContainerBroadcast.prepare();
        mDanMuHelper.add(mDanMuContainerBroadcast);

        // 当前房间内的弹幕
        mDanMuContainerRoom = (DanMuView) findViewById(R.id.danmaku_container_room);
        mDanMuContainerRoom.prepare();
        mDanMuHelper.add(mDanMuContainerRoom);

        findViewById(R.id.button).setOnClickListener(view -> {
            DanmakuEntity danmakuEntity = new DanmakuEntity();
            danmakuEntity.setType(DanmakuEntity.DANMAKU_TYPE_USERCHAT);
            danmakuEntity.setName("小A");
            danmakuEntity.setAvatar("http://q.qlogo.cn/qqapp/100229475/E573B01150734A02F25D8E9C76AFD138/100");
            danmakuEntity.setLevel(23);
            danmakuEntity.setText("滚滚长江东逝水，浪花淘尽英雄~~");

            addRoomDanmaku(danmakuEntity);
        });

        findViewById(R.id.button2).setOnClickListener(view -> {
            String jsonStr = "{\"type\":306,\"name\":\"\",\"text\":\"恭喜小A在小马过河的房间12200031赠送幸运礼物-300棒棒糖，中奖500倍，获得5000钻石。\",\"richText\":[{\"type\":\"text\",\"content\":\"恭喜\",\"color\":\"89F9DF\"},{\"type\":\"text\",\"content\":\"小A\"},{\"type\":\"text\",\"content\":\"在\",\"color\":\"89F9DF\"},{\"type\":\"text\",\"content\":\"小马过河\"},{\"type\":\"text\",\"content\":\"的房间\",\"color\":\"89F9DF\"},{\"type\":\"text\",\"content\":12200031},{\"type\":\"text\",\"content\":\"赠送\",\"color\":\"89F9DF\"},{\"type\":\"icon_gift\",\"extend\":\"text\",\"gift_id\":3816,\"content\":\"300棒棒糖\"},{\"type\":\"text\",\"content\":\"，中奖\",\"color\":\"89F9DF\"},{\"type\":\"text\",\"content\":\"500倍\",\"color\":\"FFED0A\"},{\"type\":\"text\",\"content\":\"，获得\",\"color\":\"89F9DF\"},{\"type\":\"text\",\"content\":\"5000钻石。\",\"color\":\"FFED0A\"}],\"live_id\":\"1220003114804106040\"}";

            Gson json = new Gson();
            DanmakuEntity danmakuEntity = json.fromJson(jsonStr, DanmakuEntity.class);
            danmakuEntity.setType(DanmakuEntity.DANMAKU_TYPE_SYSTEM);

            addDanmaku(danmakuEntity);
        });
    }


    /**
     * 发送一条全站弹幕
     */
    private void addDanmaku(DanmakuEntity danmakuEntity) {
        if (mDanMuHelper != null) {
            mDanMuHelper.addDanMu(danmakuEntity, true);
        }
    }

    /**
     * 发送一条房间内的弹幕
     */
    private void addRoomDanmaku(DanmakuEntity danmakuEntity) {
        if (mDanMuHelper != null) {
            mDanMuHelper.addDanMu(danmakuEntity, false);
        }
    }

    @Override
    protected void onDestroy() {
        if (mDanMuHelper != null) {
            mDanMuHelper.release();
            mDanMuHelper = null;
        }

        super.onDestroy();
    }

}
