package com.example.demo3.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 员工实体 —— 一个类对应数据库的 employee 表
 *
 * <p><b>讲解要点：</b></p>
 * <ul>
 *   <li>① @TableName("employee") — 类名和表名不一致时指定</li>
 *   <li>② @TableId(type = IdType.AUTO) — 主键自增</li>
 *   <li>③ 金额用 BigDecimal，不要用 float/double（精度丢失）</li>
 *   <li>④ 日期用 LocalDateTime，不要用 java.util.Date（已过时）</li>
 *   <li>⑤ @TableField(fill = ...) — 配合 MetaObjectHandler 自动填充</li>
 *   <li>⑥ @TableField(exist = false) — 非数据库字段，仅业务用</li>
 * </ul>
 */
@Data
@TableName("employee")
public class Employee {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;
    private Integer age;
    private String email;
    private Long departmentId;

    /** 薪资——必须用 BigDecimal */
    private BigDecimal salary;

    /** 状态：1=在职, 0=离职 */
    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /** 逻辑删除：0=正常, 1=已删除（MyBatis Plus 自动处理） */
    @TableLogic
    private Integer deleted;

    /** 非数据库字段——关联查询时附带部门名 */
    @TableField(exist = false)
    private String departmentName;
}
