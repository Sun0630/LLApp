package com.umeng.soexample.bean;

/**
 * Created by admin on 2017/3/28.
 */

public class MsBean {

    private String title;
    private int icon;
    private String old_price;
    private String now_price;

    public MsBean(String title, int icon, String old_price, String now_price) {
        this.title = title;
        this.icon = icon;
        this.old_price = old_price;
        this.now_price = now_price;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getOld_price() {
        return old_price;
    }

    public void setOld_price(String old_price) {
        this.old_price = old_price;
    }

    public String getNow_price() {
        return now_price;
    }

    public void setNow_price(String now_price) {
        this.now_price = now_price;
    }
}
