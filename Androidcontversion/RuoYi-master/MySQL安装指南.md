# MySQL安装和配置指南

## 1. 下载MySQL

### 方法一：官方安装包
1. 访问：https://dev.mysql.com/downloads/mysql/
2. 选择 "MySQL Community Server"
3. 下载适合Windows的版本（推荐8.0或5.7）

### 方法二：XAMPP（推荐新手）
1. 访问：https://www.apachefriends.org/
2. 下载XAMPP for Windows
3. 安装后包含MySQL、Apache、PHP等

## 2. 安装MySQL

### 使用官方安装包
1. 运行下载的安装程序
2. 选择 "Developer Default" 或 "Server only"
3. 配置root密码（请记住这个密码）
4. 完成安装

### 使用XAMPP
1. 运行XAMPP安装程序
2. 选择安装组件（至少选择MySQL）
3. 完成安装
4. 启动XAMPP控制面板
5. 点击MySQL旁边的 "Start" 按钮

## 3. 验证MySQL安装

打开命令提示符，输入：
```bash
mysql -u root -p
```
输入密码后，如果看到MySQL提示符，说明安装成功。

## 4. 创建数据库

在MySQL命令行中执行：
```sql
CREATE DATABASE ry CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
```

## 5. 导入数据

### 方法一：命令行导入
```bash
mysql -u root -p ry < D:\JAVA\RuoYi-master\sql\ry_20250416.sql
```

### 方法二：使用图形化工具
1. 下载Navicat、DBeaver或使用phpMyAdmin
2. 连接到MySQL
3. 选择ry数据库
4. 导入sql/ry_20250416.sql文件

## 6. 配置项目连接

编辑文件：`ruoyi-admin/src/main/resources/application-druid.yml`

修改以下配置：
```yaml
spring:
    datasource:
        druid:
            master:
                url: jdbc:mysql://localhost:3306/ry?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8
                username: root         # 改为您的MySQL用户名
                password: your_password # 改为您的MySQL密码
```

## 7. 常见问题

### 问题1：MySQL服务未启动
- 打开服务管理器（services.msc）
- 找到MySQL服务，右键启动

### 问题2：连接被拒绝
- 检查MySQL是否启动
- 检查端口3306是否被占用
- 检查防火墙设置

### 问题3：密码错误
- 重置MySQL root密码
- 或创建新用户并授权

## 8. 测试连接

在MySQL命令行中测试：
```sql
USE ry;
SHOW TABLES;
```

如果能看到表列表，说明数据库创建和导入成功。

## 9. 下一步

完成MySQL配置后，运行项目：
1. 双击 `快速启动.bat`
2. 选择启动方式
3. 等待项目启动完成
4. 访问 http://localhost:80 