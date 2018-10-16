package org.xueliang.commons.support.verifier.model;

import org.json.JSONObject;

import java.io.Serializable;


public class BaseCaptcha implements Serializable {

    private static final long serialVersionUID = 1L;
    
    public JSONObject toJSONObject() {
        return new JSONObject(this);
    }
    public String toJSONString() {
        return toJSONObject().toString();
    }
    @Override
    public String toString() {
        return toJSONString();
    }
}
