package com.umeng.soexample.fragment;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.ViewGroup;


import com.android.core.StaticValue;
import com.android.core.base.AbsBaseFragment;
import com.umeng.soexample.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * @author: liulei
 * @date: 2016-10-27 09:46
 */
public class HomeFragment extends AbsBaseFragment {
    @Bind(R.id.tabs)
    TabLayout  tabLayout;
    @Bind(R.id.viewPager)
    ViewPager viewPager;
    private ArrayList<String> datas = new ArrayList<>();

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_home;
    }

    @Override
    protected void onInitView() {
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
