-- ============================================
-- 文件分享表 (share)
-- ============================================
-- 功能: 存储文件分享记录
-- 包含: 分享码、提取码、文件ID、有效期、状态、访问统计等
-- 特性: 支持提取码验证、有效期管理、访问统计
-- ============================================

-- 设置客户端字符编码
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;

-- 选择数据库
USE cloud_db;

-- 如果表已存在则删除
DROP TABLE IF EXISTS `share_access_log`;
DROP TABLE IF EXISTS `share`;

-- 创建文件分享表
CREATE TABLE `share` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '分享ID',
    `share_code` VARCHAR(16) NOT NULL COMMENT '分享码（8位唯一标识，用于URL访问）',
    `extract_code` VARCHAR(8) NOT NULL COMMENT '提取码（4位随机码，验证访问权限）',
    `file_id` BIGINT NOT NULL COMMENT '分享的文件ID',
    `user_id` BIGINT NOT NULL COMMENT '分享创建者用户ID',
    `expire_type` TINYINT NOT NULL COMMENT '有效期类型：1-7天, 2-30天, 3-永久',
    `expire_time` DATETIME DEFAULT NULL COMMENT '过期时间（永久有效则为NULL）',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '分享状态：0-已取消, 1-正常, 2-已过期',
    `view_count` INT NOT NULL DEFAULT 0 COMMENT '访问次数（查看次数）',
    `download_count` INT NOT NULL DEFAULT 0 COMMENT '下载次数',
    `failed_attempts` INT NOT NULL DEFAULT 0 COMMENT '提取码错误尝试次数（预留，主要使用Redis）',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注信息',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_share_code` (`share_code`),
    INDEX `idx_file_id` (`file_id`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_status` (`status`),
    INDEX `idx_expire_time` (`expire_time`),
    INDEX `idx_create_time` (`create_time`),
    CONSTRAINT `fk_share_file` FOREIGN KEY (`file_id`) REFERENCES `file`(`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_share_user` FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文件分享表';

-- ============================================
-- 分享访问日志表 (share_access_log)
-- ============================================
-- 功能: 记录分享的所有访问行为
-- 包含: 访问类型、IP地址、浏览器信息、访问时间、是否成功
-- 用途: 审计追踪、统计分析、安全监控
-- ============================================

-- 创建分享访问日志表
CREATE TABLE `share_access_log` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '日志ID',
    `share_id` BIGINT NOT NULL COMMENT '分享ID',
    `access_type` TINYINT NOT NULL COMMENT '访问类型：1-查看, 2-下载, 3-验证失败',
    `ip_address` VARCHAR(50) NOT NULL COMMENT '访问者IP地址',
    `user_agent` VARCHAR(500) DEFAULT NULL COMMENT '用户代理（浏览器信息）',
    `access_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '访问时间',
    `success` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否成功（针对提取码验证）',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注（如错误信息）',
    PRIMARY KEY (`id`),
    INDEX `idx_share_id` (`share_id`),
    INDEX `idx_ip_address` (`ip_address`),
    INDEX `idx_access_time` (`access_time`),
    INDEX `idx_access_type` (`access_type`),
    CONSTRAINT `fk_log_share` FOREIGN KEY (`share_id`) REFERENCES `share`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='分享访问日志表';

-- ============================================
-- 初始化说明
-- ============================================
-- 表特性:
-- 1. share_code: 唯一索引，用于URL访问，防止冲突
-- 2. expire_time: 可为NULL，表示永久有效
-- 3. 外键约束: CASCADE DELETE，文件删除时自动删除分享
-- 4. 索引优化: file_id, user_id, status, expire_time 加速查询
-- 5. 访问日志: 记录所有访问行为，支持审计和统计
-- ============================================
