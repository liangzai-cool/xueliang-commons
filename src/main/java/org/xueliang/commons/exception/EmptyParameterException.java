package org.xueliang.commons.exception;

/**
 * @author XueLiang
 * @date 2018/9/4 20:33
 */
public class EmptyParameterException extends InvalidParameterException {

    public EmptyParameterException(String parameterName) {
        super(parameterName + " 不能为空");
    }
}
