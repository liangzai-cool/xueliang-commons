package org.xueliang.commons.exception;

/**
 * @author XueLiang
 * @date 2018/9/4 0:57
 */
public class CaptchaRequiredException extends BaseException {

    public CaptchaRequiredException() {
        super("请输入验证码");
    }
}
