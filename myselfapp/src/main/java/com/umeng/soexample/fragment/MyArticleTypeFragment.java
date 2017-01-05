package com.umeng.soexample.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.umeng.soexample.R;

/**
 * 文章类别 Fragment
 * @author: liulei
 * @date: 2016-10-23 14:38
 */
public class MyArticleTypeFragment extends Fragment{
    private View views;

    public MyArticleTypeFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        views = inflater.inflate(R.layout.fragment_article_type, container, false);
        return views;
    }


}
