package com.umeng.soexample.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.core.adapter.RecyclerAdapter;
import com.android.core.adapter.RecyclerViewHolder;
import com.umeng.soexample.R;
import com.umeng.soexample.bean.MsBean;

import java.util.List;

/**
 * Created by admin on 2017/3/28.
 */

public class MsRecyclerAdapter extends RecyclerAdapter<MsBean> {

    public MsRecyclerAdapter(Context context, int layoutId, List<MsBean> datas) {
        super(context, layoutId, datas);
    }

    @Override
    public void convert(RecyclerViewHolder hepler, MsBean msBean) {
        hepler.setText(R.id.title,msBean.getTitle());
        hepler.setText(R.id.old_price,msBean.getOld_price());
        hepler.setText(R.id.now_price,msBean.getNow_price());
        ((TextView)hepler.getView(R.id.old_price)).getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);//设置中划线
        ((ImageView)hepler.getView(R.id.icon)).setImageResource(msBean.getIcon());
    }
}
