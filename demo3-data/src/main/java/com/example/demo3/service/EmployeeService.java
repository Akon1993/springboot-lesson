package com.example.demo3.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.demo3.entity.Employee;
import java.util.List;

/**
 * 员工 Service 接口
 *
 * <p>定义业务能力（做什么），实现类写具体逻辑（怎么做）。
 * Controller 只依赖接口，不依赖实现。</p>
 */
public interface EmployeeService {

    Employee getById(Long id);

    List<Employee> listAll();

    IPage<Employee> page(int current, int size, String name, Long departmentId, Integer status);

    Employee create(Employee employee);

    Employee update(Employee employee);

    void delete(Long id);
}
