package org.xueliang.commons.exception;

/**
 * @author XueLiang
 * @date 2018/9/4 0:57
 */
public class AccessDeniedException extends BaseException {

    public AccessDeniedException() {
        this("无权操作");
    }

    public AccessDeniedException(String message) {
        super(message);
    }
}
