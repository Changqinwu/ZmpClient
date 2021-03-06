package com.dfqm.web.webdemo.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.dfqm.web.webdemo.R;
import com.dfqm.web.webdemo.application.AppApplication;
import com.dfqm.web.webdemo.constants.Constant;
import com.dfqm.web.webdemo.entity.EventMessageBean;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2017/5/15.
 */



public class ExitAppUtils {

    public static String DefaultPwd = "dfqm";
    public static String setWifiPwd = "set_";
    public static String resetIdPwd = "dfqm_reset";

    //退出app弹窗
    public static void dialogExit(final Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.home_dialog_exit_layout, null);
        final EditText mEditPwd = (EditText) view.findViewById(R.id.edit_pwd);
        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setTitle("提示")
                .setMessage("输入密码退出应用？")
                .setView(view)
                .setCancelable(false)
                .setIcon(R.mipmap.zmp_app_icon)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final String pwd = mEditPwd.getText().toString();
                        if (!pwd.equals("")) {
                            //如果输入的是df+整型的话进行存储
                            Pattern p = Pattern.compile("df_+[0-9]*");
                            Matcher m = p.matcher(pwd);
                            if (m.matches()) {
//                                String str_df = pwd.replace("df_", "");
//                                //判断是否符合屏幕ID规范
//                                if (str_df.equals("df_1234")) {
//
//                                } else {
//                                    Toast.makeText(context, "输入有误...", Toast.LENGTH_SHORT).show();
//                                }
                            } else if (pwd.equals(DefaultPwd)) {
                                System.exit(0);
                                //结束Activity& 从栈中移除该Activity
//                                AppApplication.getApp().exit();
                            }else if (pwd.equals(resetIdPwd)) {
                                //删除唯一识别码
                                FileUtils.RecursionDeleteFile(new File("/sdcard/zmpfile/uniqueId.txt"));
                                //重新加载页面
                                EventBus.getDefault().post(new EventMessageBean(Constant.RLOAD,"1"));
                            }else if (pwd.equals(setWifiPwd)) {
                                //跳转到wifi设置界面
                                Intent intent = new Intent(Settings.ACTION_SETTINGS);
                                context.startActivity(intent);
                            }else {
                                Toast.makeText(context, "输入有误...", Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            Toast.makeText(context, "密码不能为空...", Toast.LENGTH_SHORT).show();
                        }

                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        alertDialog.show();
    }
}
