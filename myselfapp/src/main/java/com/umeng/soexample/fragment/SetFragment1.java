package com.umeng.soexample.fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.core.StaticValue;
import com.android.core.base.AbsBaseFragment;
import com.android.core.control.statusbar.StatusBarUtil;
import com.android.core.listener.ThemeChangeListener;
import com.bumptech.glide.Glide;
import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.heaton.liulei.utils.utils.SPUtils;
import com.umeng.soexample.App;
import com.umeng.soexample.Constants;
import com.umeng.soexample.R;
import com.umeng.soexample.activity.FingerDialog;
import com.umeng.soexample.activity.SetPatternActivity;
import com.umeng.soexample.activity.SuggestActivity;
import com.umeng.soexample.custom.TextDrawable;
import com.umeng.soexample.custom.ToggleButton;
import com.umeng.soexample.manager.ConfigManage;
import com.umeng.soexample.utils.GlideCacheUtil;

import butterknife.Bind;
import butterknife.OnClick;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import zwh.com.lib.FPerException;
import zwh.com.lib.FingerPrinterView;
import zwh.com.lib.RxFingerPrinter;

/**
 * @author: liulei
 * @date: 2016-10-24 09:06
 */
public class SetFragment1 extends AbsBaseFragment implements View.OnClickListener {

    @Bind(R.id.set_toolbar_layout)
    CollapsingToolbarLayout toolbarLayout;
    @Bind(R.id.set_fragment_toolbar)
    Toolbar toolbar;

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_setting1;
    }

    @Override
    protected void onInitView() {
        initToolBar();
        initTranslucentBar();
    }

    //自己新添加的
    private void initToolBar() {

    }


    @Override
    public void onClick(View view) {

    }

    private void initTranslucentBar() {
        StatusBarUtil.setTranslucentForImageView(getActivity(), 0, toolbarLayout);
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) toolbarLayout.getLayoutParams();
        layoutParams.setMargins(0, -StatusBarUtil.getStatusBarHeight(getActivity()), 0, 0);
        ViewGroup.MarginLayoutParams layoutParams2 = (ViewGroup.MarginLayoutParams) toolbar.getLayoutParams();
        layoutParams2.setMargins(0, StatusBarUtil.getStatusBarHeight(getActivity()), 0, 0);
    }


}
