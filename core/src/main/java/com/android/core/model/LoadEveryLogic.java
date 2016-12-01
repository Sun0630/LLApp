package com.android.core.model;

import com.android.core.base.BaseView;

import retrofit2.Response;

/**
 * @作者: liulei
 * @公司：希顿科技
 */
public interface LoadEveryLogic<T> {

    void onLoadCompleteData(Response<T> response);

    void onFailer(String msg);

    interface LoadEveryView<T> extends BaseView {
        void onLoadComplete(T body);

        void onLoadFailer(String msg);
    }
}
