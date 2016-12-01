package com.example.myselfapp.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.android.core.Help;
import com.android.core.StaticValue;
import com.android.core.base.AbsBaseActivity;
import com.example.myselfapp.R;
import com.example.myselfapp.fragment.LoginFragment;
import com.heaton.liulei.utils.utils.LiuleiUtils;
import com.heaton.liulei.utils.utils.WebUtils;

import java.lang.reflect.Method;

/**
 * 作者：刘磊 on 2016/9/27 14:50
 * 公司：希顿科技
 */

public class MusicActivity extends AppCompatActivity{

    private ViewPager mViewPager;
    private MySectionsPagerAdapter mSectionsPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        onInitView();
    }


    protected void onInitView() {
        setTitle("登录");
        Help.initSystemBar(this, StaticValue.color);//这个对所有的都适合
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar1);
        toolbar.setBackgroundColor(getResources().getColor(StaticValue.color));
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.WHITE);//设置ToolBar的titl颜色
        toolbar.setNavigationIcon(R.mipmap.abc_ic_ab_back_mtrl_am_alpha);//必须放在setSupportActionBar后才有用，否则没有，设置返回图标
        toolbar.setNavigationOnClickListener(back_btn);//添加按键监听

        mSectionsPagerAdapter = new MySectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager)findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout)findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
//                Snackbar.make(view,"敬请期待",Snackbar.LENGTH_LONG)
//                        .setAction("Action",null).show();

            }
        });

    }

    /**
     * 返回按钮的监听事件
     */
    View.OnClickListener back_btn = new View.OnClickListener(){
        @Override
        public void onClick(View v){
            //localBroadcastManager.unregisterReceiver(ui_receiver);
            finish();//结束这个活动
        }
    };

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class MySectionsPagerAdapter extends FragmentPagerAdapter{

        public MySectionsPagerAdapter(FragmentManager fm){
            super(fm);
        }

        @Override
        public Fragment getItem(int position){
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return new LoginFragment();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position){
            return super.instantiateItem(container,position);
        }

        @Override
        public int getItemPosition(Object object){
            Log.i("getItemPosition","获取页面");
            return PagerAdapter.POSITION_NONE;
        }

        @Override
        public int getCount(){
            // Show 3 total pages.
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position){
            switch(position) {
                case 0:
                    return "单曲";
                case 1:
                    return "歌手";
                case 2:
                    return "专辑";
                case 3:
                    return "文件夹";
            }
            return null;
        }
    }

    /**
     * 这里是toolbar的按钮选项菜单
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_local_music_list,menu);//实例化菜单选项
        return true;
    }

    /**
     * 菜单项被选中时调用
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if(id == R.id.action_settings) {
            return true;
        }
        else
        {
            switch(id)
            {
                case R.id.menu_search_btn://搜索按钮
                    break;
                case R.id.menu_more_scanMusic://扫描音乐按钮
                    break;
                case R.id.menu_more_orderStyle://选择排序方式按钮
                    break;
                case R.id.menu_more_getLrc://一键获取封面歌词按钮
                    break;
                case R.id.menu_more_updateMusic://升级音质按钮
                    break;
            }
        }

        return super.onOptionsItemSelected(item);
    }
    /**
     * 这个方法用来解决超出的menuItem不显示图标的问题，这个方法在AppCompatActivity中不被调用
     * @param featureId
     * @param menu
     * @return
     */
    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        if (featureId == Window.FEATURE_ACTION_BAR && menu != null) {
            if (menu.getClass().getSimpleName().equals("MenuBuilder")) {
                try {
                    Method m = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
                    m.setAccessible(true);
                    m.invoke(menu, true);
                } catch (Exception e) {
                }
            }
        }
        return super.onMenuOpened(featureId, menu);
    }

    /**
     * 这个方法用来解决超出的menuItem不显示图标的问题,这个方法在AppCompatActivity中被调用
     * @param view
     * @param menu
     * @return
     */
    @Override
    protected boolean onPrepareOptionsPanel(View view, Menu menu) {
        if (menu != null) {
            if (menu.getClass().getSimpleName().equals("MenuBuilder")) {
                try{
                    Method m = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
                    m.setAccessible(true);
                    m.invoke(menu, true);
                } catch (Exception e) {
                    Log.e(getClass().getSimpleName(), "onMenuOpened...unable to set icons for overflow menu", e);
                }
            }
        }
        return super.onPrepareOptionsPanel(view, menu);
    }
}
