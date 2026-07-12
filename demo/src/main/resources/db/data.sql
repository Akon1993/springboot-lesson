-- =============================================================
-- 测试数据（每次启动时自动插入）
-- 用于演示 CRUD 接口时方便查看效果
-- =============================================================

-- 插入部门
INSERT INTO department (name, description) VALUES
('技术部', '负责产品研发和技术架构'),
('产品部', '负责需求分析和产品设计'),
('市场部', '负责市场推广和销售'),
('人事部', '负责招聘和员工关系'),
('财务部', '负责公司财务和预算管理');

-- 插入员工（关联到各部门）
INSERT INTO employee (name, age, email, department_id, salary, status) VALUES
-- 技术部 (department_id=1)
('张三', 28, 'zhangsan@example.com', 1, 18000.00, 1),
('李四', 32, 'lisi@example.com', 1, 25000.00, 1),
('王五', 25, 'wangwu@example.com', 1, 14000.00, 1),

-- 产品部 (department_id=2)
('赵六', 30, 'zhaoliu@example.com', 2, 22000.00, 1),
('孙七', 27, 'sunqi@example.com', 2, 16000.00, 1),

-- 市场部 (department_id=3)
('周八', 35, 'zhouba@example.com', 3, 20000.00, 1),
('吴九', 29, 'wujiu@example.com', 3, 15000.00, 0),  -- 已离职

-- 人事部 (department_id=4)
('郑十', 33, 'zhengshi@example.com', 4, 21000.00, 1),

-- 财务部 (department_id=5)
('陈一', 31, 'chenyi@example.com', 5, 23000.00, 1);
