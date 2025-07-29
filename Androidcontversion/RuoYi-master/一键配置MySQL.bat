@echo off
chcp 65001
title 一键配置MySQL

echo.
echo ========================================
echo           一键配置MySQL
echo ========================================
echo.

echo 此脚本将帮助您快速配置MySQL数据库
echo.

echo 请确保您已经：
echo 1. 安装了MySQL
echo 2. 启动了MySQL服务
echo 3. 记住了MySQL密码
echo.

set /p choice="是否继续？(y/n): "
if /i not "%choice%"=="y" (
    echo 请先完成MySQL安装后再运行此脚本
    pause
    exit /b 1
)

echo.
echo 请输入MySQL root密码：
set /p mysql_password="密码: "

echo.
echo 正在创建数据库...

REM 创建数据库
echo CREATE DATABASE IF NOT EXISTS ry CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci; | mysql -u root -p%mysql_password%

if errorlevel 1 (
    echo.
    echo ❌ 创建数据库失败
    echo 请检查：
    echo 1. MySQL服务是否启动
    echo 2. 密码是否正确
    echo 3. MySQL是否已正确安装
    echo.
    echo 启动MySQL服务的方法：
    echo - 如果使用XAMPP：启动XAMPP控制面板，点击MySQL的Start
    echo - 如果使用官方安装包：检查服务管理器中的MySQL服务
    pause
    exit /b 1
)

echo ✅ 数据库创建成功

echo.
echo 正在导入数据...

REM 检查SQL文件
if not exist "sql\ry_20250416.sql" (
    echo ❌ 未找到SQL文件：sql\ry_20250416.sql
    echo 请检查项目文件是否完整
    pause
    exit /b 1
)

REM 导入数据
mysql -u root -p%mysql_password% ry < sql\ry_20250416.sql

if errorlevel 1 (
    echo ❌ 导入数据失败
    echo 请检查：
    echo 1. SQL文件是否完整
    echo 2. 数据库连接是否正常
    pause
    exit /b 1
)

echo ✅ 数据导入成功

echo.
echo 正在配置项目连接...

REM 备份原配置文件
if exist "ruoyi-admin\src\main\resources\application-druid.yml" (
    copy "ruoyi-admin\src\main\resources\application-druid.yml" "ruoyi-admin\src\main\resources\application-druid.yml.bak"
    echo ✅ 配置文件已备份
)

echo.
echo 请手动修改数据库配置文件：
echo 文件位置：ruoyi-admin\src\main\resources\application-druid.yml
echo.
echo 需要修改的内容：
echo username: root
echo password: %mysql_password%
echo.

echo 配置完成后，您可以：
echo 1. 运行 快速启动.bat 启动项目
echo 2. 访问 http://localhost:80
echo 3. 使用 admin/admin123 登录
echo.

echo ========================================
echo MySQL配置完成！
echo ========================================
echo.

set /p run_project="是否立即启动项目？(y/n): "
if /i "%run_project%"=="y" (
    echo.
    echo 正在启动项目...
    call 快速启动.bat
) else (
    echo.
    echo 您可以稍后运行 快速启动.bat 来启动项目
)

pause 