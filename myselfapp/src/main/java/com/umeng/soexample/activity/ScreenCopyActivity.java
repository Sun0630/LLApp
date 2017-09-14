package com.umeng.soexample.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.baronzhang.android.router.RouterInjector;
import com.baronzhang.android.router.annotation.inject.Inject;
import com.baronzhang.android.router.annotation.inject.InjectUriParam;
import com.bumptech.glide.Glide;
import com.heaton.liulei.utils.utils.FileOperateUtils;
import com.heaton.liulei.utils.utils.ImageUtils;
import com.heaton.liulei.utils.utils.LogUtils;
import com.umeng.soexample.MainActivity;
import com.umeng.soexample.R;
import com.heaton.liulei.utils.utils.ToastUtil;
import com.umeng.soexample.compress.Luban;
import com.umeng.soexample.compress.OnCompressListener;
import com.umeng.soexample.task.TaskExecutor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 作者：刘磊 on 2016/11/2 16:56
 * 公司：希顿科技
 */

public class ScreenCopyActivity extends Activity {

    private static final String TAG = "ScreenCopyActivity";

    private ImageView copy;
    private Button save;
    private float downx = 0;
    private float downy = 0;
    private float x = 0;
    private float y = 0;
    // 画笔
    private Paint paint;
    // 画布
    private Canvas canvas;
    // 缩放后的图片
    private Bitmap bitmap;
    // 缩放后的图片副本
    private Bitmap copyBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_copy);
        RouterInjector.inject(this);

        copy = (ImageView) findViewById(R.id.copy_img);
        save = (Button) findViewById(R.id.save);

        String shotDir = getIntent().getStringExtra("shotDir");
        if(shotDir != null && !TextUtils.isEmpty(shotDir)){
            bitmap = BitmapFactory.decodeFile(shotDir);
            // 创建缩放后的图片副本
            copyBitmap = Bitmap.createBitmap(bitmap.getWidth(),
                    bitmap.getHeight(), bitmap.getConfig());
            // 创建画布
            canvas = new Canvas(copyBitmap);
            // 创建画笔
            paint = new Paint();
            // 设置画笔颜色
            paint.setColor(Color.GREEN);
            // 设置画笔宽度
            paint.setStrokeWidth(10);
            // 开始作画，把原图的内容绘制在白纸上
            canvas.drawBitmap(bitmap, new Matrix(), paint);
            // 将处理后的图片放入imageview中
            copy.setImageBitmap(copyBitmap);
        }
        // 设置imageview监听
        copy.setOnTouchListener(new MyTouchListener());

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(copyBitmap!=null){
                    try {
                        //图片的名称跟系统时间有关
                        // getContentResolver().insert();源码中提到
                        //long startTime = SystemClock.uptimeMillis();
                        //Uri createdRow = provider.insert(mPackageName, url, values);
                        //long durationMillis = SystemClock.uptimeMillis() - startTime;
                        //maybeLogUpdateToEventLog(durationMillis, url, "insert", null /* where */);

//                        // 获取图库Uri路径
//                        Uri imageUri = getContentResolver().insert(
//                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new ContentValues());
//                        // 获取输出流
//                        OutputStream outputStream = getContentResolver()
//                                .openOutputStream(imageUri);
//                        // 将alterBitmap存入图库
//                        copyBitmap.compress(Bitmap.CompressFormat.PNG, 50, outputStream);
                        File file = FileOperateUtils.createFile(FileOperateUtils.CACHE_PATH + "/shot_creen/");
                        File shotFile = new File(file, FileOperateUtils.createFileNmae(".png"));
                        FileOutputStream out = new FileOutputStream(shotFile);
                        copyBitmap.compress(Bitmap.CompressFormat.PNG, 50, out);

                        if(bitmap!=null){
                            bitmap.recycle();
                            Log.d("ScreenCopyActivity", "bitmap is recycled");
                            System.gc();  //提醒系统及时回收
                        }
                        try {
                            out.flush();
                            out.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        ToastUtil.showToast("保存截图成功");
                        finish();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }

    public class MyTouchListener implements View.OnTouchListener {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int action = event.getAction();
            switch (action) {
                // 按下
                case MotionEvent.ACTION_DOWN:
                    downx = event.getX();
                    downy = event.getY();
                    break;
                // 移动
                case MotionEvent.ACTION_MOVE:
                    // 路径画板
                    x = event.getX();
                    y = event.getY();
                    // 画线
                    canvas.drawLine(downx, downy, x, y, paint);
                    // 刷新image
                    copy.invalidate();
                    downx = x;
                    downy = y;
                    break;
                case MotionEvent.ACTION_UP:
                    break;

                default:
                    break;
            }
            // true：告诉系统，这个触摸事件由我来处理
            // false：告诉系统，这个触摸事件我不处理，这时系统会把触摸事件传递给imageview的父节点
            return true;
        }

    }


}
