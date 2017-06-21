package com.dfqm.web.webdemo.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.dfqm.web.webdemo.R;
import com.dfqm.web.webdemo.application.AppApplication;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2017/5/15.
 */



public class ExitAppUtils {

    public static String DefaultPwd = "dfqm";

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
//                            //如果输入的是df+整型的话进行存储
//                            Pattern p = Pattern.compile("df_+[0-9]*");
//                            Matcher m = p.matcher(pwd);
//                            if (m.matches()) {
//                                String str_df = pwd.replace("df_", "");
//                                //判断是否符合屏幕ID规范
//                                if (!str_df.equals("")) {
////                                    int param = Integer.valueOf(str_df);
////                                    SharedPreferencesUtils.setInt(context, "param", param);
//                                } else {
//                                    Toast.makeText(context, "输入有误...", Toast.LENGTH_SHORT).show();
//                                }
//                            } else {
//                                Toast.makeText(context, "输入有误...", Toast.LENGTH_SHORT).show();
//                            }
                            if (pwd.equals(DefaultPwd)) {
//                                System.exit(0);
                                //结束Activity& 从栈中移除该Activity
                                AppApplication.getApp().exit();
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
