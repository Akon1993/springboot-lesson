package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring Boot 启动类
 *
 * <p><b>📖 讲解要点：</b></p>
 * <ul>
 *   <li>① {@code @SpringBootApplication} 一个注解干了三件事：
 *     <ul>
 *       <li>{@code @Configuration} —— 允许定义 Bean</li>
 *       <li>{@code @EnableAutoConfiguration} —— <b>Spring Boot 的核心！自动配置</b></li>
 *       <li>{@code @ComponentScan} —— 扫描当前包及子包的所有组件</li>
 *     </ul>
 *   </li>
 *   <li>② {@code SpringApplication.run()} 启动内嵌 Tomcat 并加载所有 Bean</li>
 *   <li>③ 对比传统 Spring MVC：不需要 web.xml、不需要安装外部 Tomcat、
 *       不需要打成 WAR 包部署——一个 main 方法直接跑</li>
 *   <li>④ 这就是"约定大于配置"的入口体现</li>
 * </ul>
 *
 * <p><b>🆚 AI 协作提示：</b>告诉 AI "使用 Spring Boot 3.x" 它会自动生成这个启动类。
 * 如果 AI 生成的是 Spring Boot 2.x 版本，pom.xml 里的 parent 版本和
 * 一些 API 会不同（比如 {@code javax.*} vs {@code jakarta.*}）。</p>
 *
 * @see SpringBootApplication
 */
@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
        System.out.println("""

                ============================================
                🚀 员工管理系统 Demo 启动成功！
                📖 API 文档: http://localhost:8080/api/
                🗄️  H2 控制台: http://localhost:8080/h2-console
                ============================================
                """);
    }
}
