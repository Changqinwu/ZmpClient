package com.dfqm.web.webdemo.activity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;

import com.dfqm.web.webdemo.R;
import com.dfqm.web.webdemo.constants.Constant;
import com.dfqm.web.webdemo.utils.LoadWebViewDataUtil;
import com.dfqm.web.webdemo.utils.MD5Utils;
import com.dfqm.web.webdemo.utils.ToastUtil;
import com.pili.pldroid.player.PLMediaPlayer;
import com.pili.pldroid.player.widget.PLVideoTextureView;

import org.greenrobot.eventbus.EventBus;

import static com.dfqm.web.webdemo.constants.Constant.PLAY_VIDEO_ERROR;
import static com.dfqm.web.webdemo.constants.Constant.PLAY_VIDEO_FINISH;

public class WebViewVideoActivity extends BaseActivity implements View.OnClickListener {

    private PLVideoTextureView mVv;
    private String param;
    private ImageView mImaExit;
    private ImageView mImaOpenVideoList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view_video_layout);

        //初始化view
        initView();
        //获取数据
        initData();

    }

    private void initData() {
        //获取参数播放不同视频
        Intent intent = getIntent();
        if (intent != null) {
            param = intent.getStringExtra(Constant.START_VIDEO_PARAM);
        }

        //拿到文件路径播放视频
//        mVideoUri = Uri.parse("/sdcard/MyVideo/"+param+".mp4");
//        mVv.setVideoURI(mVideoUri);
        mVv.setVideoPath("/sdcard/MyVideo/" + param + ".mp4");

        //开始播放
        mVv.setOnPreparedListener(new PLMediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(PLMediaPlayer plMediaPlayer) {
                mVv.start();
            }
        });

        //播放完成
        mVv.setOnCompletionListener(new PLMediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(PLMediaPlayer plMediaPlayer) {
                //播放完成，通知前端播放下一个内容
                EventBus.getDefault().post(PLAY_VIDEO_FINISH);
                //关闭界面
                finish();

            }
        });

        //判断视频是否旋转了90度
        mVv.setOnInfoListener(new PLMediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(PLMediaPlayer plMediaPlayer, int what, int extra) {
                if (extra == 90) {
                    //如果视频角度是90度
                    //旋转视频
                    mVv.setDisplayOrientation(270);
                }
                return false;
            }
        });

        mVv.setOnErrorListener(new PLMediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(PLMediaPlayer plMediaPlayer, int i) {
                //视频出错，关闭界面，告诉前端播放下一个内容
                ToastUtil.show(WebViewVideoActivity.this,"视频出错了...");
                EventBus.getDefault().post(PLAY_VIDEO_ERROR);
                return false;
            }
        });



    }

    private void initView() {
        mVv = (PLVideoTextureView) findViewById(R.id.vidioview);
        mImaExit = (ImageView) findViewById(R.id.ima_exit_app);
        mImaOpenVideoList = (ImageView) findViewById(R.id.ima_open_videolist);
        mImaExit.setOnClickListener(this);
        mImaOpenVideoList.setOnClickListener(this);
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
                showSelecFileLists(this);
                break;

        }
    }
}



