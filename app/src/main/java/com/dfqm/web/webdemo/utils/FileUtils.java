package com.dfqm.web.webdemo.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * Created by Administrator on 2017/3/30.
 */

public class FileUtils {

    /**
     * 递归删除文件和文件夹
     *
     * @param file 要删除的根目录
     */
    public static void RecursionDeleteFile(File file) {
        if (file.isFile()) {
            file.delete();
            return;
        }
        if (file.isDirectory()) {
            File[] childFile = file.listFiles();
            if (childFile == null || childFile.length == 0) {
                file.delete();
                return;
            }
            for (File f : childFile) {
                RecursionDeleteFile(f);
            }
            file.delete();
        }
    }


    /*
    *    遍历外部存储文件
    *
    * */
    public static ArrayList<String> getSpecificTypeOfFile(Context context, String[] extension, ArrayList<String> Filelists) {
        //从外存中获取
        Uri fileUri = MediaStore.Files.getContentUri("external");
        //筛选列，这里只筛选了：文件路径和不含后缀的文件名
        String[] projection = new String[]{
                MediaStore.Files.FileColumns.DATA, MediaStore.Files.FileColumns.TITLE
        };
        //构造筛选语句
        String selection = "";
        for (int i = 0; i < extension.length; i++) {
            if (i != 0) {
                selection = selection + " OR ";
            }
            selection = selection + MediaStore.Files.FileColumns.DATA + " LIKE '%" + extension[i] + "'";
        }
        //按时间递增顺序对结果进行排序;待会从后往前移动游标就可实现时间递减
        String sortOrder = MediaStore.Files.FileColumns.DATE_MODIFIED;
        //获取内容解析器对象
        ContentResolver resolver = context.getContentResolver();
        //获取游标
        Cursor cursor = resolver.query(fileUri, projection, selection, null, sortOrder);
        if (cursor != null){
            //游标从最后开始往前递减，以此实现时间递减顺序（最近访问的文件，优先显示）
            if (cursor.moveToLast()) {
                do {
                    //输出文件的完整路径
                    String data = cursor.getString(0);
                    Filelists.add(data);
                } while (cursor.moveToPrevious());

            }
            cursor.close();
            ToastUtil.show(context,"扫描到："+Filelists.size()+"个文件");
        }
        return Filelists;
    }

    /**
     * byte(字节)根据长度转成kb(千字节)和mb(兆字节)
     *
     * @param bytes
     * @return
     */
    public static String bytes2kb(long bytes) {
        BigDecimal filesize = new BigDecimal(bytes);
        BigDecimal megabyte = new BigDecimal(1024 * 1024);
        float returnValue = filesize.divide(megabyte, 2, BigDecimal.ROUND_UP)
                .floatValue();
        if (returnValue > 1)
            return (returnValue + "MB");
//            return  returnValue;
        BigDecimal kilobyte = new BigDecimal(1024);
        returnValue = filesize.divide(kilobyte, 2, BigDecimal.ROUND_UP)
                .floatValue();
        return (returnValue + "KB");
//        return returnValue;
    }

    /**
     * 获取设备唯一标识
     */

    public String getUniquePsuedoID() {
        String m_szDevIDShort = "35" +
                // 主板
                Build.BOARD +
                // android系统定制商
                Build.BRAND +
                // cpu指令集
                Build.CPU_ABI +
                // 设备参数
                Build.DEVICE +
                // 显示屏参数
                Build.DISPLAY +
                Build.HOST +
                // 修订版本列表
                Build.ID +
                // 硬件制造商
                Build.MANUFACTURER +
                // 版本
                Build.MODEL+
                // 手机制造商
                Build.PRODUCT +
                // 描述build的标签
                Build.TAGS +
                // builder类型
                Build.TYPE+
                Build.USER; //13 位

//        //A hardware serial number, if available. Alphanumeric only, case-insensitive.
//        String serial = Build.SERIAL;
        //使用硬件信息拼凑出来的15位号码

        return m_szDevIDShort;
    }


    /**
     * 获取指定字段信息
     * @return
     */
    public String getDeviceInfo(){
        StringBuffer sb =new StringBuffer();
        sb.append("主板："+Build.BOARD);
        sb.append("\n系统启动程序版本号："+ Build.BOOTLOADER);
        sb.append("\n系统定制商："+Build.BRAND);
        sb.append("\ncpu指令集："+Build.CPU_ABI);
        sb.append("\ncpu指令集2："+Build.CPU_ABI2);
        sb.append("\n设置参数："+Build.DEVICE);
        sb.append("\n显示屏参数："+Build.DISPLAY);
        sb.append("\n无线电固件版本："+Build.getRadioVersion());
        sb.append("\n硬件识别码："+Build.FINGERPRINT);
        sb.append("\n硬件名称："+Build.HARDWARE);
        sb.append("\nHOST:"+Build.HOST);
        sb.append("\n修订版本列表："+Build.ID);
        sb.append("\n硬件制造商："+Build.MANUFACTURER);
        sb.append("\n版本："+Build.MODEL);
        sb.append("\n硬件序列号："+Build.SERIAL);
        sb.append("\n手机制造商："+Build.PRODUCT);
        sb.append("\n描述Build的标签："+Build.TAGS);
        sb.append("\nTIME:"+Build.TIME);
        sb.append("\nbuilder类型："+Build.TYPE);
        sb.append("\nUSER:"+Build.USER);
        return sb.toString();
    }

}
