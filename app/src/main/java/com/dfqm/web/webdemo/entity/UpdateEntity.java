package com.dfqm.web.webdemo.entity;

/**
 * Created by Administrator on 2017/1/24.
 */

public class UpdateEntity {


    /**
     * versionCode : 2
     * versionName : 1.0.0
     * versonAddress : http://7xqjbb.com1.z0.glb.clouddn.com/app-release.apk
     * versonDetail : 2.0发布了，增加了实时聊天功能
     */

    private String versionCode;
    private String versionName;
    private String versonAddress;
    private String versonDetail;
    private String mainTain;

    public String getMainTain() {
        return mainTain;
    }

    public void setMainTain(String mainTain) {
        this.mainTain = mainTain;
    }

    public String getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(String versionCode) {
        this.versionCode = versionCode;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getVersonAddress() {
        return versonAddress;
    }

    public void setVersonAddress(String versonAddress) {
        this.versonAddress = versonAddress;
    }

    public String getVersonDetail() {
        return versonDetail;
    }

    public void setVersonDetail(String versonDetail) {
        this.versonDetail = versonDetail;
    }
}
