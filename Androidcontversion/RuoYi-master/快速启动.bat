@echo off
chcp 65001
title 若依项目快速启动

echo.
echo ========================================
echo           若依项目快速启动
echo ========================================
echo.

echo 选择启动方式：
echo.
echo 1. 检查数据库配置后启动（推荐）
echo 2. 直接启动项目
echo 3. 退出
echo.

set /p choice="请选择 (1-3): "

if "%choice%"=="1" (
    call 数据库配置.bat
) else if "%choice%"=="2" (
    call 运行项目.bat
) else if "%choice%"=="3" (
    echo 退出程序
    exit /b 0
) else (
    echo 无效选择，请重新运行
    pause
    exit /b 1
) 