package com.umeng.soexample.activity;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.android.core.base.AbsBaseActivity;
import com.google.gson.Gson;
import com.heaton.liulei.utils.utils.ToastUtil;
import com.heaton.liulei.utils.utils.WeakHandler;
import com.turing.androidsdk.TuringApiManager;
import com.umeng.soexample.Constants;
import com.umeng.soexample.bean.Message;
import com.umeng.soexample.R;
import com.umeng.soexample.adapter.RobotAdapter;
import com.umeng.soexample.bean.RobotVO;
import com.umeng.soexample.task.TaskExecutor;

import org.litepal.crud.DataSupport;
import org.litepal.exceptions.DataSupportException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;

public class ChatRobotActivity extends AbsBaseActivity {

    //我们刚才的获取的API地址:
    private static final String URL="http://www.tuling123.com/openapi/api";
    //我们刚才的获取的APIKey:
    private static final String APP_KAY="9d7aa2da2c3b4cfc9df31ea4a728ab7a";

    @Bind(R.id.robot_listView)
    RecyclerView mListView;
    @Bind(R.id.send)
    Button send;
    @Bind(R.id.edit)
    EditText editText;

    private TuringApiManager m;
    private RobotAdapter mAdapter;
    private List<Message> mMsgs =  new ArrayList<>();
    @SuppressLint("SimpleDateFormat")
    private SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//    private MessageHandler mHandler;
    private WebView web;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_chat_robot;
    }

//    public static class MessageHandler extends Handler{
//        private WeakReference<ChatRobotActivity>mActivityReference;
//
//        public MessageHandler(ChatRobotActivity activity){
//            mActivityReference = new WeakReference<ChatRobotActivity>(activity);
//        }
//
//        @Override
//        public void handleMessage(android.os.Message msg) {
//            //等待接收，子线程完成数据的返回
//            Message message = (Message) msg.obj;
//            ChatRobotActivity activity = mActivityReference.get();
//            if(activity == null){
//                return;
//            }
//            activity.addMessage(message);
//        }
//    }

//    public static com.umeng.soexample.utils.WeakHandler mHandler = new com.umeng.soexample.utils.WeakHandler(this){
//
//    @Override
//    public void handleMessage(android.os.Message msg) {
//        Message message = (Message) msg.obj;
//        ((ChatRobotActivity)this.getObj()).addMessage(message);
//    }
//};

    private WeakHandler mHandler = new WeakHandler(msg -> {
        Message message = (Message) msg.obj;
        addMessage(message);
        return false;
    });

//    private Handler mHandler = new Handler(){
//        @Override
//        public void dispatchMessage(android.os.Message msg) {
//            Message message = (Message) msg.obj;
//            addMessage(message);
//        }
//    };


    private void addMessage(Message message) {
        mMsgs.add(message);
        //保存到本地数据库
        message.save();
        mAdapter.notifyDataSetChanged();
        if(mAdapter.getItemCount() > 0){
            mListView.getLayoutManager().smoothScrollToPosition(mListView,null,mAdapter.getItemCount() - 1);
        }
    }

    @Override
    protected void onInitView() {
//        mHandler = new MessageHandler(this);
        setTitle("小艾");
        findDB();//查找数据库
//        init();
        send.setOnClickListener(v -> {
            String msg = editText.getText().toString().trim();
            if(msg.length()>0){
//                    m.requestTuringAPI(msg);
//                    Message message = new Message.Builder(Message.TYPE_TO_MESSAGE).message(msg).username("LiuLei").date(new Date()).build();
                Message message = new Message();
                message.setType(Constants.TYPE_TO_MESSAGE);
//                    message.setData(df.format(new Date()));
                message.setMessage(msg);
                message.setUsername("LiuLei");
                addMessage(message);
                editText.setText("");
                hideSoftKeyboard();
                TaskExecutor.executeTask(new Runnable() {
                    @Override
                    public void run() {
                        Message fromMessage = sendMessage(msg);
                        android.os.Message m1 = android.os.Message.obtain();
                        m1.obj = fromMessage;
                        mHandler.sendMessage(m1);
                    }
                });
            }else {
                ToastUtil.showToast("请输入文字");
            }
        });
        mAdapter = new RobotAdapter(this,mMsgs);
        mListView.setLayoutManager(new LinearLayoutManager(this));
        mListView.setAdapter(mAdapter);
        mListView.setOnTouchListener((v, event) -> {
            hideSoftKeyboard();
            return false;
        });

        initDatas();
    }

    private void findDB() {
        //查找数据库记录
        try {
            mMsgs = DataSupport.findAll(Message.class);
            Log.e(TAG,mMsgs.size()+"");
        } catch (DataSupportException e) {
            e.printStackTrace();
        }
    }

    private void initDatas() {
//        Message message = new Message.Builder(Message.TYPE_FROM_MESSAGE).message("你好，我是小艾！有什么能帮助你的吗？").username("小艾").date(new Date()).build();
        Message message = new Message();
        message.setType(Constants.TYPE_FROM_MESSAGE);
//        message.setData(df.format(new Date()));
        message.setMessage("你好，我是小艾！有什么能帮助你的吗？");
        message.setUsername("小艾");
//        addMessage(message);
        mMsgs.add(message);
        mAdapter.notifyDataSetChanged();
        if(mAdapter.getItemCount()>0){
            mListView.getLayoutManager().smoothScrollToPosition(mListView,null,mAdapter.getItemCount() - 1);
        }
    }

