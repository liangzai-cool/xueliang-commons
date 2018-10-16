package org.xueliang.commons.support.verifier.model;

import org.xueliang.commons.enums.ActionEnum;

import java.time.LocalDateTime;

public class EmailCaptcha extends BaseCaptcha {
    
    private static final long serialVersionUID = 1L;

    private String id;
    
    private String value;
    
    private String email;
    
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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
