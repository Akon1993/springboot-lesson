package com.example.demo.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.demo.common.Result;
import com.example.demo.entity.Employee;
import com.example.demo.service.EmployeeService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 员工管理 Controller —— 完整 CRUD + 分页 + 批量操作
 *
 * <p><b>📖 讲解要点：</b></p>
 * <ul>
 *   <li>分页查询参数：current（当前页）、size（每页条数）—— 前端必备</li>
 *   <li>{@code @Valid} + {@code @NotBlank} —— 参数校验，在 Controller 层就拦住非法数据</li>
 *   <li>业务逻辑在 Service 层，Controller 只负责<b>接收参数、调用 Service、返回结果</b></li>
 * </ul>
 *
 * <p><b>🆚 AI 协作提示：</b></p>
 * <ul>
 *   <li>AI 常把业务逻辑写在 Controller 里——审查时要注意 Controller 是否"太胖"</li>
 *   <li>AI 容易遗漏 {@code @Valid} 参数校验</li>
 * </ul>
 */
@Slf4j
@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    /**
     * 分页查询员工
     *
     * <p>GET /api/employees?current=1&size=10&name=张&departmentId=1&status=1</p>
     * <p>所有查询参数都是可选的</p>
     */
    @GetMapping
    public Result<IPage<Employee>> page(@RequestParam(defaultValue = "1") int current,
                                         @RequestParam(defaultValue = "10") int size,
                                         @RequestParam(required = false) String name,
                                         @RequestParam(required = false) Long departmentId,
                                         @RequestParam(required = false) Integer status) {
        IPage<Employee> page = employeeService.page(current, size, name, departmentId, status);
        return Result.success(page);
    }

    @GetMapping("/{id}")
    public Result<Employee> getById(@PathVariable Long id) {
        return Result.success(employeeService.getById(id));
    }

    /**
     * 按部门查员工
     */
    @GetMapping("/by-department/{departmentId}")
    public Result<List<Employee>> listByDepartment(@PathVariable Long departmentId) {
        return Result.success(employeeService.listByDepartment(departmentId));
    }

    /**
     * 新增员工 —— {@code @Valid} 触发参数校验
     */
    @PostMapping
    public Result<Employee> create(@Valid @RequestBody Employee employee) {
        return Result.success(employeeService.create(employee));
    }

    @PutMapping("/{id}")
    public Result<Employee> update(@PathVariable Long id,
                                    @RequestBody Employee employee) {
        employee.setId(id);
        return Result.success(employeeService.update(employee));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        employeeService.delete(id);
        return Result.success();
    }

    /**
     * 批量调薪 —— 展示 {@code @Transactional} 的实际应用
     *
     * <p>POST /api/employees/batch-adjust-salary</p>
     * <pre>{@code
     * {
     *   "departmentId": 1,
     *   "ratio": 0.1     // 涨 10%
     * }
     * }</pre>
     */
    @PostMapping("/batch-adjust-salary")
    public Result<Void> batchAdjustSalary(
            @RequestBody @Valid SalaryAdjustRequest request) {
        employeeService.batchAdjustSalary(request.departmentId, request.ratio);
        return Result.success("批量调薪完成", null);
    }

    /**
     * 请求体 DTO（Data Transfer Object）—— 专门为接口参数定义
     *
     * <p><b>📖 讲解要点：</b>不用 Entity 直接接收请求参数，
     * 而是定义专门的 DTO 类，职责更清晰。</p>
     */
    public static class SalaryAdjustRequest {
        @NotNull(message = "部门 ID 不能为空")
        public Long departmentId;

        @NotNull(message = "调薪比例不能为空")
        @DecimalMin(value = "-0.5", message = "降薪幅度不能超过 50%")
        @DecimalMax(value = "1.0", message = "涨薪幅度不能超过 100%")
        public BigDecimal ratio;
    }
}
