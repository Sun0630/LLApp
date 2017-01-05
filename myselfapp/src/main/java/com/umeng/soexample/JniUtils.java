package com.umeng.soexample;

/**
 * 作者：刘磊 on 2016/10/27 10:26
 * 公司：希顿科技
 */

public class JniUtils {

    static {
        System.loadLibrary("LiuleiJni");   //defaultConfig.ndk.moduleName
    }

    public native String getMyName();
}
