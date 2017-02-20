package com.umeng.soexample.fragment;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.android.core.StaticValue;
import com.android.core.base.AbsBaseFragment;
import com.android.core.control.Glides;
import com.android.core.control.StatusBarUtil;
import com.umeng.soexample.MainActivity;
import com.umeng.soexample.R;
import com.umeng.soexample.activity.QrViewActivity;
import com.umeng.soexample.activity.SetActivity;
import com.umeng.soexample.custom.ToShare;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * @author: liulei
 * @date: 2016-10-27 09:46
 */
public class HomeFragment extends AbsBaseFragment implements NavigationView.OnNavigationItemSelectedListener {
    @Bind(R.id.tabs)
    TabLayout  tabLayout;
    @Bind(R.id.viewPager)
    ViewPager viewPager;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.drawer_layout)
    DrawerLayout drawer;
    @Bind(R.id.nav_view)
    NavigationView navigationView;
    RelativeLayout navBgView;
    ImageView headImg;
    private ArrayList<String> datas = new ArrayList<>();

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_home;
    }

    @Override
    protected void onInitView() {
//        setHasOptionsMenu(true);
        initToolBar();
        initDrawer();
        tabLayout.setTabTextColors(R.color.black,StaticValue.color);
        tabLayout.setSelectedTabIndicatorColor(StaticValue.color);
        for (int i=0;i<5;i++){
            datas.add("精选"+i);
        }
        if (viewPager != null) {
            setupViewPager(viewPager);
        }
        if(datas.size() >= 5) {  // Tab 数量大于5，就允许水平滑动
            tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        }else {
            tabLayout.setTabMode(TabLayout.MODE_FIXED);
        }
        tabLayout.setupWithViewPager(viewPager);
    }

    //自己新添加的
    private void initToolBar() {
        toolbar.setBackgroundColor(StaticValue.color);
        if (toolbar != null) {
            ((TextView) toolbar.findViewById(com.android.core.R.id.toolbar_title)).setText("首页");
        }
    }

    private void initDrawer() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                getActivity(), drawer, toolbar, R.string.open_nav_drawer, R.string.close_nav_drawer);
        drawer.setDrawerListener(toggle);
        drawer.setFitsSystemWindows(true);
        drawer.setClipToPadding(false);
        toggle.syncState();

        if (navigationView != null) {
//            StatusBarUtil.setColorNoTranslucentForDrawerLayout(MainActivity.this,drawer,getColor(R.color.black));
            navigationView.setNavigationItemSelectedListener(this);
            navigationView.setItemIconTintList(ColorStateList.valueOf(StaticValue.color));
            navigationView.setCheckedItem(R.id.nav_home);
            navigationView.setItemTextColor(ColorStateList.valueOf(StaticValue.color));
        }

        View header = navigationView.getHeaderView(0);
        navBgView = (RelativeLayout) header.findViewById(R.id.nav_head_bg);
        headImg = (ImageView) header.findViewById(R.id.nav_header);
        navBgView.setBackgroundResource(R.mipmap.b_1);
        Glides.getInstance().loadCircle(getActivity(),R.mipmap.ai1,headImg);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    private void setupViewPager(ViewPager viewPager) {
        Adapter adapter = new Adapter(getFragmentManager(), datas);
        Fragment fragment;
        for (int i =0;i<datas.size();i++) {
//            Bundle bundle = new Bundle();
//            bundle.putString("ID", model.getID());
            if(i==0){
                fragment = new TxtFragment();
            }else {
                fragment = new MyArticleTypeFragment();
            }
//            fragment.setArguments(bundle);
            adapter.addFragment(fragment);
        }
        viewPager.setAdapter(adapter);
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_home:

                break;
            case R.id.nav_share:
                startActivity(ToShare.class);
                break;
            case R.id.nav_mode:

                break;
            case R.id.nav_scan:
                startActivity(QrViewActivity.class);
                break;
            case R.id.nav_set:
                startActivity(SetActivity.class);
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    static class Adapter extends FragmentPagerAdapter {

        private final List<Fragment> fragments = new ArrayList<>();
        ArrayList<String> datas;

        public Adapter(FragmentManager fm, ArrayList<String> datas) {
            super(fm);
            this.datas = datas;
        }

        public void addFragment(Fragment fragment) {
            fragments.add(fragment);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return datas.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            super.destroyItem(container, position, object);
        }

    }


}
