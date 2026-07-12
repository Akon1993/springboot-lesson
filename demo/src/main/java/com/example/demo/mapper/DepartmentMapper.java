package com.example.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.entity.Department;
import org.apache.ibatis.annotations.Mapper;

/**
 * 部门 Mapper 接口
 *
 * <p><b>📖 讲解要点：</b></p>
 * <ul>
 *   <li>继承 {@code BaseMapper<Department>} 就拥有了完整的单表 CRUD 能力</li>
 *   <li><b>不需要写实现类！</b> MyBatis Plus 在运行期自动生成代理实现</li>
 *   <li>继承后自动获得的方法：insert、deleteById、updateById、selectById、
 *       selectList、selectPage... <b>零 SQL</b></li>
 *   <li>{@code @Mapper} 注解告诉 Spring 这是一个 MyBatis Mapper</li>
 * </ul>
 *
 * <p><b>对比传统 MyBatis：</b></p>
 * 以前需要：1个 Mapper 接口 + 1个 XML 映射文件 + 每个方法手写 SQL
 * <br>
 * 现在：1个接口继承 BaseMapper，完事。
 */
@Mapper
public interface DepartmentMapper extends BaseMapper<Department> {
    // 自定义复杂查询方法才需要写在这里
    // 简单 CRUD 全由 BaseMapper 提供
}
