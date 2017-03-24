package com.umeng.soexample;

import android.graphics.Color;

/**
 * Created by admin on 2016/11/16.
 */

public class AppConfig {

    public static boolean DIALOG_BUTTON_REVERSAL;
    public static boolean CENTER_SINGLE_BUTTON;
    public final static int PAGE_SIZE_CATEGORY = 10;
    public final static String APP_HOST = "http://media.e-toys.cn/";

    public AppConfig() {
    }
    static {
        DIALOG_BUTTON_REVERSAL = false;
        CENTER_SINGLE_BUTTON = false;
    }

}
