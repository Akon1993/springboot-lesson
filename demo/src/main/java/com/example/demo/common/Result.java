package com.example.demo.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * 统一响应结果包装类
 *
 * <p><b>📖 讲解要点：</b></p>
 * <ul>
 *   <li>前端需要一个<b>确定的、统一的</b>数据格式来解析所有接口返回值</li>
 *   <li>{@code Result<T>} 泛型包装：code + message + data 三元组</li>
 *   <li>静态工厂方法 {@code success()} / {@code error()} 让调用方一目了然</li>
 *   <li>{@code @JsonInclude(JsonInclude.Include.NON_NULL)} 空值不返回，减少传输体积</li>
 * </ul>
 *
 * <p><b>🆚 AI 协作提示：</b>告诉 AI "所有接口统一返回 {@code Result<T>}"，
 * AI 就会自动用这个类包装响应。如果不说，AI 可能有的接口返回 String，
 * 有的返回 Entity，格式五花八门。</p>
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Result<T> {

    /** 状态码：200 成功，其他表示异常 */
    private int code;

    /** 提示信息 */
    private String message;

    /** 响应数据（泛型，可以是对象、列表、分页结果...） */
    private T data;

    // ==================== 工厂方法 ====================

    public static <T> Result<T> success() {
        return success(null);
    }

    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage("操作成功");
        result.setData(data);
        return result;
    }

    public static <T> Result<T> success(String message, T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage(message);
        result.setData(data);
        return result;
    }

    public static <T> Result<T> error(int code, String message) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        return result;
    }

    public static <T> Result<T> error(String message) {
        return error(500, message);
    }
}
