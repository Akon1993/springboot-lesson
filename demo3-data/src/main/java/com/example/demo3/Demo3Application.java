package com.example.demo3;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * demo3-data：demo2 + MyBatis Plus + H2 → 完整 CRUD 后端
 *
 * <p>和 demo2 的启动类一模一样。但 pom.xml 多了 MyBatis Plus 和 H2 两个依赖。
 * 加两个依赖，工程就拥有了完整的数据库操作能力。</p>
 *
 * <p><b>讲解要点：</b></p>
 * <ul>
 *   <li>① 启动类代码从 demo1 → demo2 → demo3 完全没有变化</li>
 *   <li>② 每多一个 Starter，能力就多一层——代码不用改，配好就能用</li>
 *   <li>③ 启动日志里能看到：SQL 初始化脚本执行、MyBatis Plus 分页插件加载</li>
 * </ul>
 */
@SpringBootApplication
public class Demo3Application {

    public static void main(String[] args) {
        SpringApplication.run(Demo3Application.class, args);
    }
}
