package com.dfqm.web.webdemo.activity;



import android.content.BroadcastReceiver;
import android.content.Context;

import android.content.Intent;
import android.content.IntentFilter;

import android.os.CountDownTimer;

import android.os.Bundle;

import android.util.Log;

import android.view.View;
import android.webkit.JavascriptInterface;

import android.widget.ImageView;
import android.widget.TextView;


import com.dfqm.web.webdemo.API.zmpApi;
import com.dfqm.web.webdemo.R;
import com.dfqm.web.webdemo.constants.Constant;
import com.dfqm.web.webdemo.utils.DownLoadVideoUtil;
import com.dfqm.web.webdemo.utils.FileUtils;
import com.dfqm.web.webdemo.utils.LoadWebViewDataUtil;
import com.dfqm.web.webdemo.utils.ToastUtil;
import com.tencent.smtt.sdk.WebView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.dfqm.web.webdemo.constants.Constant.ACTION_MAIN;
import static com.dfqm.web.webdemo.constants.Constant.ACTION_SID;
import static com.dfqm.web.webdemo.constants.Constant.CANNOT_CHANGE;
import static com.dfqm.web.webdemo.constants.Constant.SID;


public class MainActivity extends BaseActivity implements View.OnClickListener {

    //退出app
    private ImageView mImaExit;
    //错误页面
    private ImageView mImaError;
    //倒计时
    private TextView mTvCount;
    private long exitTime = 0;
    private WebView mWebView;
    private View view;
    //打开本地视频列表
    private ImageView mImaOpenVideoList;
    private JavaScriptInterface JSInterface;
    private MyBroadcastRecevier recevier;
    //下载视频列表
    private ArrayList<String> videoLists = new ArrayList<>();
    private String uniquePsuedoID;
    private CountDownTimer timer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //获取设备唯一标识
        uniquePsuedoID = new FileUtils().getUniquePsuedoID();
        ToastUtil.show(this, "手机uniquePsuedoID:" + uniquePsuedoID);

