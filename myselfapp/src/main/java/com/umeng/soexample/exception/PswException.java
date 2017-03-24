package com.umeng.soexample.exception;

/**
 * Created by admin on 2017/3/2.
 */

public class PswException extends Exception {

    private static final long serialVersionUID = -2992284109668400826L;

    //    public PswException(String message){
//        super(message);
//    }
    public PswException(String message) {
        super(message);
//        throw new IllegalArgumentException("输入密码类型错误");
    }
}
