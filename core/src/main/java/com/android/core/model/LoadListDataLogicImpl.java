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
//public class LoadListDataLogicImpl<T> extends BasePresenter<LoadListDataLogic.LoadListView> implements LoadListDataLogic<T> {
//    @Override
//    public void onLoadComplete(Response<T> response, boolean isMore) {
//        getView().onLoadComplete(isMore);
//        T body = response.body();
//        if (body != null)
//            getView().onLoadCompleteData(body, isMore);
//    }
//
//    @Override
//    public void onFailer(String msg) {
//        getView().showErrorMessage("网络错误", msg);
//    }
//}
