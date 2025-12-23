-- ============================================
-- 云存储后台管理系统 - 初始化数据脚本
-- ============================================
-- 包含:
-- 1. 默认角色 (管理员、普通用户、游客)
-- 2. 默认用户 (admin管理员、普通用户、guest游客)
-- 3. 系统菜单 (用户管理、文件管理、日志)
-- 4. 角色权限绑定
-- ============================================
-- ============================================
-- 1. 插入默认角色
-- ============================================
INSERT INTO `role` (`name`, `name_zh`, `desc`, `status`) VALUES
('ROLE_ADMIN', '管理员', '系统管理员，拥有所有权限', 1),
('ROLE_GUEST', '游客', '游客用户，只读权限', 1);
SELECT '>>> 角色数据插入完成' AS '步骤1';

-- ============================================
-- 2. 插入默认用户
-- ============================================
-- 注意: 密码使用BCrypt加密(rounds=10)
-- 在线BCrypt生成器: https://bcrypt-generator.com/
-- 密码明文:
--   admin账号密码: admin123
--   guest账号密码: 123456
-- 2.1 管理员用户
INSERT INTO `user` (`uuid`, `username`, `password`, `nickname`, `email`, `status`) VALUES
(UUID(), 'admin', '$2a$10$g/znm4NE6DPjLWw30pWP0u24zWaGzQ4w31DVQ/X9EBAfzEih3tfgG', '系统管理员', 'admin@example.com', 1);
-- 2.2 游客用户 (密码: 123456)
INSERT INTO `user` (`uuid`, `username`, `password`, `nickname`, `email`, `status`) VALUES
(UUID(), 'guest', '$2a$10$Ksz3B9TAsvWBdv.vLDjbw.37R2Fkl/XiawBfffHJTU5uHcL9C/7Ru', '游客用户', 'guest@example.com', 1);
SELECT '>>> 用户数据插入完成 (admin/guest)' AS '步骤2';

-- ============================================
-- 3. 绑定用户-角色关系
-- ============================================
-- 3.1 绑定管理员角色到 admin 用户
INSERT INTO `user_role` (`user_id`, `role_id`)
SELECT u.id, r.id FROM `user` u, `role` r
WHERE u.username = 'admin' AND r.name = 'ROLE_ADMIN';


-- 3.2 绑定游客角色到 guest 用户
INSERT INTO `user_role` (`user_id`, `role_id`)
SELECT u.id, r.id FROM `user` u, `role` r
WHERE u.username = 'guest' AND r.name = 'ROLE_GUEST';

SELECT '>>> 用户-角色绑定完成' AS '步骤3';

-- ============================================
-- 4. 插入系统菜单
-- ============================================
-- 先插入根节点（必须！）
INSERT INTO `menu` (`id`, `name`, `type`, `parent_menu_id`, `icon`, `order`, `status`)
VALUES (1, '根目录', 1, NULL, NULL, 0, 1);

-- ============================================
-- 2. 系统管理模块
-- ============================================
INSERT INTO `menu` (`id`, `name`, `type`, `parent_menu_id`, `icon`, `order`, `path`, `component`, `permission`, `status`)
VALUES (2, '系统管理', 1, 1, 'fa-gear', 1, 'system', NULL, NULL, 1);

-- 2.1 文件管理（页面）- 提前到第一位
INSERT INTO `menu` (`id`, `name`, `type`, `parent_menu_id`, `icon`, `order`, `path`, `component`, `permission`, `status`)
VALUES (3, '文件管理', 2, 2, 'fa-folder', 1, 'file', 'File', NULL, 1);

-- 2.1.1 文件管理按钮权限
INSERT INTO `menu` (`id`, `name`, `type`, `parent_menu_id`, `icon`, `order`, `path`, `component`, `permission`, `status`)
VALUES
    (4, '文件查询', 3, 3, NULL, 1, NULL, NULL, 'file:query', 1),
    (5, '文件上传', 3, 3, NULL, 2, NULL, NULL, 'file:upload', 1),
    (6, '文件下载', 3, 3, NULL, 3, NULL, NULL, 'file:download', 1),
    (7, '文件删除', 3, 3, NULL, 4, NULL, NULL, 'file:delete', 1),
    (8, '文件预览', 3, 3, NULL, 5, NULL, NULL, 'file:preview', 1),
    (9, '文件重命名', 3, 3, NULL, 6, NULL, NULL, 'file:rename', 1);

-- 2.2 用户管理（页面）
INSERT INTO `menu` (`id`, `name`, `type`, `parent_menu_id`, `icon`, `order`, `path`, `component`, `permission`, `status`)
VALUES (10, '用户管理', 2, 2, 'fa-user', 2, 'user', 'User', NULL, 1);

