package com.example.demo3.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo3.common.BusinessException;
import com.example.demo3.entity.Employee;
import com.example.demo3.mapper.EmployeeMapper;
import com.example.demo3.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 员工 Service 实现 —— 完整展示数据层最佳实践
 *
 * <p><b>讲解要点：</b></p>
 * <ul>
 *   <li>① @Service — 标记为 Spring Bean</li>
 *   <li>② @RequiredArgsConstructor — Lombok 生成构造器，Spring 自动注入 Mapper</li>
 *   <li>③ @Transactional — 增删改必须加，保证原子性（全部成功或全部回滚）</li>
 *   <li>④ LambdaQueryWrapper — 链式调用构建条件，类型安全</li>
 *   <li>⑤ 条件拼接要判空 — 前端不传就是 null，直接 eq 会出错</li>
 *   <li>⑥ 业务校验不通过抛 BusinessException，不要 return null</li>
 * </ul>
 */
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
    public List<Employee> listAll() {
        return employeeMapper.selectList(
                new LambdaQueryWrapper<Employee>().orderByDesc(Employee::getId));
    }

    @Override
    public IPage<Employee> page(int current, int size, String name,
                                 Long departmentId, Integer status) {
        Page<Employee> page = new Page<>(current, size);

        // 🔑 构建查询条件——每个条件都要判空！
        LambdaQueryWrapper<Employee> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(name), Employee::getName, name)
               .eq(departmentId != null, Employee::getDepartmentId, departmentId)
               .eq(status != null, Employee::getStatus, status)
               .orderByDesc(Employee::getId);

        return employeeMapper.selectPage(page, wrapper);
    }

    @Override
    @Transactional
    public Employee create(Employee employee) {
        if (!StringUtils.hasText(employee.getName())) {
            throw new BusinessException("员工姓名不能为空");
        }
        employeeMapper.insert(employee);
        return employee;
    }

    @Override
    @Transactional
    public Employee update(Employee employee) {
        getById(employee.getId());  // 先确保存在
        employeeMapper.updateById(employee);
        return getById(employee.getId());
    }

    @Override
    @Transactional
    public void delete(Long id) {
        getById(id);
        employeeMapper.deleteById(id);  // MyBatis Plus 逻辑删除：实际执行 UPDATE SET deleted=1
    }
}
