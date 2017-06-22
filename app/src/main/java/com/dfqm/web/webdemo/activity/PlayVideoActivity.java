package com.dfqm.web.webdemo.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.dfqm.web.webdemo.R;
import com.dfqm.web.webdemo.constants.Constant;
import com.pili.pldroid.player.PLMediaPlayer;
import com.pili.pldroid.player.widget.PLVideoTextureView;

import java.util.ArrayList;

public class PlayVideoActivity extends BaseActivity implements View.OnClickListener {

    private PLVideoTextureView videoView;
    private VideoControl videoControl;
    private ArrayList<String> play_path = new ArrayList<>();
    private ImageView mImaExit;
    private long exitTime;
    private ImageView mImaOpenVideoList;
    private PlayVideoBroadcastreceiver receiver;
    private int mVideoRotation;
    private ImageView mImaExitRightApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        initView();
        //播放视频
        palyVideo();


    }

    @Override
    protected void onStart() {
        super.onStart();
        receiver = new PlayVideoBroadcastreceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constant.CLOSE_VIDEO);
        registerReceiver(receiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
        //关闭viedview,停止播放视频
        videoView.stopPlayback();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void palyVideo() {
        //获得选中视频列表
        final Intent intent = getIntent();
        play_path.clear();
        ArrayList<String> path = intent.getStringArrayListExtra(Constant.VIDEO_URL);
        play_path.addAll(path);
        //调用videoview控制器
        videoControl = new VideoControl();
        videoControl.setData(play_path, videoView);
        videoView.setVideoPath(path.get(0));


        videoView.setOnPreparedListener(new PLMediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(PLMediaPlayer plMediaPlayer) {
                //开始播放
                plMediaPlayer.start();
            }
        });



        videoView.setOnInfoListener(new PLMediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(PLMediaPlayer plMediaPlayer, int what, int extra) {

                int displayOrientation = videoView.getDisplayOrientation();
                Log.i("extra:", extra + "");
                Log.i("displayOrientation:", displayOrientation + "");
                if (extra == 90) {
                    //如果视频角度是90度
                    //旋转视频
                    videoView.setDisplayOrientation(270);
                }

                return false;
            }
        });

        videoView.setOnVideoSizeChangedListener(new PLMediaPlayer.OnVideoSizeChangedListener() {
            @Override
            public void onVideoSizeChanged(PLMediaPlayer plMediaPlayer, int width, int height, int i2, int i3) {
////                if (width > height&&mVideoRotation==0) {
////                    //旋转方向
////                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
////                }
//                    //如果视频角度是90度
//                    if(mVideoRotation == 90)
//                    {
//                        //旋转视频
//                        videoView.setDisplayOrientation(270);
//                    }

            }

        });

        videoView.setOnCompletionListener(new PLMediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(PLMediaPlayer plMediaPlayer) {
                //初始化旋转视频
                videoView.setDisplayOrientation(0);

                //循环播放
                videoControl.playVideo(PlayVideoActivity.this);
            }
        });

        videoView.setOnErrorListener(new PLMediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(PLMediaPlayer plMediaPlayer, int i) {
                Toast.makeText(PlayVideoActivity.this, "播放出错了...", Toast.LENGTH_SHORT).show();

                //播放出错跳转到主界面
                Intent intent = new Intent(PlayVideoActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                return true;
            }
        });


    }

    private void initView() {
        videoView = (PLVideoTextureView) findViewById(R.id.videoview);
        mImaExit = (ImageView) findViewById(R.id.ima_exit_app);
        mImaOpenVideoList = (ImageView) findViewById(R.id.ima_open_videolist);
        mImaExitRightApp = (ImageView) findViewById(R.id.ima_rignt_top_exit_app);
        mImaExitRightApp.setOnClickListener(this);
        mImaExit.setOnClickListener(this);
        mImaOpenVideoList.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            //退出app弹窗
            case R.id.ima_exit_app:
                exitAppDialog();
                break;
            //打开列表选择
            case R.id.ima_open_videolist:
                showSelecFileLists(this,this);
                break;
            //右上角退出app
            case R.id.ima_rignt_top_exit_app:
                exitAppDialog();
                break;
        }
    }

    private void exitAppDialog() {
        if ((System.currentTimeMillis() - exitTime) > 500) {
            exitTime = System.currentTimeMillis();
        } else {
            dialogExit(this);
        }
    }

    class PlayVideoBroadcastreceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Constant.CLOSE_VIDEO == action) {
                videoView.pause();
                PlayVideoActivity.this.finish();
            }
        }
    }
}