        if (!"".equals(uniquePsuedoID)) {
            //初始化view
            initView();
            //获取设备状态信息
            initData(uniquePsuedoID);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        //注册广播
        recevier = new MyBroadcastRecevier();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constant.CHANGE);
        filter.addAction(CANNOT_CHANGE);
        filter.addAction(ACTION_SID);
        filter.addAction(ACTION_MAIN);
        registerReceiver(recevier, filter);
    }

    public void initData(String deviceId) {
        //更新qpp
        updateApp(deviceId);
    }


    private void initView() {
        mImaExit = (ImageView) findViewById(R.id.ima_exit_app);
        mWebView = (WebView) findViewById(R.id.webview);
        mImaError = (ImageView) findViewById(R.id.ima_web_page_error);
        mTvCount = (TextView) findViewById(R.id.tv_request_count_time);
        mImaOpenVideoList = (ImageView) findViewById(R.id.ima_open_videolist);
        view = findViewById(R.id.activity_main);
        mImaExit.setOnClickListener(this);
        mImaOpenVideoList.setOnClickListener(this);

    }

    private void loadWebViewData(String param) {
        //出错界面隐藏
        mImaError.setVisibility(View.GONE);
        mTvCount.setVisibility(View.GONE);
        //测试轮播图加参数ID的webview
//        LoadWebViewParamDataUtil mLoad = new LoadWebViewParamDataUtil(this, mWebView, mImaError, zmpApi.main_url, mTvCount, param);
        //测试轮播边放图片边放视频webview
        LoadWebViewDataUtil mLoad = new LoadWebViewDataUtil(this, mWebView, mImaError, zmpApi.main_url + param, mTvCount);
        mLoad.initData();

        // 设置js接口  第一个参数事件接口实例，第二个是实例在js中的别名，这个在js中会用到
        JSInterface = new JavaScriptInterface(this); ////------
        mWebView.addJavascriptInterface(JSInterface, "JSInterface");


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            //退出app弹窗
            case R.id.ima_exit_app:
                if ((System.currentTimeMillis() - exitTime) > 500) {
                    exitTime = System.currentTimeMillis();
                } else {
                    exitAppDialog(this);
                }
                break;
            //打开本地视频列表
            case R.id.ima_open_videolist:
                showSelecFileLists(this, this);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //防止内存溢出
        if (mWebView != null) {
            mWebView.destroy();
        }

        //注销广播
        unregisterReceiver(recevier);
    }

    class MyBroadcastRecevier extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Constant.CHANGE.equals(action)) {
                //关闭弹窗
                progressDialog.dismissProgressDialog();
                //能修改播放方案，参数0
                mWebView.loadUrl("javascript:androidJs(0)");

            } else if (Constant.CANNOT_CHANGE.equals(action)) {
                //不能修改播放方案，参数1
                mWebView.loadUrl("javascript:androidJs(1)");
            } else if (ACTION_SID.equals(action)) {
                //已授权过，显示轮播界面
                String sid = intent.getStringExtra(SID);
                ToastUtil.show(context, "SID:" + sid);
                //请求webview，显示轮播方案
                loadWebViewData(sid);
            } else if (ACTION_MAIN.equals(action)) {
//                //重新加载主界面
//                initData(uniquePsuedoID);
                //出错界面显示
                mImaError.setVisibility(View.VISIBLE);
                mTvCount.setVisibility(View.VISIBLE);
                //开始倒计时
                startCountDownTime(10);
            }

        }
    }

    //与H5页面交互类
    public class JavaScriptInterface {
        Context mContext;

        JavaScriptInterface(Context c) {
            mContext = c;
        }

        //播放webview轮播视频
        @JavascriptInterface
        public void changeActivity(String param, String url) {
            ToastUtil.show(MainActivity.this, "参数" + param);
            videoLists.clear();
            if ("1".equals(param)) {
                //关闭视频界面
                Intent intent = new Intent(Constant.CLOSE_VIDEO);
                sendBroadcast(intent);
                //关闭下载界面
                Intent intent2 = new Intent(Constant.CLOSE_DOWNLOAD_VIDEO);
                sendBroadcast(intent2);

            } else if ("2".equals(param)) {
                //下载视频
//                //关闭视频界面
                Intent intent = new Intent(Constant.CLOSE_VIDEO);
                sendBroadcast(intent);
                //判读有没逗号字符
                int p = url.indexOf(",");
                if (p > 0) {
                    String[] url_split = url.split(",");
                    List<String> url_paths = Arrays.asList(url_split);
                    videoLists.addAll(url_paths);
                } else {
                    videoLists.add(url);
                }
                DownLoadVideoUtil loadVideoUtil = new DownLoadVideoUtil();
                loadVideoUtil.loadVideoListData(MainActivity.this, videoLists);
            }
            ToastUtil.show(MainActivity.this, "列表数" + videoLists.size());
        }

    }

    //退出app弹窗
    public void exitAppDialog(final Context context) {
        dialogExit(context);
    }

    public void startCountDownTime(long time) {
        /**
         * 最简单的倒计时类，实现了官方的CountDownTimer类（没有特殊要求的话可以使用）
         * 即使退出activity，倒计时还能进行，因为是创建了后台的线程。
         * 有onTick，onFinsh、cancel和start方法
         */
        timer = new CountDownTimer(time * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                //每隔countDownInterval秒会回调一次onTick()方法
                Log.e("倒计时", millisUntilFinished + "");
                mTvCount.setText(millisUntilFinished / 1000 + "\n" + "秒后尝试重新连接~~~");
            }

            @Override
            public void onFinish() {
                initData(uniquePsuedoID);
            }
        };
        timer.start();// 开始计时
        //timer.cancel(); // 取消
    }

}
