package com.tancong.core.entity.dto;

import com.tancong.core.entity.Role;
import com.tancong.core.entity.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tancong.core.entity.Menu;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * ===================================
 * 数据传输层UserDTO
 * ===================================
 *
 * @author tancong
 * @version 1.0.0
 * @create 2025/11/26
 */
@Data
@Accessors(chain = true)  // ✅ 添加链式调用，与父类保持一致
public class UserDTO extends User implements Serializable {
    @JsonIgnore
    private String password;
    private String roleIds;
    private List<Role> roles;
    private List<Menu> menus;
}
