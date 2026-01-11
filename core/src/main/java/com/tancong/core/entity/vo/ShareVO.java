package com.tancong.core.entity.vo;

import com.tancong.core.entity.Share;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * ===================================
 * 分享详情VO（给分享创建者）
 * ===================================
 *
 * @author tancong
 * @version 1.0.0
 * @create 2025/12/25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class ShareVO extends Share implements Serializable {

    // 文件信息（关联查询）
    private FileVO fileInfo;

    // 分享者用户名
    private String sharerName;

    // 分享链接（完整URL）
    private String shareUrl;

    // 是否已过期（计算属性）
    private Boolean expired;

    // 剩余有效天数（-1表示永久）
    private Integer remainingDays;
}
