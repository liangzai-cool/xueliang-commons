package org.xueliang.commons.web;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class JSONResponse {
    
    private Object data;
    private String returnUrl = "";
    private List<DefaultException.Error> errors = new ArrayList<DefaultException.Error>();

    public Object getData() {
        return data == null ? new Object() : data;
    }

    public void setData(Object data) {
        this.data = data;
    }
    
    @SuppressWarnings("unchecked")
    public JSONResponse putData(String key, Object value) {
        if (this.data == null || !(this.data instanceof HashMap<?, ?>)) {
            this.data = new HashMap<String, Object>();
        }
        ((HashMap<String, Object>) this.data).put(key, value);
        return this;
    }

    public String getReturnUrl() {
        return returnUrl;
    }

    public void setReturnUrl(String returnUrl) {
        this.returnUrl = returnUrl;
    }

    public List<DefaultException.Error> getErrors() {
        return errors;
    }

    public JSONResponse setErrors(List<DefaultException.Error> errors) {
        this.errors = errors;
        return this;
    }
    
    public JSONResponse addError(DefaultException.Error error) {
        this.errors.add(error);
        return this;
    }
    
    public void addError(DefaultException.Error error, String message) {
        error.setMessage(message);
        this.errors.add(error);
    }
    
    public Timestamp getServerTime() {
        return Timestamp.valueOf(LocalDateTime.now());
    }
}
