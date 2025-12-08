package com.tancong.core.entity.dto;


import com.tancong.core.entity.Menu;
import com.tancong.core.entity.Role;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * ===================================
 * <p>
 * ===================================
 *
 * @author tancong
 * @version 1.0.0
 * @create 2025/12/04
 */
@Data
@Accessors(chain = true)  // ✅ 添加链式调用，与父类保持一致
public class RoleDTO extends Role {
    private List<Menu> menus;
}
