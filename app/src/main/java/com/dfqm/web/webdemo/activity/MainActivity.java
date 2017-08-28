package com.dfqm.web.webdemo.activity;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.CountDownTimer;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dfqm.web.webdemo.customview.CustomViewGroup;
import com.dfqm.web.webdemo.entity.AuthorizeEntity;
import com.dfqm.web.webdemo.utils.SharedPreferencesUtils;
import com.dfqm.web.webdemo.utils.ToastUtil;
import com.dfqm.web.webdemo.utils.WifiAdminUtils;
import com.dfqm.web.webdemo.utils.WifiConnectUtils;
import com.dfqm.web.webdemo.zmpapi.ZmpApi;
import com.dfqm.web.webdemo.tojs.ToJavaScriptInterface;
import com.dfqm.web.webdemo.R;
import com.dfqm.web.webdemo.constants.Constant;
import com.dfqm.web.webdemo.entity.EventMessageBean;
import com.dfqm.web.webdemo.utils.CreatUniqueIdUtils;
import com.dfqm.web.webdemo.utils.LoadWebViewDataUtil;
import com.dfqm.web.webdemo.utils.SelectFolderUtils;
import com.google.gson.Gson;
import com.tencent.smtt.sdk.WebView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;

import okhttp3.Call;

import static com.dfqm.web.webdemo.constants.Constant.ACTION_MAIN;
import static com.dfqm.web.webdemo.constants.Constant.ACTION_SID;
import static com.dfqm.web.webdemo.constants.Constant.AUTHORIZE_ID;
import static com.dfqm.web.webdemo.constants.Constant.CANNOT_CHANGE;
import static com.dfqm.web.webdemo.constants.Constant.DEVICEID;
import static com.dfqm.web.webdemo.constants.Constant.RLOAD;
import static com.dfqm.web.webdemo.constants.Constant.SID;
import static com.dfqm.web.webdemo.constants.Constant.STYPE;
import static com.dfqm.web.webdemo.utils.WifiConnectUtils.WifiCipherType.WIFICIPHER_WPA;
import static com.dfqm.web.webdemo.zmpapi.ZmpApi.authroize_url;


public class MainActivity extends BaseActivity implements View.OnClickListener {

