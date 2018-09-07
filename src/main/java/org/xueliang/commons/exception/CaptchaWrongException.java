package org.xueliang.commons.exception;

/**
 * @author XueLiang
 * @date 2018/9/4 20:52
 */
public class CaptchaWrongException extends InvalidParameterException {

    public CaptchaWrongException() {
        super("验证码错误，请刷新后重试");
    }
}
