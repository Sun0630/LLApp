package com.umeng.soexample.manager;

import android.content.Context;

import com.heaton.liulei.utils.utils.SPUtils;

/**
 * Created by admin on 2017/2/24.
 */

public enum ConfigManage {
    INSTANCE;

    private final String spName = "app_config";
    private final String key_isListShowImg = "isListShowImg";

    private boolean isListShowImg;

    public void initConfig(Context context){
        //列表是否显示图片
        isListShowImg = SPUtils.get(context,key_isListShowImg,false);
    }

    public boolean isListShowImg(){
        return isListShowImg;
    }

    public void setListShowImg(Context context,boolean listShowImg){
        SPUtils.put(context,key_isListShowImg,listShowImg);
        isListShowImg = listShowImg;
    }
}
