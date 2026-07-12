-- =============================================================
-- demo3-data：测试数据
-- 应用启动时 Spring Boot 自动执行
-- =============================================================

INSERT INTO employee (name, age, email, department_id, salary, status) VALUES
('张三', 28, 'zhangsan@example.com', 1, 18000.00, 1),
('李四', 32, 'lisi@example.com', 1, 25000.00, 1),
('王五', 25, 'wangwu@example.com', 1, 14000.00, 1),
('赵六', 30, 'zhaoliu@example.com', 2, 22000.00, 1),
('孙七', 27, 'sunqi@example.com', 2, 16000.00, 1),
('周八', 35, 'zhouba@example.com', 3, 20000.00, 1),
('吴九', 29, 'wujiu@example.com', 3, 15000.00, 0),
('郑十', 33, 'zhengshi@example.com', 4, 21000.00, 1),
('陈一', 31, 'chenyi@example.com', 5, 23000.00, 1);
