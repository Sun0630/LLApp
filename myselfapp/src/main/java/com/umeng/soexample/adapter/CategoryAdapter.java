package com.umeng.soexample.adapter;

import android.content.Context;

import com.android.core.adapter.RecyclerAdapter;
import com.android.core.adapter.RecyclerViewHolder;
import com.umeng.soexample.bean.CategoryVO;

import java.util.List;

/**
 * Created by admin on 2017/2/21.
 */

public class CategoryAdapter extends RecyclerAdapter<CategoryVO> {

    public CategoryAdapter(Context context, int layoutId, List<CategoryVO> datas) {
        super(context, layoutId, datas);
    }

    @Override
    public void convert(RecyclerViewHolder hepler, CategoryVO categoryVO) {

    }
}
