package com.tancong.core.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tancong.core.entity.enums.StatusEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * ===================================
 * 用户实体类
 * ===================================
 *
 * @author tancong
 * @version 1.0.0
 * @create 2025/11/26
 */
@Data
@TableName(value = "user")
@Accessors(chain = true) // 生成链式设置方法
@NoArgsConstructor      // ✅ 生成无参构造函数
@AllArgsConstructor     // ✅ 生成全参构造函数
public class User extends BaseEntity{
    @TableId(type = IdType.AUTO)
    private Long id;
    @JsonIgnore
    private String uuid;
    private String username;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    private String nickname;
    private String avatar;
    private String email;
    private StatusEnum status;
}
