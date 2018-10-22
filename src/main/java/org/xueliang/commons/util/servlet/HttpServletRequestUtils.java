package org.xueliang.commons.util.servlet;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HttpServletRequestUtils {

    private static final Logger LOGGER = LogManager.getLogger(HttpServletRequestUtils.class);

    public static final List<String> LOCALHOST_IP = new ArrayList<>();
    
    static {
        LOCALHOST_IP.add("127.0.0.1");  // ipv4
        LOCALHOST_IP.add("::1");        // ipv6
    }

    private HttpServletRequestUtils() {

    }
    
    public static String getIP(HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        LOGGER.info("remote addr: {}", ip);
        if (!LOCALHOST_IP.contains(ip)) {
            return ip;
        }
        ip = request.getHeader("X-Real-IP");
        LOGGER.info("X-Real-IP header: {}", ip);
        if (StringUtils.isNotEmpty(ip)) {
            return ip;
        }
        ip = request.getHeader("X-Forwarded-For");
        LOGGER.info("X-Forwarded-For header: {}", ip);
        if (StringUtils.isNotEmpty(ip)) {
            return ip;
        }
        return request.getRemoteAddr();
    }
}
