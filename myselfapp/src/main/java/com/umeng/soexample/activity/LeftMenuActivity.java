package com.umeng.soexample.activity;

import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.core.StaticValue;
import com.android.core.base.AbsBaseActivity;
import com.android.core.control.Glides;
import com.android.core.control.MDTintUtil;
import com.android.core.control.statusbar.StatusBarUtil;
import com.android.core.widget.glide.ImageLoader;
import com.android.core.widget.glide.ImageLoaderUtil;
import com.baronzhang.android.router.RouterInjector;
import com.umeng.soexample.App;
import com.umeng.soexample.R;
import com.umeng.soexample.custom.ToShare;
import com.umeng.soexample.custom.ToggleButton;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 作者：刘磊 on 2016/10/26 14:04
 * 公司：希顿科技
 */

public class LeftMenuActivity extends AbsBaseActivity implements View.OnClickListener {//  implements NavigationView.OnNavigationItemSelectedListener

    @Bind(R.id.container)
    LinearLayout bg;
//    @Bind(R.id.appbar_left_menu)
//    AppBarLayout mAppbarMenu;

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.drawer_layout)
    DrawerLayout drawer;
    @Bind(R.id.nav_view)
    NavigationView navigationView;
    RelativeLayout navBgView;
    ImageView headImg;

    @Override
    protected int getLayoutResource() {
        isTransparentSystem(true);
        return R.layout.activity_left_menu;
    }

//    @Override
    protected void onInitView() {
        toolbar.setBackgroundColor(getResources().getColor(R.color.transparent));
        bg.setBackgroundResource(R.mipmap.b_1);

        setTitle("侧滑菜单界面");
        initDrawer();
    }

    private void initDrawer() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.open_nav_drawer, R.string.close_nav_drawer);
        drawer.setDrawerListener(toggle);
        drawer.setFitsSystemWindows(true);
        drawer.setClipToPadding(false);
        toggle.syncState();

        if (navigationView != null) {
//            StatusBarUtil.setColorNoTranslucentForDrawerLayout(MainActivity.this,drawer,getColor(R.color.black));
//            navigationView.setNavigationItemSelectedListener(this);
//            navigationView.setItemIconTintList(ColorStateList.valueOf(StaticValue.color));
//            navigationView.setCheckedItem(R.id.nav_home);
//            navigationView.setItemTextColor(ColorStateList.valueOf(StaticValue.color));
        }

        //这里为了兼容4.4及以下版本
        navigationView.inflateHeaderView(R.layout.nav_header_main);
        View headerView = navigationView.getHeaderView(0);
        ImageView ivAvatar = (ImageView) headerView.findViewById(R.id.iv_avatar);
//        Glides.getInstance().loadCircle(this,R.mipmap.ai1,ivAvatar);
        ImageLoader imageLoader = new ImageLoader.Builder()
                .imgView(ivAvatar)
                .placeHolder(R.mipmap.ai1)
                .url("https://ss3.baidu.com/-fo3dSag_xI4khGko9WTAnF6hhy/image/h%3D360/sign=caa2d267cfef7609230b9f991edca301/6d81800a19d8bc3e7763d030868ba61ea9d345e5.jpg")
                .build();
        ImageLoaderUtil.getInstance().loadCircleImage(this,imageLoader);
        LinearLayout llNavHomepage = (LinearLayout) headerView.findViewById(R.id.ll_nav_homepage);
        LinearLayout llNavShare = (LinearLayout) headerView.findViewById(R.id.ll_nav_share);
        RelativeLayout llNavMode = (RelativeLayout) headerView.findViewById(R.id.ll_nav_mode);
        LinearLayout llNavSet = (LinearLayout) headerView.findViewById(R.id.ll_nav_set);
        ImageView nav_main_img = (ImageView) headerView.findViewById(R.id.nav_main_img);
        TextView nav_main_text = (TextView) headerView.findViewById(R.id.nav_main_text);
        ImageView nav_share_img = (ImageView) headerView.findViewById(R.id.nav_share_img);
        TextView nav_share_text = (TextView) headerView.findViewById(R.id.nav_share_text);
        ImageView nav_mode_img = (ImageView) headerView.findViewById(R.id.nav_mode_img);
        TextView nav_mode_text = (TextView) headerView.findViewById(R.id.nav_mode_text);
        ToggleButton toggleButton = (ToggleButton) headerView.findViewById(R.id.toggle_mode);
        ImageView nav_set_img = (ImageView) headerView.findViewById(R.id.nav_set_img);
        TextView nav_set_text = (TextView) headerView.findViewById(R.id.nav_set_text);

        toggleButton.setOnColor(StaticValue.color);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            MDTintUtil.setTint(nav_main_img,StaticValue.color);
            nav_main_text.setTextColor(StaticValue.color);
            MDTintUtil.setTint(nav_share_img,StaticValue.color);
            nav_share_text.setTextColor(StaticValue.color);
            MDTintUtil.setTint(nav_mode_img,StaticValue.color);
            nav_mode_text.setTextColor(StaticValue.color);
            MDTintUtil.setTint(nav_set_img,StaticValue.color);
            nav_set_text.setTextColor(StaticValue.color);
        }
        llNavHomepage.setOnClickListener(this);
        llNavShare.setOnClickListener(this);
        llNavMode.setOnClickListener(this);
        llNavSet.setOnClickListener(this);
        toggleButton.setOnToggleChanged(new ToggleButton.OnToggleChanged() {
            @Override
            public void onToggle(boolean on) {
                if(on){
                    showMessage("切换到夜间模式");
                }else {
                    showMessage("切换到正常模式");
                }
            }
        });

//        View header = navigationView.getHeaderView(0);
//        navBgView = (RelativeLayout) header.findViewById(R.id.nav_head_bg);
//        headImg = (ImageView) header.findViewById(R.id.nav_header);
//        navBgView.setBackgroundResource(R.mipmap.b_1);
//        Glides.getInstance().loadCircle(this,R.mipmap.ai1,headImg);
        ivAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(PersonActivity.class);
                drawer.closeDrawer(GravityCompat.START);
            }
        });


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ll_nav_homepage:
                startActivity(NavHomePageActivity.class);
                break;
            case R.id.ll_nav_share:
                startActivity(ToShare.class);
                break;
            case R.id.ll_nav_mode:

                break;
            case R.id.ll_nav_set:
                startActivity(SetActivity.class);
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
    }

//    @Override
//    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.nav_home:
//
//                break;
//            case R.id.nav_share:
//                startActivity(ToShare.class);
//                break;
//            case R.id.nav_mode:
//
//                break;
//            case R.id.nav_scan:
//                startActivity(QrViewActivity.class);
//                break;
//            case R.id.nav_set:
//                startActivity(SetActivity.class);
//                break;
//        }
//        drawer.closeDrawer(GravityCompat.START);
//        return true;
//    }
}
