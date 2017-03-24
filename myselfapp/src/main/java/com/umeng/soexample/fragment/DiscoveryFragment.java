package com.umeng.soexample.fragment;

import android.view.View;

import com.android.core.Help;
import com.android.core.StaticValue;
import com.android.core.base.AbsBaseFragment;
import com.android.core.control.XRecyclerViewHelper;
import com.android.core.listener.ThemeChangeListener;
import com.android.core.widget.CustomViewpager;
import com.heaton.liulei.utils.utils.ToastUtil;
import com.umeng.soexample.R;
import com.umeng.soexample.adapter.CustomViewPageAdapter;
import com.umeng.soexample.adapter.DisCoverRecyclerAdapter;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.umeng.soexample.custom.floatView.FloatingActionButton;
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
    private DisCoverRecyclerAdapter recyclerAdapter;
    private CustomViewpager mViewpage;
    private int index = 0;// 当前第几次加载
    private int mCurrntDataSize = 20;//当前数据量   最大为100
    private List<Integer> mAdList;
    /**
     * 广告图片
     */
    private List<Integer> posterImage = null;


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

        listView.setLoadingListener(this);
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
}
