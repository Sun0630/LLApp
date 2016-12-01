package com.heaton.liulei.utils.utils;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by liulei on 2016/5/6.
 */

/**
 * 其他一些重要的工具，统一写入该类中
 */
public class OtherUtils {

    /**
     * 隐藏华为等手机底部虚拟键
     * @param activity
     */
    public static void hideSystemUI(Activity activity) {
        // Set the IMMERSIVE flag.
        // Set the content to appear under the system bars so that the content
        // doesn't resize when the system bars hide and show.
        activity.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
    }
    /**
     * * 获取状态栏高度
     * * @return
     */
    public static int getStatusBarHeight(Activity activity) {
        Class<?> c = null;
        Object obj = null;
        java.lang.reflect.Field field = null;
        int x = 0;
        int statusBarHeight = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            statusBarHeight = activity.getResources().getDimensionPixelSize(x);
            return statusBarHeight;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return statusBarHeight;
    }

    public static int getActionBarHeight(Context context) {
//        int contentTop = activity.getWindow().findViewById(Window.ID_ANDROID_CONTENT).getTop();
//        //statusBarHeight是上面状态栏的高度
////        int titleBarHeight = contentTop - statusBarHeight;
//        return contentTop;
        int actionBarHeight = 0;
        TypedValue tv = new TypedValue();
        if (context.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, context.getResources().getDisplayMetrics());
        }
        return actionBarHeight;

    }

    public static final String APPS_ROOT_DIR = getExternalStorePath() + "/HEATON";

    //生成手机唯一码   部分山寨机可能有问题
    public static String getUniqueCode(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String deviceid = tm.getDeviceId();
        if (deviceid == null || deviceid.length() == 0) {
            WifiManager manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            if (manager != null) {
                deviceid = manager.getConnectionInfo().getMacAddress();
            }
        }
        return deviceid;
    }

    public static String paserTimeToYM(long time) {
        System.setProperty("user.timezone", "Asia/Shanghai");
        TimeZone tz = TimeZone.getTimeZone("Asia/Shanghai");
        TimeZone.setDefault(tz);
        SimpleDateFormat format = new SimpleDateFormat("MM月dd日");
        return format.format(new Date(time * 1000L));
    }

    public static String getTime(String time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
        SimpleDateFormat format = new SimpleDateFormat("MM月dd日");
//        long l = 0;
        Date date = null;
        try {
            date = sdf.parse(time);
//            l = date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return format.format(date.getTime());
    }

    public static int getWidth(Context context) {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();
        return width;
    }

    /**
     * 检测Sdcard是否存在
     *
     * @return
     */
    public static boolean isExitsSdcard() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * 判断SD卡是否存在
     *
     * @return
     */
    public static boolean isSDExsit() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }


    /**
     * 是否是合法的手机号
     *
     * @param mobiles
     * @return
     */
    public static boolean isMobileNO(String mobiles) {
        Pattern p = Pattern
                .compile("^(1[3-9][0-9])\\d{8}$");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }

    /**
     * 外置存储卡的路径
     *
     * @return
     */
    public static String getExternalStorePath() {
        if (isExistExternalStore()) {
            return Environment.getExternalStorageDirectory().getAbsolutePath();
        }
        return null;
    }

    /**
     * 是否有外存卡
     *
     * @return
     */
    public static boolean isExistExternalStore() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * 拷贝文件
     *
     * @param fileDir
     * @param fileName
     * @param buffer
     * @return
     */
    public static int copyFile(String fileDir, String fileName, byte[] buffer) {
        if (buffer == null) {
            return -2;
        }

        try {
            File file = new File(fileDir);
            if (!file.exists()) {
                file.mkdirs();
            }
            File resultFile = new File(file, fileName);
            if (!resultFile.exists()) {
                resultFile.createNewFile();
            }
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(
                    new FileOutputStream(resultFile, true));
            bufferedOutputStream.write(buffer);
            bufferedOutputStream.flush();
            bufferedOutputStream.close();
            return 0;

        } catch (Exception e) {
        }
        return -1;
    }

    /**
     * @param filePath
     * @param seek
     * @param length
     * @return
     */
    public static byte[] readFlieToByte(String filePath, int seek, int length) {
        if (TextUtils.isEmpty(filePath)) {
            return null;
        }
        File file = new File(filePath);
        if (!file.exists()) {
            return null;
        }
        if (length == -1) {
            length = (int) file.length();
        }

        try {
            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");
            byte[] bs = new byte[length];
            randomAccessFile.seek(seek);
            randomAccessFile.readFully(bs);
            randomAccessFile.close();
            return bs;
        } catch (Exception e) {
            e.printStackTrace();
//            LogUtil.e(LogUtil.getLogUtilsTag(FileUtils.class), "readFromFile : errMsg = " + e.getMessage());
            return null;
        }
    }

    /**
     * decode file length
     *
     * @param filePath
     * @return
     */
    public static int decodeFileLength(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return 0;
        }
        File file = new File(filePath);
        if (!file.exists()) {
            return 0;
        }
        return (int) file.length();
    }

    public static File getFile() {
        String name = DateFormat.format("yyyyMMdd_hhmmss",
                Calendar.getInstance(Locale.CHINA))
                + ".jpg";
        File file = new File(Environment.getExternalStorageDirectory() + "/heaton1/");
        if (!file.exists()) {
//            file.delete();
            file.mkdir();
        }
        File headerFile = new File(file, name);
        return headerFile;
    }

    public static File getCacheFile() {
        File cache = new File(Environment.getExternalStorageDirectory(), "cache");
        if (!cache.exists()) {
            cache.mkdirs();
        }
        return cache;
    }

    public static void downVoice(final String downloadUrl, final String path) {
        //创建文件夹 VideoCarVoice，在存储卡下
        final String fileName = getVoicePath(path);
        File file1 = new File(fileName);
        if (file1.exists()) {
            return ;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(downloadUrl);
                    //打开连接
                    URLConnection conn = url.openConnection();
                    //打开输入流
                    InputStream is = conn.getInputStream();
                    //获得长度
                    int contentLength = conn.getContentLength();
                    Log.e("Utils", "contentLength = " + contentLength);
//                    //创建文件夹 MyDownLoad，在存储卡下
//                    fileName = getVoicePath(path);
//                    final File file1 = new File(fileName);
//                    if (file1.exists()) {
//                        return ;
//                    }
                    //创建字节流
                    byte[] bs = new byte[1024];
                    int len;
                    OutputStream os = new FileOutputStream(fileName);
                    //写数据
                    while ((len = is.read(bs)) != -1) {
                        os.write(bs, 0, len);
                    }
                    //完成后关闭流
                    Log.e("Utils", "download-finish");
                    os.close();
                    is.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @NonNull
    public static String getVoicePath(String path) {
        String dirName = Environment.getExternalStorageDirectory() + "/VideoCarVoice/";
        File file = new File(dirName);
        //不存在创建
        if (!file.exists()) {
            file.mkdir();
        }
        //下载后的文件名
        String filePath = dirName + path+".amr";
        return filePath;
    }


    //切换后将EditText光标置于末尾
    public static void editToEnd(EditText editText) {
        Editable ea = editText.getText();
        editText.setSelection(ea.length());
    }

    //初始化editText光标
    public static void initFocus(EditText editText) {
        editToEnd(editText);
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
        editText.findFocus();
    }

    /**
     * 打开软键盘
     *
     * @param mEditText
     *            输入框
     * @param mContext
     *            上下文
     */
    public static void openKeybord(EditText mEditText, Context mContext)
    {
        InputMethodManager imm = (InputMethodManager) mContext
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(mEditText, InputMethodManager.RESULT_SHOWN);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,
                InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    /**
     * 关闭软键盘
     *
     * @param mEditText
     *            输入框
     * @param mContext
     *            上下文
     */
    public static void closeKeybord(EditText mEditText, Context mContext)
    {
        InputMethodManager imm = (InputMethodManager) mContext
                .getSystemService(Context.INPUT_METHOD_SERVICE);

        imm.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
    }

}
