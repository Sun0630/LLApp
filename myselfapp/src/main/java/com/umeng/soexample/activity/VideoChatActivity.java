package com.umeng.soexample.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.android.core.base.AbsBaseActivity;
import com.umeng.soexample.R;
//import com.example.myselfapp.SingleChatClient;

import butterknife.Bind;
import butterknife.OnClick;
//import cn.niusee.chat.sdk.Message;
//import cn.niusee.chat.sdk.Point;
//import cn.niusee.chat.sdk.Session;
//import cn.niusee.chat.sdk.Stream;
//import cn.niusee.chat.sdk.callback.OnSessionCallback;
//import cn.niusee.chat.sdk.exception.CameraNotFoundException;
//import cn.niusee.chat.sdk.exception.SessionExistException;
//import cn.niusee.chat.sdk.exception.StreamEmptyException;
//import cn.niusee.chat.sdk.util.CameraDeviceUtil;

public class VideoChatActivity extends AbsBaseActivity{

//    private static final String TAG = "VideoChatActivity";

//    @Bind(R.id.chat_surface_call)
//    GLSurfaceView chatSurfaceCall;
    @Bind(R.id.accept)
    Button accept;
    @Bind(R.id.line_state)
    TextView state;

//    private SingleChatClient mSingleChatClient;
//    private Stream mStream;
//    private Session mSession;
    private String callName;//发起会话者名称
//    private Stream mLocalStream;
//    private Stream mRemoteStream;
    private String userName = "sender";

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_video_chat;
    }

    @Override
    protected void onInitView() {
        setTitle("视频聊天");
        toolbar.setNavigationIcon(R.mipmap.abc_ic_ab_back_mtrl_am_alpha);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        initEvent();

    }

    @OnClick(R.id.line_service)
    void line(){
//        if(mSingleChatClient==null){
//            initEvent();
//        }
        // 创建令牌
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setMessage("作为控制端还是被控端")
                .setPositiveButton("控制端", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        userName = "receiver";
//                        login("sender");
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("被控端", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        userName = "sender";
//                        login("receiver");
                        dialog.dismiss();
                    }
                }).create();
        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    @OnClick(R.id.close_service)
    void close(){
//        if (mSingleChatClient != null) {
//            mSingleChatClient.destroy();
//        }
        Log.e("MainActivity===","断开视频服务器");
        state.setText("未连接视频服务器");
    }

    @OnClick(R.id.send)
    void send(){
        call();
    }

    @OnClick(R.id.hang_up)
    void hang_up(){
        hangup();
    }

    @OnClick(R.id.accept)
    void accept(){
//        if (mSession != null) {
//            mSession.accept();
//        }
    }

    private void initEvent(){
//        mSingleChatClient = SingleChatClient.getInstance(getApplication());
        //初始化显示界面
//        mSingleChatClient.getChatClient().initPlayView(chatSurfaceCall);
    }

    private void hangup() {
        accept.setVisibility(View.GONE);
        Log.e("MainActivity===","已主动挂断,"+"对方的名称:"+userName);
//        chatSurfaceCall = null;
//        if (mLocalStream != null) {
//            mLocalStream.stop();
//            mLocalStream.release();
//        }
//        if (mSession != null) {
//            mSingleChatClient.getToken().closeSession(userName);
//            mSession = null;
//        }
    }

    private void call() {
//        try {
//            Log.e("MainActivity===","对方username："+userName);
//            mSession = mSingleChatClient.getToken().createSession(userName);
//        } catch (SessionExistException e) {
//            e.printStackTrace();
//        }
//        requestPermission(new String[]{Manifest.permission.CAMERA}, "请求设备相机权限", new GrantedResult() {
//            @Override
//            public void onResult(boolean granted) {
//                if(granted){//表示用户允许
//                    createLocalStream();
//                }else {//用户拒绝
//                    ToastUtil.showToast("权限拒绝");
//                }
//            }
//        });
//        mSession.setOnSessionCallback(new OnSessionCallback() {
//            @Override
//            public void onAccept() {
//                ToastUtil.showToast("通话建立成功");
//            }
//            @Override
//            public void onReject() {
//                ToastUtil.showToast("对方拒绝了您的视频通话请求");
//            }
//            @Override
//            public void onConnect() {
//            }
//            @Override
//            public void onClose() {
//                mSingleChatClient.getToken().closeSession(userName);
//                Log.e(TAG, "onClose  我是呼叫方");
//                hangup();
//                ToastUtil.showToast("对方已中断视频通话");
//            }
//            @Override
//            public void onRemote(Stream stream) {
//                mStream = stream;
//                Log.e(TAG, "onRemote  我是呼叫方");
//                ToastUtil.showToast("视频建立成功");
//                mSingleChatClient.getChatClient().playStream(stream, new Point(0, 0, 100, 100, false));
//                mSingleChatClient.getChatClient().playStream(mLocalStream, new Point(72, 72, 25, 25, false));
//            }
//            @Override
//            public void onPresence(Message message) {
//            }
//        });
//        if (mSession != null) {
//            mSession.call();
//        }
    }

