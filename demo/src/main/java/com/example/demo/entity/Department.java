package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 部门实体 —— 一张表对应一个 Entity 类
 *
 * <p><b>📖 讲解要点：</b></p>
 * <ul>
 *   <li>{@code @TableName} —— 类名和数据库表名不一致时指定映射</li>
 *   <li>{@code @TableId} —— 标记主键，type=IdType.AUTO 表示数据库自增</li>
 *   <li>{@code @TableField} —— 当字段名和数据库列名不一致时使用</li>
 *   <li>{@code @TableLogic} —— 逻辑删除标记（0=正常, 1=已删除），deleteById 实际执行 UPDATE 而非 DELETE</li>
 * </ul>
 */
@Data
@TableName("department")
public class Department {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 部门名称 */
    private String name;

    /** 部门描述 */
    private String description;

    /** 创建时间 —— 插入时自动填充 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /** 更新时间 —— 插入和更新时自动填充 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /** 逻辑删除标记 */
    @TableLogic
    private Integer deleted;
}
