package com.tancong.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tancong.core.entity.User;
import com.tancong.core.entity.dto.UserDTO;
import com.tancong.core.entity.enums.UserOpenType;
import org.apache.ibatis.annotations.*;

/**
 * ===================================
 * User数据访问层，用来访问数据库中User数据
 * ===================================
 *
 * @author tancong
 * @version 1.0.0
 * @create 2025/11/26
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {


    @Select("select u.* from user u " +
            "where u.username=#{username} AND u.password=#{password}")
    @Results(id = "userRoleMap", value = {// @Result 注解描述了如何将查询结果的列映射到结果对象的属性。
            @Result(id = true,
                    property = "id", // Java对象对应的属性项
                    column = "id"), // 查询的数据项
            // 关联映射：一个用户有多个角色
            @Result(property = "roles",
                    column = "id",
                    many = @Many(select = "com.tancong.cloud.core.mapper.RoleMapper.selectIdAndNameByUserId")),
    })
    UserDTO selectByUsernameAndPassword(User user);

    /**
     * 通过用户名查询用户信息
     */
    @Select("SELECT u.* FROM user u " +
            " WHERE u.username = #{username}")
    @ResultMap(value = "userRoleMap")
    UserDTO selectByUsername(String username);


    /**
     * qq快捷登录
     */
    @Select("SELECT u.* FROM user u LEFT JOIN user_open o ON u.id = o.user_id" +
            " WHERE o.type = #{type} AND o.open_id = #{openId}")
    @ResultMap(value = "userRoleMap")
    UserDTO selectByOpenId(@Param("openId") String openId, @Param("type") UserOpenType type);

    /**
     * 将用户状态改为已删除
     */
    @Update("UPDATE user SET status = 2 WHERE id = #{userId}")
    int updateUserStatusToDeleted(Integer userId);

    /**
     * 删除所有的用户角色信息
     */
    @Delete("DELETE FROM user_role WHERE user_id = #{userId}")
    void deleteUserRole(Long userId);

    /**
     * 插入用户角色信息
     */
    @Insert("INSERT INTO user_role(user_id, role_id) VALUES (#{userId}, #{roleId})")
    int insertUserRole(Long userId, Integer roleId); // 返回插入成功行数
}
