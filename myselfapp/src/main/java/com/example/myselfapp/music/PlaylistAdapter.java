package com.example.myselfapp.music;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.myselfapp.R;

import java.util.ArrayList;

/**
 * Copyright © 2013 ZhouFu.All rights reserved.
 *
 * @version V1.0
 * @Title: PlayAdapter.java
 * @Description: 播放列表适配器
 */
public class PlaylistAdapter extends BaseAdapter {

    private Context mContext;

    private ArrayList<Playlist> mList;

    public PlaylistAdapter(Context context, ArrayList<Playlist> list) {
        mContext = context;
        mList = list;
    }

    public void setData(ArrayList<Playlist> list) {
        mList = list;
    }

    @Override
    public int getCount() {
//        Log.e("getCount", "size() = " + mList.size());
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_playlist, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.number = (TextView) convertView.findViewById(R.id.music_numer_tv);
            viewHolder.name = (TextView) convertView.findViewById(R.id.music_song_name);
            viewHolder.author = (TextView) convertView.findViewById(R.id.music_singer_name);
            convertView.setTag(viewHolder);
        }
        viewHolder = (ViewHolder) convertView.getTag();
        Playlist playlist = mList.get(position);
        viewHolder.number.setText((++position > 9) ? (position + "") : ("0" + position));
        viewHolder.name.setText(playlist.getTitle());
        viewHolder.author.setText(playlist.getArtist());
        return convertView;
    }

    static class ViewHolder {
        TextView number;
        TextView name;
        TextView author;
    }

}
