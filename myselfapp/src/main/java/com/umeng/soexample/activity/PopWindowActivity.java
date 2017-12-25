package com.umeng.soexample.activity;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.core.base.AbsBaseActivity;
import com.umeng.soexample.R;
import com.umeng.soexample.adapter.PopWindowAdapter;
import com.umeng.soexample.custom.CustomPopWindow;
import com.umeng.soexample.custom.popAnima.PublishPopWindow;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2016/12/8.
 */

public class PopWindowActivity extends AbsBaseActivity implements View.OnClickListener{

    private TextView mButton1,mButton2,mButton3,mButton4;
    private CustomPopWindow mCustomPopWindow;
    private CustomPopWindow mListPopWindow;
    private ImageView imageView;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_popwindow;
    }

    @Override
    protected void onInitView() {
        setTitle("弹出框界面");
        toolbar.setNavigationIcon(R.mipmap.abc_ic_ab_back_mtrl_am_alpha);
        toolbar.setNavigationOnClickListener(v -> finish());


        mButton1 = (TextView) findViewById(R.id.button1);
        mButton1.setOnClickListener(this);
        mButton2 = (TextView) findViewById(R.id.button2);
        mButton2.setOnClickListener(this);
        mButton3 = (TextView) findViewById(R.id.button3);
        mButton3.setOnClickListener(this);
        mButton4 = (TextView) findViewById(R.id.button4);
        mButton4.setOnClickListener(this);
        imageView = (ImageView) findViewById(R.id.iv_start);
        imageView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button1:
                showPopBottom();
                break;
            case R.id.button2:
                showPopTop();
                break;
            case R.id.button3:
                showPopMenu();
                break;
            case R.id.button4:
                showPopListView();
                break;
            case R.id.iv_start:
                PublishPopWindow popWindow = new PublishPopWindow(PopWindowActivity.this);
                popWindow.showMoreWindow(v);
                break;
        }
    }

    private void showPopBottom(){
        CustomPopWindow popWindow = new CustomPopWindow.PopupWindowBuilder(this)
                .setView(R.layout.pop_layout1)
                .setFocusable(true)
                .setOutsideTouchable(true)
                .create()
                .showAsDropDown(mButton1,0,10);
    }

    private void showPopTop(){
        CustomPopWindow popWindow = new CustomPopWindow.PopupWindowBuilder(this)
                .setView(R.layout.pop_layout2)
                .create();
        popWindow .showAsDropDown(mButton2,0,  - (mButton2.getHeight() + popWindow.getHeight()));
        //popWindow.showAtLocation(mButton1, Gravity.NO_GRAVITY,0,0);
    }

    private void showPopMenu(){
        View contentView = LayoutInflater.from(this).inflate(R.layout.pop_menu,null);
        //处理popWindow 显示内容
        handleLogic(contentView);
        //创建并显示popWindow
        mCustomPopWindow= new CustomPopWindow.PopupWindowBuilder(this)
                .setView(contentView)
                .create()
                .showAsDropDown(mButton3,0,20);


    }

    private void showPopListView(){
        View contentView = LayoutInflater.from(this).inflate(R.layout.pop_list,null);
        //处理popWindow 显示内容
        handleListView(contentView);
        //创建并显示popWindow
        mListPopWindow= new CustomPopWindow.PopupWindowBuilder(this)
                .setView(contentView)
                .size(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT)//显示大小
                .create()
                .showAsDropDown(mButton4,0,20);
    }

    private void handleListView(View contentView){
        RecyclerView recyclerView = (RecyclerView) contentView.findViewById(R.id.recyclerView);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);
        PopWindowAdapter adapter = new PopWindowAdapter();
        adapter.setData(mockData());
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }

    private List<String> mockData(){
        List<String> data = new ArrayList<>();
        for (int i=0;i<100;i++){
            data.add("Item:"+i);
        }

        return data;
    }

    /**
     * 处理弹出显示内容、点击事件等逻辑
     * @param contentView
     */
    private void handleLogic(View contentView){
        View.OnClickListener listener = v -> {
            if(mCustomPopWindow!=null){
                mCustomPopWindow.dissmiss();
            }
            String showContent = "";
            switch (v.getId()){
                case R.id.menu1:
                    showContent = "点击 Item菜单1";
                    break;
                case R.id.menu2:
                    showContent = "点击 Item菜单2";
                    break;
                case R.id.menu3:
                    showContent = "点击 Item菜单3";
                    break;
                case R.id.menu4:
                    showContent = "点击 Item菜单4";
                    break;
                case R.id.menu5:
                    showContent = "点击 Item菜单5" ;
                    break;
            }
            Toast.makeText(PopWindowActivity.this,showContent,Toast.LENGTH_SHORT).show();
        };
        contentView.findViewById(R.id.menu1).setOnClickListener(listener);
        contentView.findViewById(R.id.menu2).setOnClickListener(listener);
        contentView.findViewById(R.id.menu3).setOnClickListener(listener);
        contentView.findViewById(R.id.menu4).setOnClickListener(listener);
        contentView.findViewById(R.id.menu5).setOnClickListener(listener);
    }
}
