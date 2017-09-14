package com.umeng.soexample.bean;

import java.util.Date;

/**
 * Created by admin on 2017/4/20.
 */

public class Builder {
    private final int mType;
    private String mUsername;
    private String mMessage;
    private Date mDate;

    public Builder(int type) {
        mType = type;
    }

    public Builder username(String username) {
        mUsername = username;
        return this;
    }

    public Builder message(String message) {
        mMessage = message;
        return this;
    }

    public Builder date(Date date){
        mDate = date;
        return this;
    }

//    public Message build() {
//        Message message = new Message();
//        message.mType = mType;
//        message.mUsername = mUsername;
//        message.mMessage = mMessage;
//        message.data = mDate;
//        return message;
//    }
}
