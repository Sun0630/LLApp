package com.umeng.soexample.adapter;

import android.content.Context;
import android.view.ViewGroup;

import com.android.core.adapter.RecyclerAdapter;
import com.android.core.adapter.RecyclerViewHolder;
import com.umeng.soexample.Constants;
import com.umeng.soexample.bean.Message;
import com.umeng.soexample.R;

import java.util.List;

/**
 *
 * Created by admin on 2017/4/20.
 */
public class RobotAdapter extends RecyclerAdapter<Message>{

    private List<Message>mMessage;

    public RobotAdapter(Context context,List<Message> datas) {
        super(context,datas);
        mMessage = datas;
    }

    //若实现多列表布局，则需要重写onCreateViewHolder   并实现上述构造方法（不带layout参数的构造方法）
    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layout = -1;
        switch (viewType) {
            case Constants.TYPE_FROM_MESSAGE:
                layout = R.layout.item_rec_message;
                break;
            case Constants.TYPE_TO_MESSAGE:
                layout = R.layout.item_send_message;
                break;
        }
        RecyclerViewHolder viewHolder = RecyclerViewHolder.get(mContext, null, parent, layout, -1);
        setListener(parent, viewHolder, viewType);
        return viewHolder;
    }

    @Override
    public void convert(RecyclerViewHolder hepler, Message message) {
        hepler.setText(R.id.message,message.getMessage());
        hepler.setText(R.id.username,message.getUsername());
    }

    @Override
    public int getItemViewType(int position) {
        return mMessage.get(position).getType();
    }
}
