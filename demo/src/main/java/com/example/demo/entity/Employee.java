package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 员工实体
 *
 * <p><b>📖 讲解要点：</b></p>
 * <ul>
 *   <li>日期用 {@code LocalDateTime}，<b>不要用 {@code java.util.Date}</b>（已过时）</li>
 *   <li>金额用 {@code BigDecimal}，<b>不要用 {@code float/double}</b>（精度丢失）</li>
 *   <li>{@code @TableField(exist = false)} —— 标记非数据库字段，仅用于业务逻辑</li>
 * </ul>
 *
 * <p><b>🆚 AI 协作提示：</b>AI 老版本知识可能生成 {@code java.util.Date} 和 {@code float}。
 * 审查时必须纠正。</p>
 */
@Data
@TableName("employee")
public class Employee {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 员工姓名 */
    private String name;

    /** 年龄 */
    private Integer age;

    /** 邮箱 */
    private String email;

    /** 所属部门 ID */
    private Long departmentId;

    /** 薪资（必须用 BigDecimal，float/double 有精度问题） */
    private BigDecimal salary;

    /** 状态：1=在职, 0=离职 */
    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;

    // ========== 非数据库字段（仅用于返回数据时附带） ==========

    /** 部门名称 —— 查员工时顺带查出部门名，但数据库表里没这一列 */
    @TableField(exist = false)
    private String departmentName;
}
