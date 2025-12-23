@echo off
chcp 65001 > nul
echo ========================================
echo 云存储管理系统 - 数据库初始化
echo ========================================

echo [提示] 请确保已安装MySQL客户端工具
echo.
pause

REM 检查配置文件是否存在
if not exist "mysql.cnf" (
    echo.
    echo [错误] 找不到配置文件 mysql.cnf
    echo [提示] 请先创建配置文件：
    echo.
    echo mysql.cnf 内容示例:
    echo [client]
    echo host=101.42.242.33
    echo port=3306
    echo user=tancong
    echo password=你的密码
    echo database=cloud_db
    echo default-character-set=utf8mb4
    echo.
    pause
    exit /b 1
)
echo [成功] 配置文件检查通过！

echo.
echo ========================================
echo 开始创建数据库表...
echo ========================================

REM 执行SQL文件（使用配置文件，避免重复输入密码）
echo [1/6] 创建用户表 (user)...
mysql --defaults-extra-file=mysql.cnf --default-character-set=utf8mb4 < 01_create_user_table.sql
if %errorlevel% neq 0 (
    echo [错误] 用户表创建失败！
    pause
    exit /b 1
)

echo [2/6] 创建角色表 (role)...
mysql --defaults-extra-file=mysql.cnf --default-character-set=utf8mb4 < 02_create_role_table.sql
if %errorlevel% neq 0 (
    echo [错误] 角色表创建失败！
    pause
    exit /b 1
)

echo [3/6] 创建菜单表 (menu)...
mysql --defaults-extra-file=mysql.cnf --default-character-set=utf8mb4 < 03_create_menu_table.sql
if %errorlevel% neq 0 (
    echo [错误] 菜单表创建失败！
    pause
    exit /b 1
)

echo [4/6] 创建用户-角色关联表 (user_role)...
mysql --defaults-extra-file=mysql.cnf --default-character-set=utf8mb4 < 04_create_user_role_table.sql
if %errorlevel% neq 0 (
    echo [错误] 用户-角色关联表创建失败！
    pause
    exit /b 1
)

echo [5/6] 创建角色-菜单关联表 (role_menu)...
mysql --defaults-extra-file=mysql.cnf --default-character-set=utf8mb4 < 05_create_role_menu_table.sql
if %errorlevel% neq 0 (
    echo [错误] 角色-菜单关联表创建失败！
    pause
    exit /b 1
)

echo [6/6] 创建日志表 (log)...
mysql --defaults-extra-file=mysql.cnf --default-character-set=utf8mb4 < 06_create_log_table.sql
if %errorlevel% neq 0 (
    echo [错误] 日志表创建失败！
    pause
    exit /b 1
)

REM 插入初始数据
echo.
echo ========================================
echo 插入初始数据 (管理员和访客用户)...
echo ========================================
mysql --defaults-extra-file=mysql.cnf --default-character-set=utf8mb4 < init_data.sql
if %errorlevel% neq 0 (
    echo [警告] 初始数据插入失败！
    pause
    exit /b 1
)

REM 完成提示
echo.
echo ========================================
echo 数据库初始化完成！
echo ========================================
echo.
echo 默认账号:
echo   [管理员] username: admin, password: admin123
echo   [访客]   username: guest, password: 123456
echo.
echo ========================================
echo 提示: 首次登录请务必修改默认密码
echo ========================================
echo.
pause