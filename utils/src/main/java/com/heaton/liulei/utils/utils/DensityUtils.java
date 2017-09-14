package com.heaton.liulei.utils.utils;

/**
 * 作者：刘磊 on 2016/10/31 14:51
 * 公司：希顿科技
 */

import android.util.TypedValue;

/**
 * 常用单位转换的辅助类
 */
public class DensityUtils {
    /**
     * dp转px
     *
     * @param dpVal
     * @return
     */
    public static int dp2px(float dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, LLUtils.getmContext().getResources().getDisplayMetrics());
    }

    /**
     * sp转px
     *
     * @param spVal
     * @return
     */
    public static int sp2px( float spVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                spVal, LLUtils.getmContext().getResources().getDisplayMetrics());
    }

    /**
     * px转dp
     *
     * @param pxVal
     * @return
     */
    public static float px2dp(float pxVal) {
        final float scale = LLUtils.getmContext().getResources().getDisplayMetrics().density;
        return (pxVal / scale);
    }

    /**
     * px转sp
     *
     * @param pxVal
     * @return
     */
    public static float px2sp( float pxVal) {
        return (pxVal / LLUtils.getmContext().getResources().getDisplayMetrics().scaledDensity);
    }

}
