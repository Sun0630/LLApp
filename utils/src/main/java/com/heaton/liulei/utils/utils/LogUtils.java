package com.heaton.liulei.utils.utils;

import android.util.Log;

import com.heaton.liulei.utils.BuildConfig;

/**
 * Created by LiuLei on 2017/5/16.
 */

public class LogUtils {

    private static final boolean isDebug = BuildConfig.DEBUG;

    private static String getTag(Object o){
        String tag = "";
        if(o instanceof String){
            tag = (String) o;
        }else if(o instanceof Number){
            tag = String.valueOf(o);
        }else {
            tag = o.getClass().getSimpleName();
        }
        return tag;
    }

    public static void e(Object o, String msg){
        if(isDebug){
            Log.e(getTag(o),msg);
        }
    }

    public static void i(Object o, String msg){
        if(isDebug){
            Log.i(getTag(o),msg);
        }
    }

    public static void w(Object o, String msg){
        if(isDebug){
            Log.w(getTag(o),msg);
        }
    }

    public static void d(Object o, String msg){
        if(isDebug){
            Log.d(getTag(o),msg);
        }
    }

}
