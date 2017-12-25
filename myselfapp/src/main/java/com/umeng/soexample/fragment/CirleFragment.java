package com.umeng.soexample.fragment;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.core.StaticValue;
import com.android.core.adapter.RecyclerAdapter;
import com.android.core.adapter.RecyclerViewHolder;
import com.android.core.base.AbsBaseActivity;
import com.android.core.base.AbsBaseFragment;
import com.android.core.widget.recyclerviewwithfooter.OnLoadMoreListener;
import com.android.core.widget.recyclerviewwithfooter.RecyclerViewWithFooter;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringListener;
import com.facebook.rebound.SpringSystem;
import com.facebook.rebound.SpringUtil;
import com.heaton.liulei.utils.utils.DensityUtils;
import com.heaton.liulei.utils.utils.ImageUtils;
import com.heaton.liulei.utils.utils.ScreenUtils;
import com.heaton.liulei.utils.utils.ToastUtil;
import com.umeng.soexample.App;
import com.umeng.soexample.Constants;
import com.umeng.soexample.R;
import com.umeng.soexample.activity.BigPhotoActivity;
import com.umeng.soexample.activity.BloggerActivity;
import com.umeng.soexample.activity.CameraAty;
import com.umeng.soexample.activity.ChatActivity;
import com.umeng.soexample.activity.ChatRobotActivity;
import com.umeng.soexample.activity.CircleEditActivity;
import com.umeng.soexample.activity.CompressImgActivity;
import com.umeng.soexample.activity.DanmuActivity;
import com.umeng.soexample.activity.DownloadManagerDemo;
import com.umeng.soexample.activity.FloatViewActivity;
import com.umeng.soexample.activity.LeftMenuActivity;
import com.umeng.soexample.activity.MediaPlayerActivtiy;
import com.umeng.soexample.activity.MusicActivity;
import com.umeng.soexample.activity.NotifyActivity;
import com.umeng.soexample.activity.PersonActivity;
import com.umeng.soexample.activity.PopWindowActivity;
import com.umeng.soexample.activity.QrViewActivity;
import com.umeng.soexample.activity.RouterActivity;
import com.umeng.soexample.activity.SQLActivity;
import com.umeng.soexample.activity.ScreenCopyActivity;
import com.umeng.soexample.activity.ShareActivity;
import com.umeng.soexample.activity.SwipBackActivity;
import com.umeng.soexample.activity.TemperatureActivity;
import com.umeng.soexample.activity.VideoChatActivity;
import com.umeng.soexample.adapter.CircleAdapter;
import com.umeng.soexample.adapter.RecycleViewDivider;
import com.umeng.soexample.bean.CircleVO;
import com.umeng.soexample.bean.ImageBDInfo;
import com.umeng.soexample.bean.ImageInfo;
import com.umeng.soexample.bean.SendEvent;
import com.umeng.soexample.listener.SensonListener;
import com.umeng.soexample.music.MusicModeActivity;
import com.umeng.soexample.run.RunActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import butterknife.Bind;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import me.iwf.photopicker.PhotoPicker;

import static android.app.Activity.RESULT_OK;

/**
 * 作者：liulei
 * 公司：希顿科技
 */
public class CirleFragment extends AbsBaseFragment implements OnLoadMoreListener, SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {

    @Bind(R.id.circle_listview)
    RecyclerViewWithFooter mRecyclerView;
    @Bind(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @Bind(R.id.nav_toolbar_layout)
    CollapsingToolbarLayout toolbarLayout;
    @Bind(R.id.app_bar)
    AppBarLayout mAppBarLayout;
    @Bind(R.id.fab_share)
    FloatingActionButton share;
    @Bind(R.id.iv_circle_banner)
    ImageView iv_banner;
    @Bind(R.id.commit_soft_layout)
    LinearLayout soft_layout;

    private ArrayList<CircleVO>mArrayList;
    private ArrayList<String> mList;//图片

    private CircleAdapter mAdapter;
    private boolean isBannerAniming; // banner 放大缩小的动画是否正在执行
    private boolean isBannerBig; // banner 是否是大图

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_circle;
    }

    @Override
    protected void onInitView() {

        //注册EventBus
        EventBus.getDefault().register(this);

        toolbarLayout.setTitle(getString(R.string.circle));
        toolbarLayout.setContentScrimColor(StaticValue.color);

        mArrayList = new ArrayList<>();
        mList = new ArrayList<>();
        setDatas();

        mSwipeRefreshLayout.setColorSchemeResources(
                R.color.username1,
                R.color.username2,
                R.color.username3,
                R.color.username4,
                R.color.username5,
                R.color.username6);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        mAdapter = new CircleAdapter(getActivity(),R.layout.circle_item,mArrayList,soft_layout);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.addItemDecoration(new RecycleViewDivider(getActivity(), LinearLayoutManager.HORIZONTAL));
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setOnLoadMoreListener(this);
        mRecyclerView.setEmpty();

//        AlertDialog dialog = new AlertDialog.Builder(getActivity())
//                .setView(R.layout.circle_select_dialog)
//                .create();
//        TextView takepicture = (TextView) dialog.findViewById(R.id.takepicture);
//        TextView gallery = (TextView) dialog.findViewById(R.id.gallery);
        share.setOnClickListener(this);
        iv_banner.setOnClickListener(this);

    }

    private void setDatas() {
        for(int i = 0;i < 5;i++){
            CircleVO.Images images = new CircleVO.Images(mList);
            CircleVO circleVO = new CircleVO("什么也没有发表","小艾"+i,"",images);
            mArrayList.add(circleVO);
        }
        if(mAdapter != null){
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onLoadMore() {
        setDatas();
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(() -> mSwipeRefreshLayout.setRefreshing(false),1500);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fab_share:
                PhotoPicker.builder()
                        .setPhotoCount(9)
                        .setShowCamera(true)
                        .setShowGif(true)
                        .setPreviewEnabled(false)
                        .start(getContext(),CirleFragment.this, PhotoPicker.REQUEST_CODE);
                break;
            case R.id.iv_circle_banner:
                if (isBannerAniming) {
                    return;
                }
                startBannerAnim();
                break;
//            case R.id.takepicture:
//
//                break;
//            case R.id.gallery:
//
//                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //反注册EventBus
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == PhotoPicker.REQUEST_CODE) {
            if (data != null) {
                ArrayList<String> photos = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);

//                File imgFile = new File(photos.get(0));
                startActivity(new Intent(getActivity(), CircleEditActivity.class).putStringArrayListExtra("photos",photos));
            }
        }
    }

    @Subscribe //在ui线程执行
    public void onEventMainThread(SendEvent event) {
        if (event != null) {
            CircleVO circleVO = event.getMsg();
            mArrayList.add(0,circleVO);
            mAdapter.notifyDataSetChanged();

        }
    }


    private void startBannerAnim() {
        final CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) mAppBarLayout.getLayoutParams();
        ValueAnimator animator;
        if (isBannerBig) {
            animator = ValueAnimator.ofInt(ScreenUtils.getScreenHeight(), DensityUtils.dp2px(240));
        } else {
            animator = ValueAnimator.ofInt(DensityUtils.dp2px(240), ScreenUtils.getScreenHeight());
        }
        animator.setDuration(1000);
        animator.addUpdateListener(valueAnimator -> {
            layoutParams.height = (int) valueAnimator.getAnimatedValue();
            mAppBarLayout.setLayoutParams(layoutParams);
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                isBannerBig = !isBannerBig;
                isBannerAniming = false;
            }
        });
        animator.start();
        isBannerAniming = true;
    }

}
