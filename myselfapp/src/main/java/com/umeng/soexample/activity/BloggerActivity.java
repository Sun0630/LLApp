package com.umeng.soexample.activity;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.android.core.StaticValue;
import com.android.core.base.AbsBaseActivity;
import com.heaton.liulei.utils.utils.OtherUtils;
import com.heaton.liulei.utils.utils.ScreenUtils;
import com.umeng.soexample.R;
import com.umeng.soexample.custom.ToShare;

import butterknife.Bind;

/**
 * Created by liulei on 2016/5/31.
 */
public class BloggerActivity extends AbsBaseActivity {

    public static final String GANK_URL = "me.bakumon.gank.module.webview.WebViewActivity.gank_url";
    public static final String GANK_TITLE = "me.bakumon.gank.module.webview.WebViewActivity.gank_title";

    public static final String csdn_URL = "http://blog.csdn.net/liulei823581722";

    @Bind(R.id.webView)
    WebView mWebView;
    @Bind(R.id.progressbar_webview)
    ProgressBar mProgressbar;
    @Bind(R.id.appbar)
    AppBarLayout mAppbar;

    private ProgressDialog dialog;

    private String url = "http://blog.csdn.net/liulei823581722";
    private String title = "博客";

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_blogger;
    }

    @Override
    protected void onInitView() {
        if(getIntent().getStringExtra(GANK_URL) != null){
            url = getIntent().getStringExtra(GANK_URL);
        }
        if(getIntent().getStringExtra(GANK_TITLE) != null){
            title = getIntent().getStringExtra(GANK_TITLE);
        }
//        initPressDialog();
        setTitle(title);
        toolbar.setNavigationIcon(R.drawable.ic_webview_finish);
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
        WebSettings settings = mWebView.getSettings();
        settings.setLoadWithOverviewMode(true);
        settings.setJavaScriptEnabled(true);
        settings.setAppCacheEnabled(true);
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        settings.setSupportZoom(true);

        mWebView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                mProgressbar.setVisibility(View.VISIBLE);
                mProgressbar.setProgress(newProgress);
            }
        });

        mWebView.setWebViewClient(new WebViewClient() {
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
//                dialog.dismiss();
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                Log.i("onPageStarted", url);
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                Log.i("onPageFinished", url);
//                dialog.dismiss();
                mProgressbar.setVisibility(View.GONE);
            }
        });
        mWebView.loadUrl(url);

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

        if (mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            finish();
        }

    }

    //android webview点击返回键返回上一个html
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && mWebView.canGoBack()) {
            mWebView.goBack();//返回前一个页面
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_webview,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            case R.id.menu_share:
                startActivity(ToShare.class);
                break;
            case R.id.menu_copy_link:
                if (OtherUtils.copyText(this,url)) {
                    Snackbar.make(mWebView, "链接复制成功", Snackbar.LENGTH_LONG).show();
                }
                break;
            case R.id.menu_open_with:
                OtherUtils.openWithBrowser(this, url);
                break;
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        mWebView.clearCache(true);
//        article_con_layout.removeView(mWeb);
        mWebView.destroy();
        super.onDestroy();
    }

}
