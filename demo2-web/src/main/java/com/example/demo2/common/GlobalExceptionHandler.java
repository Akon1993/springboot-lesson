package com.example.demo2.common;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 *
 * <p><b>讲解要点：</b></p>
 * <ul>
 *   <li>@RestControllerAdvice 一个注解，全局处理整个应用的所有异常</li>
 *   <li>Controller 里不需要写任何 try-catch</li>
 *   <li>对比传统 Spring MVC：每个方法写 try-catch / web.xml 配 error-page
 *       ——现在一个类统一搞定</li>
 *   <li>业务异常返回友好提示；未知异常记录详细日志但只返回通用提示（安全考虑）</li>
 * </ul>
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public Result<?> handleBusinessException(BusinessException e) {
        return Result.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public Result<?> handleException(Exception e) {
        // 生产环境应该用 log.error 记录详细堆栈，这里简化
        System.err.println("[ERROR] " + e.getMessage());
        return Result.error(500, "服务器内部错误，请联系管理员");
    }
}
