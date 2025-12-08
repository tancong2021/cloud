package com.tancong.core.entity.dto;

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

    private String username;
    private String password;
}
