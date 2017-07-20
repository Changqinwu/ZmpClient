package com.dfqm.web.webdemo.JsClass;

import android.content.Context;
import android.content.Intent;
import android.webkit.JavascriptInterface;

import com.dfqm.web.webdemo.activity.MainActivity;
import com.dfqm.web.webdemo.constants.Constant;
import com.dfqm.web.webdemo.utils.DownLoadVideoUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Administrator on 2017/7/18.
 */

public class ToJavaScriptInterface {
    private final ArrayList<String> videoLists;
    Context mContext;

    public ToJavaScriptInterface(Context c, ArrayList<String> videoLists) {
        mContext = c;
        this.videoLists = videoLists;
    }

    //播放webview轮播视频
    @JavascriptInterface
    public void changeActivity(String param, String url) {
//            ToastUtil.show(MainActivity.this, "参数" + param);
        videoLists.clear();
        if ("1".equals(param)) {
            //关闭视频界面
            Intent intent = new Intent(Constant.CLOSE_VIDEO);
            mContext.sendBroadcast(intent);
            //关闭下载界面
            Intent intent2 = new Intent(Constant.CLOSE_DOWNLOAD_VIDEO);
            mContext.sendBroadcast(intent2);

        } else if ("2".equals(param)) {
            //下载视频
            //关闭视频界面
            Intent intent = new Intent(Constant.CLOSE_VIDEO);
            mContext.sendBroadcast(intent);
            //判读有没逗号字符
            int p = url.indexOf(",");
            if (p > 0) {
                String[] url_split = url.split(",");
                List<String> url_paths = Arrays.asList(url_split);
                videoLists.addAll(url_paths);
            } else {
                videoLists.add(url);
            }
            DownLoadVideoUtil loadVideoUtil = new DownLoadVideoUtil();
            loadVideoUtil.loadVideoListData(mContext, videoLists);
        }
//            ToastUtil.show(MainActivity.this, "列表数" + videoLists.size());
    }
}
