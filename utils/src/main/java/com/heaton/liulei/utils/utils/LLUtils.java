package com.heaton.liulei.utils.utils;

import android.content.Context;
import android.os.Environment;

import java.io.File;

/**
 * 作者：刘磊 on 2016/10/25 16:31
 * 公司：希顿科技
 */

public class LLUtils {
    public static Context mContext;


    /**
     * 进入应用之后，要在application中进行初始化
     * @param context
     */
    public static void init(Context context){
        mContext = context;
        FileOperateUtils.init();
    }

    public static Context getmContext(){
        if(mContext == null){
            throw new IllegalArgumentException("Context can not be null,please init LiuleiUtils");
        }else {
            return mContext;
        }
    }
}
