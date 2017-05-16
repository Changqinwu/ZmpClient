package com.dfqm.web.webdemo.utils;

import android.app.ProgressDialog;
import android.content.Context;


/**
 * Created by Administrator on 2016/12/17.
 */

public class ProgressDialogUtil {

    private Context context;
    private ProgressDialog dialog;



    public void showProgressDialog(Context context,String str) {
        if (dialog == null) {
            dialog = new ProgressDialog(context);
            dialog.setIndeterminate(false);
            dialog.setCancelable(false);
            dialog.setMessage(str);
            dialog.show();
        }

    }

    public void dismissProgressDialog() {
        if (dialog != null) {
            dialog.dismiss();
        }

    }


}
