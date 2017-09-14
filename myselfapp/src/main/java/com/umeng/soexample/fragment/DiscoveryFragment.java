package com.umeng.soexample.fragment;

import android.database.Cursor;
import android.graphics.Color;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.core.Help;
import com.android.core.StaticValue;
import com.android.core.base.AbsBaseFragment;
import com.android.core.control.XRecyclerViewHelper;
import com.android.core.listener.ThemeChangeListener;
import com.android.core.widget.CustomViewpager;
import com.android.core.widget.recyclerviewwithfooter.OnLoadMoreListener;
import com.android.core.widget.recyclerviewwithfooter.RecyclerViewWithFooter;
import com.heaton.liulei.utils.utils.ToastUtil;
import com.umeng.soexample.MainActivity;
import com.umeng.soexample.R;
import com.umeng.soexample.activity.PersonActivity;
import com.umeng.soexample.adapter.CustomViewPageAdapter;
import com.umeng.soexample.adapter.DisCoverRecyclerAdapter;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.umeng.soexample.adapter.MsRecyclerAdapter;
import com.umeng.soexample.adapter.RecycleViewDivider;
import com.umeng.soexample.bean.AdBean;
import com.umeng.soexample.bean.MsBean;
import com.umeng.soexample.custom.LimitScrollerView;
import com.umeng.soexample.custom.floatView.FloatingActionButton;
import com.umeng.soexample.custom.timer.TimerUtils;
import com.umeng.soexample.task.TaskExecutor;
import com.umeng.soexample.task.ThreadPoolManager;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * Created by LiuLei on 2017/2/23.
 *
 */
public class DiscoveryFragment extends AbsBaseFragment implements ThemeChangeListener, OnLoadMoreListener {

    private static final String TAG = "DiscoveryFragment";

    @Bind(R.id.listView)
    RecyclerViewWithFooter listView;
    @Bind(R.id.fab2)
    FloatingActionButton fab;
    @Bind(R.id.limitScroll)
    LimitScrollerView limitScroll;
    @Bind(R.id.timer)
    LinearLayout timer;
    @Bind(R.id.recyclerView)
    RecyclerView recyclerView_ms;
    private MsRecyclerAdapter msRecyclerAdapter;
    private DisCoverRecyclerAdapter recyclerAdapter;
    @Bind(R.id.viewpager)
    CustomViewpager mViewpage;
    @Bind(R.id.find_toolbar)
    Toolbar toolbar;
    @Bind(R.id.collapsing_toolbar)
    CollapsingToolbarLayout mCollapsingToolbar;
    private int index = 0;// 当前第几次加载
    private int mCurrntDataSize = 20;//当前数据量   最大为100
    private List<Integer> mAdList;
    private List<MsBean>msDatas;
    private MyLimitScrollAdapter mLimitAdapter;
    private SearchView mSearchView;
    private SearchView.SearchAutoComplete mSearchAutoComplete;


