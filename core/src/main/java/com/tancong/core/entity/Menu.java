package com.tancong.core.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.tancong.core.entity.enums.MenuType;
import com.tancong.core.entity.enums.StatusEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * ===================================
 * 菜单实体类
 * ===================================
 *
 * @author tancong
 * @version 1.0.0
 * @create 2025/11/26
 */
@Data
@TableName(value = "menu")
@Accessors(chain = true) // 生成链式设置方法
@NoArgsConstructor      // ✅ 生成无参构造函数
@AllArgsConstructor     // ✅ 生成全参构造函数
public class Menu {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String name; // 菜单名称
    private MenuType type; // 菜单类型
    private Integer parentMenuId; // 上级菜单
    private String icon; // 菜单图标
    @TableField(value = "order")
    private int order; // 排序
    private String path; // 组件路径
    private String component; // 组件名称
    private String permission; // 权限许可
    private StatusEnum status; // 状态
}
