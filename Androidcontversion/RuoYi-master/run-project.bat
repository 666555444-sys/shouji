@echo off
echo 正在启动若依项目...

REM 检查Java环境
java -version >nul 2>&1
if errorlevel 1 (
    echo 错误：未找到Java环境，请先安装Java 8或更高版本
    pause
    exit /b 1
)

REM 检查Maven
mvn -version >nul 2>&1
if errorlevel 1 (
    echo Maven未安装，正在下载Maven...
    
    REM 创建临时目录
    if not exist "temp" mkdir temp
    cd temp
    
    REM 下载Maven
    echo 正在下载Maven...
    powershell -Command "Invoke-WebRequest -Uri 'https://archive.apache.org/dist/maven/maven-3/3.9.6/binaries/apache-maven-3.9.6-bin.zip' -OutFile 'maven.zip'"
    
    if not exist "maven.zip" (
        echo 下载Maven失败，请检查网络连接
        pause
        exit /b 1
    )
    
    REM 解压Maven
    echo 正在解压Maven...
    powershell -Command "Expand-Archive -Path 'maven.zip' -DestinationPath '.' -Force"
    
    REM 设置Maven环境变量
    set MAVEN_HOME=%CD%\apache-maven-3.9.6
    set PATH=%MAVEN_HOME%\bin;%PATH%
    
    cd ..
)

echo 正在编译项目...
mvn clean package -DskipTests

if errorlevel 1 (
    echo 编译失败，请检查项目配置
    pause
    exit /b 1
)

echo 正在启动项目...
java -jar ruoyi-admin/target/ruoyi-admin.jar

pause 