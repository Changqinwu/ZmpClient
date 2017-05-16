package com.dfqm.web.webdemo.entity;

/**
 * Created by Administrator on 2017/1/24.
 */

public class AuthorizeEntity {


    /**
     * success : true
     * sid :  12
     */

    private boolean success;
    private String sid;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }
}
