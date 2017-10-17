package com.cdbwsoft.library.net;


import android.util.Log;

import com.cdbwsoft.library.AppConfig;
import com.cdbwsoft.library.net.entity.ProgressFileBody;
import com.cdbwsoft.library.net.entity.Response;
import com.cdbwsoft.library.net.entity.SuperResponse;

import java.util.List;

/**
 * 文件上传监听
 * Created by DDL on 2015/7/20 0020.
 */
public abstract class FileListener implements com.android.volley.Response.Listener<SuperResponse> {

    public static final String TAG = "FileListener";
    private FileRequest mFileRequest;
    public void onProgress(long current, long total) {
        if(AppConfig.DEBUG) {
            Log.d(TAG,"正在上传：" + ((int) current / total * 100) + "%");
        }
    }

    public void setFileRequest(FileRequest fileRequest){
        mFileRequest = fileRequest;
    }
    public FileRequest getFileRequest(){
        return mFileRequest;
    }

    public List<ProgressFileBody> getFiles() {
        return null;
    }

}
