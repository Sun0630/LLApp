//package com.example.myselfapp.net;
//
//
//import android.util.Log;
//
//
//import com.example.myselfapp.net.entity.ProgressFileBody;
//import com.example.myselfapp.net.entity.Response;
//import com.heaton.liulei.utils.AppConfig;
//
//import java.util.List;
//
///**
// * 文件上传监听
// * Created by DDL on 2015/7/20 0020.
// */
//public abstract class FileListener implements com.android.volley.Response.Listener<Response> {
//
//    public static final String TAG = "FileListener";
//    public void onProgress(long current, long total) {
//        if(AppConfig.DEBUG) {
//            Log.d(TAG,"正在上传：" + ((int) current / total * 100) + "%");
//        }
//    }
//
//    public List<ProgressFileBody> getFiles() {
//        return null;
//    }
//
//}
