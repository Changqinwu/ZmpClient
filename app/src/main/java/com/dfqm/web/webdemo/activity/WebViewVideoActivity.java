package com.dfqm.web.webdemo.activity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;

import com.dfqm.web.webdemo.R;
import com.dfqm.web.webdemo.constants.Constant;

public class WebViewVideoActivity extends BaseActivity implements View.OnClickListener {

    private VideoView mVv;
    private Handler handler = new Handler();
    private String param;
    private ImageView mImaExit;
    private ImageView mImaOpenVideoList;
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            handler.removeCallbacks(mRunnable);
            mVv.pause();
            finish();
            overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view_video_layout);

        //获取参数播放不同视频
        Intent intent = getIntent();
        if (intent != null) {
            param = intent.getStringExtra(Constant.START_VIDEO_PARAM);
            //初始化view
            initView();
        }
    }

    private void initView() {

        mVv = (VideoView) findViewById(R.id.vidioview);
        mImaExit = (ImageView) findViewById(R.id.ima_exit_app);
        mImaOpenVideoList = (ImageView) findViewById(R.id.ima_open_videolist);
        mImaExit.setOnClickListener(this);
        mImaOpenVideoList.setOnClickListener(this);
        //拿到文件路径播放视频
//        mVideoUri = Uri.parse("/sdcard/MyVideo/"+param+".mp4");
//        mVv.setVideoURI(mVideoUri);
        mVv.setVideoPath("/sdcard/MyVideo/" + param + ".mp4");
        mVv.start();
        //16秒后关闭页面
        handler.postDelayed(mRunnable, 16000);


        mVv.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {

            }
        });

        mVv.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                finish();

                return false;
            }
        });


    }

    @Override
    protected void onStop() {
        super.onStop();
        mVv.pause();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            //退出app
            case R.id.ima_exit_app:
                dialogExit(this);
                break;
            //图片视频选择列表
            case R.id.ima_open_videolist:
                showSelecFileLists(this,this);
                break;

        }
    }
}



