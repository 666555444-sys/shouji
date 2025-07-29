@echo off
chcp 65001
title 若依项目启动器

echo.
echo ========================================
echo           若依项目启动器
echo ========================================
echo.

echo 正在检查环境...
echo.

REM 检查Java
echo [1/4] 检查Java环境...
java -version >nul 2>&1
if errorlevel 1 (
    echo ❌ Java未安装或未配置环境变量
    echo 请先安装Java 8或更高版本
    pause
    exit /b 1
)
echo ✅ Java环境正常

REM 检查Maven
echo [2/4] 检查Maven环境...
mvn -version >nul 2>&1
if errorlevel 1 (
    echo ⚠️  Maven未安装，正在自动下载...
    
    if not exist "temp" mkdir temp
    cd temp
    
    echo 正在下载Maven...
    powershell -Command "try { Invoke-WebRequest -Uri 'https://archive.apache.org/dist/maven/maven-3/3.9.6/binaries/apache-maven-3.9.6-bin.zip' -OutFile 'maven.zip' -UseBasicParsing; Write-Host '下载完成' } catch { Write-Host '下载失败'; exit 1 }"
    
    if not exist "maven.zip" (
        echo ❌ 下载失败，请检查网络连接
        pause
        exit /b 1
    )
    
    echo 正在解压Maven...
    powershell -Command "Expand-Archive -Path 'maven.zip' -DestinationPath '.' -Force"
    
    set MAVEN_HOME=%CD%\apache-maven-3.9.6
    set PATH=%MAVEN_HOME%\bin;%PATH%
    
    cd ..
    echo ✅ Maven安装完成
) else (
    echo ✅ Maven环境正常
)

REM 编译项目
echo [3/4] 编译项目...
echo 正在编译，请耐心等待...
mvn clean package -DskipTests -q
if errorlevel 1 (
    echo ❌ 编译失败
    echo 请检查：
    echo 1. 网络连接是否正常
    echo 2. 磁盘空间是否充足
    echo 3. 项目文件是否完整
    pause
    exit /b 1
)
echo ✅ 编译完成

REM 启动项目
echo [4/4] 启动项目...
echo.
echo ========================================
echo 项目正在启动...
echo.
echo 启动成功后请访问：
echo http://localhost:80
echo.
echo 默认登录信息：
echo 用户名：admin
echo 密码：admin123
echo ========================================
echo.

if exist "ruoyi-admin\target\ruoyi-admin.jar" (
    java -jar ruoyi-admin\target\ruoyi-admin.jar
) else (
    echo ❌ 未找到生成的jar文件
    pause
    exit /b 1
)

pause 