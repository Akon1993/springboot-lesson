package com.example.demo.controller;

import com.example.demo.common.Result;
import com.example.demo.entity.Department;
import com.example.demo.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 部门管理 Controller —— RESTful API
 *
 * <p><b>📖 讲解要点（对照你同事讲过的 Spring MVC）：</b></p>
 * <ul>
 *   <li>{@code @RestController} = @Controller + @ResponseBody（每个方法都返回 JSON）</li>
 *   <li>{@code @RequestMapping("/api/departments")} 统一前缀</li>
 *   <li>HTTP 方法映射：
 *     GET → 查询、POST → 新增、PUT → 更新、DELETE → 删除</li>
 *   <li><b>你不需要配置任何东西！</b> Spring Boot 自动配好了消息转换器、JSON 序列化</li>
 *   <li>对比传统 Spring MVC：不用在 XML 里配 {@code <mvc:annotation-driven/>}</li>
 * </ul>
 *
 * <p><b>URL 设计约定（RESTful）：</b>
 * GET /api/departments —— 查全部<br>
 * GET /api/departments/{id} —— 查单个<br>
 * POST /api/departments —— 新增<br>
 * PUT /api/departments/{id} —— 更新<br>
 * DELETE /api/departments/{id} —— 删除</p>
 */
@RestController
@RequestMapping("/api/departments")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService departmentService;

    @GetMapping
    public Result<List<Department>> list() {
        return Result.success(departmentService.listAll());
    }

    @GetMapping("/{id}")
    public Result<Department> getById(@PathVariable Long id) {
        return Result.success(departmentService.getById(id));
    }

    @PostMapping
    public Result<Department> create(@RequestBody Department department) {
        return Result.success(departmentService.create(department));
    }

    @PutMapping("/{id}")
    public Result<Department> update(@PathVariable Long id,
                                      @RequestBody Department department) {
        department.setId(id);
        return Result.success(departmentService.update(department));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        departmentService.delete(id);
        return Result.success();
    }
}
