package com.umeng.soexample.fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.widget.TextView;

import com.android.core.Help;
import com.android.core.StaticValue;
import com.android.core.base.AbsBaseActivity;
import com.android.core.base.AbsBaseFragment;
import com.android.core.utils.LocationUtils;
import com.umeng.soexample.App;
import com.umeng.soexample.Constants;
import com.umeng.soexample.R;
import com.umeng.soexample.activity.BigPhotoActivity;
import com.umeng.soexample.activity.BloggerActivity;
import com.umeng.soexample.activity.CameraAty;
import com.umeng.soexample.activity.ChatActivity;
import com.umeng.soexample.activity.DownloadManagerDemo;
import com.umeng.soexample.activity.FloatViewActivity;
import com.umeng.soexample.activity.MusicActivity;
import com.umeng.soexample.activity.MediaPlayerActivtiy;
import com.umeng.soexample.activity.NotifyActivity;
import com.umeng.soexample.activity.PersonActivity;
import com.umeng.soexample.activity.PopWindowActivity;
import com.umeng.soexample.activity.QrViewActivity;
import com.umeng.soexample.activity.SQLActivity;
import com.umeng.soexample.activity.ScreenCopyActivity;
import com.umeng.soexample.activity.ShareActivity;
import com.umeng.soexample.activity.SwipBackActivity;
import com.umeng.soexample.activity.VideoChatActivity;
import com.umeng.soexample.JniUtils;
import com.umeng.soexample.listener.SensonListener;
import com.umeng.soexample.music.MusicModeActivity;
import com.heaton.liulei.utils.utils.ScreenUtils;
import com.heaton.liulei.utils.utils.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

import butterknife.Bind;
import butterknife.OnClick;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * 作者：liulei
 * 公司：希顿科技
 */
public class PersonFragment extends AbsBaseFragment {

    @Bind(R.id.jni)
    TextView jni;
    @Bind(R.id.address)
    TextView address;
    private Socket mSocket;
    private SensonListener mSensor;

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        ((AbsBaseActivity)getActivity()).requestPermission(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, "打开定位权限", new AbsBaseActivity.GrantedResult() {
            @Override
            public void onResult(boolean granted) {
                if(granted){

                }else {
                    ToastUtil.showToast("请打开位置权限");
                }
            }
        });
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_person;
    }

    @Override
    protected void onInitView() {
        App app = (App)getActivity().getApplication();
        mSocket = app.getSocket();

//        //初始化重力感应
//        mSensor = new SensonListener(getContext());
//        mSensor.setOnSensonChangeListener(this);

//        JniUtils helper = new JniUtils();
//        jni.setText("自定义jni："+"\n"+helper.getMyName());

        mSocket.on("login", onLogin);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        LocationUtils.unregister();
    }

    @OnClick(R.id.button)
    void start(){
        startActivity(MusicActivity.class);
    }

    @OnClick(R.id.swip_back)
    void swip_back(){
        SwipBackActivity.start(getContext());
    }

    @OnClick(R.id.chat)
    void start_chat(){
        String userName = "liulei"+ UUID.randomUUID().toString().substring(0,3);
        Constants.user_name = userName;
        mSocket.emit("add user", userName);
        startActivity(ChatActivity.class);
    }

    @OnClick(R.id.person)
    void person(){
        startActivity(PersonActivity.class);
    }

    @OnClick(R.id.video)
    void video(){
        startActivity(MediaPlayerActivtiy.class);
    }

    @OnClick(R.id.theme)
    void theme(){
        new AlertDialog.Builder(getActivity()).setTitle("请选择主题颜色").setIcon(android.R.drawable.ic_dialog_info)
                .setSingleChoiceItems(new String[]{"红色", "黄色", "蓝色", "绿色", "青色"}, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if(which == 0){
                            StaticValue.color = R.color.username0;
                        }else if(which == 1){
                            StaticValue.color = R.color.username3;
                        }else if(which == 2){
                            StaticValue.color = R.color.username8;
                        }else if(which == 3){
                            StaticValue.color = R.color.username5;
                        }else if(which == 4){
                            StaticValue.color = R.color.username6;
                        }
//                        StaticValue.color = com.android.core.R.color.red_btn_bg_color;
                        Help.initSystemBar(getActivity(), StaticValue.color);
                        ((AbsBaseActivity)getActivity()).toolbar.setBackgroundColor(getActivity().getResources().getColor(StaticValue.color));
                    }
                }).setNegativeButton("取消",null).show();
    }

    @OnClick(R.id.video_chat)
    void video_chat(){
        startActivity(VideoChatActivity.class);
    }

    @OnClick(R.id.blogger)
    void blogger(){
        startActivity(BloggerActivity.class);
    }

    @OnClick(R.id.rq_view)
    void rq_view(){
        startActivity(QrViewActivity.class);
    }

    @OnClick(R.id.look_photo)
    void look_photo(){
        startActivity(BigPhotoActivity.class);
    }

    @OnClick(R.id.float_view)
    void float_view(){
        startActivity(FloatViewActivity.class);
    }

    @OnClick(R.id.download)
    void download(){
        startActivity(DownloadManagerDemo.class);
    }

    @OnClick(R.id.share)
    void share(){
        startActivity(ShareActivity.class);
    }

    @OnClick(R.id.music)
    void music(){
        startActivity(MusicModeActivity.class);
    }

