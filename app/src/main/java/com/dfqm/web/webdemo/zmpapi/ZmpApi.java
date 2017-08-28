package com.dfqm.web.webdemo.zmpapi;

/**
 * Created by Administrator on 2017/1/24.
 */

public class ZmpApi {
    //主界面
    public static final String main_url = "http://i.zimeiping.com/Vshop/GetRotatorSetting.aspx?sid=";
    //更新appApi
    public static final String update_url = "http://i.zimeiping.com/API/CheckUpdateZMP.Json";
    //是否维护界面
    public static final String maintain_url = "http://999.zimeiping.com/wcq/zmp/maintaintext.txt";
    //授权界面
    public static final String authroize_url = "http://i.zimeiping.com/API/PRequestRotator.ashx?action=CheckSkeyExist&skey=";
    //测试更新
//    public static final String update_url = "http://999.zimeiping.com/wcq/zmp/zmp-version.txt";
    //二维码地址
    public static final String qr_url = "http://i.zimeiping.com/Personal/GetScreenQRImage.aspx?skey=";
    //用户协议
    public static final String agreement_url = "http://999.zimeiping.com/wcq/useragreement/agreement.html";
    //视频列表
    public static final String video_url = "http://999.zimeiping.com/wcq/zmp/videolist.txt";

    //测试界面
//    public static final String main_url = "http://999.zimeiping.com/hhw/zmp_app_banner/GetRotatorSetting.html?sid=";

    //竖屏横屏二维码
    public static final String horizontal_vertical_url = "http://999.zimeiping.com/hhw/zmp_app_banner/ScreenDetailQRImage.html?sid=";
}
