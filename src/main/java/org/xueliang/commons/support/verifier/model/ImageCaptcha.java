package org.xueliang.commons.support.verifier.model;

import org.xueliang.commons.enums.ActionEnum;

import java.time.LocalDateTime;

/**
 * @author XueLiang
 * @date 2018/9/14 0:24
 */
public class ImageCaptcha extends BaseCaptcha {

    private String id;

    private String value;

    private LocalDateTime createdTime;

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

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }

    public ActionEnum getAction() {
        return action;
    }

    public void setAction(ActionEnum action) {
        this.action = action;
    }
}
