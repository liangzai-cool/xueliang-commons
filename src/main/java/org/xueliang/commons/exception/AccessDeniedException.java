package org.xueliang.commons.exception;

/**
 * @author XueLiang
 * @date 2018/9/4 0:57
 */
public class AccessDeniedException extends BaseException {

    public AccessDeniedException() {
        super("无权操作");
    }
}
