package com.tancong.common.utils;


import org.springframework.web.util.HtmlUtils;

/**
 * ===================================
 * <p>
 * ===================================
 *
 * @author tancong
 * @version 1.0.0
 * @create 2025/12/23
 */
public class FileSecurityUtils {

    /**
     * 文件名净化防止 XSS
     */
    public static String sanitizeFilename(String filename) {
        if (filename == null) return null;

        // HTML 转义
        String safe = HtmlUtils.htmlEscape(filename);

        // 移除危险字符
        safe = safe.replaceAll("[<>\"'&]", "_");

        // 限制长度
        if (safe.length() > 255) {
            safe = safe.substring(0, 255);
        }

        return safe;
    }
}
