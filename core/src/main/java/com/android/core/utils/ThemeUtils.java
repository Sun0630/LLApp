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


public class ThemeUtils {

    private ThemeUtils() {}

    private static final int[] THEME_IDS = new int[] {
            R.style.Base_Theme_DesignDemo,
            R.style.NightTheme
    };

    public static int getThemeId(Context context) {
//        int index = Integer.valueOf(SPUtils.get(context, StaticValue.KEY_THEME,
//                StaticValue.DEFAULT_THEME));
        int index = StaticValue.THEME_MODE;
        Log.e("ThemeUtils",index+"");
        return THEME_IDS[index];
    }

    public static void applyTheme(Activity activity) {
        activity.setTheme(getThemeId(activity));
    }
}
