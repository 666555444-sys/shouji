@echo off
echo 正在下载Maven...

REM 创建临时目录
if not exist "temp" mkdir temp
cd temp

REM 下载Maven
echo 正在下载Maven 3.9.6...
powershell -Command "Invoke-WebRequest -Uri 'https://archive.apache.org/dist/maven/maven-3/3.9.6/binaries/apache-maven-3.9.6-bin.zip' -OutFile 'maven.zip'"

if not exist "maven.zip" (
    echo 下载失败，请检查网络连接
    pause
    exit /b 1
)

echo 下载完成！

REM 解压Maven
echo 正在解压Maven...
powershell -Command "Expand-Archive -Path 'maven.zip' -DestinationPath '.' -Force"

REM 设置Maven环境变量
set MAVEN_HOME=%CD%\apache-maven-3.9.6
set PATH=%MAVEN_HOME%\bin;%PATH%

echo Maven已安装到: %MAVEN_HOME%

REM 验证Maven安装
echo 正在验证Maven安装...
%MAVEN_HOME%\bin\mvn.cmd -version

if errorlevel 1 (
    echo Maven验证失败
) else (
    echo Maven安装成功！
)

cd ..

echo Maven下载和安装完成！
pause 