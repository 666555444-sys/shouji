@echo off
chcp 65001
echo ========================================
echo           数据库配置检查
echo ========================================
echo.

echo 请确保您已经：
echo 1. 安装了MySQL数据库
echo 2. 创建了名为 'ry' 的数据库
echo 3. 导入了 sql/ry_20250416.sql 文件
echo 4. 修改了数据库连接配置
echo.

echo 当前数据库配置：
echo 文件：ruoyi-admin\src\main\resources\application-druid.yml
echo 默认配置：
echo   URL: jdbc:mysql://localhost:3306/ry
echo   用户名: root
echo   密码: password
echo.

echo 如果需要修改数据库配置，请编辑上述文件
echo.

set /p choice="是否继续运行项目？(y/n): "
if /i "%choice%"=="y" (
    echo 正在启动项目...
    call start-project.bat
) else (
    echo 请先配置数据库后再运行项目
    pause
) 