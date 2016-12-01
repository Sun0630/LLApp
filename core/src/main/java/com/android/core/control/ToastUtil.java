package com.android.core.control;

import android.widget.Toast;

import com.android.core.MainApp;

/**
 * @作者: liulei
 * @公司：希顿科技
 */
public class ToastUtil {

    private static Toast mToast;

    public static void show(String msg) {
        if (mToast == null) {
            mToast = Toast.makeText(MainApp.getContext(), msg, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(msg);
            mToast.setDuration(Toast.LENGTH_SHORT);
        }
        mToast.show();
    }

    public static void show(int msg) {
        if (mToast == null) {
            mToast = Toast.makeText(MainApp.getContext(), msg, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(msg);
            mToast.setDuration(Toast.LENGTH_SHORT);
        }
        mToast.show();
    }

    public static void cancelToast() {
        if (mToast != null) {
            mToast.cancel();
        }
    }
}
