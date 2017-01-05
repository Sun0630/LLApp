package com.umeng.soexample.listener;

import android.content.Context;

/**
 * 作者：刘磊 on 2016/10/31 15:25
 * 公司：希顿科技
 */

public interface RequestPermissionListener {

    void requestPermission(String permission,String reason,Runnable runnable);
    Context getContext();

}
