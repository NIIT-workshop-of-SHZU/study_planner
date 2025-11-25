# 🎯 智能学习计划生成器

基于 Spring Boot + MyBatis + MySQL + LLM API 的智能学习计划平台。

## ✨ 功能特性

- 🤖 **AI智能生成计划** - 基于大语言模型，根据学习目标自动生成个性化学习计划
- 📅 **每日打卡** - 记录学习进度，培养学习习惯
- 📊 **学习统计** - 可视化展示学习数据，进度追踪
- 💬 **AI助手** - 学习过程中的智能问答助手

## 🛠️ 技术栈

- **后端**: Spring Boot 3.2 + MyBatis
- **前端**: HTML5 + CSS3 + JavaScript + Bootstrap 5
- **数据库**: MySQL 8.x
- **LLM**: DeepSeek / 通义千问 / OpenAI (可配置)

## 📁 项目结构

```
study-planner/
├── src/main/java/com/studyplanner/
│   ├── controller/    # 控制器层
│   ├── service/       # 服务层
│   ├── mapper/        # MyBatis映射
│   ├── entity/        # 实体类
│   ├── dto/           # 数据传输对象
│   └── config/        # 配置类
├── src/main/resources/
│   ├── static/        # 前端静态文件
│   ├── mapper/        # MyBatis XML
│   └── application.yml
└── pom.xml
```

## 🚀 快速开始

### 1. 环境要求

- JDK 17+
- Maven 3.8+
- MySQL 8.0+

### 2. 数据库初始化

```sql
-- 执行 sql/init.sql 创建数据库和表
source sql/init.sql
```

### 3. 配置修改

编辑 `src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/study_planner
    username: root
    password: your_password  # 修改为你的密码

llm:
  api:
    api-key: your_api_key    # 修改为你的API Key
```

### 4. 启动项目

```bash
# 进入项目目录
cd study-planner

# Maven 编译运行
mvn spring-boot:run
```

### 5. 访问项目

打开浏览器访问: http://localhost:8080

## 📡 API接口

### 用户接口
- `POST /api/user/register` - 用户注册
- `POST /api/user/login` - 用户登录
- `POST /api/user/logout` - 用户登出
- `GET /api/user/info` - 获取用户信息

### 计划接口
- `POST /api/plan/generate` - AI生成计划
- `GET /api/plan/list` - 获取计划列表
- `GET /api/plan/{id}` - 获取计划详情
- `DELETE /api/plan/{id}` - 删除计划

### 打卡接口
- `POST /api/checkin` - 打卡签到
- `GET /api/checkin/stats` - 获取学习统计
- `GET /api/checkin/calendar` - 获取日历数据

## 👥 团队分工

| 成员 | 职责 |
|------|------|
| 成员A | 后端 - 用户系统 |
| 成员B | 后端 - 计划管理、LLM对接 |
| 成员C | 前端 - 首页、登录注册 |
| 成员D | 前端 - 仪表盘、计划页面 |
| 成员E | 数据库、打卡系统 |
| 成员F | 测试、文档、答辩 |

## 📝 开发日志

- 2025-11-25: 项目初始化，完成框架搭建

## 📄 License

MIT License
