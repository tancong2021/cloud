package com.tancong.core.service;

import com.tancong.core.entity.dto.MenuDTO;
import com.tancong.core.entity.Menu;

import java.util.List;
import java.util.Set;

/**
 * ===================================
 * 菜单服务层相关接口
 * ===================================
 *
 * @author tancong
 * @version 1.0.0
 * @create 2025/11/30
 */
public interface MenuService extends BaseService<Menu>{
    /**
     * 删除单个菜单项
     * 若菜单有子节点，应先检查或级联删除。
     * 推荐在事务中执行并返回删除结果
     * @param id
     * @return
     */
    int removeById(Integer id);

    int removeByIds(Set<Integer> ids);

    List<Menu> getByParentId(Integer parentId);

    void refreshMenus();

    List<Menu> getByParentId(Integer parentId, boolean getChild);

    List<MenuDTO> getByParentIdOfTree(Integer parentId, boolean includeButton);

    /**
     * 查询某个角色能访问的菜单（通常只返回菜单实体，不包含树结构）。
     * @param roleId
     * @return
     */
    List<Menu> getByRoleId(Integer roleId);

    List<Menu> getByRoleIds(String roleId);

    /**
     * 把扁平的 Menu 列表构造成 树形的 MenuDTO 列表（根节点集合）。
     * 通常用于前端菜单渲染时生成嵌套的菜单结构。
     * @param menuList
     * @return
     */
    List<MenuDTO> buildTree(List<Menu> menuList);

    List<MenuDTO> buildTree(List<Menu> menuList, Integer parentId);

    List<MenuDTO> buildTree(List<Menu> menuList, Integer parentId, boolean includeParent);

    /**
     * 获取系统中所有菜单（可选择是否包含 BUTTON 类型的菜单项）。通常用于管理后台或管理员权限。
     * @param includeButton
     * @return
     */
    List<Menu> getAll(boolean includeButton);

    /**
     * 获取所有启用（enabled = true）的菜单项，includePermission 决定是否加载 permission 字段
     * @param includePermission
     * @return
     */
    List<Menu> getAllEnable(boolean includePermission);
}
