package com.android.core.base;

import android.app.Activity;

/**
 * @author: liulei
 * @date: 2016-09-26 17:09
 */
public interface BaseView {

    /**
     * 跳转到指定的activity   无参数
     * @param targetActivity
     */
    void startActivity(Class<? extends Activity> targetActivity);

//    /**
//     * 带参数的activity跳转
//     * @param targetActivity
//     * @param params
//     */
//    void startActivity(Class<? extends Activity> targetActivity,NameValuePair...params);

    void showMessage(String msg);

    void showProgress(String msg);

    void showProgress(String msg, int progress);

    void hideProgress();

    void showErrorMessage(String msg, String content);

}
