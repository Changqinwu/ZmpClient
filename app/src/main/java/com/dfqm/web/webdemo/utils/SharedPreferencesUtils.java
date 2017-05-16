package com.dfqm.web.webdemo.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import java.io.File;

public class SharedPreferencesUtils {
    public static final String PREF_NAME = "dfqm_info";
    private static SharedPreferences sp;

    // 获取boolean类型的sp
    public static boolean getBoolean(Context context, String key, boolean defaultValue) {

        sp = context.getSharedPreferences(PREF_NAME, context.MODE_PRIVATE);
        return sp.getBoolean(key, defaultValue);
    }

    // 设置boolean类型的sp
    public static void setBoolean(Context context, String key, boolean defaultValue) {
        sp = context.getSharedPreferences(PREF_NAME, context.MODE_PRIVATE);
        sp.edit().putBoolean(key, defaultValue).commit();
    }

    // 获取String类型的sp
    public static String getString(Context context, String key, String defaultValue) {

        sp = context.getSharedPreferences(PREF_NAME, context.MODE_PRIVATE);
        String string = sp.getString(key, defaultValue);
        String result;
        if (!TextUtils.isEmpty(string)) {
            result = MD5Utils.encryptmd5(string);
        } else {
            result = "";
        }
        return result;
    }

    // 设置String类型的sp
    public static void setString(Context context, String key, String defaultValue) {
        sp = context.getSharedPreferences(PREF_NAME, context.MODE_PRIVATE);
        String encryptKey;
        if (!TextUtils.isEmpty(defaultValue)) {
            encryptKey = MD5Utils.encryptmd5(defaultValue);
        } else {
            encryptKey = "";
        }
        sp.edit().putString(key, encryptKey).commit();
    }

    // 获取int类型的sp
    public static int getInt(Context context, String key, int defaultValue) {

        sp = context.getSharedPreferences(PREF_NAME, context.MODE_PRIVATE);
        return sp.getInt(key, defaultValue);
    }

    // 设置int类型的sp
    public static void setInt(Context context, String key, int defaultValue) {
        sp = context.getSharedPreferences(PREF_NAME, context.MODE_PRIVATE);
        sp.edit().putInt(key, defaultValue).commit();
    }


    //清除sp内容
    public static void clear() {
        sp.edit().clear().commit();
    }
}
