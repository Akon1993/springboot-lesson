package com.example.demo2.service.impl;

import com.example.demo2.service.GreetingService;
import org.springframework.stereotype.Service;

/**
 * 中文问候服务 —— 和 demo1 完全一样
 */
@Service
public class ChineseGreetingService implements GreetingService {

    @Override
    public String greet(String name) {
        return "你好，" + name + "！欢迎来到 Spring Boot 的世界。";
    }
}
