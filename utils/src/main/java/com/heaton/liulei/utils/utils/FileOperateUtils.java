package com.heaton.liulei.utils.utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;

import com.heaton.liulei.utils.R;

import java.io.File;
import java.io.FilenameFilter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 作者：刘磊 on 2016/10/31 10:23
 * 公司：希顿科技
 */

public class FileOperateUtils {
    public final static String TAG = "FileOperateUtil";

    public final static int ROOT = 0;//根目录
    public final static int TYPE_IMAGE = 1;//图片
    public final static int TYPE_THUMBNAIL = 2;//缩略图
    public final static int TYPE_VIDEO = 3;//视频
    private Paint paint = new Paint();

    /**
     * 获取文件夹路径
     *
     * @param type     文件夹类别
     * @param rootPath 根目录文件夹名字 为业务流水号
     * @return
     */
    public static String getFolderPath(Context context, int type, String rootPath) {
        //本业务文件主目录
        StringBuilder pathBuilder = new StringBuilder();
        //添加应用存储路径
        pathBuilder.append(context.getExternalFilesDir(null).getAbsolutePath());
        pathBuilder.append(File.separator);
        //添加文件总目录
        pathBuilder.append(context.getString(R.string.Files));
        pathBuilder.append(File.separator);
        //添加当然文件类别的路径
        pathBuilder.append(rootPath);
        pathBuilder.append(File.separator);
        switch (type) {
            case TYPE_IMAGE:
                pathBuilder.append(context.getString(R.string.Image));
                break;
            case TYPE_VIDEO:
                pathBuilder.append(context.getString(R.string.Video));
                break;
            case TYPE_THUMBNAIL:
                pathBuilder.append(context.getString(R.string.Thumbnail));
                break;
            default:
                break;
        }
        return pathBuilder.toString();
    }

    /**
     * 获取目标文件夹内指定后缀名的文件数组,按照修改日期排序
     *
     * @param file      目标文件夹路径
     * @param format 指定后缀名
     * @param content   包含的内容,用以查找视频缩略图
     * @return
     */
    public static List<File> listFiles(String file, final String format, String content) {
        return listFiles(new File(file), format, content);
    }

    public static List<File> listFiles(String file, final String format) {
        return listFiles(new File(file), format, null);
    }

    /**
     * 获取目标文件夹内指定后缀名的文件数组,按照修改日期排序
     *
     * @param file      目标文件夹
     * @param extension 指定后缀名
     * @param content   包含的内容,用以查找视频缩略图
     * @return
     */
    public static List<File> listFiles(File file, final String extension, final String content) {
        File[] files = null;
        if (file == null || !file.exists() || !file.isDirectory())
            return null;
        files = file.listFiles(new FilenameFilter() {

            @Override
            public boolean accept(File arg0, String arg1) {
                // TODO Auto-generated method stub
                if (content == null || content.equals(""))
                    return arg1.endsWith(extension);
                else {
                    return arg1.contains(content) && arg1.endsWith(extension);
                }
            }
        });
        if (files != null) {
            List<File> list = new ArrayList<File>(Arrays.asList(files));
            sortList(list, false);
            return list;
        }
        return null;
    }

    /**
     * 根据修改时间为文件列表排序
     *
     * @param list 排序的文件列表
     * @param asc  是否升序排序 true为升序 false为降序
     */
    public static void sortList(List<File> list, final boolean asc) {
        //按修改日期排序
        Collections.sort(list, new Comparator<File>() {
            public int compare(File file, File newFile) {
                if (file.lastModified() > newFile.lastModified()) {
                    if (asc) {
                        return 1;
                    } else {
                        return -1;
                    }
                } else if (file.lastModified() == newFile.lastModified()) {
                    return 0;
                } else {
                    if (asc) {
                        return -1;
                    } else {
                        return 1;
                    }
                }

            }
        });
    }

    /**
     * @param extension 后缀名 如".jpg"
     * @return
     */
    public static String createFileNmae(String extension) {
        DateFormat format = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
        // 转换为字符串
        String formatDate = format.format(new Date());
        //查看是否带"."
        if (!extension.startsWith("."))
            extension = "." + extension;
        return formatDate + extension;
    }

