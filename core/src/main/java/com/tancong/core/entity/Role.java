package com.tancong.core.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.tancong.core.entity.enums.StatusEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * ===================================
 * 角色实体类
 * ===================================
 *
 * @author tancong
 * @version 1.0.0
 * @create 2025/11/26
 */
@Data
@NoArgsConstructor      // ✅ 生成无参构造函数
@AllArgsConstructor     // ✅ 生成全参构造函数
@TableName(value = "role")
@Accessors(chain = true) // 生成链式设置方法
public class Role {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String name;
    private String nameZh;
    @TableField(value = "`desc`")
    private String desc;
    private StatusEnum status;
}
