package com.example.myselfapp.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.myselfapp.R;

/**
 * Created by Administrator on 2015/8/29.
 * 文章类别 Fragment
 */
public class MyDiscoverTypeFragment extends Fragment{
    private View views;

    public MyDiscoverTypeFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        views = inflater.inflate(R.layout.fragment_person, container, false);
        return views;
    }


}
