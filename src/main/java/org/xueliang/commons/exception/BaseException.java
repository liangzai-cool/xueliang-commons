package org.xueliang.commons.exception;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.apache.commons.lang3.StringUtils;

/**
 * @author XueLiang
 * @date 2018/9/4 0:43
 */
@JsonIgnoreProperties({"suppressed", "cause", "stackTrace", "localizedMessage"})
public class BaseException extends Exception {

    protected String name;

    protected String message;

    public BaseException() {
    }

    public BaseException(String message) {
        super(message);
        this.message = message;
        this.name = resolveName();
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String resolveName() {
        String clazzName = this.getClass().getSimpleName();
        String exceptionName = clazzName.substring(0, clazzName.lastIndexOf("Exception"));
        StringBuilder nameBuilder = new StringBuilder();
        for (int i = 0, len = exceptionName.length(); i < len; i++) {
            char letter = exceptionName.charAt(i);
            if (i == 0) {
                nameBuilder.append(Character.toLowerCase(letter));
            } else if (Character.isUpperCase(letter)) {
                nameBuilder.append("_").append(Character.toLowerCase(letter));
            } else {
                nameBuilder.append(letter);
            }
        }
        return nameBuilder.toString();
    }

    public String getName() {
        return this.name;
    }
}
