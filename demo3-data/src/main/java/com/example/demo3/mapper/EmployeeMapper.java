package com.example.demo3.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo3.entity.Employee;
import org.apache.ibatis.annotations.Mapper;

/**
 * 员工 Mapper —— MyBatis Plus 的核心演示
 *
 * <p><b>讲解要点（数据层的"约定大于配置"）：</b></p>
 * <ul>
 *   <li>① 继承 BaseMapper&lt;Employee&gt; —— 就这一行！</li>
 *   <li>② 自动获得的方法（无需写任何 SQL、无需写实现类）：
 *     <ul>
 *       <li>insert(Employee) —— 新增</li>
 *       <li>deleteById(Long) —— 按 ID 删除（逻辑删除自动变 UPDATE）</li>
 *       <li>updateById(Employee) —— 按 ID 更新</li>
 *       <li>selectById(Long) —— 按 ID 查询</li>
 *       <li>selectList(Wrapper) —— 条件查询</li>
 *       <li>selectPage(Page, Wrapper) —— 分页查询</li>
 *       <li>selectCount(Wrapper) —— 统计数量</li>
 *     </ul>
 *   </li>
 *   <li>③ 对比传统 MyBatis：需要 Mapper 接口 + XML 映射文件 + 每条 SQL 手写</li>
 *   <li>④ 如果有复杂查询（多表关联），可以在这里写 @Select 注解或配 XML</li>
 * </ul>
 */
@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {
    // 空接口！
    // 但继承了 BaseMapper<Employee> 之后，你拥有了十几种数据库操作方法。
    // MyBatis Plus 在运行期自动生成代理实现类——"约定大于配置"在数据层的体现。
}
