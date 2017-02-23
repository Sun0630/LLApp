package com.umeng.soexample.fragment;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.core.StaticValue;
import com.android.core.adapter.RecyclerAdapter;
import com.android.core.adapter.RecyclerViewHolder;
import com.android.core.base.AbsBaseActivity;
import com.android.core.base.AbsBaseFragment;
import com.umeng.soexample.App;
import com.umeng.soexample.Constants;
import com.umeng.soexample.R;
import com.umeng.soexample.activity.BigPhotoActivity;
import com.umeng.soexample.activity.BloggerActivity;
import com.umeng.soexample.activity.CameraAty;
import com.umeng.soexample.activity.ChatActivity;
import com.umeng.soexample.activity.DanmuActivity;
import com.umeng.soexample.activity.DownloadManagerDemo;
import com.umeng.soexample.activity.FloatViewActivity;
import com.umeng.soexample.activity.LeftMenuActivity;
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
import com.umeng.soexample.listener.SensonListener;
import com.umeng.soexample.music.MusicModeActivity;
import com.heaton.liulei.utils.utils.ScreenUtils;
import com.heaton.liulei.utils.utils.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import butterknife.Bind;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * 作者：liulei
 * 公司：希顿科技
 */
public class PersonFragment extends AbsBaseFragment{

//    @Bind(R.id.jni)
//    TextView jni;
//    @Bind(R.id.address)
//    TextView address;
    @Bind(R.id.listView)
    RecyclerView mRecyclerView;
    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.appbar_fuction)
    AppBarLayout mAppbarFuction;

    private Socket mSocket;
    private SensonListener mSensor;
    private List<DemoInfo> mTargetActs;
    private List<String>mDatas;

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        ((AbsBaseActivity)getActivity()).requestPermission(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, "打开定位权限", new AbsBaseActivity.GrantedResult() {
            @Override
            public void onResult(boolean granted) {
                if(granted){
//                    ToastUtil.showToast("已打开位置权限");
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
        initToolBar();
        App app = (App)getActivity().getApplication();
        mSocket = app.getSocket();

//        //初始化重力感应
//        mSensor = new SensonListener(getContext());
//        mSensor.setOnSensonChangeListener(this);

//        JniUtils helper = new JniUtils();
//        jni.setText("自定义jni："+"\n"+helper.getMyName());

        mSocket.on("login", onLogin);
        initData();
        initActs();

        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(manager);

        RecyclerAdapter adapter = new RecyclerAdapter(getActivity(),R.layout.list_item,mDatas) {
            @Override
            public void convert(RecyclerViewHolder hepler, Object o) {
                hepler.setText(R.id.text_content, (String) o);
            }
        };
        mRecyclerView.setAdapter(adapter);
        //设置自定义的分割线
//        mRecyclerView.addItemDecoration(new RecycleViewDivider(getActivity(),LinearLayoutManager.VERTICAL,10, getResources().getColor(R.color.text)));
//        mRecyclerView.addItemDecoration(new SpacesItemDecoration(2));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL));

        adapter.setOnItemClickListener(new RecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(ViewGroup parent, View view, Object o, int position) {
                if(position == 3){//聊天
                    String userName = "liulei"+ UUID.randomUUID().toString().substring(0,3);
                    Constants.user_name = userName;
                    mSocket.emit("add user", userName);
                    startActivity(mTargetActs.get(position).demoClass);
                }else if(position == 13){
                    ((AbsBaseActivity)getActivity()).requestPermission(new String[]{Manifest.permission.CAMERA}, "请求打开相机权限", new AbsBaseActivity.GrantedResult() {
                        @Override
                        public void onResult(boolean granted) {
                            if(granted){
                                startActivity(CameraAty.class);
                            }else {
                                ToastUtil.showToast("权限未被允许");
                                return;
                            }
                        }
                    });
                }else if(position == 14){
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
                }else {
                    startActivity(mTargetActs.get(position).demoClass);
                }
            }
            @Override
            public boolean onItemLongClick(ViewGroup parent, View view, Object o, int position) {
                return false;
            }
        });

    }

    //自己新添加的
    private void initToolBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mAppbarFuction.setPadding(
                    mAppbarFuction.getPaddingLeft(),
                    mAppbarFuction.getPaddingTop() + ScreenUtils.getStatusBarHeight(getActivity()),
                    mAppbarFuction.getPaddingRight(),
                    mAppbarFuction.getPaddingBottom());
        }
        toolbar.setBackgroundColor(StaticValue.color);
        if (toolbar != null) {
            ((TextView) toolbar.findViewById(com.android.core.R.id.toolbar_title)).setText("功能");
        }
    }

    private void initActs() {
        mTargetActs = new ArrayList<>();

        mTargetActs.add(new DemoInfo(LeftMenuActivity.class));
        mTargetActs.add(new DemoInfo(SwipBackActivity.class));
        mTargetActs.add(new DemoInfo(PersonActivity.class));
        mTargetActs.add(new DemoInfo(ChatActivity.class));
        mTargetActs.add(new DemoInfo(MediaPlayerActivtiy.class));
        mTargetActs.add(new DemoInfo(VideoChatActivity.class));
        mTargetActs.add(new DemoInfo(BloggerActivity.class));
        mTargetActs.add(new DemoInfo(QrViewActivity.class));
        mTargetActs.add(new DemoInfo(MusicModeActivity.class));
        mTargetActs.add(new DemoInfo(BigPhotoActivity.class));
        mTargetActs.add(new DemoInfo(FloatViewActivity.class));
        mTargetActs.add(new DemoInfo(DownloadManagerDemo.class));
        mTargetActs.add(new DemoInfo(ShareActivity.class));
        mTargetActs.add(new DemoInfo(CameraAty.class));
        mTargetActs.add(new DemoInfo(ScreenCopyActivity.class));
        mTargetActs.add(new DemoInfo(SQLActivity.class));
        mTargetActs.add(new DemoInfo(NotifyActivity.class));
        mTargetActs.add(new DemoInfo(PopWindowActivity.class));
        mTargetActs.add(new DemoInfo(DanmuActivity.class));
        mTargetActs.add(new DemoInfo(MusicActivity.class));
    }

    private List<String> initData(){
        mDatas = new ArrayList<>();
        mDatas.add("跳转到侧滑界面");
        mDatas.add("跳转到右滑关闭界面");
        mDatas.add("跳转到个人中心界面");
        mDatas.add("跳转到聊天界面");
        mDatas.add("跳转到播放器界面");
        mDatas.add("跳转到视频聊天界面");
        mDatas.add("跳转到我的博客");
        mDatas.add("跳转到二维码界面");
        mDatas.add("跳转到音乐播放器界面");
        mDatas.add("跳转到查看大图界面");
        mDatas.add("跳转到悬浮窗界面");
        mDatas.add("下载apk并安装");
        mDatas.add("跳转到分享界面");
        mDatas.add("自定义相机拍摄视频或图片");
        mDatas.add("截图");
        mDatas.add("模糊查询");
        mDatas.add("通知栏界面");
        mDatas.add("弹出窗界面");
        mDatas.add("弹幕界面");
        mDatas.add("跳转到登录界面");
        return mDatas;
    }

    //把每个activity转成class
    private static class DemoInfo {
        private final Class<? extends android.app.Activity> demoClass;

        public DemoInfo(Class<? extends android.app.Activity> demoClass) {
            this.demoClass = demoClass;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    boolean isSensor = false;//当前是否是重力感应状态
//    @OnClick(R.id.shark)
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
