package com.dfqm.web.webdemo.activity;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.dfqm.web.webdemo.R;

public class MainTainActivity extends BaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_tain);

        //重新连接
        reConnect();
    }

    private void reConnect() {

    }
}
