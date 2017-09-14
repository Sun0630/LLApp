package com.android.core;

import android.app.Application;
import android.os.Build;
import android.os.StrictMode;
import com.android.core.control.logcat.BuildConfig;
import com.android.core.control.logcat.Logcat;
import com.android.core.utils.ThemeUtils;
import com.heaton.liulei.utils.utils.LLUtils;

/**
 * 作者： liulei
 * 公司：希顿科技
 */
public class BaseApplication extends Application {

    public static final String WX_APPID = "wx387b0430eb11c163";
    public static final String WX_APPSecret = "66a6edf9c1fb372e5d12d2d0f85532f5";

    private static BaseApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();

        //解决7.0  FileUriExposedException
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        }

        instance = this;
        StaticValue.color = ThemeUtils.getThemeColor(this);
        //开启debug模式，方便定位错误，具体错误检查方式可以查看http://dev.umeng.com/social/android/quick-integration的报错必看，正式发布，请关闭该模式
        BuildConfig.DEBUG = false;
        //初始化LiuleiUtils
        LLUtils.init(this);
//        //litepal的配置
//        LitePalApplication.initialize(this);
        //崩溃日志
        //注册crashHandler
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(this);
        //Android crash 上传服务器回掉  暂时注释
//        HttpReportCallback report = new HttpReportCallback() {
//            @Override
//            public void uploadException2remote(File file) {
//                //可以直接上传文件
//            }
//        };
//        AndroidCrash.getInstance().setCrashReporter(report).init(this);
        if (BuildConfig.DEBUG) {
            Logcat.init("com.android.racofix").hideThreadInfo().methodCount(3);
        }
        //检查程序哪里出现ANR异常
//        BlockLooper.initialize(new BlockLooper.Builder(this)
//        .setIgnoreDebugger(true)
//        .setReportAllThreadInfo(true)
//        .setSaveLog(true)
//        .setOnBlockListener(new BlockLooper.OnBlockListener() {
//            @Override
//            public void onBlock(BlockError blockError) {
//                blockError.printStackTrace();
//            }
//        })
//        .build());
//        BlockLooper.getBlockLooper().start();//启动检测
    }

    public static BaseApplication getInstance() {
        if (instance == null) {
            instance = new BaseApplication();
        }
        return instance;
    }


//    private static final ThreadLocal threadSession = new ThreadLocal();
//
//    public static Session getSession() throws InfrastructureException {
//        Session s = (Session) threadSession.get();
//        try {
//            if (s == null) {
//                s = getSessionFactory().openSession();
//                threadSession.set(s);
//            }
//        } catch (HibernateException ex) {
//            throw new InfrastructureException(ex);
//        }
//        return s;
//    }

}
