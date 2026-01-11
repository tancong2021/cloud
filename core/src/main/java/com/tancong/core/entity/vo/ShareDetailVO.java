package com.tancong.core.entity.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * ===================================
 * 公共访问分享详情VO（验证提取码后返回）
 * ===================================
 *
 * @author tancong
 * @version 1.0.0
 * @create 2025/12/25
 */
@Data
@Accessors(chain = true)
public class ShareDetailVO implements Serializable {

    // 分享码
    private String shareCode;

    // 文件信息（不包含敏感字段）
    private FileVO fileInfo;

    // 如果是文件夹，递归包含子文件列表
    private List<FileVO> children;

    // 分享者名称（脱敏）
    private String sharerName;

    // 过期时间
    private String expireTime;

    // 访问次数
    private Integer viewCount;
}