-- 2.2.1 用户管理按钮权限
INSERT INTO `menu` (`id`, `name`, `type`, `parent_menu_id`, `icon`, `order`, `path`, `component`, `permission`, `status`)
VALUES
    (11, '用户查询', 3, 10, NULL, 1, NULL, NULL, 'user:query', 1),
    (12, '用户添加', 3, 10, NULL, 2, NULL, NULL, 'user:add', 1),
    (13, '用户修改', 3, 10, NULL, 3, NULL, NULL, 'user:modify', 1),
    (14, '用户删除', 3, 10, NULL, 4, NULL, NULL, 'user:delete', 1);

-- 2.3 角色管理（页面）
INSERT INTO `menu` (`id`, `name`, `type`, `parent_menu_id`, `icon`, `order`, `path`, `component`, `permission`, `status`)
VALUES (15, '角色管理', 2, 2, 'fa-user-plus', 3, 'role', 'Role', NULL, 1);

-- 2.3.1 角色管理按钮权限
INSERT INTO `menu` (`id`, `name`, `type`, `parent_menu_id`, `icon`, `order`, `path`, `component`, `permission`, `status`)
VALUES
    (16, '角色查询', 3, 15, NULL, 1, NULL, NULL, 'role:query', 1),
    (17, '角色添加', 3, 15, NULL, 2, NULL, NULL, 'role:add', 1),
    (18, '角色修改', 3, 15, NULL, 3, NULL, NULL, 'role:modify', 1),
    (19, '角色删除', 3, 15, NULL, 4, NULL, NULL, 'role:delete', 1);

-- 2.4 菜单管理（页面）
INSERT INTO `menu` (`id`, `name`, `type`, `parent_menu_id`, `icon`, `order`, `path`, `component`, `permission`, `status`)
VALUES (20, '菜单管理', 2, 2, 'fa-list', 4, 'menu', 'Menu', NULL, 1);

-- 2.4.1 菜单管理按钮权限
INSERT INTO `menu` (`id`, `name`, `type`, `parent_menu_id`, `icon`, `order`, `path`, `component`, `permission`, `status`)
VALUES
    (21, '菜单查询', 3, 20, NULL, 1, NULL, NULL, 'menu:query', 1),
    (22, '菜单添加', 3, 20, NULL, 2, NULL, NULL, 'menu:add', 1),
    (23, '菜单修改', 3, 20, NULL, 3, NULL, NULL, 'menu:modify', 1),
    (24, '菜单删除', 3, 20, NULL, 4, NULL, NULL, 'menu:delete', 1);

SELECT '>>> 系统菜单插入完成' AS '步骤4';

-- ============================================
-- 5. 绑定角色-菜单权限
-- ============================================

-- 5.1 管理员角色绑定所有菜单（全部权限）
INSERT INTO `role_menu` (`role_id`, `menu_id`)
SELECT r.id, m.id
FROM `role` r
CROSS JOIN `menu` m
WHERE r.name = 'ROLE_ADMIN';

-- 5.2 游客角色绑定只读菜单（仅文件查看和下载）
INSERT INTO `role_menu` (`role_id`, `menu_id`)
SELECT r.id, m.id
FROM `role` r
CROSS JOIN `menu` m
WHERE r.name = 'ROLE_GUEST'
  AND m.name IN ('文件管理', '文件列表', '下载文件');




SELECT '>>> 角色-菜单权限绑定完成' AS '步骤5';

-- ============================================
-- 6. 验证数据
-- ============================================
SELECT '========================================' AS '';
SELECT '初始化数据插入完成！' AS '';
SELECT '========================================' AS '';

SELECT '=== 角色数据 ===' AS '';
SELECT * FROM `role`;

SELECT '=== 用户数据 ===' AS '';
SELECT id, uuid, username, nickname, email, status FROM `user`;

SELECT '=== 用户-角色关系 ===' AS '';
SELECT u.username AS '用户名', r.name_zh AS '角色'
FROM `user` u
JOIN `user_role` ur ON u.id = ur.user_id
JOIN `role` r ON ur.role_id = r.id;

SELECT '=== 菜单统计 ===' AS '';
SELECT COUNT(*) AS '菜单总数' FROM `menu`;

SELECT '=== 管理员权限菜单数量 ===' AS '';
SELECT COUNT(*) AS '权限数量'
FROM `role_menu` rm
JOIN `role` r ON rm.role_id = r.id
WHERE r.name = 'ROLE_ADMIN';

SELECT '=== 游客权限菜单数量 ===' AS '';
SELECT COUNT(*) AS '权限数量'
FROM `role_menu` rm
JOIN `role` r ON rm.role_id = r.id
WHERE r.name = 'ROLE_GUEST';

SELECT '========================================' AS '';
SELECT '默认账号信息:' AS '';
SELECT '  管理员 - username: admin, password: admin123' AS '';
SELECT '  游客   - username: guest, password: 123456' AS '';
SELECT '========================================' AS '';

-- ============================================
-- 初始化完成
-- ============================================
