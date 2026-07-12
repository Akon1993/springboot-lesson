package com.example.demo2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * demo2-web：demo1 + Web Starter → REST API 服务
 *
 * <p>和 demo1 的启动类一模一样，但 pom.xml 多了一个 spring-boot-starter-web。
 * 就这一个依赖，控制台程序变成了 Web 服务。</p>
 *
 * <p><b>讲解要点：</b></p>
 * <ul>
 *   <li>① 启动类代码和 demo1 完全一样——变的只是 pom.xml 里多了一行依赖</li>
 *   <li>② 启动日志里会出现：Tomcat started on port 8080</li>
 *   <li>③ 这就是 Starter 的威力——加依赖就加能力，不用改代码</li>
 * </ul>
 */
@SpringBootApplication
public class Demo2Application {

    public static void main(String[] args) {
        SpringApplication.run(Demo2Application.class, args);
    }
}
