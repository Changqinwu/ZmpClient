package com.dfqm.web.webdemo.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.JavascriptInterface;

import android.widget.ImageView;

import android.widget.TextView;

import com.dfqm.web.webdemo.API.zmpApi;
import com.dfqm.web.webdemo.R;

import com.dfqm.web.webdemo.utils.LoadWebViewDataUtil;
import static com.dfqm.web.webdemo.constants.Constant.ACTION_SID;
import static com.dfqm.web.webdemo.constants.Constant.DEVICEID;
import static com.dfqm.web.webdemo.constants.Constant.SID;

public class QRcodeActivity extends BaseActivity implements View.OnClickListener {


    private com.tencent.smtt.sdk.WebView mWebView;
    private ImageView mImaExit;
    private ImageView mImaError;
    private TextView mTvCount;
    private String deviceId;
    private JavaScriptInterface JSInterface;
    private ImageView mImaOpenVideoList;
    private long exitTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);

        //初始化view
        initView();
        //显示二维码界面
        Intent intent = getIntent();
        if (intent != null) {
            deviceId = intent.getStringExtra(DEVICEID);
            initData(deviceId);
        }

    }

    private void initData(String deviceId) {
        LoadWebViewDataUtil mLoad = new LoadWebViewDataUtil(this, mWebView, mImaError, zmpApi.qr_url+deviceId, mTvCount);
        mLoad.initData();

        // 设置js接口  第一个参数事件接口实例，第二个是实例在js中的别名，这个在js中会用到
        JSInterface = new JavaScriptInterface(this); ////------
        mWebView.addJavascriptInterface(JSInterface, "JSInterface");

//        //给前端传deviceId
//        mWebView.loadUrl("javascript:androidJs("+"'"+deviceId+"'"+")");

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            //退出app弹窗
            case R.id.ima_exit_app:
                if ((System.currentTimeMillis() - exitTime) > 500) {
                    exitTime = System.currentTimeMillis();
                } else {
                    dialogExit(this);
                }
                break;
            //打开列表选择
            case R.id.ima_open_videolist:
                showSelecFileLists(this,this);
                break;
        }

    }

    public class JavaScriptInterface {
        Context mContext;

        JavaScriptInterface(Context c) {
            mContext = c;
        }

        //跳到主界面
        @JavascriptInterface
        public void changeActivity(String param,String sid) {

            if ("1".equals(param)) {
                //显示主界面
                Intent intent = new Intent(ACTION_SID);
                intent.putExtra(SID, sid);
                sendBroadcast(intent);
                finish();
            }
        }

    }


    private void initView() {
        mImaExit = (ImageView) findViewById(R.id.ima_exit_app);
        mWebView = (com.tencent.smtt.sdk.WebView) findViewById(R.id.webview);
        mImaError = (ImageView) findViewById(R.id.ima_web_page_error);
        mTvCount = (TextView) findViewById(R.id.tv_request_count_time);
        mImaExit = (ImageView) findViewById(R.id.ima_exit_app);
        mImaOpenVideoList = (ImageView) findViewById(R.id.ima_open_videolist);
        mImaExit.setOnClickListener(this);
        mImaOpenVideoList.setOnClickListener(this);
    }



}