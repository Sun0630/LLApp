package com.example.myselfapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;

import com.android.core.adapter.CommonAdapter;
import com.android.core.adapter.ViewHolder;
import com.bumptech.glide.Glide;
import com.example.myselfapp.R;
import com.example.myselfapp.bean.UserVO;

import java.util.List;

/**
 * 作者：刘磊 on 2016/11/3 12:39
 * 公司：希顿科技
 */

public class ResultAdapter extends CommonAdapter<UserVO> {

    private Context context;

    public ResultAdapter(Context context, List<UserVO> mDatas, int itemLayoutId) {
        super(context, mDatas, itemLayoutId);
        this.context = context;
    }

    @Override
    public void convert(ViewHolder helper, final UserVO item) {
        helper.setText(R.id.result_name,item.getUsername(),R.color.text_color);
        helper.setText(R.id.num,item.getPhonenum(),R.color.text);
        if(item.getBitmap()!=null){
            helper.setImageBitmap(R.id.header,item.getBitmap());
        }else {
            helper.setImageResource(R.id.header,R.mipmap.touxiang);
        }
        helper.getView(R.id.call).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_CALL);
                Uri data = Uri.parse("tel:"+item.getPhonenum());
                intent.setData(data);
                context.startActivity(intent);
            }
        });

    }
}
