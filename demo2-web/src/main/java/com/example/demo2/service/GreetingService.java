package com.example.demo2.service;

/**
 * 问候服务接口 —— 和 demo1 完全一样
 *
 * <p>在 demo2 中，这个 Service 被 Controller 调用，而不是 CommandLineRunner。
 * 同样的代码，换个调用方，从控制台输出变成了 HTTP 响应。</p>
 */
public interface GreetingService {
    String greet(String name);
}
