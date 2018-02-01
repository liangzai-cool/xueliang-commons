package org.xueliang.commons.web;

import com.fasterxml.jackson.annotation.JsonFormat;

public class DefaultException extends Exception {

    private static final long serialVersionUID = 6304732905146351972L;
    
    private String errorCode;
    
    private Error error;
    
    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    public static enum Error {
        not_found("请求的资源不存在"),
        login_required("请先登录"),
        server_internal_error("服务器内部错误"),
        invalid_parameter("无效参数"),
        invalid_app_id("无效的appId"),
        invalid_passport("无效通行凭证"),
        duplicate_data("数据已存在"),
        access_denied("无权操作"),
        captcha_required("请输入验证码"),
        reached_the_limit("已达上限"),
        invalid_captcha("验证码错误");
        
        private String message;
        
        public String getMessage() {
            return message;
        }
        
        public void setMessage(String message) {
            this.message = message;
        }
        
        public String getName() {
            return this.name();
        }
        
        Error(String message){
            this.message = message;
        }
    }
    
    public DefaultException(Error error) {
        this(error.name(), error.message);
        this.error = error;
    }
    
    public DefaultException(String code, String message) {
        super(message);
        this.errorCode = code;
    }
    
    public DefaultException(Error error, String message) {
        this(error.name(), message);
        this.error = error;
        this.error.setMessage(message);
    }
    
    public String getErrorCode() {
        return errorCode;
    }

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }
}
