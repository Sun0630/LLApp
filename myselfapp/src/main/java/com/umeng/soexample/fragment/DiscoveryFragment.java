package com.umeng.soexample.fragment;

import android.graphics.Color;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
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
import com.heaton.liulei.utils.utils.ToastUtil;
import com.umeng.soexample.MainActivity;
import com.umeng.soexample.R;
import com.umeng.soexample.activity.PersonActivity;
import com.umeng.soexample.adapter.CustomViewPageAdapter;
import com.umeng.soexample.adapter.DisCoverRecyclerAdapter;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.umeng.soexample.adapter.MsRecyclerAdapter;
import com.umeng.soexample.bean.AdBean;
import com.umeng.soexample.bean.MsBean;
import com.umeng.soexample.custom.LimitScrollerView;
import com.umeng.soexample.custom.floatView.FloatingActionButton;
import com.umeng.soexample.custom.timer.TimerUtils;
import com.umeng.soexample.task.TaskExecutor;
import com.umeng.soexample.task.ThreadPoolManager;

import java.util.ArrayList;
import java.util.List;
import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * author meikoz on 2016/4/19.
 * email  meikoz@126.com
 */
public class DiscoveryFragment extends AbsBaseFragment implements XRecyclerView.LoadingListener,ThemeChangeListener{

    @Bind(R.id.listView)
    XRecyclerView listView;
    @Bind(R.id.fab2)
    FloatingActionButton fab;
//    @Bind(R.id.appbar)
//    AppBarLayout mAppBarLayout;
//    @Bind(R.id.collapsing_toolbar)
//    CollapsingToolbarLayout mCollapsingToolbar;
    @Bind(R.id.discover_tool)
    LinearLayout discover_tool;
    @Bind(R.id.ll_discover_search)
    LinearLayout ll_search;
    @Bind(R.id.study)
    Button study;
    LimitScrollerView limitScroll;
    private LinearLayout timer;
    private RecyclerView recyclerView_ms;
    private MsRecyclerAdapter msRecyclerAdapter;
    private DisCoverRecyclerAdapter recyclerAdapter;
    private CustomViewpager mViewpage;
    private int index = 0;// 当前第几次加载
    private int mCurrntDataSize = 20;//当前数据量   最大为100
    private List<Integer> mAdList;
    private List<MsBean>msDatas;
    private MyLimitScrollAdapter mLimitAdapter;
    /**
     * 广告图片
     */
    private List<Integer> posterImage = null;
    private int height;
    private float mDistanceY = 0, mLastY = 0, mLastDeltaY;


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
            return;
        }
        for (int i = 0; i < 10; i++) {
            datas.add("测试数据" + (mCurrntDataSize + i));
        }

        mCurrntDataSize += 10;
    }

    @Override
    protected void onInitView() {

        getAdData();

        fab.setColorNormal(StaticValue.color);
        XRecyclerViewHelper.init().setLinearLayout(getActivity(), listView);
        for (int i = 0; i < 20; i++) {
            datas.add("测试数据" + i);
        }
        View headView = mInflater.inflate(R.layout.find_head_layout, listView, false);
        limitScroll = (LimitScrollerView) headView.findViewById(R.id.limitScroll);
//        mAppBarLayout = (AppBarLayout) headView.findViewById(R.id.appbar);
//        mCollapsingToolbar = (CollapsingToolbarLayout) headView.findViewById(R.id.collapsing_toolbar);
//        discover_tool = (LinearLayout) headView.findViewById(R.id.discover_tool);
//        ll_search = (LinearLayout) headView.findViewById(R.id.ll_discover_search);
//        study = (Button) headView.findViewById(R.id.study);
        timer = (LinearLayout) headView.findViewById(R.id.timer);
        recyclerView_ms = (RecyclerView) headView.findViewById(R.id.recyclerView);

        mViewpage = (CustomViewpager) headView.findViewById(R.id.viewpager);
        listView.addHeaderView(headView);
        CustomViewPageAdapter adapter = new CustomViewPageAdapter(getActivity(), mAdList);
        mViewpage.updateIndicatorView(mAdList.size(), 0);
        mViewpage.setAdapter(adapter);
        mViewpage.startScorll();

        recyclerAdapter = new DisCoverRecyclerAdapter(getActivity(), R.layout.discover_item, datas);
        listView.setAdapter(recyclerAdapter);

        fab.attachToRecyclerView(listView);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listView.smoothScrollToPosition(0);
            }
        });
        listView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                //滑动的距离
                mDistanceY += dy;
                //toolbar的高度
                int toolbarHeight = discover_tool.getBottom();

                //当滑动的距离 <= toolbar高度的时候，改变Toolbar背景色的透明度，达到渐变的效果
                if (mDistanceY <= toolbarHeight) {
                    float scale = (float) mDistanceY / toolbarHeight;
                    float alpha = scale * 255;
                    discover_tool.setBackgroundColor(Color.argb((int) alpha, 128, 0, 0));
                } else {
                    //上述虽然判断了滑动距离与toolbar高度相等的情况，但是实际测试时发现，标题栏的背景色
                    //很少能达到完全不透明的情况，所以这里又判断了滑动距离大于toolbar高度的情况，
                    //将标题栏的颜色设置为完全不透明状态
                    discover_tool.setBackgroundResource(R.color.colorPrimary);
                }

            }
        });

        listView.setLoadingListener(this);

        //API:1、设置数据适配器
        mLimitAdapter = new MyLimitScrollAdapter();
        limitScroll.setDataAdapter(mLimitAdapter);

        initHeader();
        initTimer();
        initData();
        initMs();
    }

    private void initHeader() {
        discover_tool.setBackgroundColor(getResources().getColor(R.color.transparent));
        ll_search.setAlpha(0.5f);
        study.setAlpha(0.5f);
//        mCollapsingToolbar.setContentScrimColor(StaticValue.color);
//        mAppBarLayout.setBackgroundColor(StaticValue.color);
    }

    private void initMs() {
        initMsData();
        msRecyclerAdapter = new MsRecyclerAdapter(getActivity(),R.layout.item_ms,msDatas);
        recyclerView_ms.setAdapter(msRecyclerAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView_ms.setLayoutManager(layoutManager);

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

    @Override
    public void onRefresh() {
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
        ThreadPoolManager.getmInstance().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                getActivity().runOnUiThread(() -> listView.refreshComplete());//使用lamdba表达式
            }
        });
    }

    @Override
    public void onLoadMore() {
        new Thread(() -> {
            try {
                Thread.sleep(3000);
                getActivity().runOnUiThread(this::loadMore);//使用lamdba表达式
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void loadMore() {
        index++;
        addData(index);
        listView.loadMoreComplete();
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
            View itemView = LayoutInflater.from(getContext()).inflate(R.layout.limit_scroller_item, null, false);
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
