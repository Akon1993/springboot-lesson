package com.example.demo1;

import com.example.demo1.service.GreetingService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * demo1-initializer：最纯粹的 Spring Boot
 *
 * <p>这个工程只有核心 Starter，没有 Web，没有数据库。
 * 它证明了一件事：你什么都没配，Spring 的核心能力（IoC + AOP）全在运行。</p>
 *
 * <p><b>讲解要点：</b></p>
 * <ul>
 *   <li>① @SpringBootApplication 一个注解启动整个 Spring 容器</li>
 *   <li>② GreetingService 通过构造器注入——你同事讲过的 IoC，不需要任何 XML</li>
 *   <li>③ CommandLineRunner：应用启动后自动执行，演示效果直接看控制台</li>
 *   <li>④ "这个工程没有 Web 能力——加一个 starter 会怎样？" → 过渡到 demo2</li>
 * </ul>
 */
@SpringBootApplication
public class Demo1Application implements CommandLineRunner {

    private final GreetingService greetingService;

    /**
     * 构造器注入 —— Spring 自动将 GreetingService 的实现类注入进来。
     * 注意：你没有 new ChineseGreetingService()，Spring 帮你做了。
     */
    public Demo1Application(GreetingService greetingService) {
        this.greetingService = greetingService;
    }

    public static void main(String[] args) {
        SpringApplication.run(Demo1Application.class, args);
    }

    @Override
    public void run(String... args) {
        System.out.println("=".repeat(50));
        System.out.println(greetingService.greet("产品团队"));
        System.out.println(greetingService.greet("Spring Boot"));
        System.out.println("=".repeat(50));
    }
}
