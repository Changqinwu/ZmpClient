package com.dfqm.web.webdemo.revceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.dfqm.web.webdemo.activity.MainActivity;
import com.dfqm.web.webdemo.constants.Constant;
import com.dfqm.web.webdemo.utils.FileUtils;
import com.dfqm.web.webdemo.utils.SharedPreferencesUtils;

import java.io.File;

/**
 * Created by Administrator on 2017/3/29.
 */

public class InitApkBroadCastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        PackageManager manager = context.getPackageManager();
//        if (intent.getAction().equals(Intent.ACTION_PACKAGE_ADDED)) {
//            String packageName = intent.getData().getSchemeSpecificPart();
//            Toast.makeText(context, "安装成功" + packageName, Toast.LENGTH_LONG).show();
//            Toast.makeText(context, "执行1...", Toast.LENGTH_LONG).show();
//        }
//        if (intent.getAction().equals(Intent.ACTION_PACKAGE_REMOVED)) {
//            String packageName = intent.getData().getSchemeSpecificPart();
//            Toast.makeText(context, "卸载成功" + packageName, Toast.LENGTH_LONG).show();
//            Toast.makeText(context, "执行2...", Toast.LENGTH_LONG).show();
//
//        }
        if (intent.getAction().equals(Intent.ACTION_PACKAGE_REPLACED)) {
//            删除安装包
            String absolutePath = Environment.getExternalStorageDirectory().getAbsolutePath();
            String pac_path = absolutePath + "/zmp.apk";
            if (pac_path != null) {
//                Toast.makeText(context, "删除安装包地址："+pac_path, Toast.LENGTH_LONG).show();
                FileUtils.RecursionDeleteFile(new File(pac_path));
            }

//            Intent intent2 = new Intent(context, MainActivity.class);
//            intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            context.startActivity(intent2);

        }

    }

}
