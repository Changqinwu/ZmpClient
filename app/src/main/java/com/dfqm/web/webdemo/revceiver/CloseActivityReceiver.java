package com.dfqm.web.webdemo.revceiver;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.dfqm.web.webdemo.constants.Constant;

/**
 * Created by Administrator on 2017/4/28.
 */

public class CloseActivityReceiver extends BroadcastReceiver{

    private final Activity activity;

    public CloseActivityReceiver(Activity activity){
        this.activity =activity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (Constant.CLOSE_VIDEO == action) {
            //关闭页面
//            activity.finish();
        }
    }
}
