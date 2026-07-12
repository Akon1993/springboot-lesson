package com.example.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.common.BusinessException;
import com.example.demo.entity.Department;
import com.example.demo.mapper.DepartmentMapper;
import com.example.demo.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 部门 Service 实现
 *
 * <p><b>📖 讲解要点：</b></p>
 * <ul>
 *   <li>{@code @Service} 标记为 Spring Bean，被自动扫描和注入</li>
 *   <li>{@code @RequiredArgsConstructor} = Lombok 自动生成构造器，Spring 通过构造器注入 Mapper</li>
 *   <li>{@code @Transactional} 开启事务（增删改必须加）</li>
 *   <li>{@code LambdaQueryWrapper} 链式构建查询条件，比字符串拼接安全且不易写错字段名</li>
 * </ul>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentMapper departmentMapper;

    @Override
    public Department getById(Long id) {
        Department department = departmentMapper.selectById(id);
        if (department == null) {
            throw new BusinessException("部门不存在: id=" + id);
        }
        return department;
    }

    @Override
    public List<Department> listAll() {
        // 按 ID 倒序返回所有部门
        LambdaQueryWrapper<Department> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Department::getId);
        return departmentMapper.selectList(wrapper);
    }

    @Override
    public IPage<Department> page(int current, int size) {
        Page<Department> page = new Page<>(current, size);
        return departmentMapper.selectPage(page, null);
    }

    @Override
    @Transactional  // ← 写操作必须加事务
    public Department create(Department department) {
        departmentMapper.insert(department);
        log.info("创建部门: {} (id={})", department.getName(), department.getId());
        return department;
    }

    @Override
    @Transactional
    public Department update(Department department) {
        Department existing = getById(department.getId());  // 先查是否存在（不存在会抛异常）
        departmentMapper.updateById(department);
        log.info("更新部门: {} (id={})", department.getName(), department.getId());
        return department;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        getById(id);  // 先确保记录存在
        departmentMapper.deleteById(id);  // MyBatis Plus 逻辑删除：实际执行 UPDATE SET deleted=1
        log.info("删除部门: id={}", id);
    }
}
