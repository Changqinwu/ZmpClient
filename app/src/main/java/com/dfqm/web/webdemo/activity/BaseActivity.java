package com.dfqm.web.webdemo.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.dfqm.web.webdemo.API.ZmpApi;
import com.dfqm.web.webdemo.R;
import com.dfqm.web.webdemo.application.AppApplication;
import com.dfqm.web.webdemo.callback.UpdateAppCallBack;
import com.dfqm.web.webdemo.constants.Constant;
import com.dfqm.web.webdemo.entity.EventMessageBean;
import com.dfqm.web.webdemo.utils.ExitAppUtils;
import com.dfqm.web.webdemo.utils.ProgressDialogUtil;
import com.dfqm.web.webdemo.utils.SharedPreferencesUtils;
import com.tencent.smtt.sdk.WebView;
import com.umeng.analytics.MobclickAgent;
import com.zhy.http.okhttp.OkHttpUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

import static com.dfqm.web.webdemo.API.ZmpApi.update_url;
import static com.dfqm.web.webdemo.constants.Constant.VERSIONID;

@RuntimePermissions
public class BaseActivity extends AppCompatActivity {
    //判断是否禁止获取权限
    private boolean isPermission;
    private AlertDialog.Builder builder;
    private PackageInfo mPackageInfo;
    private String localVersionName;
    private int localversionCode;
    public ProgressDialogUtil progressDialog;
    private UsbBroadcastReceiver mBroadcastReceiver;
    public String file_path;
    //判断屏幕是否横屏
    public boolean screenDirection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //隐藏顶部导航栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_base);
        //屏幕常亮
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //判断屏幕方向
        screenDirection();
        //弹窗
        progressDialog = new ProgressDialogUtil();
        //获取屏幕宽度和高度
        getWindowsHeightWidth();
        //权限初始化
        BaseActivityPermissionsDispatcher.showPermissionWithCheck(this);
        //友盟统计
        MobclickAgent.setDebugMode(true);
        MobclickAgent.openActivityDurationTrack(false);
        MobclickAgent.setScenarioType(this, MobclickAgent.EScenarioType.E_UM_NORMAL);
        //添加界面
        AppApplication.getApp().addActivity(this);

        //eventbus注册
        EventBus.getDefault().register(this);

    }

    public void screenDirection() {
        //横屏
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Log.i("info", "landscape");
            screenDirection = true;
        }
        //竖屏
        else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            screenDirection = false;
        }
    }

    private void getWindowsHeightWidth() {
        WindowManager wm = (WindowManager) getSystemService(this.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();
        int height = wm.getDefaultDisplay().getHeight();
        Log.e("屏幕高和宽", "宽度" + width + ">>>>>" + "高度" + height);
    }




    @Override
    protected void onResume() {
        super.onResume();
        if (isPermission) {
            //再次询问
            showAskAgain();
        }
        super.onResume();
        //友盟统计
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //友盟统计
        MobclickAgent.onPause(this);
    }

    //接受eventbus的消息
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(String msg) {

    }

    @NeedsPermission({Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void showPermission() {
        isPermission = false;

    }

    //权限回调
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        BaseActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);

    }

    @OnShowRationale({Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void showRationale(final PermissionRequest request) {
//        new AlertDialog.Builder(this)
//                .setMessage("获取权限")
//                .setPositiveButton("知道了", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        request.proceed();//再次执行请求
//                    }
//                })
//                .show();
        request.proceed();

    }

    @OnPermissionDenied({Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void showDeny() {
        builder = new android.app.AlertDialog.Builder(BaseActivity.this);
        builder.setTitle("请允许获取设备信息")
                .setMessage("需要获取设备信息的权限才能正常使用")
                .setCancelable(false)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //初始化权限
                        BaseActivityPermissionsDispatcher.showPermissionWithCheck(BaseActivity.this);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        System.exit(0);
                    }
                });
        builder.show();

    }

    @OnNeverAskAgain({Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void showAskAgain() {
        isPermission = false;
        if (!isPermission) {
//            ToastUtil.show(this,"不在询问该权限");
            builder = new AlertDialog.Builder(BaseActivity.this);
            builder.setTitle("请允许获取设备信息")
                    .setMessage("需要获取设备信息的权限才能正常使用")
                    .setCancelable(false)
                    .setPositiveButton("去设置", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //跳转到设置应用信息权限
                            Intent localIntent = new Intent();
                            localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            if (Build.VERSION.SDK_INT >= 9) {
                                localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                                localIntent.setData(Uri.fromParts("package", getPackageName(), null));
                            } else if (Build.VERSION.SDK_INT <= 8) {
                                localIntent.setAction(Intent.ACTION_VIEW);
                                localIntent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
                                localIntent.putExtra("com.android.settings.ApplicationPkgName", getPackageName());
                            }
                            startActivity(localIntent);
                            isPermission = true;
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            System.exit(0);
                        }
                    });
            builder.show();
        }
    }


    public void updateApp(String deviceId) {

        //获取本地版本
        PackageManager manager = this.getPackageManager();
        try {
            mPackageInfo = manager.getPackageInfo(this.getPackageName(), 0);
            localVersionName = mPackageInfo.versionName;
            localversionCode = mPackageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
//        ToastUtil.show(BaseActivity.this, "版本名称：" + localVersionName);
        //获取服务器版本
        OkHttpUtils.get()
                .id(VERSIONID)
                .url(update_url)
                .build()
                .execute(new UpdateAppCallBack(this, localVersionName, localversionCode, this, deviceId));
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {

            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    //退出app弹窗
    public void dialogExit(final Context context) {
        ExitAppUtils exitAppUtils = new ExitAppUtils();
        exitAppUtils.dialogExit(context);
    }


    @Override
    protected void onStart() {
        super.onStart();
        registerUsbBroadcast();
    }


    //注册广播的方法
    public void registerUsbBroadcast() {
        IntentFilter iFilter = new IntentFilter();
        iFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        iFilter.addDataScheme("file");
        mBroadcastReceiver = new UsbBroadcastReceiver();
        registerReceiver(mBroadcastReceiver, iFilter);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);
        //eventbus注销
        EventBus.getDefault().unregister(this);
    }


    class UsbBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            //当有U盘插入时，系统接收到信息
            if (action.equals(Intent.ACTION_MEDIA_MOUNTED)) {
                String path = intent.getDataString();
                file_path = path.replace("file://", "");
                Toast.makeText(context, "usb路径" + file_path + "", Toast.LENGTH_LONG).show();
                SharedPreferencesUtils.setString(context, "path", file_path);

            }
        }
    }

    public void setWebview(WebView webview) {
        webview.loadUrl(ZmpApi.horizontal_vertical_url);
        webview.setBackgroundColor(0);
        webview.getBackground().setAlpha(0);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);//设置js可以直接打开窗口，如window.open()，默认为false
        webview.getSettings().setSupportZoom(true);//是否可以缩放，默认true
        webview.getSettings().setBuiltInZoomControls(false);//是否显示缩放按钮，默认false
        webview.getSettings().setUseWideViewPort(true);//设置此属性，可任意比例缩放。大视图模式
        webview.getSettings().setLoadWithOverviewMode(true);//和setUseWideViewPort(true)一起解决网页自适应问题
        webview.getSettings().setAppCacheEnabled(false);//是否使用缓存
        webview.getSettings().setDomStorageEnabled(true);
    }

}
