# 数据库初始化脚本说明

## 📁 文件列表

```
sql/
├── 01_create_user_table.sql          # 用户表
├── 02_create_role_table.sql          # 角色表
├── 03_create_menu_table.sql          # 菜单表
├── 04_create_user_role_table.sql     # 用户-角色关联表
├── 05_create_role_menu_table.sql     # 角色-菜单关联表
├── 06_create_log_table.sql           # 日志表
├── init_data.sql                     # 初始化数据（包含管理员和游客用户）
├── init_database.bat                 # Windows一键执行脚本
└── README.md                         # 本说明文档
```

## 🎯 功能说明

本目录包含云存储后台管理系统的数据库初始化脚本，用于创建数据库表结构和插入初始数据。

**核心功能**:
- ✅ 创建6张核心表（用户、角色、菜单、日志、关联表）
- ✅ 插入2个默认角色（管理员、游客）
- ✅ 插入2个默认用户（admin管理员、guest游客）
- ✅ 插入系统菜单（用户管理、文件管理、系统日志）
- ✅ 配置权限绑定（RBAC权限控制）

---

## 🚀 快速开始

### 方法1: 使用一键执行脚本 (推荐)

**Windows系统**:
```bash
# 进入sql目录
cd sql

# 双击运行或命令行执行
init_database.bat
```

脚本会自动按顺序执行所有SQL文件，并显示执行进度。

---

### 方法2: 手动执行SQL文件

**连接数据库**:
```bash
mysql -h 101.42.242.33 -u tancong -p
```

**选择数据库**:
```sql
USE cloud_db;
```

**按顺序执行建表脚本**:
```sql
SOURCE 01_create_user_table.sql;
SOURCE 02_create_role_table.sql;
SOURCE 03_create_menu_table.sql;
SOURCE 04_create_user_role_table.sql;
SOURCE 05_create_role_menu_table.sql;
SOURCE 06_create_log_table.sql;
```

**插入初始化数据**:
```sql
SOURCE init_data.sql;
```

**验证数据**:
```sql
-- 查看所有表
SHOW TABLES;

-- 查看用户数据
SELECT * FROM user;

-- 查看角色数据
SELECT * FROM role;

-- 查看用户-角色关系
SELECT u.username, r.name_zh AS role_name
FROM user u
JOIN user_role ur ON u.id = ur.user_id
JOIN role r ON ur.role_id = r.id;
```

---

## 👥 默认账号信息

### 管理员账号

| 字段 | 值 |
|------|-----|
| 用户名 | `admin` |
| 密码 | `admin123` |
| 角色 | 管理员 (ROLE_ADMIN) |
| 权限 | 所有权限（用户管理、文件管理、日志查看） |

### 游客账号

| 字段 | 值 |
|------|-----|
| 用户名 | `guest` |
| 密码 | `123456` |
| 角色 | 游客 (ROLE_GUEST) |
| 权限 | 只读权限（仅查看和下载文件） |

> ⚠️ **安全提示**: 首次登录后请立即修改默认密码！

---

## 📊 数据库表结构

### 核心表

| 表名 | 说明 | 主要字段 |
|------|------|----------|
| `user` | 用户表 | id, uuid, username, password, nickname, email, status |
| `role` | 角色表 | id, name, name_zh, desc, status |
| `menu` | 菜单表 | id, name, type, parent_menu_id, permission, path |
| `log` | 日志表 | id, title, type, method, ip, user_id, create_time |

### 关联表

| 表名 | 说明 | 主要字段 |
|------|------|----------|
| `user_role` | 用户-角色关联表 | user_id, role_id |
| `role_menu` | 角色-菜单关联表 | role_id, menu_id |

---

## 🔑 权限说明

### 管理员权限

- ✅ 用户管理（新增、编辑、删除用户）
- ✅ 文件管理（上传、下载、删除文件）
- ✅ 系统日志（查看操作日志）
- ✅ 所有系统菜单和功能

### 游客权限

- ✅ 文件列表查看
- ✅ 文件下载
- ❌ 文件上传
- ❌ 文件删除
- ❌ 用户管理
- ❌ 系统日志查看

---

## 🔐 密码加密说明

用户密码使用 **BCrypt** 算法加密（rounds=10）。

### 生成新的密码哈希值

**方法1: 使用在线工具**
- 访问: https://bcrypt-generator.com/
- 输入密码并设置 rounds=10
- 复制生成的哈希值替换SQL中的密码字段

**方法2: 使用Java代码**
```java
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class Test {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        System.out.println(encoder.encode("your_password"));
    }
}
```

---

## ⚠️ 注意事项

### 执行前检查

1. ✅ 确保MySQL服务已启动
2. ✅ 确认数据库连接信息正确（主机、用户名、密码）
3. ✅ 确保数据库 `cloud_db` 已创建
4. ✅ 备份现有数据（如有）

### 执行顺序

**重要**: 必须按以下顺序执行SQL文件（因为存在外键依赖关系）:

```
1. 用户表 (user)
2. 角色表 (role)
3. 菜单表 (menu)
4. 用户-角色关联表 (user_role) ← 依赖user和role
5. 角色-菜单关联表 (role_menu) ← 依赖role和menu
6. 日志表 (log) ← 依赖user
7. 初始化数据 (init_data.sql) ← 依赖所有表
```

### 常见问题

**Q1: 执行SQL时报错 "Table already exists"**
- A: 每个SQL文件开头都有 `DROP TABLE IF EXISTS` 语句，会自动删除旧表。如果仍报错，请手动删除表后重试。

**Q2: 外键约束错误**
- A: 确保按正确顺序执行SQL文件，先创建父表再创建子表。

**Q3: 密码哈希值在哪里修改？**
- A: 在 `init_data.sql` 文件的第23行（admin密码）和第27行（guest密码）。

**Q4: 如何新增其他默认用户？**
- A: 在 `init_data.sql` 中仿照现有格式添加INSERT语句即可。

---

## 📝 修改配置

### 修改数据库连接信息

编辑 `init_database.bat` 文件:
```batch
set DB_HOST=你的数据库主机地址
set DB_USER=你的数据库用户名
set DB_NAME=你的数据库名称
```

### 修改默认用户信息

编辑 `init_data.sql` 文件的第15-28行，修改用户名、昵称、邮箱等信息。

---

## 🎉 验证安装

执行完所有SQL后，运行以下命令验证:

## 📞 技术支持

如遇问题，请检查:
1. MySQL客户端是否已安装
2. 数据库连接信息是否正确
3. 是否按正确顺序执行SQL文件
4. 查看MySQL错误日志

---

**创建时间**: 2025-01-XX
**适用版本**: Spring Boot 3.4.12 + MySQL 8.0.33
**维护人员**: 云存储系统开发团队
