package com.umeng.soexample.adapter;

import android.content.Context;

import com.android.core.adapter.RecyclerAdapter;
import com.android.core.adapter.RecyclerViewHolder;
import com.umeng.soexample.R;

import java.util.List;

/**
 * @author: liulei
 * @date: 2016-10-26 16:23
 */
public class DisCoverRecyclerAdapter extends RecyclerAdapter<String> {

    private String URL_Base = "http://tnfs.tngou.net/image";

    public DisCoverRecyclerAdapter(Context context, int layoutId, List<String> datas) {
        super(context, layoutId, datas);
    }

    @Override
    public void convert(final RecyclerViewHolder holder, final String item) {
//        Uri uri = Uri.parse(URL_Base + item.getImg());
        holder.setText(R.id.discover_text, item);
//        holder.setOnClickListener(R.id.sv_classitfy_img, new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Snackbar.make(holder.getView(R.id.sv_classitfy_img), item.getTitle(), Snackbar.LENGTH_LONG).show();
//                SwipBackActivity.start(mContext);
//            }
//        });
    }
}
