# 设置执行策略
Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser -Force

Write-Host "正在下载Maven..." -ForegroundColor Green

# 创建临时目录
$tempDir = "temp"
if (!(Test-Path $tempDir)) {
    New-Item -ItemType Directory -Name $tempDir -Force
}

# 切换到临时目录
Set-Location $tempDir

# Maven下载URL
$mavenUrl = "https://archive.apache.org/dist/maven/maven-3/3.9.6/binaries/apache-maven-3.9.6-bin.zip"
$mavenZip = "maven.zip"

Write-Host "正在从 $mavenUrl 下载Maven..." -ForegroundColor Yellow

try {
    # 下载Maven
    Invoke-WebRequest -Uri $mavenUrl -OutFile $mavenZip -UseBasicParsing
    Write-Host "下载完成！" -ForegroundColor Green
} catch {
    Write-Host "下载失败: $($_.Exception.Message)" -ForegroundColor Red
    Read-Host "按任意键退出"
    exit 1
}

Write-Host "正在解压Maven..." -ForegroundColor Yellow

try {
    # 解压Maven
    Expand-Archive -Path $mavenZip -DestinationPath "." -Force
    Write-Host "解压完成！" -ForegroundColor Green
} catch {
    Write-Host "解压失败: $($_.Exception.Message)" -ForegroundColor Red
    Read-Host "按任意键退出"
    exit 1
}

# 设置Maven环境变量
$mavenHome = (Get-Location).Path + "\apache-maven-3.9.6"
$env:MAVEN_HOME = $mavenHome
$env:PATH = $mavenHome + "\bin;" + $env:PATH

Write-Host "Maven已安装到: $mavenHome" -ForegroundColor Green

# 验证Maven安装
try {
    $mvnVersion = & "$mavenHome\bin\mvn.cmd" -version 2>&1
    Write-Host "Maven安装成功！" -ForegroundColor Green
    Write-Host $mvnVersion -ForegroundColor Cyan
} catch {
    Write-Host "Maven验证失败: $($_.Exception.Message)" -ForegroundColor Red
}

# 返回上级目录
Set-Location ".."

Write-Host "Maven下载和安装完成！" -ForegroundColor Green
Write-Host "现在可以运行项目了。" -ForegroundColor Yellow

Read-Host "按任意键继续" 