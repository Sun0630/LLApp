package com.android.core.base;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.SparseArray;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.android.core.Help;
import com.android.core.R;
import com.android.core.StaticValue;
import com.android.core.control.StatusBarUtil;
import com.android.core.control.ToastUtil;
import com.android.core.control.logcat.Logcat;
import com.android.core.model.LogicProxy;
import com.android.core.utils.ThemeUtils;
import com.android.core.widget.dialog.DialogManager;
import com.heaton.liulei.utils.utils.SPUtils;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;


/**
 * @author: liulei
 * @date: 2016-09-24 18:09
 */
public abstract class AbsBaseActivity extends AppCompatActivity implements BaseView {

    public Context mContext = null;//context
    protected BasePresenter mPresenter;
    public Toolbar toolbar;
    private TextView abTitle;
    public String TAG;
    private boolean isShowTool = true;//设置是否显示toolbar  默认显示
    private boolean isTransparent = false;//设置状态是否透明   默认不透明
    private AlertDialog mAlertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //设置主题   白天和夜间模式
        ThemeUtils.applyTheme(this);
        super.onCreate(savedInstanceState);
        Logcat.d("Activity Location (%s.java:0)", getClass().getSimpleName());
        mContext = this;
        TAG = getClass().getSimpleName();

        setContentView(getLayoutResource());
        ButterKnife.bind(this);
        onInitView();
    }

    //设置是否显示toolbar   不设置默认显示  setContentView方法之前调用
    public void isShowTool(boolean isShowTool){
        this.isShowTool = isShowTool;
    }

    //设置状态栏是否透明
    public void isTransparentSystem(boolean isTransparent){
        this.isTransparent = isTransparent;
    }

    //自己新添加的
    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        if(isTransparent){
            //自己新添加的
//        setTranslucent();//沉浸式  这个对api<=19的适合
            Help.initSystemBar(this, R.color.transparent);//这个对所有的都适合
        }else {
            Help.initSystemBar(this, StaticValue.color);
        }
        if(isShowTool){
            initToolBar();
        }
    }

    //自己新添加的
    @Override
    protected void onTitleChanged(CharSequence title, int color) {
        super.onTitleChanged(title, color);
        if (abTitle != null) {
            abTitle.setText(title);
        }
    }

    //自己新添加的
    private void initToolBar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(getResources().getColor(StaticValue.color));
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            abTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        }
        if (abTitle != null) {
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayShowTitleEnabled(false);
            }
        }
    }

    protected abstract int getLayoutResource();

    protected abstract void onInitView();

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        // 打开Activity动画
    }

    @Override
    public void startActivity(Class<? extends Activity> targetActivity) {
        startActivity(new Intent(mContext, targetActivity));
    }

    //获得该页面的实例
    public <T> T getLogicImpl(Class cls, BaseView o) {
        return LogicProxy.getInstance().bind(cls, o);
    }

    @Override
    public void finish() {
        super.finish();
        // 关闭动画
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        if (mPresenter != null)
            mPresenter.detachView();
    }

    @Override
    public void showMessage(String msg) {
        ToastUtil.show(msg);
    }

    public void showProgress(String message) {
        DialogManager.showProgressDialog(mContext, message);
    }

    @Override
    public void showProgress(String message, int progress) {
        DialogManager.showProgressDialog(mContext, message, progress);
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

    public void showDialog(Context context, String title, String message, String confirmText, String cancelText,
                           boolean cancelable,final DialogManager.DialogLisener dialogLisener){
        DialogManager.showDialog(context,title,message,confirmText,cancelText,cancelable,dialogLisener);
    }

    public AlertDialog showDialog(String title, String message) {
        return this.showDialog(title, message, this.getResources().getString(R.string.btn_sure), (String)null, (String)null, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
    }

    public AlertDialog showDialog(String title, String message, String btnSure) {
        return this.showDialog(title, message, btnSure, (String)null, (String)null, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
    }

    public AlertDialog showDialog(String title, String message, String btnSure, String btnCancel, DialogInterface.OnClickListener onClickListener) {
        return this.showDialog(title, message, btnSure, btnCancel, (String)null, onClickListener);
    }

    public AlertDialog showDialog(String title, String message, String btnSure, String btnCancel, String btnCenter, DialogInterface.OnClickListener onClickListener) {
        if(this.mAlertDialog == null) {
            this.mAlertDialog = (new AlertDialog.Builder(this)).create();
        }

        this.mAlertDialog.setCanceledOnTouchOutside(false);
        this.mAlertDialog.setTitle(title);
        this.mAlertDialog.setMessage(message);
        this.mAlertDialog.setButton(-1, btnSure, onClickListener);
        this.mAlertDialog.setButton(-2, btnCancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        this.mAlertDialog.setButton(-3, btnCenter, onClickListener);
        this.mAlertDialog.show();
        return this.mAlertDialog;
    }

    //自己新添加的
    private void setTranslucent() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            // Translucent status bar
            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    private int mPermissionIdx = 0x10;//请求权限索引
    private SparseArray<GrantedResult> mPermissions = new SparseArray<>();//请求权限运行列表

    @SuppressLint("Override")
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        GrantedResult runnable = mPermissions.get(requestCode);
        if (runnable == null) {
            return;
        }
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            runnable.mGranted = true;
        }
        runOnUiThread(runnable);
    }

    public void requestPermission(String[] permissions, String reason, GrantedResult runnable) {
        if (runnable == null) {
            return;
        }
        runnable.mGranted = false;
        if (Build.VERSION.SDK_INT < 23 || permissions == null || permissions.length == 0) {
            runnable.mGranted = true;//新添加
            runOnUiThread(runnable);
            return;
        }
        final int requestCode = mPermissionIdx++;
        mPermissions.put(requestCode, runnable);

		/*
            是否需要请求权限
		 */
        boolean granted = true;
        for (String permission : permissions) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                granted = granted && checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
            }
        }

        if (granted) {
            runnable.mGranted = true;
            runOnUiThread(runnable);
            return;
        }

		/*
            是否需要请求弹出窗
		 */
        boolean request = true;
        for (String permission : permissions) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                request = request && !shouldShowRequestPermissionRationale(permission);
            }
        }

        if (!request) {
            final String[] permissionTemp = permissions;
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setMessage(reason)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                requestPermissions(permissionTemp, requestCode);
                            }
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            GrantedResult runnable = mPermissions.get(requestCode);
                            if (runnable == null) {
                                return;
                            }
                            runnable.mGranted = false;
                            runOnUiThread(runnable);
                        }
                    }).create();
            dialog.show();
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(permissions, requestCode);
            }
        }
    }

    public static abstract class GrantedResult implements Runnable {
        private boolean mGranted;

        public abstract void onResult(boolean granted);

        @Override
        public void run() {
            onResult(mGranted);
        }
    }
}
