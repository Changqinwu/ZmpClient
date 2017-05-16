package com.dfqm.web.webdemo.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;

import com.dfqm.web.webdemo.R;
import com.dfqm.web.webdemo.activity.MainActivity;
import com.dfqm.web.webdemo.activity.PictureListActivity;
import com.dfqm.web.webdemo.activity.VideoListActivity;

/**
 * Created by Administrator on 2017/5/15.
 */

public class SelectFolderUtils {

    public void showSelecFileLists(final Context context, final Activity activity){
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_select_dir_layout, null);
        final AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setTitle("请选择...")
                .setView(view)
                .create();
        alertDialog.show();

        //打开视频列表
        view.findViewById(R.id.tv_video).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                Intent intent = new Intent(context, VideoListActivity.class);
                context.startActivity(intent);
                activity.finish();
            }
        });

        //打开图片列表
        view.findViewById(R.id.tv_pic).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                Intent intent = new Intent(context, PictureListActivity.class);
                context.startActivity(intent);
                activity.finish();
            }
        });

        //退到主界面
        view.findViewById(R.id.tv_main).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                Intent intent = new Intent(context, MainActivity.class);
                context.startActivity(intent);
                activity.finish();
            }
        });

    }
}
