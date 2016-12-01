package com.example.myselfapp.bean;

import android.graphics.Bitmap;
import android.net.Uri;

import org.litepal.crud.DataSupport;

/**
 * 作者：刘磊 on 2016/11/3 10:50
 * 公司：希顿科技
 */

public class UserVO extends DataSupport {

    private String username;
    private String birth;
    private int age;
    private String phonenum;
    private Bitmap bitmap;

    public String getPhonenum() {
        return phonenum;
    }

    public void setPhonenum(String phonenum) {
        this.phonenum = phonenum;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getBirth() {
        return birth;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
