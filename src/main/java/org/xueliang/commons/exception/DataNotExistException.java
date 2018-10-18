package org.xueliang.commons.exception;

/**
 * @author XueLiang
 * @date 2018/9/4 23:06
 */
public class DataNotExistException extends BaseException {

    public DataNotExistException() {
        super("数据不存在");
    }

    public DataNotExistException(String message) {
        super(message);
    }
}
