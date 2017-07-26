package com.dfqm.web.webdemo.entity;

import java.util.List;

/**
 * Created by Administrator on 2017/3/14.
 */

public class EventMessageBean {

    private String  msgId;
    private String msgContent;

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getMsgContent() {
        return msgContent;
    }

    public void setMsgContent(String msgContent) {
        this.msgContent = msgContent;
    }

    public EventMessageBean(String msgId, String msgContent) {
        this.msgId = msgId;
        this.msgContent = msgContent;
    }

    @Override
    public String toString() {
        return "EventMessageBean{" +
                "msgId=" + msgId +
                ", msgContent='" + msgContent + '\'' +
                '}';
    }
}
