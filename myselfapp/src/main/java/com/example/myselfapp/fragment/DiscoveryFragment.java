package com.example.myselfapp.fragment;

import android.view.View;

import com.android.core.base.AbsBaseFragment;
import com.android.core.control.XRecyclerViewHelper;
import com.android.core.widget.CustomViewpager;
import com.example.myselfapp.R;
import com.example.myselfapp.adapter.CustomViewPageAdapter;
import com.example.myselfapp.adapter.DisCoverRecyclerAdapter;
import com.example.myselfapp.custom.floatView.FloatingActionButton;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * author meikoz on 2016/4/19.
 * email  meikoz@126.com
 */
public class DiscoveryFragment extends AbsBaseFragment implements XRecyclerView.LoadingListener {

    @Bind(R.id.listView)
    XRecyclerView listView;
    @Bind(R.id.fab2)
    FloatingActionButton fab;
    private DisCoverRecyclerAdapter recyclerAdapter;
    private CustomViewpager mViewpage;
    /**
     * 广告图片
     */
    private List<Integer> posterImage = null;


    private List<String>datas = new ArrayList<>();

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_discover;
    }

    public static List<Integer>getAdData(){
        List<Integer> adList  = new ArrayList<>();

        adList.add(R.mipmap.ai1);
        adList.add(R.mipmap.ai2);
        adList.add(R.mipmap.ai3);
        adList.add(R.mipmap.ai4);
        adList.add(R.mipmap.ai5);
        return adList;
    }

    @Override
    protected void onInitView() {
        XRecyclerViewHelper.init().setLinearLayout(getActivity(), listView);

        for (int i = 0;i<20;i++){
            datas.add("测试数据"+i);
        }
        View headView = mInflater.inflate(R.layout.find_head_layout, listView, false);
        mViewpage = (CustomViewpager) headView.findViewById(R.id.viewpager);
        listView.addHeaderView(headView);
//        listView.setAdapter(new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1,datas));
        CustomViewPageAdapter adapter = new CustomViewPageAdapter(getActivity(), getAdData());
        mViewpage.updateIndicatorView(getAdData().size(),0);
        mViewpage.setAdapter(adapter);
        mViewpage.startScorll();

        recyclerAdapter = new DisCoverRecyclerAdapter(getActivity(), R.layout.discover_item, datas);
        listView.setAdapter(recyclerAdapter);

        fab.attachToRecyclerView(listView);

        listView.setLoadingListener(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onRefresh() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            listView.refreshComplete();
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void onLoadMore() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            listView.loadMoreComplete();
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


}
