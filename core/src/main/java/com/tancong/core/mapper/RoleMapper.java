package com.tancong.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tancong.core.entity.Role;
import com.tancong.core.entity.dto.RoleDTO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * ===================================
 * Role数据访问层
 * ===================================
 *
 * @author tancong
 * @version 1.0.0
 * @create 2025/11/26
 */
@Mapper
public interface RoleMapper extends BaseMapper<Role> {
    /**
     * 通过用户id查询角色id和名称
     * 在 UserMapper 中调用
     * @param userId
     * @return
     */
    @Select("select r.id, r.name from role r " +
            " left join user_role ur on r.id = ur.role_id" +
            " left join user u on ur.user_id = u.id" +
            " where u.id = #{userId}")
    List<Role> selectIdAndNameByUserId(Integer userId);

    /**
     * 通过用户 id 查询角色信息
     * @param userId
     * @return
     */
    @Select("SELECT r.* FROM role r " +
            " LEFT JOIN user_role ur ON ur.role_id = r.id" +
            " LEFT JOIN `user` u ON ur.user_id = u.id" +
            " WHERE u.id = #{userId}")
    List<Role> selectByUserId(Integer userId);

    @Select("SELECT * FROM `role` WHERE role.id = #{userId}")
    RoleDTO findIncludeMenuByUserId(Integer userId);

    /**
     * 清除角色权限
     *
     * @param roleId
     * @return
     */
    @Delete("DELETE FROM `role_menu` WHERE `role_id` = #{roleId}")
    int clearRoleMenu(Integer roleId);

    @Insert("INSERT INTO `role_menu`(`role_id`, `menu_id`) VALUES (#{roleId}, #{menuId})")
    int insertRoleMenu(Integer roleId, Integer menuId);
}
