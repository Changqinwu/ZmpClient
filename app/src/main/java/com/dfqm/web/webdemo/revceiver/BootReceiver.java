package com.dfqm.web.webdemo.revceiver;

        import android.content.BroadcastReceiver;
        import android.content.Context;
        import android.content.Intent;
        import android.os.Handler;
        import android.util.Log;

        import com.dfqm.web.webdemo.activity.MainActivity;

/**
 * Created by Administrator on 2016/12/28.
 */

public class BootReceiver extends BroadcastReceiver {
    private String ACTION = "android.intent.action.BOOT_COMPLETED";
    private Handler mHandler = new Handler();

    @Override
    public void onReceive(final Context context, Intent intent) {
//        //启动app
//        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
//            Log.e("启动》》》", "启动成功。。。");
//            //延时10秒启动
//            mHandler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    Intent intent = new Intent(context, MainActivity.class);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    context.startActivity(intent);
//                }
//            }, 1000);
//
//        }

    }

}