    /**
     * 删除缩略图 同时删除源图或源视频
     *
     * @param thumbPath 缩略图路径
     * @return
     */
    public static boolean deleteThumbFile(String thumbPath, Context context) {
        boolean flag = false;

        File file = new File(thumbPath);
        if (!file.exists()) { // 文件不存在直接返回
            return flag;
        }

        flag = file.delete();
        //源文件路径
        String sourcePath = thumbPath.replace(context.getString(R.string.Thumbnail),
                context.getString(R.string.Image));
        file = new File(sourcePath);
        if (!file.exists()) { // 文件不存在直接返回
            return flag;
        }
        flag = file.delete();
        return flag;
    }

    /**
     * 删除源图或源视频 同时删除缩略图
     *
     * @param sourcePath 缩略图路径
     * @return
     */
    public static boolean deleteSourceFile(String sourcePath, Context context) {
        boolean flag = false;

        File file = new File(sourcePath);
        if (!file.exists()) { // 文件不存在直接返回
            return flag;
        }

        flag = file.delete();
        //缩略图文件路径
        String thumbPath = sourcePath.replace(context.getString(R.string.Image),
                context.getString(R.string.Thumbnail));
        file = new File(thumbPath);
        if (!file.exists()) { // 文件不存在直接返回
            return flag;
        }
        flag = file.delete();
        return flag;
    }

    // 获取指定Activity的截屏，保存到png文件
    public static Bitmap takeScreenShot(Activity activity) {
        // View是你需要截图的View
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap b1 = view.getDrawingCache();
        // 获取状态栏高度
        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;
        Log.i("linwb", "" + statusBarHeight);
        // 获取屏幕长和高
        int width = activity.getWindowManager().getDefaultDisplay().getWidth();
        int height = activity.getWindowManager().getDefaultDisplay()
                .getHeight();
        // 去掉标题栏
        // Bitmap b = Bitmap.createBitmap(b1, 0, 25, 320, 455);
        Bitmap b = Bitmap.createBitmap(b1, 0, statusBarHeight, width, width
                - statusBarHeight);
        view.destroyDrawingCache();
        return b;
    }

//    public static Bitmap convertViewToBitmap(View view) {
//        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
//        view.layout(0, 0, LviewUtil.getwidth(), LviewUtil.getwidth());
//        view.buildDrawingCache();
//        Bitmap bitmap = view.getDrawingCache();
//
//        return bitmap;
//    }

    /**
     * Try to return the absolute file path from the given Uri
     *
     * @param context
     * @param uri
     * @return the file path or null
     */
    public static String getRealFilePath(final Context context, final Uri uri) {
        if (null == uri) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null)
            data = uri.getPath();
        else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (index > -1) {
                        data = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        return data;
    }

    public static Bitmap getBitmapFromUri(Context context, Uri uri) {
        try {
            // 读取uri所在的图片
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Bitmap getViewBitmap(View v) {
        v.clearFocus();
        v.setPressed(false);

        boolean willNotCache = v.willNotCacheDrawing();
        v.setWillNotCacheDrawing(false);

        // Reset the drawing cache background color to fully transparent
        // for the duration of this operation
        int color = v.getDrawingCacheBackgroundColor();
        v.setDrawingCacheBackgroundColor(0);

        if (color != 0) {
            v.destroyDrawingCache();
        }
        v.buildDrawingCache();
        Bitmap cacheBitmap = v.getDrawingCache();
        if (cacheBitmap == null) {
            Log.e("TTTTTTTTActivity", "failed getViewBitmap(" + v + ")", new RuntimeException());
            return null;
        }

        Bitmap bitmap = Bitmap.createBitmap(cacheBitmap);

        // Restore the view
        v.destroyDrawingCache();
        v.setWillNotCacheDrawing(willNotCache);
        v.setDrawingCacheBackgroundColor(color);

        return bitmap;
    }

    /**
     * 合并两张bitmap为一张
     *
     * @param background
     * @param foreground
     * @return Bitmap
     */
    public static Bitmap combineBitmap(Bitmap background, Bitmap foreground) {
        if (background == null) {
            return null;
        }
        int bgWidth = background.getWidth();
        int bgHeight = background.getHeight();
        int fgWidth = foreground.getWidth();
        int fgHeight = foreground.getHeight();
        Bitmap newmap = Bitmap
                .createBitmap(bgWidth, bgHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(newmap);
        canvas.drawBitmap(background, 0, 0, null);
        canvas.drawBitmap(foreground, (bgWidth - fgWidth) / 2,
                +(bgHeight - fgHeight) / 2, null);
        canvas.save(Canvas.ALL_SAVE_FLAG);
        canvas.restore();
        return newmap;
    }


}
