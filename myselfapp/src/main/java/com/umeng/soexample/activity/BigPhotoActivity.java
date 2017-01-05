package com.umeng.soexample.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.view.MenuItem;

import com.android.core.base.AbsBaseActivity;
import com.bumptech.glide.Glide;
import com.umeng.soexample.R;
import com.umeng.soexample.photoview.PhotoView;
import com.umeng.soexample.task.DownloadImageTask;
import com.heaton.liulei.utils.utils.OtherUtils;
import com.heaton.liulei.utils.utils.SPUtils;
import com.heaton.liulei.utils.utils.ToastUtil;
import java.io.File;
import butterknife.Bind;
import butterknife.OnLongClick;

/**
 * Created by liulei on 2016/5/31.
 */
public class BigPhotoActivity extends AbsBaseActivity {

    public static String URL = "http://img5.5usport.com/data/attachment/forum/201509/24/162334lmpgg0qjewjimjwg.png";

    @Bind(R.id.aviter)
    PhotoView aviter;

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
        alertDialog.show();
        return true;
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
                }
            }).execute(path);
        }
    }

}
