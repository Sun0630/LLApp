package com.umeng.soexample.bean;

import org.litepal.crud.DataSupport;

/**
 * Created by admin on 2017/4/20.
 */

public class RobotVO{

    //返回码
    private int code;
    //机器人回复的内容
    private String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

}
