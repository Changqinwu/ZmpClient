package com.dfqm.web.webdemo.tojs;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.webkit.JavascriptInterface;

import com.dfqm.web.webdemo.R;
import com.dfqm.web.webdemo.activity.FtPlayVideoActivity;
import com.dfqm.web.webdemo.constants.Constant;
import com.dfqm.web.webdemo.entity.EventMessageBean;
import com.dfqm.web.webdemo.utils.DownLoadVideoUtil;
import com.dfqm.web.webdemo.utils.SharedPreferencesUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.dfqm.web.webdemo.constants.Constant.CANNOT_CHANGE;

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

    //个人版播放webview轮播视频
    @JavascriptInterface
    public void changeActivity(String param, String url, int type) {
        //播放提示音
        toPlaySound();
        videoLists.clear();
        //如果是分天版，显示竖屏或者横屏二维码
        EventBus.getDefault().post(new EventMessageBean(String.valueOf(type), Constant.VERSION_TYPE));
        //保存版本type
        SharedPreferencesUtils.setInt(mContext, Constant.VERSION_TYPE, type);
        if ("1".equals(param)) {
            //关闭个人版视频界面
            Intent intent = new Intent(Constant.CLOSE_VIDEO);
            mContext.sendBroadcast(intent);
            //关闭下载界面
            Intent intent2 = new Intent(Constant.CLOSE_DOWNLOAD_VIDEO);
            mContext.sendBroadcast(intent2);
            //关闭分天版视频界面
            EventBus.getDefault().post(Constant.CLOSE_VIDEO);
            //分天版
            if (type == 2 && !url.equals("null")) {
                downloadVideo(url, type);
            }

        } else if ("2".equals(param)) {

            downloadVideo(url, type);

        }

//            ToastUtil.show(MainActivity.this, "列表数" + videoLists.size());
    }

    private void toPlaySound() {
        MediaPlayer mediaPlayer = MediaPlayer.create(mContext, R.raw.dd);
        mediaPlayer.start();
    }

    private void downloadVideo(String url, int type) {
        //关闭视频界面
        Intent intent = new Intent(Constant.CLOSE_VIDEO);
        mContext.sendBroadcast(intent);

        //发送广播，告诉后台正在进行加载视频，不能修改方案
        Intent intent2 = new Intent(CANNOT_CHANGE);
        mContext.sendBroadcast(intent2);

        //判读Url有没逗号字符,分离出单个Url
        int p = url.indexOf(",");
        if (p > 0) {
            String[] url_split = url.split(",");
            List<String> url_paths = Arrays.asList(url_split);
            videoLists.addAll(url_paths);
        } else {
            videoLists.add(url);
        }
        DownLoadVideoUtil loadVideoUtil = new DownLoadVideoUtil();
        loadVideoUtil.loadVideoListData(mContext, videoLists, type);
    }


    //分天版播放视频
    @JavascriptInterface
    public void toPlayVideo(String url_name, int playTime, String param) {
        if (url_name != null) {
//            ToastUtil.show(mContext,"显示："+url_name);
            Intent intent = new Intent(mContext, FtPlayVideoActivity.class);
            intent.putExtra(Constant.PLAY_VIDEO_NAME, url_name);
            intent.putExtra(Constant.PLAY_TIME, playTime);
            intent.putExtra(Constant.ADJUST_ONE_VIDEO, param);
            mContext.startActivity(intent);
        }
    }

}
