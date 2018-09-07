package org.xueliang.commons.exception;

/**
 * @author XueLiang
 * @date 2018/9/4 0:47
 */
public class LoginRequiredException extends BaseException {

    public LoginRequiredException() {
        super("请先登录");
    }
}