    //退出app
    private ImageView mImaExit;
    //错误页面
    private ImageView mImaError;
    //倒计时
    private TextView mTvCount;
    private long exitTime = 0;
    private WebView mWebView;
    //打开本地视频列表
    private ImageView mImaOpenVideoList;
    private MyBroadcastRecevier recevier;
    //下载视频列表
    private ArrayList<String> videoLists;
    private String uniquePsuedoID;
    private CountDownTimer timer;
    private ImageView mImaRightExitApp;
    //判断是否倒计时完
    private boolean timeFinish = true;
    private ImageView mImaToSet;
    private RelativeLayout mRlVertical;
    private RelativeLayout mRlHorizontal;
    private android.webkit.WebView mWvVertical;
    private android.webkit.WebView mWvHorizontal;
    private String sid;
    //本地保存的唯一标识ID
    private static final String UNIQUEID_PATH = "/sdcard/zmpfile/uniqueId.txt";
    private CustomViewGroup view;
    private WindowManager manager;
    private WifiAdminUtils mWifiAdmin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //连接网络
        setWifi();
        //初始化view
        initView();
        //生成唯一标识
        existUniqueId();
        //禁止下拉
        prohibitDropDown();

    }

    private void setWifi() {
        mWifiAdmin = new WifiAdminUtils(MainActivity.this);
        String ssId = SharedPreferencesUtils.getString(this, "ssId", "");
        String wifiPwd = SharedPreferencesUtils.getString(this, "wifiPwd", "");
        String type_ = SharedPreferencesUtils.getString(this, "type", "");
        ToastUtil.show(this,type_);
        WifiConnectUtils.WifiCipherType type = null;
        if (type_ .equals("WIFICIPHER_WEP")) {
            type = WifiConnectUtils.WifiCipherType.WIFICIPHER_WEP;
        }else if (type_.equals("WIFICIPHER_WPA")) {
            type = WifiConnectUtils.WifiCipherType.WIFICIPHER_WPA;
        }else if (type_.equals("WIFICIPHER_NOPASS")) {
            type = WifiConnectUtils.WifiCipherType.WIFICIPHER_NOPASS;
        }

        boolean connect = mWifiAdmin.connect(ssId, wifiPwd, type);
        if (connect) {
            Toast.makeText(this,"连接了",Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(this,"没有连接",Toast.LENGTH_SHORT).show();
        }
    }

    private void existUniqueId() {
        //判断唯一标识的Id的txt文件的内容是否存在
        File file = new File(UNIQUEID_PATH);
        CreatUniqueIdUtils uniqueIdUtils = new CreatUniqueIdUtils();
        uniquePsuedoID = uniqueIdUtils.getFileContent(file);

        if (!"".equals(uniquePsuedoID)) {
            //获取设备状态信息
            initData(uniquePsuedoID);
//            ToastUtil.show(this, "uniquePsuedoID:" + uniquePsuedoID);
        } else {
            //创建唯一标识Id
            uniqueIdUtils.initData();
            existUniqueId();
        }

    }

    //接受eventbus的消息
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventMessageBean eventMessageBean) {

        String msgId = eventMessageBean.getMsgId();
        if (msgId.equals(RLOAD)) {
            //重新生成唯一标识
            existUniqueId();
//            ToastUtil.show(MainActivity.this,"重新加载Url...");
        } else if (screenDirection && msgId.equals("2")) {

            //横屏并且是分天版
            mRlVertical.setVisibility(View.VISIBLE);
            mRlHorizontal.setVisibility(View.GONE);
            //设置wenview
            setWebview(mWvVertical, sid);
//            ToastUtil.show(MainActivity.this,"横屏二维码显示");

        } else if (!screenDirection && msgId.equals("2")) {
            //竖屏并且是分天版
            mRlVertical.setVisibility(View.GONE);
            mRlHorizontal.setVisibility(View.VISIBLE);
            //设置wenview
            setWebview(mWvHorizontal, sid);
//            ToastUtil.show(MainActivity.this,"竖屏二维码显示");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        //注册广播
        recevier = new MyBroadcastRecevier();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constant.CHANGE);
        filter.addAction(CANNOT_CHANGE);
        filter.addAction(ACTION_SID);
        filter.addAction(ACTION_MAIN);
        registerReceiver(recevier, filter);


    }

    public void initData(String deviceId) {

        progressDialog.showProgressDialog(this,"请稍后...");

        //更新qpp
        updateApp(deviceId,progressDialog);

        //是否授权
        userAuthorize(authroize_url + deviceId, deviceId);
    }


    private void initView() {
        mImaExit = (ImageView) findViewById(R.id.ima_exit_app);
        mWebView = (WebView) findViewById(R.id.webview);
        mImaError = (ImageView) findViewById(R.id.ima_web_page_error);
        mTvCount = (TextView) findViewById(R.id.tv_request_count_time);
        mImaOpenVideoList = (ImageView) findViewById(R.id.ima_open_videolist);
        mImaRightExitApp = (ImageView) findViewById(R.id.ima_rignt_top_exit_app);
        mImaToSet = (ImageView) findViewById(R.id.ima_to_set);
        mRlVertical = (RelativeLayout) findViewById(R.id.rl_webview_vertical);
        mRlHorizontal = (RelativeLayout) findViewById(R.id.rl_webview_horizontal);
        mWvVertical = (android.webkit.WebView) findViewById(R.id.wv_vertical);
        mWvHorizontal = (android.webkit.WebView) findViewById(R.id.wv_horizontal);

        mImaExit.setOnClickListener(this);
        mImaOpenVideoList.setOnClickListener(this);
        mImaRightExitApp.setOnClickListener(this);
        mImaToSet.setOnClickListener(this);
        mRlVertical.setVisibility(View.GONE);
        mRlHorizontal.setVisibility(View.GONE);
    }

    private void loadWebViewData(String param, String stype) {
        videoLists = new ArrayList<>();
        //出错界面隐藏
        mImaError.setVisibility(View.GONE);
        mTvCount.setVisibility(View.GONE);
        //测试轮播边放图片边放视频webview
        LoadWebViewDataUtil mLoad = new LoadWebViewDataUtil(this, mWebView, mImaError, ZmpApi.main_url + param + "&stype=" + stype, mTvCount);
        mLoad.initData();

        // 设置js接口  第一个参数事件接口实例，第二个是实例在js中的别名，这个在js中会用到
        ToJavaScriptInterface js = new ToJavaScriptInterface(this, videoLists);
        mWebView.addJavascriptInterface(js, "JSInterface");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            //退出app弹窗
            case R.id.ima_exit_app:
                exitAppDialog(this);
                break;
            //打开本地视频图片选择列表
            case R.id.ima_open_videolist:
                SelectFolderUtils.showSelecFileLists(this);
                break;
            //右上角退出弹窗
            case R.id.ima_rignt_top_exit_app:
                exitAppDialog(this);
                //某应用UID消耗的流量
//                long uidRxBytes = TrafficStats.getUidRxBytes(getAppUid());
//                long uidTxBytes = TrafficStats.getUidTxBytes(getAppUid());
//                String s1 = formatSize(String.valueOf(uidRxBytes + uidTxBytes));
//                ToastUtil.show(this,"应用流量:"+s1);
                break;
            //左上角调到设置界面
            case R.id.ima_to_set:
//                Intent intent = new Intent(Settings.ACTION_SETTINGS);
//                startActivity(intent);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //防止内存溢出
        if (mWebView != null) {
            mWebView.destroy();
        }

        //注销广播
        unregisterReceiver(recevier);

    }

    class MyBroadcastRecevier extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Constant.CHANGE.equals(action)) {
                //关闭弹窗
                progressDialog.dismissProgressDialog();
                //能修改播放方案，参数0
                mWebView.loadUrl("javascript:androidJs(0)");

            } else if (Constant.CANNOT_CHANGE.equals(action)) {
                //不能修改播放方案，参数1
                mWebView.loadUrl("javascript:androidJs(1)");

            } else if (ACTION_MAIN.equals(action)) {
//                //重新加载主界面
//                initData(uniquePsuedoID);
                //出错界面显示
                mImaError.setVisibility(View.VISIBLE);
                mTvCount.setVisibility(View.VISIBLE);

                if (timeFinish) {
                    timeFinish = false;
                    //开始倒计时
                    startCountDownTime(5);
                }
            } else if (Constant.ACTION_SID.equals(action)) {
                //请求webview，显示轮播方案
//                sid = intent.getStringExtra(SID);
//                String stype = intent.getStringExtra(Constant.STYPE);
//                loadWebViewData(sid, stype);
            }

        }
    }


    //退出app弹窗
    public void exitAppDialog(final Context context) {

        if ((System.currentTimeMillis() - exitTime) > 500) {
            exitTime = System.currentTimeMillis();
        } else {
            dialogExit(context);
        }
    }

    public void startCountDownTime(final long time) {
        /**
         * 最简单的倒计时类，实现了官方的CountDownTimer类（没有特殊要求的话可以使用）
         * 即使退出activity，倒计时还能进行，因为是创建了后台的线程。
         * 有onTick，onFinsh、cancel和start方法
         */
        timer = new CountDownTimer(time * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                //每隔countDownInterval秒会回调一次onTick()方法
                Log.e("倒计时", millisUntilFinished + "");
                mTvCount.setText("未连接到网络"+"\n"+"轻触左下角，设置网络"+"("+millisUntilFinished / 1000+")");
            }

            @Override
            public void onFinish() {
                initData(uniquePsuedoID);
                //倒计时完
                timeFinish = true;
            }

        };
        timer.start();// 开始计时
    }

    private void userAuthorize(String Url, final String deviceId) {
        OkHttpUtils.get()
                .id(AUTHORIZE_ID)
                .url(Url)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        ToastUtil.show(MainActivity.this, e.getMessage());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        if (response != null) {
                            //取消弹窗
                            progressDialog.dismissProgressDialog();
                            Gson gson = new Gson();
                            AuthorizeEntity authorizeEntity = gson.fromJson(response, AuthorizeEntity.class);
                            boolean success = authorizeEntity.isSuccess();

                            if (!success) {
                                ToastUtil.show(MainActivity.this, "用户未授权...");
                                //跳转到二维码界面
                                Intent intent = new Intent(MainActivity.this, QrCodeActivity.class);
                                intent.putExtra(DEVICEID, deviceId);
                                startActivity(intent);
                            } else {
//                                ToastUtil.show(context, "用户已授权...");
                                String sid = authorizeEntity.getSid();
                                String stype = authorizeEntity.getStype();
                                //保存Sid到本地
                                SharedPreferencesUtils.setString(MainActivity.this, SID, sid);
                                SharedPreferencesUtils.setString(MainActivity.this, STYPE, stype);
                                if (!"".equals(sid) && !"".equals(stype)) {
//                                    //显示主界面
                                    loadWebViewData(sid, stype);
                                }
                            }

                        }
                    }
                });
    }


    //禁止下拉
    public void prohibitDropDown() {
        manager = ((WindowManager) getApplicationContext()
                .getSystemService(Context.WINDOW_SERVICE));
        WindowManager.LayoutParams localLayoutParams = new WindowManager.LayoutParams();
        localLayoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
        localLayoutParams.gravity = Gravity.TOP;
        localLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|
                // this is to enable the notification to recieve touch events
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                // Draws over status bar
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        localLayoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        localLayoutParams.height = (int) (50 * getResources()
                .getDisplayMetrics().scaledDensity);
        localLayoutParams.format = PixelFormat.TRANSPARENT;
        view = new CustomViewGroup(this);
        manager.addView(view, localLayoutParams);
    }

}
