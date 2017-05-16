package com.dfqm.web.webdemo.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import com.dfqm.web.webdemo.activity.DownloadActivity;
import com.dfqm.web.webdemo.activity.PlayVideoActivity;
import com.dfqm.web.webdemo.constants.Constant;
import com.dfqm.web.webdemo.download.DownloadManager;
import com.dfqm.web.webdemo.entity.TextVideoBean;
import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;

import org.xutils.common.Callback;
import org.xutils.ex.DbException;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Request;

import static com.dfqm.web.webdemo.API.zmpApi.video_url;

/**
 * Created by Administrator on 2017/4/27.
 */

public class DownLoadVideoUtil {


    private  ProgressDialog progressDialog;
    private  ArrayList<String> videoLists = new ArrayList<>();
    private RequestParams params;
//
//    public void loadVideoListData(final Context context, final String downLoadUrl) {
//
//        ToastUtil.show(context, "正在准备视频...");
//        videoLists.clear();
//        progressDialog = new ProgressDialog(context);
//        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//        progressDialog.setTitle("正在准备视频");
//        progressDialog.setCancelable(false);
//        progressDialog.show();
//        OkHttpUtils.get()
//                .url(downLoadUrl)
//                .build()
//                .execute(new FileCallBack(Environment.getExternalStorageDirectory().getAbsolutePath(), "1.mp4") {
//                    @Override
//                    public void onError(Call call, Exception e, int id) {
//                        progressDialog.dismiss();
//                        ToastUtil.show(context,"出错了："+e.getMessage());
//                        //重新加载
//                        loadVideoListData(context,downLoadUrl);
//                    }
//                    @Override
//                    public void onResponse(File response, int id) {
//                        progressDialog.dismiss();
//                        videoLists.add(response.toString());
//                        //下载成功后跳转到播放视频界面
//                        Intent intent = new Intent(context, PlayVideoActivity.class);
//                        intent.putExtra(Constant.VIDEO_URL, videoLists);
//                        context.startActivity(intent);
//
//                    }
//                    @Override
//                    public void inProgress(float progress, long total, int id) {
//                       Log.e("进度",">>>>>>"+progress+">>>>"+total);
//                        progressDialog.setProgress((int) (progress*total));
//                        progressDialog.setMax((int) total);
//                    }
//                });
//    }

    public void loadVideoListData(final Context context, final ArrayList<String> list) {
        try {
            xDownLoadVideo(context,list);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }


    private void xDownLoadVideo(Context context,ArrayList<String> list) throws DbException {


        for (int i = 0; i < list.size(); i++) {
//            String url = et_url.getText().toString();
            String url = list.get(i);
//            String label = i + "xUtils_" + System.nanoTime();
            String label = String.valueOf(i);
            DownloadManager.getInstance().startDownload(
                    url, label,
                    "/sdcard/zmpvideo/" + i + ".mp4", true, false, null);
        }


        //跳转到下载任务列表页面
        Intent intent = new Intent(context, DownloadActivity.class);
        context.startActivity(intent);


    }

}
