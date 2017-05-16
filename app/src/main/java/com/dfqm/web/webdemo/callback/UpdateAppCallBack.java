package com.dfqm.web.webdemo.callback;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dfqm.web.webdemo.API.zmpApi;
import com.dfqm.web.webdemo.activity.DownloadActivity;
import com.dfqm.web.webdemo.activity.MainActivity;
import com.dfqm.web.webdemo.activity.MainTainActivity;
import com.dfqm.web.webdemo.activity.QRcodeActivity;
import com.dfqm.web.webdemo.activity.UpdateAppActivity;
import com.dfqm.web.webdemo.constants.Constant;
import com.dfqm.web.webdemo.download.DownloadManager;
import com.dfqm.web.webdemo.entity.AuthorizeEntity;
import com.dfqm.web.webdemo.entity.MaintainEntity;
import com.dfqm.web.webdemo.entity.TextVideoBean;
import com.dfqm.web.webdemo.entity.UpdateEntity;
import com.dfqm.web.webdemo.utils.LoadWebViewDataUtil;
import com.dfqm.web.webdemo.utils.ProgressDialogUtil;
import com.dfqm.web.webdemo.utils.SharedPreferencesUtils;
import com.dfqm.web.webdemo.utils.ToastUtil;
import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.xutils.common.Callback;
import org.xutils.ex.DbException;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

import static com.dfqm.web.webdemo.API.zmpApi.authroize_url;
import static com.dfqm.web.webdemo.API.zmpApi.maintain_url;
import static com.dfqm.web.webdemo.API.zmpApi.video_url;
import static com.dfqm.web.webdemo.constants.Constant.ACTION_MAIN;
import static com.dfqm.web.webdemo.constants.Constant.ACTION_SID;
import static com.dfqm.web.webdemo.constants.Constant.AUTHORIZE_ID;
import static com.dfqm.web.webdemo.constants.Constant.DEVICEID;
import static com.dfqm.web.webdemo.constants.Constant.MIANTAINID;
import static com.dfqm.web.webdemo.constants.Constant.SID;
import static com.dfqm.web.webdemo.constants.Constant.UPDATE_ADDRESS;

/**
 * Created by Administrator on 2017/1/24.
 */

public class UpdateAppCallBack extends StringCallback {

    private final String localversionname;
    private final int localversioncode;
    private final Context context;
    private final Activity activity;
    private final String deviceId;
    private ProgressDialog progressDialog;
    private ProgressBar pb;
    private TextView mTvPercent;
    private TextView mTvTotal;
    private RequestParams params;
    private ArrayList<TextVideoBean.VideoBean> mVideoList = new ArrayList<>();
    private Handler handler = new Handler();

    public UpdateAppCallBack(Context context, String localversionname, int localversioncode, Activity activity,String deviceId) {
        this.localversionname = localversionname;
        this.localversioncode = localversioncode;
        this.context = context;
        this.activity = activity;
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
                                Intent intent = new Intent(context, QRcodeActivity.class);
                                intent.putExtra(DEVICEID, deviceId);
                                context.startActivity(intent);
                            } else {
                                ToastUtil.show(context, "用户已授权...");
                                String sid = authorizeEntity.getSid();
                                if (!"".equals(sid)) {
                                    //显示主界面
                                    Intent intent = new Intent(ACTION_SID);
                                    intent.putExtra(SID, sid);
                                    context.sendBroadcast(intent);
                                }
//                                //正常播放轮播图
//                                Intent intent = new Intent(Constant.PLAY);
//                                context.sendBroadcast(intent);
//                                try {
//                                    //webview轮播视频列表
//                                    loadVideoListData();
//                                } catch (DbException e) {
//                                    e.printStackTrace();
//                                }
                            }

                        }
                    }
                });
    }

    private void loadVideoListData() throws DbException {

        //请求列表
        params = new RequestParams(video_url);
        x.http().get(params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                if (result != null) {
                    Gson gson = new Gson();
                    Log.e("视频列表", ">>>" + result.toString());
                    TextVideoBean textVideoBean = gson.fromJson(result, TextVideoBean.class);
                    String lastEditTime = textVideoBean.getLastEditTime();
                    List<TextVideoBean.VideoBean> list = textVideoBean.getVideo();
                    mVideoList.addAll(list);
                    //得到最后一次跟新的时间
                    String time = SharedPreferencesUtils.getString(context, "time", "");
                    //判断最后一次时间是否相等
                    if (!time.equals(lastEditTime)) {
                        SharedPreferencesUtils.setString(context, "time", lastEditTime);
                        if (list.size() != 0) {
                            //开启下载任务
                            try {
                                xDownLoadVideo(list);
                            } catch (DbException e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        //正常播放轮播图
                        Intent intent = new Intent(Constant.PLAY);
                        context.sendBroadcast(intent);
                    }

                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                ToastUtil.show(context, ex.getMessage());
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    private void xDownLoadVideo(List<TextVideoBean.VideoBean> list) throws DbException {
        for (int i = 0; i < list.size(); i++) {
//            String url = et_url.getText().toString();
            String url = list.get(i).getUrl();
//            String label = i + "xUtils_" + System.nanoTime();
            String label = String.valueOf(i);
            DownloadManager.getInstance().startDownload(
                    url, label,
                    "/sdcard/MyVideo/" + list.get(i).get_id() + ".mp4", true, false, null);
        }

        //跳转到下载任务列表页面
        Intent intent = new Intent(context, DownloadActivity.class);
        context.startActivity(intent);
        activity.finish();
    }


}
