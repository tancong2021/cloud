package com.tancong.core.entity.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * ===================================
 * 验证提取码请求DTO
 * ===================================
 *
 * @author tancong
 * @version 1.0.0
 * @create 2025/12/25
 */
@Data
public class VerifyShareDTO {

    @NotBlank(message = "分享码不能为空")
    private String shareCode;

    @NotBlank(message = "提取码不能为空")
    private String extractCode;
}
