package com.tancong.core.entity.dto;

import com.tancong.core.entity.enums.ShareExpireTypeEnum;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * ===================================
 * 创建分享请求DTO
 * ===================================
 *
 * @author tancong
 * @version 1.0.0
 * @create 2025/12/25
 */
@Data
public class CreateShareDTO {

    @NotNull(message = "文件ID不能为空")
    private Long fileId;

    @NotNull(message = "有效期类型不能为空")
    private ShareExpireTypeEnum expireType;

    // 可选：自定义提取码（如果不提供则随机生成）
    private String extractCode;

    // 可选：备注
    private String remark;
}
