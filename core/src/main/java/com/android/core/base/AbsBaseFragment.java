package com.android.core.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.core.control.ToastUtil;
import com.android.core.control.logcat.Logcat;
import com.android.core.model.LogicProxy;
import com.android.core.widget.dialog.DialogManager;

import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * @author: liulei
 * @date: 2016-09-26 18:03
 */
public abstract class AbsBaseFragment extends Fragment implements BaseView {

    protected BasePresenter mPresenter;
    protected Context mContext;
    protected LayoutInflater mInflater;
    public String TAG;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mInflater = inflater;
        if (getLayoutResource() != 0) {
            return inflater.inflate(getLayoutResource(), null);
        } else {
            return super.onCreateView(inflater, container, savedInstanceState);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logcat.d("Fragment Location (%s.java:0)", getClass().getSimpleName());
        mContext = getActivity();
        TAG = getClass().getSimpleName();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        onInitView();
    }

    protected abstract int getLayoutResource();

    protected abstract void onInitView();

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        if (mPresenter != null)
            mPresenter.detachView();
    }

    @Override
    public void startActivity(Class<? extends Activity> targetActivity) {
        startActivity(new Intent(mContext,targetActivity));
    }

    @Override
    public void showMessage(String msg) {
        ToastUtil.show(msg);
    }

    @Override
    public void showProgress(String msg) {
        DialogManager.showProgressDialog(mContext, msg);
    }

    @Override
    public void showProgress(String msg, int progress) {
        DialogManager.showProgressDialog(mContext, msg, progress);
    }

    @Override
    public void hideProgress() {
        DialogManager.hideProgressDialog();
    }

    @Override
    public void showErrorMessage(String msg, String content) {
        DialogManager.showErrorDialog(mContext, msg, content, new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                sweetAlertDialog.dismissWithAnimation();
            }
        });
    }
}
