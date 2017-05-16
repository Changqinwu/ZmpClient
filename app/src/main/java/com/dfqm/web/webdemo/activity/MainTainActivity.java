package com.dfqm.web.webdemo.activity;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.dfqm.web.webdemo.R;

public class MainTainActivity extends BaseActivity {

    private Handler handle = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_tain);

        //重新连接
        reConnect();
    }

    private void reConnect() {
        handle.postDelayed(new Runnable() {
            @Override
            public void run() {
                //更新qpp
                updateApp("");
            }
        },300000);
    }
}
