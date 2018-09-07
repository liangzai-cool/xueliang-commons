package org.xueliang.commons.exception;

/**
 * @author XueLiang
 * @date 2018/9/4 22:58
 */
public class RequiredParameterException extends InvalidParameterException {

    public RequiredParameterException(String parameterName) {
        super(parameterName + " 不能为空");
    }
}
