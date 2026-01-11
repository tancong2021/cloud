package com.tancong.core.entity.dto;

import com.tancong.core.entity.enums.ShareExpireTypeEnum;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * ===================================
 * 修改分享设置DTO
 * ===================================
 *
 * @author tancong
 * @version 1.0.0
 * @create 2025/12/25
 */
@Data
public class UpdateShareDTO {

    @NotNull(message = "分享ID不能为空")
    private Long shareId;

    // 可选：更新提取码
    private String extractCode;

    // 可选：更新有效期类型
    private ShareExpireTypeEnum expireType;

    // 可选：更新备注
    private String remark;
}
