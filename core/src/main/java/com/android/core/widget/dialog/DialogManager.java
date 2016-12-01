package com.android.core.widget.dialog;

import android.content.Context;

import com.android.core.R;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * @作者: liulei
 * @公司：希顿科技
 */
public class DialogManager {

    private static SweetAlertDialog mDialog;
    private static SweetAlertDialog mTipDialog;

    private String mTitle;
    private String mMessage;
    private String mConfirmText;
    private String mCancelText;
    private boolean mCancelable;

    private DialogManager(Builder builder) {
        this.mTitle = builder.mTitle;
        this.mMessage = builder.mMessage;
        this.mConfirmText = builder.mConfirmText;
        this.mCancelText = builder.mCancelText;
        this.mCancelable = builder.mCancelable;
    }


    public static void showWarningDialog(Context context, String title, String content, SweetAlertDialog.OnSweetClickListener listener) {
        if (mDialog != null) {
            mDialog = null;
        }
        mDialog = new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                .setTitleText(title)
                .setContentText(content)
                .setConfirmText("确定")
                .setCancelText("取消")
                .setConfirmClickListener(listener);
        mDialog.show();
    }

    public static void showErrorDialog(Context context, String title, String content, SweetAlertDialog.OnSweetClickListener listener) {
        if (mDialog != null) {
            mDialog = null;
        }
        mDialog = new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                .setConfirmText("确定")
                .setTitleText(title)
                .setContentText(content)
                .setConfirmClickListener(listener);
        mDialog.show();
    }

    public static void showProgressDialog(Context context, String message) {
        if (mDialog != null) {
            mDialog = null;
        }
        mDialog = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE);
        mDialog.getProgressHelper().setBarColor(context.getResources().getColor(R.color.colorPrimary));
        mDialog.setTitleText(message);
        mDialog.setCancelable(true);
        mDialog.show();
    }


    public static void showProgressDialog(Context context, String message, int progress) {
        if (mDialog != null) {
            mDialog = null;
        }
        mDialog = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE);
        mDialog.getProgressHelper().setBarColor(context.getResources().getColor(R.color.colorPrimary));
        mDialog.setTitleText(message);
        mDialog.setCancelable(true);
        mDialog.getProgressHelper().setProgress(progress);
        mDialog.show();
    }

    public static void hideProgressDialog() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }

    public static void showDialog(Context context, String title, String message, String confirmText, String cancelText,
                                  boolean cancelable, final DialogLisener dialogLisener) {
        if (mTipDialog != null) {
            mTipDialog = null;
        }
        mTipDialog = new SweetAlertDialog(context, SweetAlertDialog.NORMAL_TYPE);
        mTipDialog.getProgressHelper().setBarColor(context.getResources().getColor(R.color.colorPrimary));
        if (title != null) {
            mTipDialog.setTitleText(title);
        }
        mTipDialog.setContentText(message);
        mTipDialog.setConfirmText(confirmText);
        if (cancelText != null) {
            mTipDialog.setCancelText(cancelText);
        }
        mTipDialog.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                dialogLisener.cancelLisener(sweetAlertDialog);
            }
        });
        mTipDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                dialogLisener.confirmLisener(sweetAlertDialog);
            }
        });
        mTipDialog.setCanceledOnTouchOutside(cancelable);
        mTipDialog.show();
    }

    public static void showDialog(Context context, Builder builder) {

    }

    public interface DialogLisener {

        void confirmLisener(SweetAlertDialog sweetAlertDialog);

        void cancelLisener(SweetAlertDialog sweetAlertDialog);

    }

    public static final class Builder {
        private Context mContext;
        private String mTitle;
        private String mMessage;
        private String mConfirmText = "确定";
        private String mCancelText = "取消";
        private boolean mCancelable;

        public Builder(Context context) {
            this.mContext = context;
        }

        public Builder title(String title) {
            this.mTitle = title;
            return this;
        }

        public Builder message(String message) {
            this.mMessage = message;
            return this;
        }

        public Builder confirmText(String confirmText) {
            this.mConfirmText = confirmText;
            return this;
        }

        public Builder cancelText(String cancelText) {
            this.mCancelText = cancelText;
            return this;
        }

        public Builder cancelable(boolean cancelable) {
            this.mCancelable = cancelable;
            return this;
        }

        public SweetAlertDialog setConfirmLisener(DialogLisener dialogLisener) {
            dialogLisener.confirmLisener(mTipDialog);
            return mTipDialog;
        }

        public SweetAlertDialog setCancelLisener(DialogLisener dialogLisener) {
            dialogLisener.cancelLisener(mTipDialog);
            return mTipDialog;
        }

        public SweetAlertDialog build() {
            if (mTipDialog != null) {
                mTipDialog = null;
            }
            mTipDialog = new SweetAlertDialog(mContext, SweetAlertDialog.NORMAL_TYPE);
            mTipDialog.setTitleText(mTitle);
            mTipDialog.setContentText(mMessage);
            mTipDialog.setConfirmText(mConfirmText);
            mTipDialog.setCancelText(mCancelText);
            mTipDialog.setCanceledOnTouchOutside(mCancelable);
            return mTipDialog;
        }

    }

}
