Write-Host "正在启动若依项目..." -ForegroundColor Green

# 检查Java环境
try {
    $javaVersion = java -version 2>&1
    Write-Host "Java版本: $javaVersion" -ForegroundColor Green
} catch {
    Write-Host "错误：未找到Java环境，请先安装Java 8或更高版本" -ForegroundColor Red
    Read-Host "按任意键退出"
    exit 1
}

# 检查Maven
try {
    $mvnVersion = mvn -version 2>&1
    Write-Host "Maven已安装" -ForegroundColor Green
} catch {
    Write-Host "Maven未安装，正在下载..." -ForegroundColor Yellow
    
    # 创建临时目录
    if (!(Test-Path "temp")) {
        New-Item -ItemType Directory -Name "temp"
    }
    
    Set-Location "temp"
    
    # 下载Maven
    Write-Host "正在下载Maven..." -ForegroundColor Yellow
    try {
        Invoke-WebRequest -Uri "https://archive.apache.org/dist/maven/maven-3/3.9.6/binaries/apache-maven-3.9.6-bin.zip" -OutFile "maven.zip"
    } catch {
        Write-Host "下载Maven失败，请检查网络连接" -ForegroundColor Red
        Read-Host "按任意键退出"
        exit 1
    }
    
    # 解压Maven
    Write-Host "正在解压Maven..." -ForegroundColor Yellow
    Expand-Archive -Path "maven.zip" -DestinationPath "." -Force
    
    # 设置Maven环境变量
    $env:MAVEN_HOME = (Get-Location).Path + "\apache-maven-3.9.6"
    $env:PATH = $env:MAVEN_HOME + "\bin;" + $env:PATH
    
    Set-Location ".."
}

# 编译项目
Write-Host "正在编译项目..." -ForegroundColor Yellow
try {
    mvn clean package -DskipTests
} catch {
    Write-Host "编译失败，请检查项目配置" -ForegroundColor Red
    Read-Host "按任意键退出"
    exit 1
}

# 启动项目
Write-Host "正在启动项目..." -ForegroundColor Green
java -jar ruoyi-admin/target/ruoyi-admin.jar

Read-Host "按任意键退出" 