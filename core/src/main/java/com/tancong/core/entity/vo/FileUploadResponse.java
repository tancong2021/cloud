package com.tancong.core.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * ===================================
 * 文件上传响应对象
 * ===================================
 *
 * @author tancong
 * @version 1.0.0
 * @create 2025/12/08
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class FileUploadResponse implements Serializable {

    /**
     * 文件ID
     */
    private Long fileId;

    /**
     * 文件UUID
     */
    private String uuid;

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 文件大小
     */
    private Long fileSize;

    /**
     * 文件访问URL
     */
    private String fileUrl;

    /**
     * 是否秒传
     */
    private Boolean quickUpload;

    /**
     * 提示信息
     */
    private String message;
}
