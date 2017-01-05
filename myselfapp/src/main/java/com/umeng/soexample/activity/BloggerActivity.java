package com.umeng.soexample.activity;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.android.core.base.AbsBaseActivity;
import com.umeng.soexample.R;

import butterknife.Bind;

/**
 * Created by liulei on 2016/5/31.
 */
public class BloggerActivity extends AbsBaseActivity {

    public static final String csdn_URL = "http://blog.csdn.net/liulei823581722";

    @Bind(R.id.webView)
    WebView webView;
    private ProgressDialog dialog;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_blogger;
    }

    @Override
    protected void onInitView() {
        initPressDialog();
        setTitle("博客");
        toolbar.setNavigationIcon(R.mipmap.abc_ic_ab_back_mtrl_am_alpha);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        initWebView();
    }

    private void initWebView() {
//        webView.loadDataWithBaseURL(null, Goods_Url, "text/html", "utf-8", null);
        webView.getSettings().setJavaScriptEnabled(true);

        webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if(newProgress > 20){
                    dialog.dismiss();
                }
            }
        });

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // TODO Auto-generated method stub
                Log.i("webview", "url = " + url);
//                return true;
                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
//                super.onReceivedSslError(view, handler, error);
                handler.proceed();
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                //这里进行无网络或错误处理，具体可以根据errorCode的值进行判断，做跟详细的处理。
//                view.loadUrl(file:///android_asset/error.html );
                dialog.dismiss();
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                Log.i("onPageStarted", url);
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                Log.i("onPageFinished", url);
                super.onPageFinished(view, url);
                dialog.dismiss();
            }
        });
        webView.loadUrl(csdn_URL);

//        dialog.cancel();
//// 设置可以支持缩放
//        webView.getSettings().setSupportZoom(true);
//// 设置出现缩放工具
//        webView.getSettings().setBuiltInZoomControls(true);
////扩大比例的缩放
//        webView.getSettings().setUseWideViewPort(true);
////自适应屏幕
//        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
//        webView.getSettings().setLoadWithOverviewMode(true);
//        webView.loadUrl(Goods_Url);
//        webView.loadDataWithBaseURL(null, null, Goods_Url, "utf-8", null);

    }

    private void initPressDialog() {
        dialog = new ProgressDialog(this);
// 设置mProgressDialog风格
        dialog.setProgress(ProgressDialog.STYLE_SPINNER);//圆形
// 设置mProgressDialog标题
        dialog.setMessage("加载中,请稍后...");
        Window window = dialog.getWindow();
        //设置背景透明度
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.alpha = 1.0f;
        lp.dimAmount = 1.0f;// 黑暗度
        window.setAttributes(lp);
// 设置mProgressDialog提示
//        mProgressDialog.setMessage("这是一个圆形进度条对话框");
// 设置mProgressDialog进度条的图标
//        mProgressDialog.setIcon(R.mipmap.ic);
// 设置mProgressDialog的进度条是否不明确
//不滚动时，当前值在最小和最大值之间移动，一般在进行一些无法确定操作时间的任务时作为提示，明确时就是根据你的进度可以设置现在的进度值
//        mProgressDialog.setIndeterminate(false);
//    mProgressDialog.setProgress(m_count++);
// 是否可以按回退键取消
        dialog.setCancelable(true);
        dialog.show();

    }

    @Override
    public void onBackPressed() {

        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            finish();
        }

    }

    //android webview点击返回键返回上一个html
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
            webView.goBack();//返回前一个页面
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        webView.clearCache(true);
//        article_con_layout.removeView(mWeb);
        webView.destroy();
        super.onDestroy();
    }

}
