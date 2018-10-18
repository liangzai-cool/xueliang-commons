package org.xueliang.commons.exception;

/**
 * @author XueLiang
 * @date 2018/9/4 0:52
 */
public class ServerInternalErrorException extends BaseException {

    public ServerInternalErrorException() {
        this("服务器内部错误");
    }

    public ServerInternalErrorException(String message) {
        super(message);
    }
}
