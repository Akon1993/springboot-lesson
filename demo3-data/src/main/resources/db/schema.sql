-- =============================================================
-- demo3-data：数据库建表脚本（H2 内存数据库）
-- 应用启动时 Spring Boot 自动执行
-- =============================================================

CREATE TABLE IF NOT EXISTS employee (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    name          VARCHAR(100) NOT NULL COMMENT '姓名',
    age           INT COMMENT '年龄',
    email         VARCHAR(200) COMMENT '邮箱',
    department_id BIGINT COMMENT '所属部门ID',
    salary        DECIMAL(10, 2) COMMENT '薪资',
    status        TINYINT DEFAULT 1 COMMENT '状态: 1=在职, 0=离职',
    create_time   DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time   DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted       TINYINT DEFAULT 0 COMMENT '逻辑删除: 0=正常, 1=已删除'
);

-- 索引
CREATE INDEX IF NOT EXISTS idx_employee_name ON employee(name);
CREATE INDEX IF NOT EXISTS idx_employee_department ON employee(department_id);
CREATE INDEX IF NOT EXISTS idx_employee_status ON employee(status);
