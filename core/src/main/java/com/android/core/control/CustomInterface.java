package com.android.core.control;

import android.support.v4.view.PagerAdapter;

/**
 * @作者: liulei
 * @公司：希顿科技
 */
public interface CustomInterface {
    void updateIndicatorView(int size, int resid);

    void setAdapter(PagerAdapter adapter);

    void startScorll();

    void endScorll();
}
