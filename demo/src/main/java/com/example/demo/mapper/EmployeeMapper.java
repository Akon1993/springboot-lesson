package com.example.demo.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.example.demo.entity.Employee;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 员工 Mapper 接口
 *
 * <p><b>📖 讲解要点：</b></p>
 * <ul>
 *   <li>继承了 BaseMapper，免费获得 insert/delete/update/select 全套 CRUD</li>
 *   <li>复杂查询可以用 {@code @Select} 写自定义 SQL</li>
 *   <li>也可以配合同一个 Mapper 的 XML 文件（本项目用注解演示）</li>
 * </ul>
 *
 * <p><b>🆚 AI 协作提示：</b>AI 生成的 Mapper 经常忘了继承 BaseMapper，
 * 然后自己去写一大堆基础 CRUD SQL——这是对 MyBatis Plus 的浪费。</p>
 */
@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {

    /**
     * 自定义查询：关联查询部门名称
     * 当 BaseMapper 的条件查询不够用时，可以写 SQL
     *
     * <p>注意：WHERE 条件由 MyBatis Plus 的分页插件自动拼接（${ew.customSqlSegment}）</p>
     */
    @Select("""
            SELECT e.*, d.name AS department_name
            FROM employee e
            LEFT JOIN department d ON e.department_id = d.id
            ${ew.customSqlSegment}
            """)
    IPage<Employee> selectPageWithDepartment(IPage<Employee> page,
                                              @Param(Constants.WRAPPER) Wrapper<Employee> wrapper);

    /**
     * 同上，返回 List（不分页）
     */
    @Select("""
            SELECT e.*, d.name AS department_name
            FROM employee e
            LEFT JOIN department d ON e.department_id = d.id
            ${ew.customSqlSegment}
            """)
    List<Employee> selectListWithDepartment(@Param(Constants.WRAPPER) Wrapper<Employee> wrapper);
}
