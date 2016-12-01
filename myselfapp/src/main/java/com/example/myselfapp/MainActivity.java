package com.example.myselfapp;

import android.os.Bundle;

import com.android.core.base.AbsBaseActivity;
import com.android.core.widget.TabStripView;
import com.example.myselfapp.fragment.DiscoveryFragment;
import com.example.myselfapp.fragment.HomeFragment;
import com.example.myselfapp.fragment.PersonFragment;
import com.example.myselfapp.fragment.SetFragment;

public class MainActivity extends AbsBaseActivity {

    private TabStripView navigateTabBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        navigateTabBar = (TabStripView) findViewById(R.id.navigateTabBar);
        //对应xml中的containerId
        navigateTabBar.setFrameLayoutId(R.id.main_container);
        //对应xml中的navigateTabTextColor
        navigateTabBar.setTabTextColor(getResources().getColor(R.color.abc_tab_text_normal));
        //对应xml中的navigateTabSelectedTextColor
        navigateTabBar.setSelectedTabTextColor(getResources().getColor(R.color.colorPrimary));

        //恢复选项状态
        navigateTabBar.onRestoreInstanceState(savedInstanceState);

        navigateTabBar.addTab(HomeFragment.class, new TabStripView.TabParam(R.drawable.ic_tab_bar_home, R.drawable.ic_tab_bar_home_selected, R.string.abc_tab_text_home));
        navigateTabBar.addTab(DiscoveryFragment.class, new TabStripView.TabParam(R.drawable.ic_tab_bar_find, R.drawable.ic_tab_bar_find_selected, R.string.abc_tab_text_find));
        navigateTabBar.addTab(PersonFragment.class, new TabStripView.TabParam(R.drawable.ic_tab_bar_person, R.drawable.ic_tab_bar_person_selected, R.string.abc_tab_text_person));
        navigateTabBar.addTab(SetFragment.class, new TabStripView.TabParam(R.drawable.ic_tab_bar_person, R.drawable.ic_tab_bar_person_selected, R.string.abc_tab_text_set));
        setTitle("首页");
        navigateTabBar.setTabSelectListener(new TabStripView.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabStripView.ViewHolder holder) {
                if(holder.tabIndex==0){
                    setTitle("首页");
                }else if(holder.tabIndex==1){
                    setTitle("发现");
                }else if(holder.tabIndex==2){
                    setTitle("个人");
                }else {
                    setTitle("设置");
                }
            }
        });
    }

    @Override
    protected int getLayoutResource() {
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

}
