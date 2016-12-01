package com.heaton.liulei.utils.custom.ad;

import android.content.Context;
import android.view.animation.Interpolator;
import android.widget.Scroller;

/**
 * @version v1.0
 * @类描述：   广告轮播
 * @项目名称：StudyDemo
 * @包 名： com.heaton.liulei.utils.widget.ad_carousel
 * @类名称：BaseAdapter
 * @创建人：刘磊
 * @创建时间：2015-06-26 16:10
 * @修改人：
 * @修改时间：
 * @修改备注：
 */
public class CustomDurationScroller extends Scroller {

    private double scrollFactor = 1;

    public CustomDurationScroller(Context context) {
        super(context);
    }

    public CustomDurationScroller(Context context, Interpolator interpolator) {
        super(context, interpolator);
    }

    /**
     * not exist in android 2.3
     *
     * @param context
     * @param interpolator
     * @param flywheel
     */
    // @SuppressLint("NewApi")
    // public CustomDurationScroller(Context context, Interpolator interpolator, boolean flywheel){
    // super(context, interpolator, flywheel);
    // }

    /**
     * Set the factor by which the duration will change
     */
    public void setScrollDurationFactor(double scrollFactor) {
        this.scrollFactor = scrollFactor;
    }

    @Override
    public void startScroll(int startX, int startY, int dx, int dy, int duration) {
        super.startScroll(startX, startY, dx, dy, (int) (duration * scrollFactor));
    }
}
