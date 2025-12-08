package com.tancong.core.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

/**
 * ===================================
 * 文件上传配置类
 * ===================================
 *
 * @author tancong
 * @version 1.0.0
 * @create 2025/12/08
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "file.upload")
public class FileUploadConfig {

    /**
     * 最大文件大小（字节），默认100MB
     */
    private Long maxFileSize = 100 * 1024 * 1024L;

    /**
     * 允许的文件类型（MIME类型白名单）
     */
    private List<String> allowedMimeTypes = Arrays.asList(
        "image/jpeg", "image/png", "image/gif", "image/webp",
        "application/pdf",
        "application/msword",
        "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
        "application/vnd.ms-excel",
        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
        "text/plain",
        "application/zip",
        "video/mp4"
    );

    /**
     * 允许的文件扩展名白名单
     */
    private List<String> allowedExtensions = Arrays.asList(
        "jpg", "jpeg", "png", "gif", "webp",
        "pdf",
        "doc", "docx",
        "xls", "xlsx",
        "txt",
        "zip",
        "mp4"
    );

    /**
     * COS存储路径前缀
     */
    private String cosPathPrefix = "files";
}
