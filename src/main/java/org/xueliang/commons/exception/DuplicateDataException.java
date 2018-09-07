package org.xueliang.commons.exception;

/**
 * @author XueLiang
 * @date 2018/9/4 0:56
 */
public class DuplicateDataException extends BaseException {

    public DuplicateDataException() {
        super("数据已存在");
    }

    public DuplicateDataException(String message) {
        super(message);
    }
}
