package org.xueliang.commons.exception;

/**
 * @author XueLiang
 * @date 2018/9/4 0:55
 */
public class InvalidPassportException extends BaseException {

    public InvalidPassportException() {
        super("无效通行凭证");
    }
}
