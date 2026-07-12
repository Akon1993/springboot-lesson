package com.example.demo2.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * 日志切面 —— 和 demo1 完全一样
 *
 * <p>AOP 在 Web 环境下同样生效。每个 Controller 调用 Service 的方法，
 * 都会被这个切面拦截并打印耗时。</p>
 */
@Aspect
@Component
public class LoggingAspect {

    @Pointcut("execution(* com.example.demo2.service..*.*(..))")
    public void serviceLayer() {
    }

    @Around("serviceLayer()")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long duration = System.currentTimeMillis() - start;
        System.out.println("[AOP] " + joinPoint.getSignature().toShortString()
                + " 执行耗时: " + duration + "ms");
        return result;
    }
}
