package com.tancong.core.entity.vo;

import com.tancong.core.entity.File;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * ===================================
 * 文件VO - 返回给前端的文件信息
 * ===================================
 *
 * @author tancong
 * @version 1.0.0
 * @create 2025/12/08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class FileVO extends File implements Serializable {

    /**
     * 文件下载URL（临时签名URL或永久URL）
     */
    private String downloadUrl;

    /**
     * 文件预览URL（图片、视频等）
     */
    private String previewUrl;

    /**
     * 缩略图URL
     */
    private String thumbnailUrl;

    /**
     * 人类可读的文件大小（如：1.5 MB）
     */
    private String fileSizeFormatted;

    /**
     * 上传者用户名
     */
    private String uploaderName;
}