//    @Override
//    public void turnLeft() {
//        Log.e(TAG,"向左。。。");
//    }
//
//    @Override
//    public void turnRight() {
//        Log.e(TAG,"向右。。。");
//    }
//
//    @Override
//    public void noChange() {
//
//    }
//
//    @Override
//    public void shark() {
//        Log.e(TAG,"摇一摇。。。");
//        new AlertDialog.Builder(getActivity()).setCancelable(false)
//        .setIcon(R.mipmap.touxiang)
//        .setMessage("摇到漂亮妹子一枚")
//        .setTitle("摇一摇")
//        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                    }
//                })
//        .setNegativeButton("我不要", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                    }
//                }).show();
//
//    }

    boolean isSensor = false;//当前是否是重力感应状态
    @OnClick(R.id.shark)
    void to_shark(){
        if(isSensor){
            isSensor = false;
            mSensor.closeSensor();
            mSensor.stopService(getActivity());
            ToastUtil.showToast("重力感应关闭了");
        }else {
            //初始化重力感应
            mSensor = new SensonListener(getContext());
            isSensor = true;
            mSensor.openSensor();
            mSensor.startService(getActivity());
            ToastUtil.showToast("重力感应打开了");
        }
    }

    @OnClick(R.id.camera)
    void camera(){
        startActivity(CameraAty.class);
    }

    @OnClick(R.id.shot_screen)
    void shot(){
        ((AbsBaseActivity)getActivity()).requestPermission(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, "请求读写权限", new AbsBaseActivity.GrantedResult() {
            @Override
            public void onResult(boolean granted) {
                if(granted){
                    String dir = ScreenUtils.shotDir(getActivity());
                    startActivity(new Intent(getActivity(), ScreenCopyActivity.class).putExtra("shotDir",dir));
                }else {
                    ToastUtil.showToast("权限未被允许");
                    return;
                }

            }
        });
    }

    @OnClick(R.id.sql)
    void sql(){
        startActivity(SQLActivity.class);
    }

    @OnClick(R.id.notify)
    void notif(){
        startActivity(NotifyActivity.class);
    }

    @OnClick(R.id.popwindow)
    void pop(){
        startActivity(PopWindowActivity.class);
    }

    private Emitter.Listener onLogin = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            JSONObject data = (JSONObject) args[0];

            int numUsers;
            try {
                numUsers = data.getInt("numUsers");
            } catch (JSONException e) {
                return;
            }

//            Intent intent = new Intent();
//            intent.putExtra("username", mUsername);
//            intent.putExtra("numUsers", numUsers);
//            setResult(RESULT_OK, intent);
//            finish();
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mSensor!=null){
            mSensor.stopService(getActivity());
        }
    }
}
