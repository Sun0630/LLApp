package com.umeng.soexample;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.android.core.StaticValue;
import com.android.core.base.AbsBaseActivity;
import com.android.core.listener.ThemeChangeListener;
import com.android.core.widget.TabStripView;
import com.heaton.liulei.utils.utils.ToastUtil;
import com.umeng.soexample.fragment.CirleFragment;
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            //透明状态栏
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            //透明导航栏
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
//        }
        //禁止截图行为和覆盖你当前的Acitivity行为
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);

        //对应xml中的containerId
        navigateTabBar.setFrameLayoutId(R.id.main_container);
        //对应xml中的navigateTabTextColor
        navigateTabBar.setTabTextColor(getResources().getColor(R.color.abc_tab_text_normal));
        //对应xml中的navigateTabSelectedTextColor
        navigateTabBar.setSelectedTabTextColor(StaticValue.color);

        //恢复选项状态
        navigateTabBar.onRestoreInstanceState(savedInstanceState);

        navigateTabBar.addTab(HomeFragment.class, new TabStripView.TabParam(R.drawable.ic_tab_bar_home, R.drawable.ic_tab_bar_home_selected, R.string.abc_tab_text_home));
        navigateTabBar.addTab(PersonFragment.class, new TabStripView.TabParam(R.drawable.ic_tab_bar_person, R.drawable.ic_tab_bar_person_selected, R.string.abc_tab_text_function));
        navigateTabBar.addTab(CirleFragment.class, new TabStripView.TabParam(R.drawable.ic_tab_bar_home, R.drawable.ic_tab_bar_home_selected, R.string.abc_tab_text_cirle));
        navigateTabBar.addTab(DiscoveryFragment.class, new TabStripView.TabParam(R.drawable.ic_tab_bar_find, R.drawable.ic_tab_bar_find_selected, R.string.abc_tab_text_find));
        navigateTabBar.addTab(SetFragment.class, new TabStripView.TabParam(R.drawable.ic_tab_bar_person, R.drawable.ic_tab_bar_person_selected, R.string.abc_tab_text_set));
        setTitle("首页");
//        navigateTabBar.setTabSelectListener(new TabStripView.OnTabSelectedListener() {
//            @Override
//            public void onTabSelected(TabStripView.ViewHolder holder) {
//                if (holder.tabIndex == 0) {
//                    setTitle("首页");
//                } else if (holder.tabIndex == 1) {
//                    setTitle("发现");
//                } else if (holder.tabIndex == 2) {
//                    setTitle("个人");
//                } else {
//                    setTitle("设置");
//                }
//            }
//        });
        requestPermission(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, "请求读写权限", new AbsBaseActivity.GrantedResult() {
            @Override
            public void onResult(boolean granted) {
                if(granted){

                }else {
                    ToastUtil.showToast("权限未被允许");
                }

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
        ToastUtil.showToast("更换主题色了");
//        navigateTabBar.setSelectedTabTextColor(StaticValue.color);
        recreate();
//        if(toolbar != null){
//            toolbar.setBackgroundColor(StaticValue.color);
//        }
//        Help.initSystemBar(this, StaticValue.color);
    }

    /**
     * 退出
     */
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN
                && event.getRepeatCount() == 0) {
            //具体的操作代码
            moveTaskToBack(true);
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

}