    private List<String> datas = new ArrayList<>();

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_discover;
    }

    public List<Integer> getAdData() {
        mAdList = new ArrayList<>();

        mAdList.add(R.mipmap.ai1);
        mAdList.add(R.mipmap.ai2);
        mAdList.add(R.mipmap.ai3);
        mAdList.add(R.mipmap.ai4);
        mAdList.add(R.mipmap.ai5);
        return mAdList;
    }

    private void addData(int index) {//模拟加载数据，每次增加10条   直到100
        if (index > 4) {
            ToastUtil.showToast("数据加载完了，亲...");
            listView.setEnd();
            return;
        }
        for (int i = 0; i < 10; i++) {
            datas.add("测试数据" + (mCurrntDataSize + i));
        }

        mCurrntDataSize += 10;
    }

    @Override
    protected void onInitView() {

        setHasOptionsMenu(true);
        initTool();

        getAdData();
        CustomViewPageAdapter adapter = new CustomViewPageAdapter(getActivity(), mAdList);
        mViewpage.updateIndicatorView(mAdList.size(), 0);
        mViewpage.setAdapter(adapter);
        mViewpage.startScorll();

//        XRecyclerViewHelper.init().setLinearLayout(getActivity(), listView);
        listView.setLayoutManager(new LinearLayoutManager(getActivity()));
        listView.addItemDecoration(new RecycleViewDivider(getActivity(), LinearLayoutManager.HORIZONTAL));
//        listView.setAdapter(mAdapter);
        for (int i = 0; i < 20; i++) {
            datas.add("测试数据" + i);
        }
        recyclerAdapter = new DisCoverRecyclerAdapter(getActivity(), R.layout.discover_item, datas);
        listView.setNestedScrollingEnabled(false);
        listView.setAdapter(recyclerAdapter);
        listView.setOnLoadMoreListener(this);

        fab.setColorNormal(StaticValue.color);
        fab.attachToRecyclerView(listView);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listView.smoothScrollToPosition(0);
            }
        });

        //API:1、设置数据适配器
        mLimitAdapter = new MyLimitScrollAdapter();
        limitScroll.setDataAdapter(mLimitAdapter);

        initTimer();
        initData();
        initMs();
    }

    private void initTool() {
        ((MainActivity)getActivity()).setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.mipmap.abc_ic_ab_back_mtrl_am_alpha);
        toolbar.setTitleTextColor(Color.WHITE);//设置ToolBar的titl颜色
        toolbar.setTitle("");
        mCollapsingToolbar.setContentScrimColor(StaticValue.color);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSearchAutoComplete.isShown()) {
                    try {
                        mSearchAutoComplete.setText("");
                        Method method = mSearchView.getClass().getDeclaredMethod("onCloseClicked");
                        method.setAccessible(true);
                        method.invoke(mSearchView);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search_view, menu);
        MenuItem searchItem = menu.findItem(R.id.menu_search);
        //通过MenuItem得到SearchView
        mSearchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        mSearchAutoComplete = (SearchView.SearchAutoComplete) mSearchView.findViewById(R.id.search_src_text);
        mSearchView.setQueryHint("搜索你感兴趣的课程");

        //设置输入框提示文字样式
        mSearchAutoComplete.setHintTextColor(getResources().getColor(android.R.color.darker_gray));
        mSearchAutoComplete.setTextColor(getResources().getColor(android.R.color.background_light));
        mSearchAutoComplete.setTextSize(14);
        //设置触发查询的最少字符数（默认2个字符才会触发查询）
        mSearchAutoComplete.setThreshold(1);

        //设置搜索框有字时显示叉叉，无字时隐藏叉叉
        mSearchView.onActionViewExpanded();
        mSearchView.setIconified(true);

        //修改搜索框控件间的间隔（这样只是为了更加接近网易云音乐的搜索框）
        LinearLayout search_edit_frame = (LinearLayout) mSearchView.findViewById(R.id.search_edit_frame);
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) search_edit_frame.getLayoutParams();
        params.leftMargin = 0;
        params.rightMargin = 0;
        search_edit_frame.setLayoutParams(params);

        //监听SearchView的内容
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {

//                Cursor cursor = TextUtils.isEmpty(s) ? null : queryData(s);

//                setAdapter(cursor);

                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void initMs() {
        initMsData();
        msRecyclerAdapter = new MsRecyclerAdapter(getActivity(),R.layout.item_ms,msDatas);
        recyclerView_ms.setAdapter(msRecyclerAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView_ms.setLayoutManager(layoutManager);
        recyclerView_ms.setNestedScrollingEnabled(false);

    }

    private void initTimer() {
        TextView tv =  TimerUtils.getTimer(TimerUtils.JD_STYLE,getActivity(),120000000,TimerUtils.TIME_STYLE_ONE,R.drawable.timer_shape)
                .setTimerPadding(10,10,10,10)//设置内间距
                .setTimerTextColor(Color.BLACK)//设置字体颜色
                .setTimerTextSize(50)//设置字体大小
                .setTimerGapColor(Color.BLACK)//设置间隔的颜色
                .getmDateTv();//拿到TextView对象
        timer.addView(tv);
        setmLayoutParams(tv);
    }

    private void setmLayoutParams(TextView tv) {
        tv.setGravity(Gravity.CENTER_HORIZONTAL);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) tv.getLayoutParams();
        params.setMargins(20,20,20,20);
        tv.setLayoutParams(params);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

//    @Override
//    public void onRefresh() {
//        new Thread(() -> {
//            try {
//                Thread.sleep(3000);
////                Runnable runnable = () -> listView.refreshComplete();
//                getActivity().runOnUiThread(() -> listView.refreshComplete());//使用lamdba表达式
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }).start();

