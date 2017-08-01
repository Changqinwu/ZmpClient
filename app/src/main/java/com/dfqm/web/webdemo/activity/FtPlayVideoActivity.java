package com.dfqm.web.webdemo.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.dfqm.web.webdemo.R;
import com.dfqm.web.webdemo.constants.Constant;
import com.dfqm.web.webdemo.entity.EventMessageBean;
import com.dfqm.web.webdemo.utils.SelectFolderUtils;
import com.dfqm.web.webdemo.utils.SharedPreferencesUtils;
import com.dfqm.web.webdemo.utils.ToastUtil;
import com.pili.pldroid.player.PLMediaPlayer;
import com.pili.pldroid.player.widget.PLVideoTextureView;
import com.tencent.smtt.sdk.WebView;


public class FtPlayVideoActivity extends BaseActivity implements View.OnClickListener {

    private PLVideoTextureView mVv;
    private String play_video_name;
    private ImageView mImaExit;
    private ImageView mImaOpenVideoList;
    private Integer play_time;
    private String param;
    private RelativeLayout mRlVertical;
    private RelativeLayout mRlHorizontal;
    private WebView mWvVertical;
    private WebView mWvHorizontal;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view_video_layout);

        //初始化view
        initView();
        //获取数据
        initData();

    }

//    //接受eventbus的消息
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onMessageEvent(EventMessageBean eventMessageBean) {
//
//        String msgId = eventMessageBean.getMsgId();
//       if (screenDirection && msgId.equals("2")) {
//            //横屏并且是分天版
//            mRlVertical.setVisibility(View.VISIBLE);
//            mRlHorizontal.setVisibility(View.GONE);
//            //设置wenview
//            setWebview(mWvVertical);
////            ToastUtil.show(MainActivity.this,"横屏二维码显示");
//
//        }else if (!screenDirection && msgId.equals("2")) {
//            //竖屏并且是分天版
//            mRlVertical.setVisibility(View.GONE);
//            mRlHorizontal.setVisibility(View.VISIBLE);
//            //设置wenview
//            setWebview(mWvHorizontal);
////            ToastUtil.show(MainActivity.this,"竖屏二维码显示");
//        }else if (msgId.equals(Constant.CLOSE_VIDEO)) {
//           //关闭界面
//           FtPlayVideoActivity.this.finish();
//           if (mVv != null) {
//               mVv.stopPlayback();
//           }
//       }
//
//    }

    private void initData() {

        //获取参数播放不同视频
        Intent intent = getIntent();
        if (intent != null) {
            //视频名称
            play_video_name = intent.getStringExtra(Constant.PLAY_VIDEO_NAME);
            //视频播放时间
            play_time = intent.getIntExtra(Constant.PLAY_TIME, 0);
            //只有一个视频
            param = intent.getStringExtra(Constant.ADJUST_ONE_VIDEO);

        }

        //拿到文件路径播放视频
//        mVideoUri = Uri.parse("/sdcard/MyVideo/"+play_video_name+".mp4");
//        mVv.setVideoURI(mVideoUri);
        mVv.setVideoPath("/sdcard/zmpvideo/" + play_video_name);

        mVv.start();


        //开始播放
        mVv.setOnPreparedListener(new PLMediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(PLMediaPlayer plMediaPlayer) {
//                mVv.start();
            }
        });

        //播放完成
        mVv.setOnCompletionListener(new PLMediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(PLMediaPlayer plMediaPlayer) {
                //循环播放
                mVv.start();
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
                ToastUtil.show(FtPlayVideoActivity.this, "视频出错了...");
//                EventBus.getDefault().post(PLAY_VIDEO_ERROR);
                return false;
            }
        });

        if ("0".equals(param)) {
            //根据前端传过来的时间关闭视频
            mVv.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mVv.removeCallbacks(this);
                    mVv.stopPlayback();
                    finish();
                }
            }, (play_time) * 1000);
        }

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
                SelectFolderUtils.showSelecFileLists(this);
                break;

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}



