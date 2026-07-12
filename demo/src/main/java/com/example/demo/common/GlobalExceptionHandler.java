package com.example.demo.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 *
 * <p><b>📖 讲解要点：</b></p>
 * <ul>
 *   <li>{@code @RestControllerAdvice} = @ControllerAdvice + @ResponseBody</li>
 *   <li>一个类统一处理整个应用的所有异常，Controller 里不需要写 try-catch</li>
 *   <li><b>对比传统 Spring MVC：</b>以前要么每个 Controller 写 try-catch，
 *       要么在 web.xml 配 error-page，要么用 HandlerExceptionResolver——
 *       现在一个注解搞定</li>
 *   <li>不同的异常类型匹配不同的 {@code @ExceptionHandler}，非常灵活</li>
 * </ul>
 *
 * <p><b>🆚 AI 协作提示：</b>AI 生成的代码经常在 Controller 里写满 try-catch。
 * 审查代码时看到 Controller 里有 try-catch，就该质疑——是不是该交给全局异常处理器？</p>
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 业务异常 —— 我们自己抛的，直接返回给前端
     */
    @ExceptionHandler(BusinessException.class)
    public Result<?> handleBusinessException(BusinessException e) {
        log.warn("业务异常: {}", e.getMessage());
        return Result.error(e.getCode(), e.getMessage());
    }

    /**
     * 参数校验失败 —— 前端传的参数不合法
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<?> handleValidationException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .reduce((a, b) -> a + "; " + b)
                .orElse("参数校验失败");
        log.warn("参数校验失败: {}", message);
        return Result.error(400, message);
    }

    /**
     * 兜底异常 —— 所有没预料的异常都到这里
     * 注意：返回给前端的是通用提示，不暴露内部错误细节（安全考虑）
     */
    @ExceptionHandler(Exception.class)
    public Result<?> handleException(Exception e) {
        log.error("系统异常", e);  // 详细错误记录到日志，便于排查
        return Result.error(500, "服务器内部错误，请联系管理员");
    }
}