//        TaskExecutor.executeTask(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    Thread.sleep(3000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                getActivity().runOnUiThread(() -> listView.refreshComplete());//使用lamdba表达式
//            }
//        });
//        ThreadPoolManager.getmInstance().execute(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    Thread.sleep(3000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                getActivity().runOnUiThread(() -> listView.refreshComplete());//使用lamdba表达式
//            }
//        });
//    }


    @Override
    public void onLoadMore() {
        new Thread(() -> {
            try {
                Thread.sleep(1000);
                getActivity().runOnUiThread(this::loadMore);//使用lamdba表达式
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void loadMore() {
        index++;
        addData(index);
        recyclerAdapter.notifyDataSetChanged();
//        listView.loadMoreComplete();
    }

    @Override
    public void onThemeChanged() {
        if(fab != null){
            fab.setColorNormal(StaticValue.color);
        }
    }

    private void initData(){

        //TODO 模拟获取服务器数据操作，此处需要修改
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<AdBean> datas = new ArrayList<>();
                datas.add(new AdBean("标题一", "1.劲爆促销中，凡在此商场消费满888的顾客，请拿着小票到前台咨询处免费领取美女一枚"));
                datas.add(new AdBean("标题二", "2.劲爆促销中，凡在此商场消费满888的顾客，请拿着小票到前台咨询处免费领取美女一枚"));
                datas.add(new AdBean("标题三", "3.劲爆促销中，凡在此商场消费满888的顾客，请拿着小票到前台咨询处免费领取美女一枚"));
                datas.add(new AdBean("标题四", "4.劲爆促销中，凡在此商场消费满888的顾客，请拿着小票到前台咨询处免费领取美女一枚"));
                datas.add(new AdBean("标题五", "5.劲爆促销中，凡在此商场消费满888的顾客，请拿着小票到前台咨询处免费领取美女一枚"));
                datas.add(new AdBean("标题六", "6.劲爆促销中，凡在此商场消费满888的顾客，请拿着小票到前台咨询处免费领取美女一枚"));

                mLimitAdapter.setDatas(datas);

            }
        }).start();

    }


    private void initMsData() {
        msDatas = new ArrayList<>();
        msDatas.add(new MsBean("【SSM】报名预约系统今日开放。",R.mipmap.ai1,"¥ 499","¥ 999"));
        msDatas.add(new MsBean("【SSM】报名预约系统今日开放。",R.mipmap.ai1,"¥ 499","¥ 999"));
        msDatas.add(new MsBean("【SSM】报名预约系统今日开放。",R.mipmap.ai1,"¥ 499","¥ 999"));
        msDatas.add(new MsBean("【SSM】报名预约系统今日开放。",R.mipmap.ai1,"¥ 499","¥ 999"));
        msDatas.add(new MsBean("【SSM】报名预约系统今日开放。",R.mipmap.ai1,"¥ 499","¥ 999"));
        msDatas.add(new MsBean("【SSM】报名预约系统今日开放。",R.mipmap.ai1,"¥ 499","¥ 999"));
    }

    class MyLimitScrollAdapter implements LimitScrollerView.LimitScrollAdapter{

        private List<AdBean> datas;
        public void setDatas(List<AdBean> datas){
            this.datas = datas;
            //API:2、开始滚动
            limitScroll.startScroll();
        }
        @Override
        public int getCount() {
            return datas==null?0:datas.size();
        }

        @Override
        public View getView(int index) {
            View itemView = LayoutInflater.from(getActivity()).inflate(R.layout.limit_scroller_item, null, false);//null异常   getActivity
            TextView tv_title = (TextView)itemView.findViewById(R.id.tv_title);
            TextView tv_text = (TextView)itemView.findViewById(R.id.tv_text);

            //绑定数据
            AdBean data = datas.get(index);
            itemView.setTag(data);
            tv_title.setText(data.getTetle());
            tv_text.setText(data.getText());
            return itemView;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if(limitScroll != null){
            limitScroll.startScroll();    //API:2、开始滚动
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        //API:3、停止滚动
        if(limitScroll != null){
            limitScroll.cancel();
        }
    }
}
