-- ============================================
-- 用户表 (user)
-- ============================================
-- 功能: 存储系统用户基本信息
-- 包含: 用户名、密码、昵称、头像、邮箱等
-- ============================================

-- 设置客户端字符编码（重要！）
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;

-- 选择数据库
USE cloud_db;
DROP TABLE IF EXISTS `user`;
-- 创建用户表
CREATE TABLE `user` (
                        `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
                        `uuid` VARCHAR(64) NOT NULL COMMENT '用户唯一标识符',
                        `username` VARCHAR(100) NOT NULL COMMENT '用户名',
                        `password` VARCHAR(255) NOT NULL COMMENT '加密密码(BCrypt)',
                        `nickname` VARCHAR(100) DEFAULT NULL COMMENT '昵称',
                        `avatar` VARCHAR(500) DEFAULT NULL COMMENT '头像URL',
                        `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱地址',
                        `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 0-禁用, 1-启用, 2-删除, 3-锁定, 4-过期',
                        `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                        `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                        PRIMARY KEY (`id`),
                        UNIQUE KEY `uk_uuid` (`uuid`),
                        UNIQUE KEY `uk_username` (`username`),
                        INDEX `idx_email` (`email`),
                        INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- ============================================
-- 角色表 (role)
-- ============================================
-- 功能: 存储系统角色信息
-- 包含: 角色名称(中英文)、描述、状态
-- 用途: RBAC权限控制
-- ============================================
-- 如果表已存在则删除
DROP TABLE IF EXISTS `role`;
-- 创建角色表
CREATE TABLE `role` (
                        `id` INT NOT NULL AUTO_INCREMENT COMMENT '角色ID',
                        `name` VARCHAR(100) NOT NULL COMMENT '角色英文名(如: ROLE_ADMIN)',
                        `name_zh` VARCHAR(100) NOT NULL COMMENT '角色中文名(如: 管理员)',
                        `desc` VARCHAR(500) DEFAULT NULL COMMENT '角色描述',
                        `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 0-禁用, 1-启用, 2-删除',
                        `update_time` DATETIME,
                        `create_time` DATETIME,
                        PRIMARY KEY (`id`),
                        UNIQUE KEY `uk_name` (`name`),
                        INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色表';


-- ============================================
-- 菜单表 (menu)
-- ============================================
-- 功能: 存储系统菜单和权限信息
-- 包含: 菜单名称、类型、父级ID、路由、权限标识
-- 特性: 树形结构(自关联)、支持三级菜单
-- ============================================
-- 如果表已存在则删除
DROP TABLE IF EXISTS `menu`;
-- 创建菜单表
CREATE TABLE `menu` (
                        `id` INT NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
                        `name` VARCHAR(100) NOT NULL COMMENT '菜单名称',
                        `type` TINYINT NOT NULL COMMENT '菜单类型: 1-一级菜单, 2-页面, 3-按钮操作',
                        `parent_menu_id` INT DEFAULT 0 COMMENT '上级菜单ID(0表示顶级菜单)',
                        `icon` VARCHAR(100) DEFAULT NULL COMMENT '菜单图标(如: el-icon-user)',
                        `order` INT NOT NULL DEFAULT 0 COMMENT '显示顺序',
                        `path` VARCHAR(200) DEFAULT NULL COMMENT '路由路径(如: /user/list)',
                        `component` VARCHAR(200) DEFAULT NULL COMMENT '前端组件路径',
                        `permission` VARCHAR(100) DEFAULT NULL COMMENT '权限标识(如: user:create)',
                        `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 0-禁用, 1-启用, 2-删除',
                        PRIMARY KEY (`id`),
                        CONSTRAINT permission_unique UNIQUE (permission),
                        CONSTRAINT self_parent_menu FOREIGN KEY (parent_menu_id) REFERENCES menu (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='菜单表';

-- ============================================
-- 用户-角色关联表 (user_role)
-- ============================================
-- 功能: 用户与角色的多对多关联关系
-- 特性: 级联删除(删除用户时自动删除关联关系)
-- ============================================
-- 如果表已存在则删除
DROP TABLE IF EXISTS `user_role`;
-- 创建用户-角色关联表
CREATE TABLE `user_role` (
                             `user_id` BIGINT NOT NULL COMMENT '用户ID',
                             `role_id` INT NOT NULL COMMENT '角色ID',
                             PRIMARY KEY (`user_id`, `role_id`),
                             INDEX `idx_user_id` (`user_id`),
                             INDEX `idx_role_id` (`role_id`),
                             CONSTRAINT `fk_user_role_user` FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE,
                             CONSTRAINT `fk_user_role_role` FOREIGN KEY (`role_id`) REFERENCES `role`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户-角色关联表';

-- ============================================
-- 角色-菜单关联表 (role_menu)
-- ============================================
-- 功能: 角色与菜单的多对多关联关系
-- 用途: 实现RBAC权限控制
-- 特性: 级联删除(删除角色时自动删除关联关系)
-- ============================================
-- 如果表已存在则删除
DROP TABLE IF EXISTS `role_menu`;

-- 创建角色-菜单关联表
CREATE TABLE `role_menu` (
                             `role_id` INT NOT NULL COMMENT '角色ID',
                             `menu_id` INT NOT NULL COMMENT '菜单ID',
                             PRIMARY KEY (`role_id`, `menu_id`),
                             INDEX `idx_role_id` (`role_id`),
                             INDEX `idx_menu_id` (`menu_id`),
                             CONSTRAINT `fk_role_menu_role` FOREIGN KEY (`role_id`) REFERENCES `role`(`id`) ON DELETE CASCADE,
                             CONSTRAINT `fk_role_menu_menu` FOREIGN KEY (`menu_id`) REFERENCES `menu`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色-菜单关联表';


-- ============================================
-- 操作日志表 (log)
-- ============================================
-- 功能: 记录系统所有操作日志
-- 包含: 操作标题、内容、错误信息、耗时、操作人等
-- 特性: 通过AOP + @LogRecord注解自动记录
-- ============================================
-- 如果表已存在则删除
DROP TABLE IF EXISTS `log`;
-- 创建日志表
CREATE TABLE `log` (
                       `id` INT NOT NULL AUTO_INCREMENT COMMENT '日志ID',
                       `title` VARCHAR(200) NOT NULL COMMENT '日志标题',
                       `content` VARCHAR(1000) DEFAULT NULL COMMENT '日志内容',
                       `error` TEXT DEFAULT NULL COMMENT '错误信息',
                       `type` VARCHAR(50) DEFAULT NULL COMMENT '日志类型(如: LOGIN, CREATE, DELETE)',
                       `method` VARCHAR(255) DEFAULT NULL COMMENT '调用的方法名',
                       `ip` VARCHAR(50) DEFAULT NULL COMMENT '操作者IP地址',
                       `time` INT DEFAULT NULL COMMENT '耗时(毫秒)',
                       `user_id` BIGINT DEFAULT NULL COMMENT '操作人ID',
                       `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
                       PRIMARY KEY (`id`),
                       INDEX `idx_user_id` (`user_id`),
                       INDEX `idx_type` (`type`),
                       INDEX `idx_create_time` (`create_time`),
                       CONSTRAINT `fk_log_user` FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='操作日志表';

-- ===================================
-- 文件资源表
-- ===================================
-- 用于存储用户上传的文件信息
-- 支持文件秒传（通过 MD5 去重）
-- ===================================
-- 如果表已存在则删除
DROP TABLE IF EXISTS `file`;
-- 创建文件资源表
CREATE TABLE `file` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '文件ID',
  `uuid` VARCHAR(64) NOT NULL COMMENT '文件唯一标识符（用于缓存和安全访问）',
  `file_name` VARCHAR(255) NOT NULL COMMENT '文件名（用户上传时的原始文件名）',
  `file_size` BIGINT NOT NULL COMMENT '文件大小（字节）',
  `file_type` VARCHAR(100) COMMENT '文件MIME类型（如：image/jpeg, application/pdf）',
  `file_extension` VARCHAR(20) COMMENT '文件扩展名（如：jpg, pdf, docx）',
  `storage_path` VARCHAR(500) NOT NULL COMMENT 'COS存储路径（相对路径或完整路径）',
  `storage_bucket` VARCHAR(100) NOT NULL COMMENT 'COS存储桶名称',
  `user_id` BIGINT NOT NULL COMMENT '所属用户ID',
  `folder_id` BIGINT DEFAULT 0 COMMENT '所属文件夹ID（0表示根目录，预留字段）',
  `type` TINYINT NOT NULL DEFAULT 1 COMMENT '类型：1-文件，2-文件夹', -- 【新增字段】
  `md5` VARCHAR(32) NOT NULL COMMENT '文件MD5值（用于秒传和去重）',
  `download_count` INT DEFAULT 0 COMMENT '下载次数',
  `status` TINYINT DEFAULT 1 COMMENT '文件状态：0-已删除，1-正常，2-待审核',
  `thumbnail_path` VARCHAR(500) COMMENT '缩略图路径（图片/视频预留）',
  `remark` VARCHAR(500) COMMENT '备注信息',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_uuid` (`uuid`),
  INDEX `idx_user_id` (`user_id`),
  INDEX `idx_md5` (`md5`),
  INDEX `idx_status` (`status`),
  INDEX `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文件信息表';
