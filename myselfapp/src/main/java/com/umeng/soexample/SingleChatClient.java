package com.umeng.soexample;


import android.content.Context;
import android.widget.Toast;


import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import cn.niusee.chat.sdk.NiuChat;
import cn.niusee.chat.sdk.Session;
import cn.niusee.chat.sdk.Token;
import cn.niusee.chat.sdk.callback.OnTokenCallback;

//import cn.niusee.chat.sdk.OnTokenCallback;

/**
 * 视频客户端的单例
 */
public class SingleChatClient implements OnTokenCallback {

    private static SingleChatClient mSingleChatClient;
    private NiuChat mChatClient;
    private Token mToken;
    private Context mContext;
    private boolean connected;
    private List<OnConnectListener> mOnConnectListenerList = new ArrayList<>();

    public SingleChatClient(Context context){
        mContext = context;
        mChatClient = new NiuChat(context);
    }

    public static SingleChatClient getInstance(Context context){
        if (mSingleChatClient == null) {
            mSingleChatClient = new SingleChatClient(context);
        }
        return mSingleChatClient;
    }

    public NiuChat getChatClient(){
        return mChatClient;
    }

    public Token connect(String name, String url) throws URISyntaxException{
        if(mToken != null){
            return mToken;
        }
        mToken = mChatClient.createToken(name);
        mToken.setOnTokenCallback(this);
        mToken.connect(url);
        return mToken;
    }

    public Token getToken(){
        return mToken;
    }

    public boolean isConnected(){
        return connected;
    }

    public void setOnConnectListener(OnConnectListener onConnectListener){
        if(!mOnConnectListenerList.contains(onConnectListener)){
            mOnConnectListenerList.add(onConnectListener);
        }
    }

    public void removeOnConnectListener(OnConnectListener onConnectListener){
        if(!mOnConnectListenerList.contains(onConnectListener)){
            mOnConnectListenerList.remove(onConnectListener);
        }
    }

    public void destroy(){
        if(mToken != null) {
            if (mToken.hasSession()) {
                mToken.closeAllSessions();
            }
            mToken.close();
        }
        mOnConnectListenerList.clear();
        mSingleChatClient = null;
    }


    @Override
    public void onConnected() {
//        ToastUtil.info(mContext,"连接服务器成功");
        connected = true;
        if(mOnConnectListenerList != null && mOnConnectListenerList.size() > 0){
            for(OnConnectListener listener: mOnConnectListenerList){
                listener.onConnect();
            }
        }
    }

    @Override
    public void onConnectFail() {
        Toast.makeText(mContext,"无法登录到服务器",Toast.LENGTH_SHORT).show();
        connected = false;
        if(mOnConnectListenerList != null && mOnConnectListenerList.size() > 0){
            for(OnConnectListener listener: mOnConnectListenerList){
                listener.onConnectFail("无法登录到服务器");
            }
        }

    }

    @Override
    public void onConnectTimeOut() {
        Toast.makeText(mContext,"登录超时",Toast.LENGTH_SHORT).show();
        connected = false;
        if(mOnConnectListenerList != null && mOnConnectListenerList.size() > 0){
            for(OnConnectListener listener: mOnConnectListenerList){
                listener.onConnectFail("登录超时");
            }
        }
    }

    @Override
    public void onError(cn.niusee.chat.sdk.Error error) {
//        ToastUtil.info(mContext,"错误: " + (error != null ? error.msg : ""));
        connected = false;
        if(mOnConnectListenerList != null && mOnConnectListenerList.size() > 0){
            for(OnConnectListener listener: mOnConnectListenerList){
                listener.onConnectFail("错误: " + (error != null ? error.msg : ""));
            }
        }
    }

   /* @Override
    public void onError(Token.Error error) {
        ToastUtil.info(mContext,"错误: " + (error != null ? error.getMsg() : ""));
        connected = false;
        if(mOnConnectListenerList != null && mOnConnectListenerList.size() > 0){
            for(OnConnectListener listener: mOnConnectListenerList){
                listener.onConnectFail("错误: " + (error != null ? error.getMsg() : ""));
            }
        }
    }*/

    @Override
    public void onDisconnect() {
        Toast.makeText(mContext,"断开连接",Toast.LENGTH_SHORT).show();
        connected = false;
        if(mOnConnectListenerList != null && mOnConnectListenerList.size() > 0){
            for(OnConnectListener listener: mOnConnectListenerList){
                listener.onConnectFail("断开连接");
            }
        }
    }

    @Override
    public void onSessionCreate(Session session) {
        if(mOnConnectListenerList != null && mOnConnectListenerList.size() > 0){
            for(OnConnectListener listener: mOnConnectListenerList){
                listener.onSessionCreate(session);
            }
        }
    }

    public interface OnConnectListener{
        void onConnect();
        void onConnectFail(String reason);
        void onSessionCreate(Session session);
    }
}
