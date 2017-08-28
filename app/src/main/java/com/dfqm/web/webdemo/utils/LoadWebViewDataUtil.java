package com.dfqm.web.webdemo.utils;

import android.content.Context;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.tencent.smtt.export.external.interfaces.SslErrorHandler;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

/**
 * Created by Administrator on 2016/12/28.
 */

public class LoadWebViewDataUtil {

    private final com.tencent.smtt.sdk.WebView mWebView;
    private final Context context;
    private final ImageView mImaError;
    private final String Url;
    private final TextView mTvCount;
    private ProgressDialogUtil progressDialog;
    public boolean isLoadFaild;

    public LoadWebViewDataUtil(Context context, com.tencent.smtt.sdk.WebView mWebView, ImageView mImaError, String Url, TextView mTvCount) {
        this.context = context;
        this.mWebView = mWebView;
        this.mImaError = mImaError;
        this.Url = Url;
        this.mTvCount = mTvCount;
    }

    public void initData() {
        progressDialog = new ProgressDialogUtil();
        mWebView.clearHistory();
        mWebView.clearCache(true);
        mWebView.loadUrl(Url);
        com.tencent.smtt.sdk.WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);//设置js可以直接打开窗口，如window.open()，默认为false
        mWebView.getSettings().setSupportZoom(true);//是否可以缩放，默认true
        mWebView.getSettings().setBuiltInZoomControls(false);//是否显示缩放按钮，默认false
        mWebView.getSettings().setUseWideViewPort(true);//设置此属性，可任意比例缩放。大视图模式
        mWebView.getSettings().setLoadWithOverviewMode(true);//和setUseWideViewPort(true)一起解决网页自适应问题
        mWebView.getSettings().setAppCacheEnabled(false);//是否使用缓存
        mWebView.getSettings().setDomStorageEnabled(true);//DOM Storage
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(com.tencent.smtt.sdk.WebView view, int newProgress) {
                if (newProgress == 100) {
                    progressDialog.dismissProgressDialog();
                } else {
                    progressDialog.showProgressDialog(context, "请稍后...");
                }

                super.onProgressChanged(view, newProgress);
            }
        });

        mWebView.setWebViewClient(new WebViewClient() {


            @Override
            public void onPageFinished(com.tencent.smtt.sdk.WebView view, String url) {
                if (isLoadFaild) {
                    view.setVisibility(View.GONE);
                    mImaError.setVisibility(View.VISIBLE);
                    mTvCount.setVisibility(View.VISIBLE);
                } else {
                    view.setVisibility(View.VISIBLE);
                    mImaError.setVisibility(View.GONE);
                    mTvCount.setVisibility(View.GONE);
                }
                super.onPageFinished(view, url);
            }


            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                //显示网络错误图片
                view.setVisibility(View.GONE);
                mImaError.setVisibility(View.VISIBLE);
                mTvCount.setVisibility(View.VISIBLE);
                isLoadFaild = true;
//                ToastUtil.show(context, "网络出错了，10秒后尝试重新连接。。。");
//                handler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//
//                    }
//                }, 10000);
                startCountDownTime(5);
                super.onReceivedError(view, errorCode, description, failingUrl);
            }

            @Override
            public void onReceivedSslError(WebView webView, SslErrorHandler sslErrorHandler, com.tencent.smtt.export.external.interfaces.SslError sslError) {
                super.onReceivedSslError(webView, sslErrorHandler, sslError);
            }
        });




    }

    public void startCountDownTime(long time) {
        /**
         * 最简单的倒计时类，实现了官方的CountDownTimer类（没有特殊要求的话可以使用）
         * 即使退出activity，倒计时还能进行，因为是创建了后台的线程。
         * 有onTick，onFinsh、cancel和start方法
         */
        CountDownTimer timer = new CountDownTimer(time * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                //每隔countDownInterval秒会回调一次onTick()方法
                Log.e("倒计时",millisUntilFinished+"");
                mTvCount.setText("未连接到网络"+"\n"+"轻触左下角，设置网络"+"("+millisUntilFinished / 1000+")");
            }

            @Override
            public void onFinish() {
                isLoadFaild = false;
                initData();
            }
        };
        timer.start();// 开始计时
        //timer.cancel(); // 取消
    }

}
