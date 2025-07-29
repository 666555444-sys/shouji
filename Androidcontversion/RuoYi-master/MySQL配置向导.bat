@echo off
chcp 65001
title MySQL配置向导

echo.
echo ========================================
echo           MySQL配置向导
echo ========================================
echo.

echo 欢迎使用MySQL配置向导
echo 此向导将帮助您完成MySQL的安装和配置
echo.

echo 请选择您的MySQL安装状态：
echo.
echo 1. 尚未安装MySQL（需要先安装）
echo 2. 已安装MySQL，需要配置数据库
echo 3. 查看安装指南
echo 4. 退出
echo.

set /p choice="请选择 (1-4): "

if "%choice%"=="1" (
    echo.
    echo 正在启动MySQL安装程序...
    call 安装MySQL.bat
) else if "%choice%"=="2" (
    echo.
    echo 正在启动MySQL配置程序...
    call 快速配置MySQL.bat
) else if "%choice%"=="3" (
    echo.
    echo 正在打开安装指南...
    if exist "MySQL安装指南.md" (
        start notepad "MySQL安装指南.md"
    ) else (
        echo 安装指南文件不存在
    )
    echo.
    pause
    call MySQL配置向导.bat
) else if "%choice%"=="4" (
    echo 退出程序
    exit /b 0
) else (
    echo 无效选择，请重新选择
    echo.
    pause
    call MySQL配置向导.bat
) 