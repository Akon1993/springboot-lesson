package com.example.demo.common;

import lombok.Getter;

/**
 * 业务异常——替代到处 try-catch 和返回各种奇怪错误码
 *
 * <p><b>📖 讲解要点：</b></p>
 * <ul>
 *   <li>Service 层遇到业务规则不满足，直接 throw，不要返回 null 或 -1</li>
 *   <li>由 {@link GlobalExceptionHandler} 统一捕获并转成 Result 格式返回</li>
 * </ul>
 *
 * <p><b>🆚 AI 协作提示：</b>AI 生成的代码经常在 Service 里 return null 表示"没找到"。
 * 应该要求 AI 改成 throw BusinessException，由全局异常处理器统一处理。</p>
 */
@Getter
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
}
