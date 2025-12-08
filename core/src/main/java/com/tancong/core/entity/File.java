package com.tancong.core.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tancong.core.entity.enums.FileStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * ===================================
 * 文件实体类
 * ===================================
 *
 * @author tancong
 * @version 1.0.0
 * @create 2025/12/08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName(value = "file")
@Accessors(chain = true) // 生成链式设置方法
@NoArgsConstructor      // 生成无参构造函数
@AllArgsConstructor     // 生成全参构造函数
public class File extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String uuid;

    private String fileName;

    private Long fileSize;

    private String fileType;

    private String fileExtension;

    @JsonIgnore  // 存储路径不返回给前端（安全考虑）
    private String storagePath;

    @JsonIgnore
    private String storageBucket;

    private Long userId;

    private Long folderId;

    @JsonIgnore  // MD5值不返回给前端
    private String md5;

    private Integer downloadCount;

    private FileStatusEnum status;

    private String thumbnailPath;

    private String remark;
}
