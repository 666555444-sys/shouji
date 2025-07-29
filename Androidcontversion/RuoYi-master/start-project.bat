@echo off
chcp 65001
echo ========================================
echo           若依项目启动脚本
echo ========================================
echo.

REM 检查Java环境
echo [1/5] 检查Java环境...
java -version >nul 2>&1
if errorlevel 1 (
    echo ❌ 错误：未找到Java环境，请先安装Java 8或更高版本
    pause
    exit /b 1
)
echo ✅ Java环境正常

REM 检查Maven
echo [2/5] 检查Maven环境...
mvn -version >nul 2>&1
if errorlevel 1 (
    echo ⚠️  Maven未安装，正在自动下载...
    
    REM 创建临时目录
    if not exist "temp" mkdir temp
    cd temp
    
    echo 正在下载Maven 3.9.6...
    powershell -Command "try { Invoke-WebRequest -Uri 'https://archive.apache.org/dist/maven/maven-3/3.9.6/binaries/apache-maven-3.9.6-bin.zip' -OutFile 'maven.zip' -UseBasicParsing; Write-Host '下载完成' } catch { Write-Host '下载失败，请检查网络连接'; exit 1 }"
    
    if not exist "maven.zip" (
        echo ❌ 下载失败，请检查网络连接
        pause
        exit /b 1
    )
    
    echo 正在解压Maven...
    powershell -Command "Expand-Archive -Path 'maven.zip' -DestinationPath '.' -Force"
    
    REM 设置Maven环境变量
    set MAVEN_HOME=%CD%\apache-maven-3.9.6
    set PATH=%MAVEN_HOME%\bin;%PATH%
    
    cd ..
    echo ✅ Maven安装完成
) else (
    echo ✅ Maven环境正常
)

REM 编译项目
echo [3/5] 编译项目...
echo 这可能需要几分钟时间，请耐心等待...
mvn clean package -DskipTests -q
if errorlevel 1 (
    echo ❌ 编译失败，请检查项目配置
    pause
    exit /b 1
)
echo ✅ 编译完成

REM 检查jar文件
echo [4/5] 检查生成文件...
if not exist "ruoyi-admin\target\ruoyi-admin.jar" (
    echo ❌ 未找到生成的jar文件
    pause
    exit /b 1
)
echo ✅ 找到jar文件

REM 启动项目
echo [5/5] 启动项目...
echo.
echo ========================================
echo 项目正在启动，请稍候...
echo 启动成功后访问：http://localhost:80
echo 默认账号：admin
echo 默认密码：admin123
echo ========================================
echo.

java -jar ruoyi-admin\target\ruoyi-admin.jar

pause 