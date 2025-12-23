package com.tancong.core.entity.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * ========================================
 * 数据传输对象，接受前端传过来数据的用户登入实体类
 * ========================================
 *
 * @author tancong
 * @version 1.0.0
 * @create 2025/11/26
 */
@Data
public class LoginUserDTO {
    @NotBlank(message = "用户名不能为空")
    private String username;
    @NotBlank(message = "密码不能为空")
    private String password;
}
