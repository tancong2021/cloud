package com.tancong.common.utils;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * ===================================
 * 这是一个获取请求的工具类
 * Spring 把 HttpServletRequest 塞进当前现场的 ThreadLocal
 * ThreadLocal 的数据只对“当前线程”可见
 * ===================================
 *
 * @author tancong
 * @version 1.0.0
 * @create 2025/11/29
 */
public class ServletUtils {
    /**
     * 获取当前请求的 request 对象
     */
    public static HttpServletRequest getRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    }


    /**
     * 是用来 获取当前请求的协议（scheme）
     * @return
     */
    public static String getScheme() {
        return getRequest().getScheme();
    }

    public static String getClientIP(HttpServletRequest request) {
        if (request == null) return null;

        String[] headers = {
                "X-Forwarded-For",
                "X-Real-IP",
                "Proxy-Client-IP",
                "WL-Proxy-Client-IP",
                "HTTP_CLIENT_IP",
                "HTTP_X_FORWARDED_FOR"
        };

        for (String header : headers) {
            String ip = request.getHeader(header);
            if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {
                return ip.split(",")[0].trim(); // 多级代理取第一个 IP
            }
        }

        return request.getRemoteAddr();
    }
}
