package org.xueliang.commons.support.verifier.model;

import org.xueliang.commons.enums.ActionEnum;

import java.time.LocalDateTime;

public class SMSCaptcha extends BaseCaptcha {
    
    private static final long serialVersionUID = 1L;

    private String id;
    
    private String value;
    
    private String smsId;
    
    private String mobile;
    
    private LocalDateTime sendTime;
    
    private ActionEnum action;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getSmsId() {
        return smsId;
    }

    public void setSmsId(String smsId) {
        this.smsId = smsId;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public LocalDateTime getSendTime() {
        return sendTime;
    }

    public void setSendTime(LocalDateTime sendTime) {
        this.sendTime = sendTime;
    }

    public ActionEnum getAction() {
        return action;
    }

    public void setAction(ActionEnum action) {
        this.action = action;
    }
}
