package com.umeng.soexample.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.heaton.liulei.utils.utils.FileOperateUtils;
import com.heaton.liulei.utils.utils.ImageUtils;
import com.heaton.liulei.utils.utils.ToastUtil;
import com.umeng.soexample.R;
import com.umeng.soexample.compress.Luban;
import com.umeng.soexample.compress.OnCompressListener;

import java.io.File;
import java.util.ArrayList;

import me.iwf.photopicker.PhotoPicker;

public class CompressImgActivity extends AppCompatActivity {

    private TextView fileSize;
    private TextView imageSize;
    private TextView thumbFileSize;
    private TextView thumbImageSize;
    private ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compress_img);

        initView();
    }

    private void initView() {
        fileSize = (TextView) findViewById(R.id.file_size);
        imageSize = (TextView) findViewById(R.id.image_size);
        thumbFileSize = (TextView) findViewById(R.id.thumb_file_size);
        thumbImageSize = (TextView) findViewById(R.id.thumb_image_size);
        image = (ImageView) findViewById(R.id.image);

        Button fab = (Button) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PhotoPicker.builder()
                        .setPhotoCount(5)
                        .setShowCamera(true)
                        .setShowGif(true)
                        .setPreviewEnabled(false)
                        .start(CompressImgActivity.this, PhotoPicker.REQUEST_CODE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == PhotoPicker.REQUEST_CODE) {
            if (data != null) {
                ArrayList<String> photos = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);

                File imgFile = new File(photos.get(0));
                fileSize.setText(imgFile.length() / 1024 + "k");
                imageSize.setText(ImageUtils.computeSize(imgFile)[0] + "*" + ImageUtils.computeSize(imgFile)[1]);

                for (String photo : photos) {
//                    compressWithRx(new File(photo));
                    compressWithLs(new File(photo));
                }
            }
        }
    }

    /**
     * 压缩单张图片 Listener 方式
     */
    private void compressWithLs(File file) {
        Luban.with(this)
                .load(file)
                .setCompressListener(new OnCompressListener() {
                    @Override
                    public void onStart() {
                        ToastUtil.showToast("I'm start");
                    }

                    @Override
                    public void onSuccess(File file) {
                        Log.i("path", file.getAbsolutePath());

                        Glide.with(CompressImgActivity.this).load(file).into(image);

                        thumbFileSize.setText(file.length() / 1024 + "k");
                        thumbImageSize.setText(ImageUtils.computeSize(file)[0] + "*" + ImageUtils.computeSize(file)[1]);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                }).launch();
    }

//    private void compressWithRx(File file) {
//        Flowable.just(file)
//                .observeOn(Schedulers.io())
//                .map(new Function<File, File>() {
//                    @Override public File apply(@NonNull File file) throws Exception {
//                        return Luban.with(MainActivity.this).load(file).get();
//                    }
//                })
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Consumer<File>() {
//                    @Override public void accept(@NonNull File file) throws Exception {
//                        Log.d(TAG, file.getAbsolutePath());
//
//                        Glide.with(MainActivity.this).load(file).into(image);
//
//                        thumbFileSize.setText(file.length() / 1024 + "k");
//                        thumbImageSize.setText(computeSize(file)[0] + "*" + computeSize(file)[1]);
//                    }
//                });
//    }

}
