package com.dfqm.web.webdemo.revceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;


/**
 * Created by Administrator on 2017/2/17.
 */

public class ConnectionChangeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        boolean success =false;
        /**
         * 获得网络连接服务
         */
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo.State state =connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
        if (NetworkInfo.State.CONNECTED==state){
            success =true;
            Toast.makeText(context,"网络连接成功...",Toast.LENGTH_SHORT).show();
        }
        state =connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
        if (NetworkInfo.State.CONNECTED==state){
            success =true;
            Toast.makeText(context,"网络连接成功...",Toast.LENGTH_SHORT).show();
        }
        if (!success){
            Toast.makeText(context,"网络连接失败...",Toast.LENGTH_SHORT).show();
//            EventBus.getDefault().post(new ForexDateEvent(ForexDateEvent.NetworkInfoState,false));
//        } else {
//            EventBus.getDefault().post(new ForexDateEvent(ForexDateEvent.NetworkInfoState,true));
        }
    }
}
