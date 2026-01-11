package com.tancong.core.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.tancong.core.entity.enums.ShareExpireTypeEnum;
import com.tancong.core.entity.enums.ShareStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * ===================================
 * 文件分享实体类
 * ===================================
 *
 * @author tancong
 * @version 1.0.0
 * @create 2025/12/25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName(value = "share")
@Accessors(chain = true) // 生成链式设置方法
@NoArgsConstructor      // 生成无参构造函数
@AllArgsConstructor     // 生成全参构造函数
public class Share extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    // 分享唯一码(8位随机字符串，用于URL访问)
    private String shareCode;

    // 提取码(4位随机数字/字母)
    private String extractCode;

    // 分享的文件ID
    private Long fileId;

    // 分享创建者用户ID
    private Long userId;

    // 有效期类型（枚举：7天、30天、永久）
    private ShareExpireTypeEnum expireType;

    // 过期时间（具体时间戳，根据expireType计算，永久则为NULL）
    private LocalDateTime expireTime;

    // 分享状态（枚举：0-已取消, 1-正常, 2-已过期）
    private ShareStatusEnum status;

    // 访问次数统计
    private Integer viewCount;

    // 下载次数统计
    private Integer downloadCount;

    // 提取码错误尝试次数（预留字段，主要使用Redis）
    private Integer failedAttempts;

    // 备注信息
    private String remark;
}
