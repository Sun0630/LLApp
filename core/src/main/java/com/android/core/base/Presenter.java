package com.android.core.base;

/**
 * @作者: liulei
 * @公司：希顿科技
 */
public interface Presenter<V> {
    void attachView(V mvpView);
    void detachView();
}
