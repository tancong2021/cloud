package com.tancong.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tancong.core.entity.Menu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * ===================================
 * 菜单数据访问层，主要查询菜单的一些数据
 * ===================================
 *
 * @author tancong
 * @version 1.0.0
 * @create 2025/11/30
 */
@Mapper
public interface MenuMapper extends BaseMapper<Menu> {


    @Select("SELECT m.* FROM `role_menu` rm " +
            "LEFT JOIN `menu` m ON rm.menu_id = m.id " +
            "WHERE rm.role_id = #{roleId}")
    List<Menu> selectAllByRoleId(Integer roleId);
}
