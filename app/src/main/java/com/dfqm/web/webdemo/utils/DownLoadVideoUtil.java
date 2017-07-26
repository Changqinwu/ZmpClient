package com.dfqm.web.webdemo.utils;

import android.content.Context;
import android.content.Intent;

import com.dfqm.web.webdemo.activity.DownloadActivity;
import com.dfqm.web.webdemo.constants.Constant;
import com.dfqm.web.webdemo.download.DownloadManager;

import org.xutils.ex.DbException;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Administrator on 2017/4/27.
 */

public class DownLoadVideoUtil {

    public void loadVideoListData(final Context context, final ArrayList<String> list, int type) {
        try {
            //清空本地视频
            FileUtils.RecursionDeleteFile(new File("/sdcard/zmpvideo/"));
            //准备下载
            xDownLoadVideo(context, list, type);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }


    private void xDownLoadVideo(Context context, ArrayList<String> list, int type) throws DbException {
        for (int i = 0; i < list.size(); i++) {
//            String url = et_url.getText().toString();
            String url = list.get(i);
            File file = new File(url);
            String name = file.getName();
//            String label = i + "xUtils_" + System.nanoTime();
            String label = String.valueOf(i);
            DownloadManager.getInstance().startDownload(
                    url, label,
                    "/sdcard/zmpvideo/" + url.toString(), true, false, null);
        }


        //跳转到下载任务列表页面
        Intent intent = new Intent(context, DownloadActivity.class);
        intent.putExtra(Constant.VERSION_TYPE, type);
        context.startActivity(intent);
    }

}
