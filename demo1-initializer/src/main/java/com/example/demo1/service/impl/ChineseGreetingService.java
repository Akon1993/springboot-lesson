package com.example.demo1.service.impl;

import com.example.demo1.service.GreetingService;
import org.springframework.stereotype.Service;

/**
 * 中文问候服务 —— GreetingService 的实现类
 *
 * <p><b>讲解要点：</b></p>
 * <ul>
 *   <li>① @Service 标记这是一个 Spring Bean → 你同事讲过的注解，照用</li>
 *   <li>② Spring 自动扫描到它，自动创建实例，自动注入到需要它的地方</li>
 *   <li>③ 不需要在 XML 里写 &lt;bean&gt; 标签——@Service 一个注解搞定</li>
 *   <li>④ 如果想加英文版？新建一个 EnglishGreetingService 加 @Service，
 *       然后用 @Qualifier 指定用哪个即可</li>
 * </ul>
 */
@Service
public class ChineseGreetingService implements GreetingService {

    @Override
    public String greet(String name) {
        return "你好，" + name + "！欢迎来到 Spring Boot 的世界。";
    }
}
