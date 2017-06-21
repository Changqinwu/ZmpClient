package com.dfqm.web.webdemo.activity;

import android.Manifest;
import android.app.Activity;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.dfqm.web.webdemo.R;
import com.dfqm.web.webdemo.application.AppApplication;
import com.dfqm.web.webdemo.callback.UpdateAppCallBack;
import com.dfqm.web.webdemo.utils.ExitAppUtils;
import com.dfqm.web.webdemo.utils.ProgressDialogUtil;
import com.dfqm.web.webdemo.utils.SelectFolderUtils;
import com.dfqm.web.webdemo.utils.SharedPreferencesUtils;
import com.dfqm.web.webdemo.utils.ToastUtil;
import com.umeng.analytics.MobclickAgent;
import com.zhy.http.okhttp.OkHttpUtils;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

import static com.dfqm.web.webdemo.API.zmpApi.update_url;
import static com.dfqm.web.webdemo.constants.Constant.VERSIONID;

@RuntimePermissions
public class BaseActivity extends AppCompatActivity {
    //判断是否禁止获取权限
    private boolean isPermission;
    private AlertDialog.Builder builder;
    private PackageInfo mPackageInfo;
    private String localVersionName;
    private int localversionCode;
    public String DefaultPwd = "dfqm";
    public ProgressDialogUtil progressDialog;
//    private AppApplication app;
    private UsbBroadcastReceiver mBroadcastReceiver;
    public String file_path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //隐藏顶部导航栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_base);
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
//        if (isTablet(this)) {
//            /**
//             * 设置为横屏
//             */
//            if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
//                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//            }
//        }

        super.onResume();
        //友盟统计
        MobclickAgent.onResume(this);
    }

    //是否宽屏
    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    @Override
    protected void onPause() {
        super.onPause();
        //友盟统计
        MobclickAgent.onPause(this);
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


    public  void updateApp(String deviceId) {

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
                .execute(new UpdateAppCallBack(this, localVersionName, localversionCode,this,deviceId));
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

    //左下角点击选择usb图片，视频文件夹
    public void showSelecFileLists(final Context context, final Activity activity){
        SelectFolderUtils selectFolderUtils = new SelectFolderUtils();
        selectFolderUtils.showSelecFileLists(context,activity);
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerUsbBroadcast();
    }


    //注册广播的方法
    public void registerUsbBroadcast() {
        IntentFilter iFilter = new IntentFilter();
//        iFilter.addAction(Intent.ACTION_MEDIA_EJECT);
        iFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);
//        iFilter.addAction(Intent.ACTION_MEDIA_REMOVED);
//        iFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
        iFilter.addDataScheme("file");
        mBroadcastReceiver = new UsbBroadcastReceiver();
        registerReceiver(mBroadcastReceiver, iFilter);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);
    }


    class UsbBroadcastReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
//            if (action.equals(Intent.ACTION_MEDIA_EJECT)) {
//                //USB设备移除，更新UI,地址保存为空
//                SharedPreferencesUtils.setString(context,"path","");
//
//            } else

                if (action.equals(Intent.ACTION_MEDIA_MOUNTED)) {
//
                String path = intent.getDataString();
                file_path = path.replace("file://", "");
//                search(new File(path));
                Toast.makeText(context, "usb路径" + file_path + "", Toast.LENGTH_LONG).show();
                SharedPreferencesUtils.setString(context,"path",file_path);

            }
        }
    }
}
