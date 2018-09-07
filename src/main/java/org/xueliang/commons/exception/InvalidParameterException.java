package org.xueliang.commons.exception;

/**
 * @author XueLiang
 * @date 2018/9/4 0:53
 */
public class InvalidParameterException extends BaseException {

    public InvalidParameterException() {
        super("无效参数");
    }

    public InvalidParameterException(String message) {
        super(message);
    }
}
