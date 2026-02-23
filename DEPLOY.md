# 博客问答平台 - 部署文档

## 项目介绍

这是一个基于 Vue3 + Spring Boot 的前后端分离博客问答平台，支持：
- 用户注册/登录
- 学习笔记发布与管理
- 公共广场问答社区

## 技术栈

### 后端
- Spring Boot 3.2
- Java 17
- MyBatis-Plus 3.5
- Spring Security + JWT
- MySQL 8.x
- Redis 7.x

### 前端
- Vue 3.4 + TypeScript
- Vite 5.x
- Element Plus
- Pinia
- Axios

## 环境要求

- Docker & Docker Compose
- JDK 17+
- Node.js 18+
- MySQL 8.x (可选，本地开发可用Docker)
- Redis 7.x (可选，本地开发可用Docker)

## 快速部署

### 使用 Docker Compose (推荐)

```bash
# 进入项目目录
cd D:/blog

# 启动所有服务
docker-compose up -d

# 查看服务状态
docker-compose ps

# 查看日志
docker-compose logs -f
```

服务启动后：
- 前端：http://localhost
- 后端API：http://localhost:8080
- MySQL：localhost:3306
- Redis：localhost:6379

### 本地开发

#### 1. 初始化数据库

```bash
# 使用Docker启动MySQL
docker run -d --name blog-mysql \
  -e MYSQL_ROOT_PASSWORD=root \
  -e MYSQL_DATABASE=blog \
  -p 3306:3306 \
  mysql:8.0

# 执行初始化SQL
docker exec -i blog-mysql mysql -uroot -proot < blog-backend/src/main/resources/sql/init.sql
```

#### 2. 启动后端

```bash
cd blog-backend

# 使用Maven启动
mvn spring-boot:run

# 或打包后运行
mvn clean package -DskipTests
java -jar target/blog-backend-1.0.0.jar
```

#### 3. 启动前端

```bash
cd blog-frontend

# 安装依赖
npm install

# 启动开发服务器
npm run dev

# 构建生产版本
npm run build
```

## 接口文档

启动后端后访问：http://localhost:8080/doc.html

### 主要API

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| POST | /api/auth/register | 用户注册 | 否 |
| POST | /api/auth/login | 用户登录 | 否 |
| GET | /api/auth/me | 获取当前用户 | 是 |
| GET | /api/notes | 获取笔记列表 | 否 |
| GET | /api/notes/{id} | 获取笔记详情 | 否 |
| POST | /api/notes | 创建笔记 | 是 |
| PUT | /api/notes/{id} | 更新笔记 | 是 |
| DELETE | /api/notes/{id} | 删除笔记 | 是 |
| GET | /api/questions | 获取问题列表 | 否 |
| GET | /api/questions/{id} | 获取问题详情 | 否 |
| POST | /api/questions | 创建问题 | 是 |
| POST | /api/answers | 创建回答 | 是 |
| POST | /api/answers/{id}/like | 点赞回答 | 是 |
| POST | /api/answers/{id}/accept | 采纳回答 | 是 |

## 配置说明

### 后端配置 (application.yml)

```yaml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/blog
    username: root
    password: root
  
  data:
    redis:
      host: localhost
      port: 6379

jwt:
  secret: your-secret-key
  expiration: 86400000  # 24小时
```

### 环境变量

| 变量 | 默认值 | 说明 |
|------|--------|------|
| SPRING_DATASOURCE_URL | jdbc:mysql://mysql:3306/blog | 数据库连接 |
| SPRING_DATASOURCE_USERNAME | root | 数据库用户名 |
| SPRING_DATASOURCE_PASSWORD | root | 数据库密码 |
| SPRING_DATA_REDIS_HOST | redis | Redis地址 |
| JWT_SECRET | - | JWT密钥 |

## 测试账号

初始化SQL中创建了测试账号：
- 用户名：admin，密码：admin123
- 用户名：testuser，密码：user123

## 目录结构

```
blog/
├── docker-compose.yml          # Docker部署配置
├── blog-backend/              # 后端项目
│   ├── src/main/java/
│   │   └── com/blog/
│   │       ├── config/       # 配置类
│   │       ├── controller/   # 控制器
│   │       ├── entity/       # 实体类
│   │       ├── mapper/       # 数据访问
│   │       ├── service/      # 业务逻辑
│   │       ├── dto/          # 数据传输对象
│   │       ├── security/     # 安全认证
│   │       └── exception/    # 异常处理
│   └── src/main/resources/
│       ├── application.yml
│       └── sql/init.sql
│
└── blog-frontend/             # 前端项目
    ├── src/
    │   ├── api/             # API封装
    │   ├── components/      # 公共组件
    │   ├── views/          # 页面视图
    │   ├── router/         # 路由配置
    │   ├── stores/         # Pinia状态
    │   └── types/          # TypeScript类型
    └── vite.config.ts
```

## 常见问题

1. **Docker镜像拉取失败**
   - 配置Docker镜像加速器：编辑 `C:\Users\xxx\.docker\config.json`

2. **端口被占用**
   - 修改docker-compose.yml中的端口映射

3. **数据库连接失败**
   - 确保MySQL容器已启动并健康
   - 检查环境变量配置

## 生产环境建议

1. 修改JWT_SECRET为强密钥
2. 使用HTTPS
3. 配置定期数据库备份
4. 启用Redis持久化
5. 配置日志收集系统

## 性能优化说明

### Redis缓存策略

系统使用Spring Cache + Redis实现多级缓存：

- **笔记缓存**：笔记列表和详情使用`notes`缓存，TTL 30分钟
- **问题缓存**：问题列表和详情使用`questions`缓存，TTL 30分钟
- **缓存更新**：创建、更新、删除操作自动清除缓存

缓存配置位于 `RedisConfig.java`：
```java
RedisCacheConfiguration.defaultCacheConfig()
    .entryTtl(Duration.ofMinutes(30))
```

### 接口限流

系统使用Bucket4j实现令牌桶限流：

- **默认限制**：60次请求/分钟
- **限流粒度**：按Token（登录用户）或IP（未登录用户）
- **配置位置**：`RateLimitFilter.java`

```java
Bandwidth limit = Bandwidth.classic(60, Refill.greedy(60, Duration.ofMinutes(1)));
```

### 前端打包优化

Vite配置已启用以下优化：

- **代码分割**：Element Plus和Vue核心库分离打包
- **Terser压缩**：移除console和debugger
- **Gzip压缩**：Nginx配置启用gzip

```javascript
build: {
    minify: 'terser',
    rollupOptions: {
        output: {
            manualChunks: {
                'element-plus': ['element-plus'],
                'vue-vendor': ['vue', 'vue-router', 'pinia']
            }
        }
    }
}
```
