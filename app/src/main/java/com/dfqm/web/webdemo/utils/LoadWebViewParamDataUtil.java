package com.dfqm.web.webdemo.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import android.widget.ImageView;
import android.widget.TextView;

import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;


/**
 * Created by Administrator on 2016/12/28.
 */

public class LoadWebViewParamDataUtil {

    private final WebView mWebView;
    private final Context context;
    private final ImageView mImaError;
    private final String Url;
    private final TextView mTvCount;
    private final String param;
    private ProgressDialogUtil progressDialog;
    public boolean isLoadFaild;



    public LoadWebViewParamDataUtil(Context context, WebView mWebView, ImageView mImaError, String Url, TextView mTvCount, String param) {
        this.context = context;
        this.mWebView = mWebView;
        this.mImaError = mImaError;
        this.Url = Url;
        this.mTvCount = mTvCount;
        this.param = param;
    }

    public void initData() {

        progressDialog = new ProgressDialogUtil();
        mWebView.loadUrl(Url+param);
        WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
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
            public void onPageFinished(WebView view, String url) {
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
                startCountDownTime(10);
                super.onReceivedError(view, errorCode, description, failingUrl);
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
                mTvCount.setText(millisUntilFinished/1000+"\n"+"秒后尝试重新连接~~~");
            }

            @Override
            public void onFinish() {
                isLoadFaild = false;
                initData();
            }
        };
        timer.start();// 开始计时
    }

}