//    public void login(String username) {
//        try {
//            SingleChatClient.getInstance(getApplication()).setOnConnectListener(new SingleChatClient.OnConnectListener() {
//                @Override
//                public void onConnect() {
////                    loadDevices();
//                    Log.e(TAG, "连接视频服务器成功");
//                    state.setText("登录视频服务器成功!");
//                }
//
//                @Override
//                public void onConnectFail(String reason) {
//                    Log.e(TAG, "连接视频服务器失败");
//                    state.setText("登录视频服务器失败!" + reason);
//                }
//
//                @Override
//                public void onSessionCreate(Session session) {
//                    Log.e(TAG, "来电者名称:" + session.callName);
//                    mSession = session;
//                    accept.setVisibility(View.VISIBLE);
//                    requestPermission(new String[]{Manifest.permission.CAMERA}, "请求设备权限", new GrantedResult() {
//                        @Override
//                        public void onResult(boolean granted) {
//                            if(granted){
//                                createLocalStream();
//                            }else {
//                                ToastUtil.showToast("权限拒绝");
//                            }
//                        }
//                    });
//                    mSession.setOnSessionCallback(new OnSessionCallback() {
//                        @Override
//                        public void onAccept() {
//                            ToastUtil.showToast("视频接收");
//                        }
//
//                        @Override
//                        public void onReject() {
//                            ToastUtil.showToast("拒绝通话");
//                        }
//
//                        @Override
//                        public void onConnect() {
//                            ToastUtil.showToast("视频建立成功");
//                        }
//
//                        @Override
//                        public void onClose() {
//                            Log.e(TAG, "onClose  我是被叫方");
//                            hangup();
//                        }
//
//                        @Override
//                        public void onRemote(Stream stream) {
//                            Log.e(TAG, "onRemote  我是被叫方");
//                            mRemoteStream = stream;
//                            mSingleChatClient.getChatClient().playStream(stream, new Point(0, 0, 100, 100, false));
//                            mSingleChatClient.getChatClient().playStream(mLocalStream, new Point(72, 72, 25, 25, false));
//                        }
//
//                        @Override
//                        public void onPresence(Message message) {
//
//                        }
//                    });
//                }
//            });
////            SingleChatClient.getInstance(getApplication()).connect(UUID.randomUUID().toString(), WEB_RTC_URL);
//            Log.e("MainActicvity===",username);
//            SingleChatClient.getInstance(getApplication()).connect(username, Constants.WEB_RTC_URL);
//        } catch (URISyntaxException e) {
//            e.printStackTrace();
//            Log.d(TAG, "连接失败");
//        }
//    }
//
//    private void createLocalStream() {
//        if (mLocalStream == null) {
//            try {
//                String camerName = CameraDeviceUtil.getFrontDeviceName();
//                if(camerName==null){
//                    camerName = CameraDeviceUtil.getBackDeviceName();
//                }
//                mLocalStream = mSingleChatClient.getChatClient().createStream(camerName,
//                        new Stream.VideoParameters(640, 480, 12, 25), new Stream.AudioParameters(true, false, true, true), null);
//            } catch (StreamEmptyException | CameraNotFoundException e) {
//                e.printStackTrace();
//            }
//        } else {
//            mLocalStream.restart();
//        }
//        mSingleChatClient.getChatClient().playStream(mLocalStream, new Point(72, 72, 25, 25, false));
//    }

}
