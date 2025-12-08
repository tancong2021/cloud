package com.tancong.core.service;

import com.tancong.core.entity.Role;

import java.util.List;
import java.util.Set;

/**
 * ===================================
 * 角色服务层相关接口
 * ===================================
 *
 * @author tancong
 * @version 1.0.0
 * @create 2025/11/30
 */
public interface RoleService extends BaseService<Role> {
    void updateRoleMenu(Set<Integer> menuIds, Integer roleId);

    /**
     * 根据用户ID查询所有的角色
     * @param userId
     * @return
     */
    List<Role> getByUserId(Integer userId);
}
