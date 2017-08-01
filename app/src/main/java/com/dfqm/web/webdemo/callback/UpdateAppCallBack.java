package com.dfqm.web.webdemo.callback;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dfqm.web.webdemo.activity.MainTainActivity;
import com.dfqm.web.webdemo.activity.QrCodeActivity;
import com.dfqm.web.webdemo.activity.UpdateAppActivity;
import com.dfqm.web.webdemo.constants.Constant;
import com.dfqm.web.webdemo.entity.AuthorizeEntity;
import com.dfqm.web.webdemo.entity.EventMessageBean;
import com.dfqm.web.webdemo.entity.MaintainEntity;
import com.dfqm.web.webdemo.entity.UpdateEntity;
import com.dfqm.web.webdemo.utils.SharedPreferencesUtils;
import com.dfqm.web.webdemo.utils.ToastUtil;
import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;

import okhttp3.Call;
import static com.dfqm.web.webdemo.API.ZmpApi.authroize_url;
import static com.dfqm.web.webdemo.API.ZmpApi.maintain_url;
import static com.dfqm.web.webdemo.constants.Constant.ACTION_MAIN;
import static com.dfqm.web.webdemo.constants.Constant.ACTION_SID;
import static com.dfqm.web.webdemo.constants.Constant.AUTHORIZE_ID;
import static com.dfqm.web.webdemo.constants.Constant.DEVICEID;
import static com.dfqm.web.webdemo.constants.Constant.MIANTAINID;
import static com.dfqm.web.webdemo.constants.Constant.SID;
import static com.dfqm.web.webdemo.constants.Constant.STYPE;
import static com.dfqm.web.webdemo.constants.Constant.UPDATE_ADDRESS;

/**
 * Created by Administrator on 2017/1/24.
 */

public class UpdateAppCallBack extends StringCallback {

    private final String localversionname;
    private final int localversioncode;
    private final Context context;
    private final String deviceId;

    public UpdateAppCallBack(Context context, String localversionname, int localversioncode, Activity activity,String deviceId) {
        this.localversionname = localversionname;
        this.localversioncode = localversioncode;
        this.context = context;
        this.deviceId = deviceId;
    }


    @Override
    public void onError(Call call, Exception e, int id) {
        ToastUtil.show(context, e.getMessage());
        //显示错误主界面
        Intent intent = new Intent(ACTION_MAIN);
        context.sendBroadcast(intent);
    }

    @Override
    public void onResponse(String response, int id) {
        //解析数据
        if (response != null) {
            Gson gson = new Gson();
            UpdateEntity updateEntity = gson.fromJson(response, UpdateEntity.class);
            int httpversionCode = Integer.valueOf(updateEntity.getVersionCode());
            String versionName = updateEntity.getVersionName();
            final String versonAddress = updateEntity.getVersonAddress();
            if (httpversionCode > localversioncode) {
                //跳转到更新app的界面
                Intent intent = new Intent(context, UpdateAppActivity.class);
                intent.putExtra(UPDATE_ADDRESS, versonAddress);
                context.startActivity(intent);
            } else {
                //是否有维护
                serverMainTain();
            }

        }

    }


    private void serverMainTain() {
        //查看服务器是否有维护
        OkHttpUtils.get()
                .id(MIANTAINID)
                .url(maintain_url)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        ToastUtil.show(context, e.getMessage());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Gson gson = new Gson();
                        MaintainEntity maintainEntity = gson.fromJson(response, MaintainEntity.class);
                        String mainTainType = maintainEntity.getMainTainType();
                        if (Constant.MIANTAIN_TYPE.equals(mainTainType)) {
                            //跳转到维护界面
                            Intent intent = new Intent(context, MainTainActivity.class);
                            context.startActivity(intent);
                        } else {
                            if (!"".equals(deviceId)) {
                                //是否授权
                                userAuthorize(authroize_url + deviceId, deviceId);
                            }

                        }
                    }
                });
    }


    private void userAuthorize(String Url, final String deviceId) {
        OkHttpUtils.get()
                .id(AUTHORIZE_ID)
                .url(Url)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        ToastUtil.show(context, e.getMessage());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        if (response != null) {
                            Gson gson = new Gson();
                            AuthorizeEntity authorizeEntity = gson.fromJson(response, AuthorizeEntity.class);
                            boolean success = authorizeEntity.isSuccess();

                            if (!success) {
                                ToastUtil.show(context, "用户未授权...");
                                //跳转到二维码界面
                                Intent intent = new Intent(context, QrCodeActivity.class);
                                intent.putExtra(DEVICEID, deviceId);
                                context.startActivity(intent);
                            } else {
//                                ToastUtil.show(context, "用户已授权...");
                                String sid = authorizeEntity.getSid();
                                String stype = authorizeEntity.getStype();
                                //保存Sid到本地
                                SharedPreferencesUtils.setString(context,SID,sid);
                                SharedPreferencesUtils.setString(context,STYPE,stype);
                                if (!"".equals(sid) && !"".equals(stype)) {
                                    //显示主界面
                                    Intent intent = new Intent(ACTION_SID);
                                    intent.putExtra(SID, sid);
                                    intent.putExtra(Constant.STYPE, stype);
                                    context.sendBroadcast(intent);
                                }
                            }

                        }
                    }
                });
    }

}
