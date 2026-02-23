-- 创建数据库
CREATE DATABASE IF NOT EXISTS blog DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE blog;

-- 用户表
CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码(BCrypt加密)',
    nickname VARCHAR(50) DEFAULT NULL COMMENT '昵称',
    email VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    avatar VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
    status TINYINT DEFAULT 1 COMMENT '状态: 0-禁用 1-正常',
    role VARCHAR(20) DEFAULT 'USER' COMMENT '角色: USER-普通用户 ADMIN-管理员',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_username (username),
    INDEX idx_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 分类表
CREATE TABLE IF NOT EXISTS category (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    name VARCHAR(50) NOT NULL COMMENT '分类名称',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='分类表';

-- 学习笔记表
CREATE TABLE IF NOT EXISTS note (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    title VARCHAR(200) NOT NULL COMMENT '标题',
    content LONGTEXT NOT NULL COMMENT '内容(Markdown)',
    summary VARCHAR(500) DEFAULT NULL COMMENT '摘要',
    category_id BIGINT DEFAULT NULL COMMENT '分类ID',
    view_count INT DEFAULT 0 COMMENT '浏览量',
    like_count INT DEFAULT 0 COMMENT '点赞数',
    status TINYINT DEFAULT 1 COMMENT '状态: 0-草稿 1-已发布 2-已删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_user_id (user_id),
    INDEX idx_category_id (category_id),
    INDEX idx_status (status),
    INDEX idx_create_time (create_time),
    FOREIGN KEY (user_id) REFERENCES sys_user(id) ON DELETE CASCADE,
    FOREIGN KEY (category_id) REFERENCES category(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学习笔记表';

-- 问题表
CREATE TABLE IF NOT EXISTS question (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    title VARCHAR(200) NOT NULL COMMENT '标题',
    content LONGTEXT NOT NULL COMMENT '内容',
    view_count INT DEFAULT 0 COMMENT '浏览量',
    answer_count INT DEFAULT 0 COMMENT '回答数',
    status TINYINT DEFAULT 1 COMMENT '状态: 0-草稿 1-已发布 2-已关闭 3-已删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_user_id (user_id),
    INDEX idx_status (status),
    INDEX idx_create_time (create_time),
    FOREIGN KEY (user_id) REFERENCES sys_user(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='问题表';

-- 回答表
CREATE TABLE IF NOT EXISTS answer (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    question_id BIGINT NOT NULL COMMENT '问题ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    content LONGTEXT NOT NULL COMMENT '回答内容',
    like_count INT DEFAULT 0 COMMENT '点赞数',
    is_accepted TINYINT DEFAULT 0 COMMENT '是否被采纳: 0-未采纳 1-已采纳',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_question_id (question_id),
    INDEX idx_user_id (user_id),
    INDEX idx_is_accepted (is_accepted),
    INDEX idx_create_time (create_time),
    FOREIGN KEY (question_id) REFERENCES question(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES sys_user(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='回答表';

-- 插入默认分类数据
INSERT INTO category (name) VALUES 
    ('前端开发'),
    ('后端开发'),
    ('移动开发'),
    ('DevOps'),
    ('数据库'),
    ('算法与数据结构'),
    ('计算机基础'),
    ('其他');

-- 插入测试管理员用户 (密码: admin123)
INSERT INTO sys_user (username, password, nickname, email, role) VALUES 
    ('admin', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '管理员', 'admin@blog.com', 'ADMIN');

-- 插入测试普通用户 (密码: user123)
INSERT INTO sys_user (username, password, nickname, email, role) VALUES 
    ('testuser', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '测试用户', 'test@blog.com', 'USER');
