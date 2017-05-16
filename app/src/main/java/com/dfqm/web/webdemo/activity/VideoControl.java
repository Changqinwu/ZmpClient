package com.dfqm.web.webdemo.activity;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;
import android.widget.VideoView;

import com.pili.pldroid.player.widget.PLVideoTextureView;

import java.util.List;

/**
 * Created by demo on 2017/4/1 0001
 */
public class VideoControl {

    private static final int GO_TIME = 15000;  //快进时间
    private static final int BACK_TIME = 5000; //快退时间
    private static int Video_index = 0;
    private PLVideoTextureView mVideoView;
    private List<String> datas;

    public void playVideo(Context context) {
        try {
            ++Video_index;
            if (Video_index >= datas.size()||Video_index<0) {
                Video_index = 0;
            }else if(datas.size()==0){
                Toast.makeText(context,"播放失败", Toast.LENGTH_LONG).show();
                return;
            }
            if (datas.get(Video_index) != null) {
                mVideoView.setVideoPath(datas.get(Video_index));
                mVideoView.start();
            }

        }catch (Exception e){
            Log.e("视频","视频播放异常"+e.toString());
            Intent intent = new Intent(context, MainActivity.class);
            context.startActivity(intent);
        }
    }

    /***
     * 暂停
     */
    public void pauseVideo() {
        if (mVideoView.isPlaying()) {
            mVideoView.pause();
        } else {
            mVideoView.start();
        }
    }


    /**
     * 下一个视频
     */
    public void nextVideo() {
        if (Video_index >= datas.size() - 1) {
            Video_index = -1;
        }
        mVideoView.setVideoPath(datas.get(Video_index + 1));
        ++Video_index;
    }


    /**
     * 上一个视频
     */
    public void lastVideo() {
        if (Video_index <= 0) {
            Video_index = datas.size();
        }
        mVideoView.setVideoPath(datas.get(Video_index - 1));
        --Video_index;
    }

    /**
     * 快进
     */
    public void slowPlay() {
        int pos = (int) mVideoView.getCurrentPosition();
        pos -=BACK_TIME; // milliseconds
        mVideoView.seekTo(pos);
    }

    /**
     * 快退
     */
    public void fastPlay() {
        int pos = (int) mVideoView.getCurrentPosition();
        pos += GO_TIME; // milliseconds
        mVideoView.seekTo(pos);
    }

    public void setData(List<String> data, PLVideoTextureView videoView) {
        this.mVideoView=videoView;
        this.datas=data;
    }
}
