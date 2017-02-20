/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package com.android.core.utils;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.android.core.R;
import com.android.core.StaticValue;
import com.heaton.liulei.utils.utils.SPUtils;


public class ThemeUtils {

    private ThemeUtils() {}

//    private static final int[] THEME_IDS = new int[] {
//            R.style.Base_Theme_DesignDemo,
//            R.style.NightTheme
//    };

    public static int getThemeColor(Context context) {
        int color = SPUtils.get(context, StaticValue.THEME_COLOR,
                StaticValue.color);
        return color;
    }

    public static void applyTheme(Activity activity) {
        activity.setTheme(getThemeColor(activity));
    }
}
