@echo off
chcp 65001
title 数据库配置检查

echo.
echo ========================================
echo           数据库配置检查
echo ========================================
echo.

echo 请确保您已经完成以下步骤：
echo.
echo 1. 安装MySQL数据库
echo 2. 启动MySQL服务
echo 3. 创建数据库：CREATE DATABASE ry CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
echo 4. 导入数据：使用 sql/ry_20250416.sql 文件
echo 5. 修改数据库连接配置
echo.

echo 当前数据库配置文件：
echo ruoyi-admin\src\main\resources\application-druid.yml
echo.

echo 默认配置：
echo   URL: jdbc:mysql://localhost:3306/ry
echo   用户名: root
echo   密码: password
echo.

echo 如果需要修改配置，请编辑上述文件
echo.

set /p choice="是否继续运行项目？(y/n): "
if /i "%choice%"=="y" (
    echo.
    echo 正在启动项目...
    call 运行项目.bat
) else (
    echo.
    echo 请先完成数据库配置后再运行项目
    echo.
    echo 配置步骤：
    echo 1. 打开 ruoyi-admin\src\main\resources\application-druid.yml
    echo 2. 修改 username 和 password 为您的MySQL用户名和密码
    echo 3. 保存文件
    echo 4. 重新运行此脚本
    echo.
    pause
) 