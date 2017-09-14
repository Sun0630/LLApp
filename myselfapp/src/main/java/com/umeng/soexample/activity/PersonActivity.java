package com.umeng.soexample.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.core.Help;
import com.android.core.StaticValue;
import com.android.core.base.AbsBaseActivity;
import com.android.core.control.statusbar.StatusBarUtil;
import com.bumptech.glide.Glide;
import com.heaton.liulei.utils.custom.RoundWhiteImageView;
import com.heaton.liulei.utils.utils.FileOperateUtils;
import com.heaton.liulei.utils.utils.ScreenUtils;
import com.umeng.soexample.AppConfig;
import com.umeng.soexample.R;
import com.umeng.soexample.custom.AppSelectPicsDialog;
import com.heaton.liulei.utils.custom.RoundImageView;
import com.heaton.liulei.utils.utils.SPUtils;
import com.heaton.liulei.utils.utils.ToastUtil;
import com.umeng.soexample.custom.DampView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by liulei on 2016/5/31.
 */
public class PersonActivity extends AppCompatActivity{

    @Bind(R.id.person_toolbar)
    Toolbar toolbar;
    @Bind(R.id.collapsing_toolbar_person)
    CollapsingToolbarLayout toolbarLayout;
    @Bind(R.id.aviter)
    RoundWhiteImageView aviter;

    private static final int CAMERA_REQUEST_CODE = 1;
    private static final int GALLERY_REQUEST_CODE = 2;
    private static final int CROP_REQUEST_CODE = 3;

    private Uri imageUri;
    private File mLocalFile;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        setContentView(R.layout.activity_person);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        toolbarLayout.setTitle("小艾");
        toolbarLayout.setContentScrimColor(StaticValue.color);

        //加载本地个人头像
        if(!getHeader().equals("")){
            Glide.with(this).load(getHeader()).into(aviter);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.aviter)
    void aviter(){
        startActivity(new Intent(PersonActivity.this,BigPhotoActivity.class));
    }

    @OnClick(R.id.layout_aviter)
    void layout_aviter(){
        AppSelectPicsDialog dialog = new AppSelectPicsDialog(this, R.style.translate_dialog);
        dialog.show();
        dialog.setDialogListner(new AppSelectPicsDialog.dialogListenner() {
            @Override
            public void setOnCameraLis(Dialog d, View v) {
                d.dismiss();
                takePicture();
            }

            @Override
            public void setOnGalleryLis(Dialog d, View v) {
                d.dismiss();
                selectPhoto();
            }

            @Override
            public void setOnCancelLis(Dialog d, View v) {
                d.dismiss();
            }
        });
    }

    private String getHeader(){
        return SPUtils.get(getApplication(),"header","");
    }

    /**
     * 调用系统相册
     */
    private void selectPhoto() {
        //将File对象转换为Uri并启动照相程序
        imageUri = Uri.fromFile(getFile());
        Intent intent;
        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image");
        } else {
//            intent = new Intent(Intent.ACTION_PICK,
//                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//            intent.setType("image");
            intent = new Intent();//19的用这个也可以
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
        }
        startActivityForResult(intent, GALLERY_REQUEST_CODE);
    }

    /**
     * 调用系统拍照功能
     */
    private void takePicture() {
        //将File对象转换为Uri并启动照相程序
        imageUri = Uri.fromFile(getFile());
//        imageUri = FileProvider.getUriForFile(this,"com.umeng.soexample.fileProvider",outputImage);//7.0新添加
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); //照相
//        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);//7.0新添加
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri); //指定图片输出地址
        startActivityForResult(intent, CAMERA_REQUEST_CODE); //启动照相
    }

    @NonNull
    private File getFile() {
        mLocalFile = FileOperateUtils.createFile(FileOperateUtils.CACHE_PATH, FileOperateUtils.createFileNmae(".jpg"));
        return mLocalFile;
    }

    /**
     * 将通过相机、图库得到的图片进行裁剪
     *
     * @param uri
     */
    private void startImageZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
//        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);//7.0新添加
//        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);//7.0新添加
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 480);
        intent.putExtra("outputY", 480);
        intent.putExtra("scale", true);
        intent.putExtra("scaleUpIfNeeded", true);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        intent.putExtra("return-data", false);
        startActivityForResult(intent, CROP_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case CAMERA_REQUEST_CODE://相机
                startImageZoom(imageUri);
                //广播刷新相册
                Intent intentBc = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                intentBc.setData(imageUri);
                this.sendBroadcast(intentBc);
                break;
            case GALLERY_REQUEST_CODE://图库
                if (data == null || data.getData() == null) {
                    return;
                }
                Uri uri = data.getData();
                startImageZoom(uri);
                break;
            case CROP_REQUEST_CODE:
                if (data == null) {
                    return;
                }
                //图片解析成Bitmap对象
                try {
                    Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                    if (bitmap == null) {
                        ToastUtil.showToast("请从相册进行选择");
                        return;
                    }
                    //保存头像路径至内存
                    SPUtils.put(getApplication(),"header",mLocalFile.getAbsolutePath());
                    Glide.with(this).load(getHeader()).into(aviter);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

}
