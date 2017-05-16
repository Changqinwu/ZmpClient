package com.dfqm.web.webdemo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.dfqm.web.webdemo.R;
import com.dfqm.web.webdemo.callback.LocaImageHolderView;

import java.util.ArrayList;

public class PlayPictureActivity extends BaseActivity implements View.OnClickListener {

    private ConvenientBanner mBanner;
    private ArrayList<String> pic_path;
    private ImageView mImaExit;
    private ImageView mImaOpenVideoList;
    private long exitTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_picture);

        initView();
    }

    private void initView() {
        mBanner = (ConvenientBanner) findViewById(R.id.convenientBanner);
        mImaExit = (ImageView) findViewById(R.id.ima_exit_app);
        mImaOpenVideoList = (ImageView) findViewById(R.id.ima_open_videolist);
        mImaExit.setOnClickListener(this);
        mImaOpenVideoList.setOnClickListener(this);
        setBanner(mBanner);
    }

    private void setBanner(ConvenientBanner convenientBanner) {
        //获取图片路径列表
        Intent intent = getIntent();
        pic_path = intent.getStringArrayListExtra("path");

        convenientBanner.setPages(new CBViewHolderCreator<LocaImageHolderView>() {
            @Override
            public LocaImageHolderView createHolder() {
                return new LocaImageHolderView(pic_path,getApplicationContext());
            }
        }, pic_path);

        //设置需要切换的View
        convenientBanner.setPointViewVisible(false)    //设置指示器是否可见
                .setPageIndicator(new int[]{R.mipmap.ic_page_indicator, R.mipmap.ic_page_indicator_focused}); //设置指示器圆点
        convenientBanner.setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.ALIGN_PARENT_RIGHT);
        convenientBanner.startTurning(5000);//设置自动切换（同时设置了切换时间间隔）
//        convenientBanner.setOnItemClickListener(new OnItemClickListener() {
//            @Override
//            public void onItemClick(int position) {
//                intent = new Intent(getActivity(), HomeNavDetailActivity.class);
//                intent.putExtra("param", Constant.SC);
//                intent.putExtra("type", Constant.SC_TYPE);
//                startActivity(intent);
//            }
//        });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            //退出app弹窗
            case R.id.ima_exit_app:
                if ((System.currentTimeMillis() - exitTime) > 500) {
                    exitTime = System.currentTimeMillis();
                } else {
                    dialogExit(this);
                }
                break;
            //打开列表选择
            case R.id.ima_open_videolist:
                showSelecFileLists(this,this);
                break;
        }
    }
}
