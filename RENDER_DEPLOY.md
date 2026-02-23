# Render 部署配置
# 部署前需要在 Render 上创建：
# 1. PostgreSQL 数据库 (Render Dashboard -> New -> PostgreSQL)
# 2. Redis (可选，Render Dashboard -> New -> Redis)
# 3. Web Service (Backend)
# 4. Static Site (Frontend)

# ==================== 后端部署 ====================
# 在 Render 上创建 PostgreSQL 后，会有 Internal Database URL:
# 格式: postgres://xxx:xxx@xxx.render.com:5432/xxx
#
# 对应的 JDBC URL:
# jdbc:postgresql://xxx.render.com:5432/xxx
#
# 环境变量配置:
# SPRING_DATASOURCE_DRIVER: org.postgresql.Driver
# SPRING_DATASOURCE_URL: jdbc:postgresql://<host>:<port>/<database>
# SPRING_DATASOURCE_USERNAME: <username>
# SPRING_DATASOURCE_PASSWORD: <password>
# SPRING_DATA_REDIS_HOST: <redis-host>
# SPRING_DATA_REDIS_PORT: 6379
# SPRING_DATA_REDIS_PASSWORD: <redis-password>
# JWT_SECRET: <your-256-bit-secret>

# ==================== 部署步骤 ====================
# 1. 将代码推送到 GitHub
# 2. 在 Render Dashboard 创建 PostgreSQL 数据库
# 3. 创建 Backend Web Service，选择 GitHub 仓库
# 4. 在 Environment 中配置上面的环境变量
# 5. 同样方式部署 Frontend (Static Site)

services:
  - type: web
    name: blog-backend
    env: docker
    region: oregon
    buildCommand: |
      mvn clean package -DskipTests
    startCommand: java -jar target/blog-backend-1.0.0.jar
    envVars:
      - key: JAVA_OPTS
        value: -Xmx512m
      # 数据库配置 (请修改为你的 Render PostgreSQL 信息)
      - key: SPRING_DATASOURCE_DRIVER
        value: org.postgresql.Driver
      - key: SPRING_DATASOURCE_URL
        value: jdbc:postgresql://your-db-host.render.cloud:5432/your-db-name
      - key: SPRING_DATASOURCE_USERNAME
        value: your-db-username
      - key: SPRING_DATASOURCE_PASSWORD
        value: your-db-password
      # Redis 配置 (如果使用 Render Redis)
      - key: SPRING_DATA_REDIS_HOST
        value: your-redis-host
      - key: SPRING_DATA_REDIS_PORT
        value: 6379
      - key: SPRING_DATA_REDIS_PASSWORD
        value: your-redis-password
      # JWT 密钥 (请修改为强密钥)
      - key: JWT_SECRET
        value: your-super-secret-jwt-key-change-in-production-at-least-256-bits-long

# 注意：Frontend 需要单独部署为 Static Site
# 在 Render 创建 Static Site，选择 blog-frontend 目录
# Build Command: npm run build
# Publish directory: dist
