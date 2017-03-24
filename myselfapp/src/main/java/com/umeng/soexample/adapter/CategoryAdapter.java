package com.umeng.soexample.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;

import com.android.core.adapter.RecyclerAdapter;
import com.android.core.adapter.RecyclerViewHolder;
import com.bumptech.glide.Glide;
import com.heaton.liulei.utils.utils.DateUtil;
import com.umeng.soexample.R;
import com.umeng.soexample.activity.BloggerActivity;
import com.umeng.soexample.bean.CategoryVO;
import com.umeng.soexample.manager.ConfigManage;

import java.util.List;

/**
 * Created by admin on 2017/2/21.
 */

public class CategoryAdapter extends RecyclerAdapter<CategoryVO.ResultsBean> {

    public CategoryAdapter(Context context, int layoutId, List<CategoryVO.ResultsBean> datas) {
        super(context, layoutId, datas);
    }

    @Override
    public void convert(RecyclerViewHolder holder, CategoryVO.ResultsBean category) {
        if(category != null){
            ImageView imageView = holder.getView(R.id.iv_item_img);
            if(ConfigManage.INSTANCE.isListShowImg()){//列表显示图片
                imageView.setVisibility(View.VISIBLE);
                if(category.images != null && category.images.size() > 0){
                    Glide.with(mContext).load(category.images.get(0) + "?imageView2/0/w/200").into(imageView);
                }else {//列表不显示图片
                    Glide.with(mContext).load(R.mipmap.image_default).into(imageView);
                }
            }else {
                imageView.setVisibility(View.GONE);
            }
            holder.setText(R.id.tv_item_title, category.desc == null ? "unknown" : category.desc);
            holder.setText(R.id.tv_item_publisher, category.who == null ? "unknown" : category.who);
            holder.setText(R.id.tv_item_time, DateUtil.dateFormat(category.publishedAt));
            holder.setOnClickListener(R.id.ll_item, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, BloggerActivity.class);
                    intent.putExtra(BloggerActivity.GANK_TITLE, category.desc);
                    intent.putExtra(BloggerActivity.GANK_URL, category.url);
                    mContext.startActivity(intent);
                }
            });

        }
    }
}
