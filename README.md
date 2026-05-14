
# Four-Zero-Four 

## 项目简介

Four Zero Four 是一个基于 Spring Boot 4.0.6 和 Vue.js 的全栈应用项目，采用前后端分离架构设计。

## 技术栈

### 后端技术
- Java
- Spring Boot 4.0.6
- MyBatis flex
- Redis/Redisson
- Quartz 定时任务
- Spring Security

### 前端技术
- Vue.js
- TypeScript
- Vite 构建工具

## 快速开始

### 环境要求
- JDK 25+
- Node.js 18+
- Maven 3.8+
- MySQL 8.0+

### 后端启动

1. 克隆项目到本地
2. 配置数据库连接信息（在 `backend/app/src/main/resources/application-dev.yaml` 中）
3. 在项目根目录执行：
~~~bash
mvn clean install 
cd backend/app 
mvn spring-boot:run
~~~

### 前端启动
~~~bash
cd frontend 
npm install 
npm run dev
~~~