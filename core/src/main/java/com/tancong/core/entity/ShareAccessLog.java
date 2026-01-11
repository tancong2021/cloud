package com.tancong.core.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.tancong.core.entity.enums.ShareAccessTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * ===================================
 * 分享访问日志实体类
 * ===================================
 *
 * @author tancong
 * @version 1.0.0
 * @create 2025/12/25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName(value = "share_access_log")
@Accessors(chain = true) // 生成链式设置方法
@NoArgsConstructor      // 生成无参构造函数
@AllArgsConstructor     // 生成全参构造函数
public class ShareAccessLog {

    @TableId(type = IdType.AUTO)
    private Long id;

    // 分享ID
    private Long shareId;

    // 访问类型（枚举：1-查看, 2-下载, 3-提取码验证失败）
    private ShareAccessTypeEnum accessType;

    // 访问者IP地址
    private String ipAddress;

    // 用户代理(浏览器信息)
    private String userAgent;

    // 访问时间
    private LocalDateTime accessTime;

    // 是否成功（针对提取码验证）
    private Boolean success;

    // 备注
    private String remark;
}
