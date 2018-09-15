package org.xueliang.commons.exception;

/**
 * @author XueLiang
 * @date 2018/9/4 20:47
 */
public class CaptchaExpiredException extends ExpiredException {

    public CaptchaExpiredException() {
        this("验证码已过期，请刷新后重试");
    }

    public CaptchaExpiredException(String message) {
        super(message);
    }
}
