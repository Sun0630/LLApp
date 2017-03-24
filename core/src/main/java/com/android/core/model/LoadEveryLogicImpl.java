//package com.android.core.model;
//
//import com.android.core.base.BasePresenter;
//
//import retrofit2.Response;
//
///**
// * @作者: liulei
// * @公司：希顿科技
// */
//public class LoadEveryLogicImpl<T> extends BasePresenter<LoadEveryLogic.LoadEveryView> implements LoadEveryLogic<T> {
//
//    @Override
//    public void onLoadCompleteData(Response<T> response) {
//        T body = response.body();
//        if (body != null)
//            getView().onLoadComplete(body);
//    }
//
//    @Override
//    public void onFailer(String msg) {
//        getView().hideProgress();
//        getView().onLoadFailer(msg);
//    }
//}
