package org.xueliang.commons.exception;

/**
 * @author XueLiang
 * @date 2018/9/4 0:52
 */
public class ServerInternalException extends BaseException {

    public ServerInternalException() {
        this("服务器内部错误");
    }

    public ServerInternalException(String message) {
        super(message);
    }
}
