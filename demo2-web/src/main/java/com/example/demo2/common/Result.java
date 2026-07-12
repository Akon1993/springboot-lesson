package com.example.demo2.common;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * 统一响应结果包装
 *
 * <p><b>讲解要点：</b></p>
 * <ul>
 *   <li>所有接口统一返回 {@code {"code":200,"message":"...","data":...}} 格式</li>
 *   <li>前端只需判断 code 是否为 200，不用猜测每个接口的返回格式</li>
 *   <li>Result&lt;T&gt; 泛型：data 可以是 String、Employee、List、Page... 任意类型</li>
 *   <li>@JsonInclude(NON_NULL)：data 为空时不返回 data 字段，减少传输体积</li>
 * </ul>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Result<T> {

    private int code;
    private String message;
    private T data;

    public int getCode() { return code; }
    public void setCode(int code) { this.code = code; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public T getData() { return data; }
    public void setData(T data) { this.data = data; }

    // ==================== 工厂方法 ====================

    public static <T> Result<T> success() {
        return success(null);
    }

    public static <T> Result<T> success(T data) {
        Result<T> r = new Result<>();
        r.code = 200;
        r.message = "操作成功";
        r.data = data;
        return r;
    }

    public static <T> Result<T> success(String message, T data) {
        Result<T> r = new Result<>();
        r.code = 200;
        r.message = message;
        r.data = data;
        return r;
    }

    public static <T> Result<T> error(int code, String message) {
        Result<T> r = new Result<>();
        r.code = code;
        r.message = message;
        return r;
    }

    public static <T> Result<T> error(String message) {
        return error(500, message);
    }
}
