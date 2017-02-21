package com.umeng.soexample.fragment;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;


import com.android.core.StaticValue;
import com.android.core.base.AbsBaseFragment;
import com.android.core.control.MDTintUtil;
import com.bumptech.glide.Glide;
import com.heaton.liulei.utils.utils.DensityUtils;
import com.umeng.soexample.R;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * @author: liulei
 * @date: 2016-10-27 09:46
 */
public class HomeFragment extends AbsBaseFragment{
    @Bind(R.id.fab_home_random)
    FloatingActionButton mFloatingActionButton;
    @Bind(R.id.appbar)
    AppBarLayout mAppBarLayout;
    @Bind(R.id.iv_home_banner)
    ImageView mIvHomeBanner;
    @Bind(R.id.tl_home_category)
    TabLayout mTlHomeCategory;
    @Bind(R.id.vp_home_category)
    ViewPager mVpCategory;
    @Bind(R.id.collapsing_toolbar)
    CollapsingToolbarLayout mCollapsingToolbar;

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_home;
    }

    @Override
    protected void onInitView() {
//        setHasOptionsMenu(true);
        initView();
        MDTintUtil.setTint(mFloatingActionButton, StaticValue.color);
        mCollapsingToolbar.setContentScrimColor(StaticValue.color);
        mAppBarLayout.setBackgroundColor(StaticValue.color);

//        mTlHomeCategory.setTabTextColors(R.color.abc_white,StaticValue.color);
//        mTlHomeCategory.setSelectedTabIndicatorColor(StaticValue.color);

        Glide.with(getActivity())
                .load(R.mipmap.b_1)
                .into(mIvHomeBanner);

    }

    private void initView() {
        setFabDynamicState();

//        String[] titles = {"App", "Android", "iOS", "前端", "瞎推荐", "拓展资源"};
        ArrayList<String>titles = new ArrayList<>();
        titles.add("App");
        titles.add("Android");
        titles.add("iOS");
        titles.add("前端");
        titles.add("瞎推荐");
        titles.add("拓展资源");
        Adapter infoPagerAdapter = new Adapter(getFragmentManager(), titles);

//        // App
//        CategoryFragment appFragment = CategoryFragment.newInstance("App");
//        // Android
//        CategoryFragment androidFragment = CategoryFragment.newInstance("Android");
//        // iOS
//        CategoryFragment iOSFragment = CategoryFragment.newInstance("iOS");
//        // 前端
//        CategoryFragment frontFragment = CategoryFragment.newInstance("前端");
//        // 瞎推荐
//        CategoryFragment referenceFragment = CategoryFragment.newInstance("瞎推荐");
//        // 拓展资源s
//        CategoryFragment resFragment = CategoryFragment.newInstance("拓展资源");

        MyArticleTypeFragment fragment = new MyArticleTypeFragment();
//        infoPagerAdapter.addFragment(fragment);
//        infoPagerAdapter.addFragment(fragment);
//        infoPagerAdapter.addFragment(fragment);
//        infoPagerAdapter.addFragment(fragment);
//        infoPagerAdapter.addFragment(fragment);
//        infoPagerAdapter.addFragment(fragment);
        for (int i =0;i<titles.size();i++) {
//            Bundle bundle = new Bundle();
//            bundle.putString("ID", model.getID());
            fragment = new MyArticleTypeFragment();
//            fragment.setArguments(bundle);
            infoPagerAdapter.addFragment(fragment);
        }

        mVpCategory.setAdapter(infoPagerAdapter);
        mTlHomeCategory.setupWithViewPager(mVpCategory);
        mTlHomeCategory.setTabGravity(TabLayout.GRAVITY_FILL);
        mVpCategory.setCurrentItem(1);
    }

    private CollapsingToolbarLayoutState state; // CollapsingToolbarLayout 折叠状态

    private enum CollapsingToolbarLayoutState {
        EXPANDED, // 完全展开
        COLLAPSED, // 折叠
        INTERNEDIATE // 中间状态
    }

    /**
     * 根据 CollapsingToolbarLayout 的折叠状态，设置 FloatingActionButton 的隐藏和显示
     */
    private void setFabDynamicState() {
        mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

                if (verticalOffset == 0) {
                    if (state != CollapsingToolbarLayoutState.EXPANDED) {
                        state = CollapsingToolbarLayoutState.EXPANDED; // 修改状态标记为展开
                    }
                } else if (Math.abs(verticalOffset) >= appBarLayout.getTotalScrollRange()) {
                    if (state != CollapsingToolbarLayoutState.COLLAPSED) {
                        mFloatingActionButton.hide();
                        state = CollapsingToolbarLayoutState.COLLAPSED; // 修改状态标记为折叠
                        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) mAppBarLayout.getLayoutParams();
                        layoutParams.height = DensityUtils.dp2px(240);
                        mAppBarLayout.setLayoutParams(layoutParams);
//                        isBannerBig = false;
                    }
                } else {
                    if (state != CollapsingToolbarLayoutState.INTERNEDIATE) {
                        if (state == CollapsingToolbarLayoutState.COLLAPSED) {
                            mFloatingActionButton.show();
                        }
                        state = CollapsingToolbarLayoutState.INTERNEDIATE; // 修改状态标记为中间
                    }
                }
            }
        });
    }

    @OnClick(R.id.ll_home_search)
    public void search(View view) {
//        startActivity();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

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
