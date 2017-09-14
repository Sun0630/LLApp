package com.umeng.soexample.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.core.adapter.CommonAdapter;
import com.android.core.adapter.RecyclerAdapter;
import com.android.core.adapter.RecyclerViewHolder;
import com.android.core.adapter.ViewHolder;
import com.bumptech.glide.Glide;
import com.umeng.soexample.R;
import com.umeng.soexample.activity.CircleEditActivity;
import com.umeng.soexample.bean.CategoryVO;
import com.umeng.soexample.bean.CircleVO;
import com.umeng.soexample.custom.CustomPopWindow;
import com.umeng.soexample.custom.InnerGridView;

import java.util.List;

/**
 *
 * Created by LiuLei on 2017/7/22.
 */
public class CircleAdapter extends RecyclerAdapter<CircleVO> {

    private Context context;
    private LinearLayout layout;

    public CircleAdapter(Context context, int layoutId, List<CircleVO> datas,LinearLayout layout) {
        super(context, layoutId, datas);
        this.context = context;
        this.layout = layout;
    }

    @Override
    public void convert(RecyclerViewHolder hepler, CircleVO circleVO) {

        hepler.setText(R.id.user_name,circleVO.getUser_name());
        hepler.setText(R.id.user_content,circleVO.getContent());
        if(circleVO.getImages() == null){
            ImageView imageView = (ImageView)hepler.getView(R.id.user_img);
            imageView.setVisibility(View.VISIBLE);
            imageView.setImageResource(R.mipmap.b_1);
        }else {
            ImageView imageView = (ImageView)hepler.getView(R.id.user_img);
            imageView.setVisibility(View.GONE);
            InnerGridView gridView = hepler.getView(R.id.gv_circle);
            gridView.setAdapter(new CommonAdapter<String>(context,circleVO.getImages().getUrls(),R.layout.grid_circle_item) {
                @Override
                public void convert(ViewHolder helper, String item) {
                    Glide.with(context)
                            .load(item)
                            .into((ImageView) helper.getView(R.id.gv_img));
                }
            });
        }

        hepler.getView(R.id.comment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int[] location = new int[2];
                v.getLocationOnScreen(location);

                View contentView = LayoutInflater.from(context).inflate(R.layout.pop_circle_comment,null);
                CustomPopWindow popWindow = new CustomPopWindow.PopupWindowBuilder(context)
                        .setView(contentView)
                        .setFocusable(true)
                        .setOutsideTouchable(true)
                        .create();
                popWindow.showAtLocation(v, Gravity.NO_GRAVITY, location[0]-popWindow.getWidth()-30, location[1]);

                TextView like = (TextView) contentView.findViewById(R.id.like);
                TextView comment = (TextView) contentView.findViewById(R.id.comment);

                like.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LinearLayout linearLayout = hepler.getView(R.id.linear_layout);
                        linearLayout.setVisibility(View.VISIBLE);
                        LinearLayout zan_layout = hepler.getView(R.id.zan_layout);
                        zan_layout.setVisibility(View.VISIBLE);
                        TextView tv = new TextView(context);
                        Drawable drawable = context.getResources().getDrawable(R.mipmap.zan);
                        drawable.setBounds(0, 0, 80, 80);
                        tv.setTextSize(15);
                        tv.setText("小艾");
                        tv.setCompoundDrawables(drawable,null,null,null);
                        zan_layout.addView(tv);
                        //关闭popwindow
                        popWindow.dissmiss();

                    }
                });

                comment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        layout.setVisibility(View.VISIBLE);
                        LinearLayout linearLayout = hepler.getView(R.id.linear_layout);
                        linearLayout.setVisibility(View.VISIBLE);
                        LinearLayout comment_layout = hepler.getView(R.id.comment_layout);
                        comment_layout.setVisibility(View.VISIBLE);
                        TextView tv = new TextView(context);
                        tv.setTextSize(15);
                        tv.setText("小艾:" + "你好啊，兄弟。。。");
                        comment_layout.addView(tv);
                        //关闭popwindow
                        popWindow.dissmiss();
                    }
                });

            }
        });


    }
}
