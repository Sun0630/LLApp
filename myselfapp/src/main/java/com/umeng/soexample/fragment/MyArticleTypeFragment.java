package com.umeng.soexample.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.core.base.AbsBaseFragment;
import com.android.core.widget.recyclerviewwithfooter.OnLoadMoreListener;
import com.android.core.widget.recyclerviewwithfooter.RecyclerViewWithFooter;
import com.umeng.soexample.R;
import com.umeng.soexample.adapter.CategoryAdapter;
import com.umeng.soexample.adapter.RecycleViewDivider;
import com.umeng.soexample.bean.CategoryVO;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

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

    private List<CategoryVO>mDatas = new ArrayList<>();

    private int mPage = 1;

    public MyArticleTypeFragment() {
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
    }


    @Override
    public void onRefresh() {
        mPage = 1;
    }

    @Override
    public void onLoadMore() {

    }
}
