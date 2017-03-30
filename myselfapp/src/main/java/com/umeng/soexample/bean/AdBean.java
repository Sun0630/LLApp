package com.umeng.soexample.bean;

/**
 * author : openXu
 * create at : 2016/11/22 12:12
 * blog : http://blog.csdn.net/xmxkf
 * gitHub : https://github.com/openXu
 * project : LimitScrollerView
 * class name : DataBean
 * version : 1.0
 * class describe： 数据
 */
public class AdBean {

    public AdBean(String title, String text) {
        this.title = title;
        this.text = text;
    }

    private String title;
    private String text;

    public String getTetle() {
        return title;
    }

    public void setTitle(int icon) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

}
