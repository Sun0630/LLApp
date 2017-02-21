package com.umeng.soexample;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.android.core.Help;
import com.android.core.StaticValue;
import com.android.core.base.AbsBaseActivity;
import com.android.core.control.Glides;
import com.android.core.control.StatusBarUtil;
import com.android.core.control.ToastUtil;
import com.android.core.listener.ThemeChangeListener;
import com.android.core.widget.TabStripView;
import com.umeng.soexample.activity.QrViewActivity;
import com.umeng.soexample.activity.SetActivity;
import com.umeng.soexample.custom.ToShare;
import com.umeng.soexample.fragment.DiscoveryFragment;
import com.umeng.soexample.fragment.HomeFragment;
import com.umeng.soexample.fragment.PersonFragment;
import com.umeng.soexample.fragment.SetFragment;

import butterknife.Bind;

public class MainActivity extends AbsBaseActivity implements ThemeChangeListener{

    @Bind(R.id.navigateTabBar)
    TabStripView navigateTabBar;
//    @Bind(R.id.drawer_layout)
//    DrawerLayout drawer;
//    @Bind(R.id.nav_view)
//    NavigationView navigationView;
    RelativeLayout navBgView;
    ImageView headImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);

        //对应xml中的containerId
        navigateTabBar.setFrameLayoutId(R.id.main_container);
        //对应xml中的navigateTabTextColor
        navigateTabBar.setTabTextColor(getResources().getColor(R.color.abc_tab_text_normal));
        //对应xml中的navigateTabSelectedTextColor
        navigateTabBar.setSelectedTabTextColor(StaticValue.color);

        //恢复选项状态
        navigateTabBar.onRestoreInstanceState(savedInstanceState);

        navigateTabBar.addTab(PersonFragment.class, new TabStripView.TabParam(R.drawable.ic_tab_bar_person, R.drawable.ic_tab_bar_person_selected, R.string.abc_tab_text_function));
        navigateTabBar.addTab(HomeFragment.class, new TabStripView.TabParam(R.drawable.ic_tab_bar_home, R.drawable.ic_tab_bar_home_selected, R.string.abc_tab_text_home));
        navigateTabBar.addTab(DiscoveryFragment.class, new TabStripView.TabParam(R.drawable.ic_tab_bar_find, R.drawable.ic_tab_bar_find_selected, R.string.abc_tab_text_find));
        navigateTabBar.addTab(SetFragment.class, new TabStripView.TabParam(R.drawable.ic_tab_bar_person, R.drawable.ic_tab_bar_person_selected, R.string.abc_tab_text_set));
        setTitle("首页");
        navigateTabBar.setTabSelectListener(new TabStripView.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabStripView.ViewHolder holder) {
//                if (holder.tabIndex == 0) {
//                    setTitle("首页");
//                } else if (holder.tabIndex == 1) {
//                    setTitle("发现");
//                } else if (holder.tabIndex == 2) {
//                    setTitle("个人");
//                } else {
//                    setTitle("设置");
//                }
            }
        });
    }

    @Override
    protected int getLayoutResource() {
        isShowTool(false);
        return R.layout.activity_main;
    }

    @Override
    protected void onInitView() {

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //保存当前选中的选项状态
        navigateTabBar.onSaveInstanceState(outState);
    }

    @Override
    public void onThemeChanged() {
        ToastUtil.show("更换主题色了");
//        navigateTabBar.setSelectedTabTextColor(StaticValue.color);
        recreate();
//        if(toolbar != null){
//            toolbar.setBackgroundColor(StaticValue.color);
//        }
//        Help.initSystemBar(this, StaticValue.color);
    }

}
