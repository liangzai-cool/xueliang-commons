package org.xueliang.commons.util.servlet;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

public class HttpServletRequestUtils {

    public static final List<String> LOCALHOST_IP = new ArrayList<>();
    
    static {
        LOCALHOST_IP.add("127.0.0.1");  // ipv4
        LOCALHOST_IP.add("::1");        // ipv6
    }

    private HttpServletRequestUtils() {

    }
    
    public static String getIP(HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        if (!LOCALHOST_IP.contains(ip)) {
            return ip;
        }
        ip = request.getHeader("X_REAL_IP");
        if (StringUtils.isNotEmpty(ip)) {
            return ip;
        }
        return request.getRemoteAddr();
    }
}
