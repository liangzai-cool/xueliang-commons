package org.xueliang.commons.exception;

/**
 * @author XueLiang
 * @date 2018/9/4 0:59
 */
public class InvalidCaptchaException extends BaseException {

    public InvalidCaptchaException() {
        super("验证码错误");
    }
}