//    private void init() {
//        requestPermission(new String[]{Manifest.permission.READ_PHONE_STATE}, "打开权限", new GrantedResult() {
//            @Override
//            public void onResult(boolean granted) {
//                if (granted){
//                    /**
//                     *E 初始化图灵组件
//                     */
//                    SDKInitBuilder sdkInitBuilder = new SDKInitBuilder(ChatRobotActivity.this);
//                    sdkInitBuilder.context = ChatRobotActivity.this;
//                    sdkInitBuilder.setTuringKey("9d7aa2da2c3b4cfc9df31ea4a728ab7a");
//                    sdkInitBuilder.setSecret("7a96c9cc208c370a");
////        sdkInitBuilder.setUniqueId(Constant.UniqueId);
//                    SDKInit.init(sdkInitBuilder, initListener);
//                }else {
//                    ToastUtil.showToast("权限被禁止");
//                }
//            }
//        });
//    }
//
//     /*C  组件状态监听*/
//    InitListener initListener = new InitListener() {
//        @Override
//        public void onComplete() {
//
////                实例化TuringApiManager类
//            m = new TuringApiManager(App.getInstance());
//            m.setHttpListener(httpConnectionListener);
//            ToastUtil.showToast("初始化成功");
//        }
//
//        @Override
//        public void onFail(String s) {
//            ToastUtil.showToast("初始化失败");
////                setRecognize();
//        }
//    };
//
//    /*B  网络请求状态监听*/
//    final HttpConnectionListener httpConnectionListener = new HttpConnectionListener() {
//        @Override
//        public void onError(ErrorMessage errorMessage) {
//
//        }
//
//        @Override
//        public void onSuccess(RequestResult requestResult) {
////                TODO 在这里应该做解析和判断
//            String text = "";
//            String code = "";
//            String url = null;
//            JSONObject object = (JSONObject) requestResult.getContent();
//            try {
//                try {
//                    code = object.getString("code");
//                    if (code.equals("200000")) {// 解析图片和其他生活帮助等信息返回的页面
//                        url = object.getString("url");
////                    web.loadUrl(url);
//                    }
//                    text = object.getString("text");
//                    addMessage(text,Message.TYPE_FROM_MESSAGE);
//                } catch (org.json.JSONException e) {
//                    e.printStackTrace();
//                }
////                ttsManager.startTTS(text);// 合成 播报
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
//    };
//
//    private void addMessage(String text,int type) {
//        addMessage(new Message.Builder(type).message(text).username(type == Message.TYPE_TO_MESSAGE ?"Liulei":"小艾").build());
//    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_robot,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_robot:
                //清空聊天记录
                DataSupport.deleteAll(Message.class);
                mMsgs.clear();
                mAdapter.notifyDataSetChanged();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private Message sendMessage(String msg){
        Message message = new Message();
        String jsonResult = getJosn(msg);
        Gson gson = new Gson();
        RobotVO robotVO = null;
        robotVO = gson.fromJson(jsonResult,RobotVO.class);
//        message = new Message.Builder(Message.TYPE_FROM_MESSAGE).message(robotVO.getText()).username("小艾").date(new Date()).build();
        message.setType(Constants.TYPE_FROM_MESSAGE);
//        message.setData(df.format(new Date()));
        message.setMessage(robotVO.getText());
        message.setUsername("小艾");
        return message;
    }


    //发送请求并且获取返回JOSN
    public String getJosn(String msg)
    {
        String result = "";
        //获取发送的请求字符串
        String url = stitchingString(msg);
        ByteArrayOutputStream baos = null;
        InputStream is = null;
        try
        {
            java.net.URL urlNet = new java.net.URL(url);
            HttpURLConnection conn = (HttpURLConnection) urlNet
                    .openConnection();
            conn.setReadTimeout(5 * 1000);
            conn.setConnectTimeout(5 * 1000);
            conn.setRequestMethod("GET");
            is = conn.getInputStream();
            int len = -1;
            byte[] buf = new byte[128];
            baos = new ByteArrayOutputStream();
            while ((len = is.read(buf)) != -1)
            {
                baos.write(buf, 0, len);
            }
            baos.flush();
            result = new String(baos.toByteArray());
        } catch (MalformedURLException e)
        {
            e.printStackTrace();
        } catch (Exception e)
        {
            e.printStackTrace();
        } finally
        {
            try
            {
                if (baos != null)
                    baos.close();
            } catch (IOException e)
            {
                e.printStackTrace();
            }

            try
            {
                if (is != null)
                {
                    is.close();
                }
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        return result;
    }


    //拼接请求字符串并设置请求编码
    private static String stitchingString(String msg)
    {
        String url = "";
        try
        {
            url = URL + "?key=" + APP_KAY + "&info="
                    + URLEncoder.encode(msg, "UTF-8");
        } catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        Log.i("url++++++", "url: "+url);
        return url;
    }


}
