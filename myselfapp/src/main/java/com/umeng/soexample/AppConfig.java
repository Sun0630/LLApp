package com.umeng.soexample;

import android.content.Context;
import android.graphics.Color;
import android.os.Environment;

import com.heaton.liulei.utils.utils.FileOperateUtils;
import com.heaton.liulei.utils.utils.LogUtils;

import java.io.File;

/**
 * Created by admin on 2016/11/16.
 */

public class AppConfig {

    private static final String TAG = "AppConfig";

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
