package org.xueliang.commons.exception;

/**
 * @author XueLiang
 * @date 2018/9/4 0:43
 */
public class NotFoundException extends BaseException {

    public NotFoundException() {
        super("请求的资源不存在");
    }
}
