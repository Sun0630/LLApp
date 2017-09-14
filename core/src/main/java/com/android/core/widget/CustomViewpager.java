package com.android.core.widget;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.android.core.R;
import com.android.core.control.ScreenUtil;
import com.android.core.control.CustomInterface;

import java.lang.ref.WeakReference;

/**
 * @作者: liulei
 * @公司：希顿科技
 *
 */
public class CustomViewpager extends RelativeLayout implements CustomInterface {

    private ViewPager vpAd;
    private LinearLayout llIndexContainer;
    private ScrollHandler mHandler;

    public static final int AUTO_SCORLL = 1;
    private boolean isStart;

    public static class ScrollHandler extends Handler{
        private WeakReference<CustomViewpager>mViewPager;

        private ScrollHandler(CustomViewpager viewpager){
            mViewPager = new WeakReference<CustomViewpager>(viewpager);
        }

        @Override
        public void handleMessage(Message msg) {
            CustomViewpager viewpager = mViewPager.get();
            if(viewpager == null){
                return;
            }
            switch (msg.what) {
                case CustomViewpager.AUTO_SCORLL:
                    if (viewpager.isStart) {
                        if (viewpager.vpAd.getChildCount() > 1) {
                            viewpager.vpAd.setCurrentItem(viewpager.vpAd.getCurrentItem() + 1, true);
                        }
                        viewpager.mHandler.sendEmptyMessageDelayed(CustomViewpager.AUTO_SCORLL, 3000);
                    }
                    break;
            }
        }
    }
    public CustomViewpager(Context context) {
        super(context);
        onInitView();
    }

    public CustomViewpager(Context context, AttributeSet attrs) {
        super(context, attrs);
        onInitView();
    }

    public CustomViewpager(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        onInitView();
    }

    private void onInitView() {
        View view = View.inflate(getContext(), R.layout.abc_viewpager_layout, this);
        vpAd = (ViewPager) view.findViewById(R.id.vp_ad);
        llIndexContainer = (LinearLayout) view.findViewById(R.id.ll_index_container);
        mHandler = new ScrollHandler(this);
    }

    @Override
    public void updateIndicatorView(int size, int resId) {
        addIndicatorImageViews(size, resId);
        //设置点滚动时候颜色的变化
        setViewPagerChangeListener(size);
    }

    @Override
    public void setAdapter(PagerAdapter adapter) {
        if (vpAd == null) {
            return;
        }
        vpAd.setAdapter(adapter);
    }

    public void startScorll() {
        isStart = true;
        mHandler.sendEmptyMessageDelayed(AUTO_SCORLL, 3000);
    }


    public void endScorll() {
        isStart = false;
        mHandler.removeMessages(AUTO_SCORLL);
    }


    // 为ViewPager设置监听器
    private void setViewPagerChangeListener(final int size) {
        vpAd.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                if (size > 0) {
                    int newPosition = position % size;
                    for (int i = 0; i < size; i++) {
                        llIndexContainer.getChildAt(i).setEnabled(false);
                        if (i == newPosition) {
                            llIndexContainer.getChildAt(i).setEnabled(true);
                        }
                    }
                }
            }

            @Override
            public void onPageScrolled(int position, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });
    }

    private void addIndicatorImageViews(int size, int resid) {
        llIndexContainer.removeAllViews();
        for (int i = 0; i < size; i++) {
            ImageView iv = new ImageView(getContext());
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ScreenUtil.dp2px(getContext(), 5), ScreenUtil.dp2px(getContext(), 5));
            if (i != 0) {
                lp.leftMargin = ScreenUtil.dp2px(getContext(), 7);
            }
            iv.setLayoutParams(lp);
            if (resid != 0)
                iv.setBackgroundResource(resid);
            else
                //设置导航点的背景图片
                iv.setBackgroundResource(R.drawable.abc_background_indicator);
            iv.setEnabled(false);
            if (i == 0) {
                iv.setEnabled(true);
            }
            llIndexContainer.addView(iv);
        }
    }
}