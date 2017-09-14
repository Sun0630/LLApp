package com.umeng.soexample.bean;

import java.util.ArrayList;

/**
 * Created by LiuLei on 2017/7/22.
 */

public class CircleVO {

    private String content;//内容
    private String user_name;
    private String user_header;
    private Images images;

    public CircleVO(String content, String user_name, String user_header, Images images) {
        this.content = content;
        this.user_name = user_name;
        this.user_header = user_header;
        this.images = images;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_header() {
        return user_header;
    }

    public void setUser_header(String user_header) {
        this.user_header = user_header;
    }

    public Images getImages() {
        return images;
    }

    public void setImages(Images images) {
        this.images = images;
    }

    public static class Images{
        private ArrayList<String> urls;

        public Images(ArrayList<String> urls) {
            this.urls = urls;
        }

        public ArrayList<String> getUrls() {
            return urls;
        }

        public void setUrls(ArrayList<String> urls) {
            this.urls = urls;
        }
    }
}
