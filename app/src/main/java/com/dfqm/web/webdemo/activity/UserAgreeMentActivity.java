package com.dfqm.web.webdemo.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dfqm.web.webdemo.R;
import com.dfqm.web.webdemo.utils.LoadWebViewDataUtil;
import com.tencent.smtt.sdk.WebView;

import static com.dfqm.web.webdemo.zmpapi.ZmpApi.agreement_url;

public class UserAgreeMentActivity extends BaseActivity {


    private TextView mTvTitle;
    private ImageView mBack;
    private WebView mWebView;
    private ImageView mImaError;
    private TextView mTvCountTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_agree_ment);

        initView();
    }

    private void initView() {
        mTvTitle = (TextView) findViewById(R.id.tv_title);
        mBack = (ImageView) findViewById(R.id.ima_back);
        mWebView = (WebView) findViewById(R.id.wb);
        mImaError = (ImageView) findViewById(R.id.ima_web_page_error);
        mTvTitle.setText("使用协议");
        //返回
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        //加载地址
        mTvCountTime = (TextView) findViewById(R.id.tv_request_count_time);
        LoadWebViewDataUtil mLoad = new LoadWebViewDataUtil(this, mWebView, mImaError, agreement_url, mTvCountTime);
        mLoad.initData();

    }
}
