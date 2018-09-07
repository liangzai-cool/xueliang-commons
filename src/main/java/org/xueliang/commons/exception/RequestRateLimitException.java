package org.xueliang.commons.exception;

/**
 * @author XueLiang
 * @date 2018/9/4 22:50
 */
public class RequestRateLimitException extends BaseException {

    public RequestRateLimitException() {
        super("请求过于频繁，请稍后再试");
    }
}
