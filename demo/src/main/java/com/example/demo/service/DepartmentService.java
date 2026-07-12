package com.example.demo.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.demo.entity.Department;

import java.util.List;

/**
 * 部门 Service 接口
 *
 * <p><b>📖 讲解要点：</b></p>
 * <ul>
 *   <li>接口定义业务能力（做什么），实现类写具体逻辑（怎么做）</li>
 *   <li>面向接口编程的好处：可以轻松替换实现（比如换 Mapper 实现）</li>
 *   <li>Controller 只依赖 Service 接口，不依赖实现类</li>
 * </ul>
 *
 * <p><b>🆚 AI 协作提示：</b>部分 AI 生成的代码会跳过 Service 接口直接写实现类。
 * 这在超简单项目里可以，但正式项目必须接口+实现分离——告诉 AI "Service 要接口和实现分开"。</p>
 */
public interface DepartmentService {

    Department getById(Long id);

    List<Department> listAll();

    IPage<Department> page(int current, int size);

    Department create(Department department);

    Department update(Department department);

    void delete(Long id);
}
