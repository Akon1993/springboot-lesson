package com.example.demo.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.demo.entity.Employee;

import java.math.BigDecimal;
import java.util.List;

/**
 * 员工 Service 接口
 */
public interface EmployeeService {

    Employee getById(Long id);

    List<Employee> listByDepartment(Long departmentId);

    /**
     * 分页查询（带部门名称）
     */
    IPage<Employee> page(int current, int size, String name, Long departmentId, Integer status);

    Employee create(Employee employee);

    Employee update(Employee employee);

    void delete(Long id);

    /**
     * 批量调薪（展示 @Transactional 的典型场景）
     *
     * @param departmentId 部门 ID
     * @param ratio        调薪比例，如 0.1 表示涨 10%
     */
    void batchAdjustSalary(Long departmentId, BigDecimal ratio);
}
