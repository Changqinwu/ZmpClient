package com.dfqm.web.webdemo.callback;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.dfqm.web.webdemo.R;
import com.dfqm.web.webdemo.utils.ProgressDialogUtil;
import com.dfqm.web.webdemo.utils.ToastUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import okhttp3.Call;

/**
 * Created by Administrator on 2017/1/9.
 */


public class RequseAppStateInfoCallback extends StringCallback {

    private final Context context;
    private final ProgressDialogUtil dialogUtil;
    private final String deviceId;
    private final View webview;
    //是否激活对话框
    private AlertDialog mActivcateDialog;
    //激活app请求的id
    private int START_APP_ID = 1;

    public RequseAppStateInfoCallback(Context context, ProgressDialogUtil dialogUtil, String deviceId, View webView) {
        this.context = context;
        this.dialogUtil = dialogUtil;
        this.deviceId = deviceId;
        this.webview = webView;
    }

    @Override
    public void onError(Call call, Exception e, int id) {
        dialogUtil.dismissProgressDialog();
        ToastUtil.show(context, "Error:" + e.getMessage().toString());
        webview.setVisibility(View.GONE);
        //激活失败
        activateDialog();

    }

    @Override
    public void onResponse(String response, int id) {
        dialogUtil.dismissProgressDialog();
        webview.setVisibility(View.VISIBLE);
    }

    //是否激活窗口
    public void activateDialog() {
        View view = LayoutInflater.from(context).inflate(R.layout.app_start_activate_layout_dialog, null);
        TextView mTvActivate = (TextView) view.findViewById(R.id.tv_activate_tip);
        mTvActivate.setText("该设备还未激活，无法正常使用，请激活...");
        mActivcateDialog = new AlertDialog.Builder(context)
                .setTitle("提示")
                .setMessage("该设备还未激活，无法正常使用，10秒后自动激活...")
                .setView(view)
                .setCancelable(false)
                .setIcon(R.mipmap.zmp_app_icon)
                .setPositiveButton("手动激活", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        //请求数据
                        initData(deviceId);
                    }
                })
                .create();
        mActivcateDialog.show();
        startCountDownTime(10, mTvActivate);
    }


    //倒计时
    public void startCountDownTime(long time, final TextView tv) {
        /**
         * 最简单的倒计时类，实现了官方的CountDownTimer类（没有特殊要求的话可以使用）
         * 即使退出activity，倒计时还能进行，因为是创建了后台的线程。
         * 有onTick，onFinsh、cancel和start方法
         */
        CountDownTimer timer = new CountDownTimer(time * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                //每隔countDownInterval秒会回调一次onTick()方法
                Log.e("倒计时", millisUntilFinished + "");
                tv.setText(millisUntilFinished / 1000 + "秒后重新激活...");
            }

            @Override
            public void onFinish() {
                mActivcateDialog.dismiss();
                //请求数据
                initData(deviceId);
            }
        };
        timer.start();// 开始计时
    }

    private void initData(String app_devieceId) {
        ProgressDialogUtil dialogUtil = new ProgressDialogUtil();
        dialogUtil.showProgressDialog(context, "正在激活...");
        OkHttpUtils.get()
                .id(START_APP_ID)
                .url("https://www.baidu.com/")
                .build()
                .execute(new RequseAppStateInfoCallback(context, dialogUtil, app_devieceId, webview));
    }


}
