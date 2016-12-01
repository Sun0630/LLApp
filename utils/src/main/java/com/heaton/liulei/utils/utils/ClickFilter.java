package com.heaton.liulei.utils.utils;

/**
 * Created by liulei on 2016/6/28.
 */

/**
 * 判断并防止用户连续点击事件
 */
public class ClickFilter {
    public static final long INTERVAL = 1000L; //防止连续点击的时间间隔
    private static long lastClickTime; //上一次点击的时间

    /**
     *
     * @return true则代表连续点击    false代表正常
     */
    public static boolean filter() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (0 < timeD && timeD < INTERVAL) {
            ToastUtil.showToast("请勿频繁操作");
            return true;
        }
        lastClickTime = time;
        return false;

    }
}
