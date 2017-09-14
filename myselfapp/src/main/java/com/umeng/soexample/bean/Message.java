package com.umeng.soexample.bean;

import org.litepal.crud.DataSupport;

import java.util.Date;

public class Message extends DataSupport{

//    public static final int TYPE_FROM_MESSAGE = 0;
//    public static final int TYPE_LOG = 1;
//    public static final int TYPE_ACTION = 2;
//    public static final int TYPE_TO_MESSAGE = 3;

    private int type;
    private String message;
    private String username;
//    private String mData;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

//    public String getData() {
//        return mData;
//    }
//
//    public void setData(String mData) {
//        this.mData = mData;
//    }
}
