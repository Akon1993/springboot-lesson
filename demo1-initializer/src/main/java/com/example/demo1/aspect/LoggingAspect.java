package com.example.demo1.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * 日志切面 —— 演示 AOP（面向切面编程）
 *
 * <p><b>讲解要点：</b></p>
 * <ul>
 *   <li>① @Aspect + @Component 两个注解，AOP 就生效了</li>
 *   <li>② "不改原代码，给所有 service 方法自动加上计时日志"</li>
 *   <li>③ 对比传统 Spring：需要在 XML 里配 &lt;aop:config&gt;、&lt;aop:aspect&gt;、
 *       &lt;aop:pointcut&gt;、&lt;aop:around&gt;... 现在两个注解搞定</li>
 *   <li>④ AOP 的典型应用场景：日志、事务、权限校验、性能监控</li>
 * </ul>
 */
@Aspect
@Component
public class LoggingAspect {

    /**
     * 切点：com.example.demo1.service 包及其子包下的所有 public 方法
     */
    @Pointcut("execution(* com.example.demo1.service..*.*(..))")
    public void serviceLayer() {
    }

    /**
     * 环绕通知：在方法执行前后各插入一段逻辑
     */
    @Around("serviceLayer()")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();

        // 执行真正的方法
        Object result = joinPoint.proceed();

        long duration = System.currentTimeMillis() - start;
        String methodName = joinPoint.getSignature().toShortString();

        System.out.println("[AOP] " + methodName + " 执行耗时: " + duration + "ms");
        return result;
    }
}
