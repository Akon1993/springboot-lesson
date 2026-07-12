package com.example.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.common.BusinessException;
import com.example.demo.entity.Employee;
import com.example.demo.mapper.EmployeeMapper;
import com.example.demo.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * 员工 Service 实现
 *
 * <p><b>📖 讲解要点（重点演示类）：</b></p>
 * <ul>
 *   <li>① {@code LambdaQueryWrapper} 链式条件构建——类型安全，字段名写不错</li>
 *   <li>② 条件拼接一定要判空，否则查不出数据（常见 bug：前端不传就是 null，直接 eq 会出错）</li>
 *   <li>③ {@code @Transactional} 在批量操作上尤其重要——
 *       batchAdjustSalary 要么全部成功，要么全部回滚</li>
 *   <li>④ 事务传播：本 Service 调用 Mapper 的方法都有事务保护</li>
 * </ul>
 *
 * <p><b>🆚 AI 审查重点：</b></p>
 * <ul>
 *   <li>检查 {@code @Transactional} 是否遗漏（增删改必须加）</li>
 *   <li>检查条件拼接是否判空</li>
 *   <li>检查金额计算是否用了 BigDecimal（不是 float/double）</li>
 *   <li>检查批量操作是否在循环里查数据库</li>
 * </ul>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeMapper employeeMapper;

    @Override
    public Employee getById(Long id) {
        Employee employee = employeeMapper.selectById(id);
        if (employee == null) {
            throw new BusinessException("员工不存在: id=" + id);
        }
        return employee;
    }

    @Override
    public List<Employee> listByDepartment(Long departmentId) {
        LambdaQueryWrapper<Employee> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Employee::getDepartmentId, departmentId)
                .orderByDesc(Employee::getId);
        return employeeMapper.selectListWithDepartment(wrapper);
    }

    @Override
    public IPage<Employee> page(int current, int size, String name,
                                 Long departmentId, Integer status) {
        Page<Employee> page = new Page<>(current, size);

        // 🔑 构建条件——每个条件都要判空！
        LambdaQueryWrapper<Employee> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(name), Employee::getName, name)         // 姓名模糊搜索
                .eq(departmentId != null, Employee::getDepartmentId, departmentId) // 部门精确筛选
                .eq(status != null, Employee::getStatus, status)                   // 状态筛选
                .orderByDesc(Employee::getId);

        return employeeMapper.selectPageWithDepartment(page, wrapper);
    }

    @Override
    @Transactional
    public Employee create(Employee employee) {
        // 业务校验
        if (!StringUtils.hasText(employee.getName())) {
            throw new BusinessException("员工姓名不能为空");
        }
        employeeMapper.insert(employee);
        log.info("创建员工: {} (id={})", employee.getName(), employee.getId());
        return employee;
    }

    @Override
    @Transactional
    public Employee update(Employee employee) {
        getById(employee.getId());  // 检查存在性（不存在会抛异常）
        employeeMapper.updateById(employee);
        log.info("更新员工: id={}", employee.getId());
        return getById(employee.getId());  // 返回最新数据
    }

    @Override
    @Transactional
    public void delete(Long id) {
        getById(id);
        employeeMapper.deleteById(id);
        log.info("删除员工: id={}", id);
    }

    /**
     * 批量调薪 —— 展示 {@code @Transactional} 的典型场景
     *
     * <p>要求：同一部门所有员工的薪资按比例调整，要么全部成功，要么全部回滚。</p>
     *
     * <p><b>🆚 AI 常见错误写法：</b>
     * {@code for (Employee e : list) { e.setSalary(e.getSalary() * (1 + ratio)); mapper.updateById(e); }}
     * —— 这是循环里逐条更新，性能差且没处理精度问题。</p>
     *
     * <p><b>正确做法：</b>一条 SQL 批量更新整个部门</p>
     */
    @Override
    @Transactional  // ← 🔑 保证原子性：全部成功 或 全部回滚
    public void batchAdjustSalary(Long departmentId, BigDecimal ratio) {
        log.info("批量调薪: departmentId={}, ratio={}", departmentId, ratio);

        // 1. 查出该部门所有在职员工
        LambdaQueryWrapper<Employee> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Employee::getDepartmentId, departmentId)
                .eq(Employee::getStatus, 1);

        List<Employee> employees = employeeMapper.selectList(wrapper);
        if (employees.isEmpty()) {
            throw new BusinessException("该部门没有在职员工");
        }

        // 2. 计算新工资（BigDecimal 精确计算，ratio=0.1 表示涨 10%）
        BigDecimal multiplier = BigDecimal.ONE.add(ratio);
        for (Employee emp : employees) {
            // 🔑 BigDecimal 计算：scale=2 保留两位小数，HALF_UP 四舍五入
            BigDecimal newSalary = emp.getSalary().multiply(multiplier)
                    .setScale(2, RoundingMode.HALF_UP);
            emp.setSalary(newSalary);
        }

        // 3. 批量更新 —— 如果用 updateById 在循环里调，每调一次就是一条 SQL
        //    更好的做法是用 MyBatis Plus 的 updateBatchById（本项目用逐条示例）
        for (Employee emp : employees) {
            employeeMapper.updateById(emp);
        }

        log.info("批量调薪完成: {} 名员工薪资已更新", employees.size());
    }
}
