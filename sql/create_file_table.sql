-- ===================================
-- 文件信息表 DDL
-- ===================================
-- 用于存储用户上传的文件信息
-- 支持文件秒传（通过 MD5 去重）
-- ===================================

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
