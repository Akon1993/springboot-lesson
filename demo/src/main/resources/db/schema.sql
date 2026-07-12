-- =============================================================
-- 数据库建表脚本（H2 内存数据库）
-- 每次启动时由 Spring Boot 自动执行
-- =============================================================

-- 部门表
CREATE TABLE IF NOT EXISTS department (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(100) NOT NULL COMMENT '部门名称',
    description VARCHAR(500) COMMENT '部门描述',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    deleted     TINYINT DEFAULT 0 COMMENT '逻辑删除: 0=正常, 1=已删除'
);

-- 员工表
CREATE TABLE IF NOT EXISTS employee (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    name          VARCHAR(100) NOT NULL COMMENT '姓名',
    age           INT COMMENT '年龄',
    email         VARCHAR(200) COMMENT '邮箱',
    department_id BIGINT COMMENT '所属部门ID',
    salary        DECIMAL(10, 2) COMMENT '薪资',
    status        TINYINT DEFAULT 1 COMMENT '状态: 1=在职, 0=离职',
    create_time   DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time   DATETIME DEFAULT CURRENT_TIMESTAMP,
    deleted       TINYINT DEFAULT 0
);

-- 为常用查询列建索引
CREATE INDEX IF NOT EXISTS idx_employee_department ON employee(department_id);
CREATE INDEX IF NOT EXISTS idx_employee_status ON employee(status);
CREATE INDEX IF NOT EXISTS idx_employee_name ON employee(name);
