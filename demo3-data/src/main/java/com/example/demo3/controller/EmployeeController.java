package com.example.demo3.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.demo3.common.Result;
import com.example.demo3.entity.Employee;
import com.example.demo3.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 员工管理 Controller —— 完整 RESTful CRUD
 *
 * <p><b>讲解要点：</b></p>
 * <ul>
 *   <li>① RESTful 风格：GET 查 / POST 增 / PUT 改 / DELETE 删</li>
 *   <li>② URL 用名词复数 /api/employees，层级清晰</li>
 *   <li>③ 分页参数 current/size，所有查询参数可选</li>
 *   <li>④ 所有返回值统一用 Result&lt;T&gt; 包装</li>
 *   <li>⑤ Controller 只负责接收参数、调用 Service、返回结果——业务逻辑全在 Service 层</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    /** GET /api/employees?current=1&size=10&name=张&departmentId=1&status=1 */
    @GetMapping
    public Result<IPage<Employee>> page(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) Integer status) {
        return Result.success(employeeService.page(current, size, name, departmentId, status));
    }

    /** GET /api/employees/1 */
    @GetMapping("/{id}")
    public Result<Employee> getById(@PathVariable Long id) {
        return Result.success(employeeService.getById(id));
    }

    /** GET /api/employees/all */
    @GetMapping("/all")
    public Result<List<Employee>> listAll() {
        return Result.success(employeeService.listAll());
    }

    /** POST /api/employees */
    @PostMapping
    public Result<Employee> create(@RequestBody Employee employee) {
        return Result.success(employeeService.create(employee));
    }

    /** PUT /api/employees/1 */
    @PutMapping("/{id}")
    public Result<Employee> update(@PathVariable Long id, @RequestBody Employee employee) {
        employee.setId(id);
        return Result.success(employeeService.update(employee));
    }

    /** DELETE /api/employees/1 */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        employeeService.delete(id);
        return Result.success();
    }
}
