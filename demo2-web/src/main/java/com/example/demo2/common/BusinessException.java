package com.example.demo2.common;

/**
 * 业务异常
 *
 * <p>Service 层遇到业务规则不满足时，直接 throw，不要 return null 或 -1。
 * 由 GlobalExceptionHandler 统一捕获并转成 Result 格式返回给前端。</p>
 */
public class BusinessException extends RuntimeException {

    private final int code;

    public BusinessException(String message) {
        super(message);
        this.code = 400;
    }

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    public int getCode() { return code; }
}
