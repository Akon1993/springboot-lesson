package com.example.demo2.controller;

import com.example.demo2.common.Result;
import com.example.demo2.service.GreetingService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 问候接口 Controller —— demo2 的核心新增代码
 *
 * <p><b>讲解要点（承上启下）：</b></p>
 * <ul>
 *   <li>① @RestController = @Controller + @ResponseBody——你同事讲过 @Controller，
 *       这个自动把返回值转 JSON</li>
 *   <li>② @GetMapping("/api/greeting")——你同事讲过 @RequestMapping，
 *       这个是简化版，限定 GET 请求</li>
 *   <li>③ GreetingService 通过构造器注入——和 demo1 一模一样，IoC 照常工作</li>
 *   <li>④ 返回 Result&lt;String&gt; 而非裸字符串——从第一个接口就建立统一响应规范</li>
 *   <li>⑤ <b>你不需要配置任何东西！</b> DispatcherServlet、视图解析器、消息转换器
 *       ——Spring Boot 全自动配好了</li>
 * </ul>
 */
@RestController
@RequestMapping("/api")
public class GreetingController {

    private final GreetingService greetingService;

    public GreetingController(GreetingService greetingService) {
        this.greetingService = greetingService;
    }

    /**
     * GET /api/greeting?name=产品团队
     * 返回统一 JSON 格式：{"code":200,"message":"操作成功","data":"你好，产品团队！"}
     */
    @GetMapping("/greeting")
    public Result<String> greet(@RequestParam(defaultValue = "世界") String name) {
        String message = greetingService.greet(name);
        return Result.success(message);
    }
}
