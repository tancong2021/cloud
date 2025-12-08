package com.tancong.core.entity.dto;

import com.tancong.core.entity.File;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * ===================================
 * 文件DTO - 用于接收前端上传参数
 * ===================================
 *
 * @author tancong
 * @version 1.0.0
 * @create 2025/12/08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class FileDTO extends File implements Serializable {

    /**
     * MD5值（前端计算后传递，用于秒传）
     */
    private String md5Hash;

    /**
     * 文件夹ID（可选，默认0表示根目录）
     */
    private Long targetFolderId;
}
