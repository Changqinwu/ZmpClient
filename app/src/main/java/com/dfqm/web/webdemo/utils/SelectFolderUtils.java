package com.dfqm.web.webdemo.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;

import com.dfqm.web.webdemo.R;
import com.dfqm.web.webdemo.activity.MainActivity;
import com.dfqm.web.webdemo.activity.UsbPictureListActivity;
import com.dfqm.web.webdemo.activity.UsbVideoListActivity;
import com.dfqm.web.webdemo.activity.WifiSetActivity;
import com.dfqm.web.webdemo.application.AppApplication;

/**
 * Created by Administrator on 2017/5/15.
 */

public class SelectFolderUtils {

    public static void showSelecFileLists(final Activity activity){
        View view = LayoutInflater.from(activity).inflate(R.layout.dialog_select_dir_layout, null);
        final AlertDialog alertDialog = new AlertDialog.Builder(activity)
                .setTitle("请选择...")
                .setView(view)
                .create();
        alertDialog.show();

        //打开视频列表
        view.findViewById(R.id.tv_video).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                Intent intent = new Intent(activity, UsbVideoListActivity.class);
                activity.startActivity(intent);
                activity.finish();
            }
        });

        //打开图片列表
        view.findViewById(R.id.tv_pic).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                Intent intent = new Intent(activity, UsbPictureListActivity.class);
                activity.startActivity(intent);
                activity.finish();

            }
        });

        //退到主界面
        view.findViewById(R.id.tv_main).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                Intent intent = new Intent(activity, MainActivity.class);
                activity.startActivity(intent);
                activity.finish();
            }
        });

        //设置网络
        view.findViewById(R.id.tv_wifi).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                Intent intent = new Intent(activity, WifiSetActivity.class);
                activity.startActivity(intent);
            }
        });

    }
}
