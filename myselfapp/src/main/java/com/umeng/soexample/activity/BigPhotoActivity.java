package com.umeng.soexample.activity;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.MenuItem;
import android.view.animation.LinearInterpolator;

import com.android.core.StaticValue;
import com.android.core.base.AbsBaseActivity;
import com.bumptech.glide.Glide;
import com.umeng.soexample.R;
import com.umeng.soexample.custom.MDTintUtil;
import com.umeng.soexample.custom.PinchImageView;
import com.umeng.soexample.photoview.PhotoView;
import com.umeng.soexample.task.DownloadImageTask;
import com.heaton.liulei.utils.utils.OtherUtils;
import com.heaton.liulei.utils.utils.SPUtils;
import com.heaton.liulei.utils.utils.ToastUtil;
import java.io.File;
import butterknife.Bind;
import butterknife.OnClick;
import butterknife.OnLongClick;

/**
 * Created by liulei on 2016/5/31.
 */
public class BigPhotoActivity extends AbsBaseActivity {

    public static String URL = "http://img5.5usport.com/data/attachment/forum/201509/24/162334lmpgg0qjewjimjwg.png";

    @Bind(R.id.aviter)
    PinchImageView aviter;

    @Bind(R.id.fab_save)
    FloatingActionButton mFabSave;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_big_photo;
    }

    @Override
    protected void onInitView() {
        String path = getHeader();
        if(!path.equals("")){
            URL = path;
        }
        setTitle("头像");
        toolbar.setNavigationIcon(R.mipmap.abc_ic_ab_back_mtrl_am_alpha);
        MDTintUtil.setTint(mFabSave, StaticValue.color);
        Glide.with(getBaseContext()).load(URL).into(aviter);
    }

    private String getHeader(){
        String path = SPUtils.get(getApplication(),"header","");
        return path;
    }

    @OnLongClick(R.id.aviter)
    boolean aviter(){
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle("提示")
                .setMessage("确定下载并保存图片？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        try {
                            getImageURI(URL, OtherUtils.getCacheFile(),"iverson");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                alertDialog.getButton(android.support.v7.app.AlertDialog.BUTTON_POSITIVE).setTextColor(StaticValue.color);
                alertDialog.getButton(android.support.v7.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(StaticValue.color);
            }
        });
        alertDialog.show();
        return true;
    }

    private ObjectAnimator mAnimator;

    @OnClick(R.id.fab_save)
    void save(){
        mFabSave.setImageResource(R.drawable.ic_loading);
        mAnimator = ObjectAnimator.ofFloat(mFabSave, "rotation", 0, 360);
        mAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mAnimator.setDuration(800);
        mAnimator.setInterpolator(new LinearInterpolator());
        mAnimator.start();
        try {
            getImageURI(URL, OtherUtils.getCacheFile(),"iverson");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void getImageURI(final String path, File cache, String name) throws Exception {
        final File file = new File(cache, name);
        // 如果图片存在本地缓存目录，则不去服务器下载
        if (file.exists()) {
            //直接弹出保存成功了。这说明已经保存过了
            ToastUtil.showToast("图片已存在!");
            stopFabSavingAnim();
            Snackbar.make(mFabSave,"图片保存成功!", Snackbar.LENGTH_LONG).show();
        } else {
            // 从网络上获取图片
            new DownloadImageTask(file.getAbsolutePath(), new DownloadImageTask.DownloadFileCallback() {
                @Override
                public void beforeDownload() {
                    ToastUtil.showToast("正在下载图片...");
                }

                @Override
                public void downloadProgress(int progress) {
                }

                @Override
                public void afterDownload(Bitmap bitmap) {
                    ToastUtil.showToast("图片保存成功!");
                    stopFabSavingAnim();
                    Snackbar.make(mFabSave,"图片保存成功!", Snackbar.LENGTH_LONG).show();
                }
            }).execute(path);
        }
    }

    public void stopFabSavingAnim() {
        mFabSave.setImageResource(R.drawable.ic_meizi_save);
        mAnimator.cancel();
        mFabSave.setRotation(0);
    }

}
