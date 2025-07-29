@echo off
chcp 65001
title MySQL安装和配置助手

echo.
echo ========================================
echo         MySQL安装和配置助手
echo ========================================
echo.

echo 检测到您的系统尚未安装MySQL
echo 此脚本将帮助您安装和配置MySQL
echo.

echo 请选择安装方式：
echo.
echo 1. 下载并安装MySQL官方版本
echo 2. 下载并安装XAMPP（包含MySQL，推荐新手）
echo 3. 手动安装MySQL
echo 4. 退出
echo.

set /p choice="请选择 (1-4): "

if "%choice%"=="1" (
    call :install_mysql_official
) else if "%choice%"=="2" (
    call :install_xampp
) else if "%choice%"=="3" (
    call :manual_install
) else if "%choice%"=="4" (
    echo 退出程序
    exit /b 0
) else (
    echo 无效选择，请重新运行
    pause
    exit /b 1
)

goto :eof

:install_mysql_official
echo.
echo 正在下载MySQL官方版本...
echo 请稍候，下载可能需要几分钟...

REM 创建下载目录
if not exist "downloads" mkdir downloads
cd downloads

REM 下载MySQL
powershell -Command "try { Invoke-WebRequest -Uri 'https://dev.mysql.com/get/Downloads/MySQLInstaller/mysql-installer-community-8.0.36.0.msi' -OutFile 'mysql-installer.msi' -UseBasicParsing; Write-Host '下载完成' } catch { Write-Host '下载失败，请手动下载'; exit 1 }"

if not exist "mysql-installer.msi" (
    echo ❌ 下载失败
    echo 请手动下载MySQL：https://dev.mysql.com/downloads/mysql/
    pause
    exit /b 1
)

echo ✅ 下载完成
echo 正在启动MySQL安装程序...
start /wait mysql-installer.msi

cd ..
echo.
echo MySQL安装完成，请继续配置数据库
call :configure_database
goto :eof

:install_xampp
echo.
echo 正在下载XAMPP...
echo 请稍候，下载可能需要几分钟...

REM 创建下载目录
if not exist "downloads" mkdir downloads
cd downloads

REM 下载XAMPP
powershell -Command "try { Invoke-WebRequest -Uri 'https://sourceforge.net/projects/xampp/files/XAMPP%20Windows/8.2.12/xampp-windows-x64-8.2.12-0-VS16-installer.exe/download' -OutFile 'xampp-installer.exe' -UseBasicParsing; Write-Host '下载完成' } catch { Write-Host '下载失败，请手动下载'; exit 1 }"

if not exist "xampp-installer.exe" (
    echo ❌ 下载失败
    echo 请手动下载XAMPP：https://www.apachefriends.org/
    pause
    exit /b 1
)

echo ✅ 下载完成
echo 正在启动XAMPP安装程序...
start /wait xampp-installer.exe

cd ..
echo.
echo XAMPP安装完成，请启动MySQL服务后继续
call :configure_database
goto :eof

:manual_install
echo.
echo 请手动安装MySQL：
echo.
echo 1. 访问 https://dev.mysql.com/downloads/mysql/
echo 2. 下载MySQL Community Server
echo 3. 运行安装程序
echo 4. 记住设置的root密码
echo 5. 安装完成后运行此脚本继续配置
echo.
pause
call :configure_database
goto :eof

:configure_database
echo.
echo ========================================
echo           配置数据库
echo ========================================
echo.

echo 请确保MySQL已安装并启动
echo.

REM 检查MySQL是否可用
mysql --version >nul 2>&1
if errorlevel 1 (
    echo ❌ MySQL未安装或未配置环境变量
    echo 请先完成MySQL安装
    pause
    exit /b 1
)

echo ✅ MySQL已安装

echo.
echo 请输入MySQL root密码：
set /p mysql_password="密码: "

echo.
echo 正在创建数据库...

REM 创建数据库
echo CREATE DATABASE IF NOT EXISTS ry CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci; | mysql -u root -p%mysql_password%

if errorlevel 1 (
    echo ❌ 创建数据库失败
    echo 请检查：
    echo 1. MySQL服务是否启动
    echo 2. 密码是否正确
    echo 3. MySQL是否已正确安装
    pause
    exit /b 1
)

echo ✅ 数据库创建成功

echo.
echo 正在导入数据...

REM 导入数据
mysql -u root -p%mysql_password% ry < sql\ry_20250416.sql

if errorlevel 1 (
    echo ❌ 导入数据失败
    echo 请检查：
    echo 1. sql\ry_20250416.sql 文件是否存在
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

pause
goto :eof 