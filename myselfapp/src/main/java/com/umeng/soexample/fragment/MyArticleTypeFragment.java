package com.umeng.soexample.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import com.android.core.base.AbsBaseFragment;
import com.android.core.widget.recyclerviewwithfooter.OnLoadMoreListener;
import com.android.core.widget.recyclerviewwithfooter.RecyclerViewWithFooter;
import com.example.http.response.BaseResponse;
import com.umeng.soexample.AppConfig;
import com.umeng.soexample.R;
import com.umeng.soexample.adapter.CategoryAdapter;
import com.umeng.soexample.adapter.RecycleViewDivider;
import com.umeng.soexample.api.http.RetrofitUtil;
import com.umeng.soexample.bean.CategoryVO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import rx.Subscriber;

/**
 * 文章类别 Fragment
 * @author: liulei
 * @date: 2016-10-23 14:38
 */
public class MyArticleTypeFragment extends AbsBaseFragment implements SwipeRefreshLayout.OnRefreshListener, OnLoadMoreListener {
    @Bind(R.id.recycler_view)
    RecyclerViewWithFooter mRecyclerView;
    @Bind(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    private CategoryAdapter mAdapter;

    private List<CategoryVO.ResultsBean>mDatas = new ArrayList<>();

    private int mPage = 1;

    private String mCategoryName;

    public MyArticleTypeFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        mCategoryName = bundle.getString("mCategoryName");
    }

    public static MyArticleTypeFragment newInstance(String mCategoryName) {
        MyArticleTypeFragment categoryFragment = new MyArticleTypeFragment();

        Bundle bundle = new Bundle();
        bundle.putString("mCategoryName", mCategoryName);

        categoryFragment.setArguments(bundle);
        return categoryFragment;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_article_type;
    }

    @Override
    protected void onInitView() {
        mSwipeRefreshLayout.setColorSchemeResources(
                R.color.username1,
                R.color.username2,
                R.color.username3,
                R.color.username4,
                R.color.username5,
                R.color.username6);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        mAdapter = new CategoryAdapter(getContext(),R.layout.category_item,mDatas);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.addItemDecoration(new RecycleViewDivider(getActivity(), LinearLayoutManager.HORIZONTAL));
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setOnLoadMoreListener(this);
        mRecyclerView.setEmpty();
        LayoutAnimationController lac = new LayoutAnimationController(AnimationUtils.loadAnimation(getContext(),R.anim.fade_in));
        lac.setOrder(LayoutAnimationController.ORDER_NORMAL);
        mRecyclerView.setLayoutAnimation(lac);
        mRecyclerView.startLayoutAnimation();

        getData(true);

    }


    @Override
    public void onRefresh() {
        mPage = 1;
        getData(true);
    }

    private void getData(boolean isRefresh) {
        RetrofitUtil.getInstance().getCategory(mCategoryName, AppConfig.PAGE_SIZE_CATEGORY, mPage, new Subscriber<CategoryVO>() {
            @Override
            public void onCompleted() {
                if(isRefresh){
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }

            @Override
            public void onError(Throwable e) {
                if(isRefresh){
                    showMessage("刷新出错");
                    mSwipeRefreshLayout.setRefreshing(false);
                }else {
                    showMessage("加载出错了");
                }
            }

            @Override
            public void onNext(CategoryVO categoryVO) {
                if(isRefresh){
//                    showMessage("刷新成功");
                    mSwipeRefreshLayout.setRefreshing(false);
                    mDatas.clear();
                    mDatas.addAll(categoryVO.results);
                    mRecyclerView.setLoading();
                }else {
//                    showMessage("加载成功");
                    mDatas.addAll(categoryVO.results);
                }
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onLoadMore() {
        mPage ++;
        getData(false);
    }
}
