package com.tancong.core.entity.vo;


import com.baomidou.mybatisplus.annotation.TableField;
import com.tancong.common.exception.SQLOperateException;
import com.tancong.core.entity.Role;
import com.tancong.core.entity.enums.StatusEnum;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Set;

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
public class RoleVO extends Role {
    @TableField(exist = false)
    private Integer userId;
    // @TableField(exist = false)
    private Set<Integer> menuIds;

    public RoleVO(Integer id, String name, String nameZh, String desc, StatusEnum status) {
        super(id, name, nameZh, desc, status);
    }

    public Integer getUserId() {
        return userId;
    }

    public RoleVO setUserId(Integer userId) {
        this.userId = userId;
        return this;
    }

    public Set<Integer> getMenuIds() {
        return menuIds;
    }

    public RoleVO setMenuIds(Set<Integer> menuIds) {
        this.menuIds = menuIds;
        return this;
    }

    @Override
    public Role setId(Integer id) {
        if (id == 1) throw new SQLOperateException("系统保留数据，请勿操作");
        return super.setId(id);
    }
}
