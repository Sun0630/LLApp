package com.example.myselfapp.fragment;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.android.core.adapter.RecyclerAdapter;
import com.android.core.adapter.RecyclerViewHolder;
import com.android.core.base.AbsBaseFragment;
import com.example.myselfapp.R;

import java.util.ArrayList;

import butterknife.Bind;

/**
 * @author: liulei
 * @date: 2016-10-24 09:06
 */
public class LoginFragment extends AbsBaseFragment {
    @Bind(R.id.local_music_list)
    RecyclerView localMusicListViews;
    private ArrayList<String> datas = new ArrayList<>();


    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_login;
    }

    @Override
    protected void onInitView() {
        for (int i=0;i<20;i++){
            datas.add("条目"+i);
        }

        localMusicListViews.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        localMusicListViews.setLayoutManager(layoutManager);

        RecyclerAdapter adapter = new RecyclerAdapter(getContext(),android.R.layout.simple_list_item_1,datas) {
            @Override
            public void convert(RecyclerViewHolder hepler, Object o) {
            }
            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            }
        };
        localMusicListViews.setAdapter(adapter);

//        listView.setAdapter(new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1,datas));
    }

//    @OnClick(R.id.button)
//    void start(){
//        startActivity(new Intent(getActivity(), MusicActivity.class));
//    }

}
