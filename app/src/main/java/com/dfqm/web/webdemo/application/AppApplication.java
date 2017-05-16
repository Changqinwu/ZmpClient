package com.dfqm.web.webdemo.application;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import com.squareup.leakcanary.LeakCanary;
import com.umeng.analytics.MobclickAgent;
import com.uuzuche.lib_zxing.activity.ZXingLibrary;

import org.xutils.BuildConfig;
import org.xutils.x;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Administrator on 2017/1/9.
 */

public class AppApplication extends Application{

    private static AppApplication mAppApplication;
    private ArrayList<Activity> activityList;

    @Override
    public void onCreate() {
        super.onCreate();
        mAppApplication = this;
        //内存分析
        LeakCanary.install(this);
        //二维码初始化
        ZXingLibrary.initDisplayOpinion(this);

        //xutils3初始化
        x.Ext.init(this);
        // 是否输出debug日志, 开启debug会影响性能.
        x.Ext.setDebug(BuildConfig.DEBUG);
    }

    /** 获取Application */
    public static AppApplication getApp() {
        if (mAppApplication == null) {
            mAppApplication = new AppApplication();
        }
        return mAppApplication;
    }

    /** 添加当前Activity 到列表中 */
    public void addActivity(Activity acitivity) {
        if(activityList == null){
            activityList = new ArrayList<Activity>();
        }
        activityList.add(acitivity);
    }

    /** 清空列表，取消引用*/
    public void clearActivity(){
        activityList.clear();
    }

    /** 遍历退出所有Activity */
    public void exit() {
        for (Activity activity : activityList) {
            activity.finish();
        }
        clearActivity();//千万记得清空取消引用。
        System.exit(0);
    }

}
