package com.tancong.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tancong.common.exception.SQLOperateException;
import com.tancong.core.entity.Role;
import com.tancong.core.entity.dto.RoleDTO;
import com.tancong.core.entity.vo.Pager;
import com.tancong.core.entity.vo.RoleVO;
import com.tancong.core.mapper.RoleMapper;
import com.tancong.core.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * ===================================
 * 角色服务实现类
 * ===================================
 *
 * @author tancong
 * @version 1.0.0
 * @create 2025/11/30
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {
    @Autowired
    RoleMapper roleMapper;

    /**
     * 给角色添加菜单
     * @param menuIds
     * @param roleId
     */
    @Override
    @Transactional
    public void updateRoleMenu(Set<Integer> menuIds, Integer roleId) {
        // 先清除所有的角色信息
        this.clearRoleMenu(roleId);
        // 在进行新的插入
        this.addRoleMenus(roleId, menuIds);
    }

    /**
     * 清除角色所有菜单权限
     * @param roleId 角色id
     */
    public void clearRoleMenu(Integer roleId) {
        roleMapper.clearRoleMenu(roleId);
    }

    /**
     * 给角色添加菜单权限
     * @param roleId  角色id
     * @param menuIds 菜单id集合
     * @return
     */
    public int addRoleMenus(Integer roleId, Set<Integer> menuIds) {
        for (Integer menuId : menuIds) {
            try {
                int i = roleMapper.insertRoleMenu(roleId, menuId);
            } catch (Exception e) {
                throw new SQLOperateException("保存失败###选择的菜单不存在");
            }
        }
        return 1;
    }

    /**
     * 分页查询
     * @param pager
     * @param wrapper
     * @return
     */
    @Override
    public Pager<Role> listOfPage(Pager<Role> pager, QueryWrapper<Role> wrapper) {
        return roleMapper.selectPage(pager, wrapper);
    }

    /**
     * 查询用户角色列表
     * @param userId
     * @return
     */
    @Override
    public List<Role> getByUserId(Integer userId) {
        return roleMapper.selectByUserId(userId);
    }

}
