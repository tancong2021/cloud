package com.tancong.core.entity.vo;

import com.tancong.core.entity.ShareAccessLog;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * ===================================
 * 分享访问日志VO
 * ===================================
 *
 * @author tancong
 * @version 1.0.0
 * @create 2025/12/25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class ShareAccessLogVO extends ShareAccessLog {

    // 可扩展字段（如IP归属地）
    private String ipLocation;
}
