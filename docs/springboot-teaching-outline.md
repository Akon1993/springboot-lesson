# Spring Boot + Spring Cloud 产品团队培训大纲

> **前置知识（上节课已讲）：** Spring IoC/DI、Spring MVC 请求处理流程、Maven 基础
> **本次定位：** 承上启下——Spring Boot 让单个服务变简单，Spring Cloud 让一堆服务能协作
> **总时长：** 约 2 小时 35 分钟（Boot 实操 ~95min + Cloud 理论 ~48min + Q&A ~10min）

---

# 上半场：Spring Boot 实操（~95min）

## 第1章：从 Spring MVC 到 Spring Boot —— 演进之路 + demo1（~35min）

> 用 demo1 一条线串到底：打开工程 → 看代码 → 跑起来 → 对比传统方式 → 拆解注解 → 总结概念。

### 1.1 快速回顾（1min）
- Spring = IoC 容器；Spring MVC = Web 框架；认识了 `@Controller`、`@Service`、`@Autowired`

### 1.2 🔌 打开 demo1-initializer

[打开 IDE，展示目录和代码]

**依赖：** 仅 `spring-boot-starter` + `spring-boot-starter-aop`，不含 Web，不含数据库。

**4 个文件：**
```
Demo1Application.java        ← @SpringBootApplication + main + CommandLineRunner
GreetingService.java         ← 接口
ChineseGreetingService.java  ← @Service 实现
LoggingAspect.java           ← @Aspect 切面
```

关键代码：ChineseGreetingService 加了 `@Service`，通过构造器注入到 Demo1Application。LoggingAspect 加了 `@Aspect`，拦截 service 方法打印耗时。没有任何 XML。

[终端运行]
```bash
mvn spring-boot:run   # 0.4 秒启动
```
```
你好，产品团队！欢迎来到 Spring Boot 的世界。
[AOP] greet 执行耗时: 2ms
```

IoC 在工作，AOP 也在工作，0.4 秒，零行 XML。

### 1.3 对比——传统方式要写什么

同样的 IoC + AOP，传统 Spring 需要：

```xml
<context:component-scan base-package="com.example.service"/>
<context:annotation-config/>
```

每个项目都要写，包名变了得跟着改。而 demo1 零行 XML。

> web.xml、spring-mvc.xml 那 80+ 行是 Web 层的——demo1 没有 Web，不需要。后面加了 Web Starter 再对比。

### 1.4 拆解 @SpringBootApplication

回到刚才的 Demo1Application.java。`@SpringBootApplication` 包含了三个注解：

| 注解 | 作用 | 上节课 |
|------|------|--------|
| `@Configuration` | 标记配置类，可定义 Bean | 涉及 |
| `@ComponentScan` | 扫描当前包及子包下组件 | 涉及 |
| `@EnableAutoConfiguration` | **自动配置——Spring Boot 的灵魂** | 🆕 |

前两个上节课学过。第三个是 Spring Boot 独有的。

### 1.5 自动配置与两条约定

> 传统 Spring = 毛坯房；Spring Boot = 精装修公寓。拎包入住，不满意可以改。

**约定一：默认扫描位置。** `@ComponentScan` 从启动类所在包向下扫描所有子包。Demo1Application 在 `com.example.demo1`，`service/`、`aspect/` 下的类自动被发现。不需要 `<context:component-scan base-package="..."/>`。类放包外则扫不到，需显式配。

**约定二：注解自动生效。** 传统 Spring 需要 `<context:annotation-config/>` 开启注解处理。Spring Boot 自动开启——`@Autowired` 直接生效。

条件装配的规则：你需要 + 没自己配 → 我来；你不需要 / 你配了 → 我靠边。

### 1.6 "约定大于配置"总结

**"配置"** = 你告诉框架每一个细节。**"约定"** = 框架预设默认行为，按规矩来就自动生效。

> 不写端口配置→8080 已监听。想改 `server.port=9090`，没写就默认。
> 把每个人都要写的东西变成默认就有的。想改可以改，不改就能用。

### 1.7 Spring Boot 是什么

| 痛点 | 解法 |
|------|------|
| 手写 XML 配置 | 自动配置 — 约定好了，不用写 |
| 6~8 个依赖 | Starter 套餐 — 一个依赖一组功能 |
| 装 Tomcat 打 WAR | 内嵌 Tomcat — `java -jar` 直接跑 |
| 搭环境半天 | Spring Initializr — 30 秒生成项目 |

> Spring Boot = Spring + Spring MVC + 自动配置 + 内嵌服务器。它是 Spring 的"自动挡"。

### 1.8 演进全景图

```
Java Servlet → Spring Framework → Spring MVC → Spring Boot → Spring Cloud
                                    ↑ 上节课         ↑ 本节课上半场      ↑ 本节课下半场
```

> "demo1 没有 Web 能力。加一个依赖会怎样？" → 进入第2章

---

## 第2章：Web 层实战 + demo2-web（~30min）⭐ 承上启下核心章

> 只讲 Web 层。**这里是与上节课 Spring MVC 内容的衔接点。** 控制台程序 → Web 服务，全靠一个 starter。

### 2.1 demo2 比 demo1 多了什么？

demo2 的 pom.xml 只比 demo1 多了一行：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

**这一行 = Tomcat + Spring MVC + JSON 序列化。** 不用装、不用配。

demo2 新增代码只有 controller 和 common 包；service 和 aspect 跟 demo1 一模一样。

### 2.2 上节课回顾——传统 Spring MVC 要写什么

同样的 Web 能力，传统方式需要 3 个 XML 文件，80+ 行配置：

```xml
<!-- ① web.xml：配置 DispatcherServlet -->
<web-app>
  <servlet>
    <servlet-name>dispatcher</servlet-name>
    <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
    <init-param>
      <param-name>contextConfigLocation</param-name>
      <param-value>/WEB-INF/spring-mvc.xml</param-value>
    </init-param>
  </servlet>
  <servlet-mapping>
    <servlet-name>dispatcher</servlet-name>
    <url-pattern>/</url-pattern>
  </servlet-mapping>
</web-app>

<!-- ② applicationContext.xml：数据源、事务、Service 扫描 -->
<beans>
  <context:component-scan base-package="com.example"/>
  <bean id="dataSource" class="org.apache.commons.dbcp.BasicDataSource">
    <property name="driverClassName" value="com.mysql.cj.jdbc.Driver"/>
    <property name="url" value="jdbc:mysql://localhost:3306/demo"/>
  </bean>
  <bean id="txManager" class="...DataSourceTransactionManager">
    <property name="dataSource" ref="dataSource"/>
  </bean>
</beans>

<!-- ③ spring-mvc.xml：视图解析器、注解驱动 -->
<beans>
  <mvc:annotation-driven/>
  <context:component-scan base-package="com.example.controller"/>
  <bean class="...InternalResourceViewResolver">
    <property name="prefix" value="/WEB-INF/views/"/>
    <property name="suffix" value=".jsp"/>
  </bean>
</beans>
```

然后还要：装 Tomcat → 打 WAR → 丢 webapps → 重启。

### 2.3 ⭐ 承上启下——上节课讲的 vs Spring Boot 做的

| 上节课讲过（传统 Spring MVC） | Spring Boot | 还需要吗？ |
|------|------|------|
| `web.xml` 配置 `DispatcherServlet` | 引入 starter-web → 自动配好 | ❌ |
| `spring-mvc.xml` 配置视图解析器、注解驱动 | `WebMvcAutoConfiguration` 自动配好 | ❌ |
| `applicationContext.xml` 配置组件扫描 | `@SpringBootApplication` 自带扫描 | ❌ |
| 写 `@Controller` + `@RequestMapping` | 写 `@RestController` + `@GetMapping` | ✅ 写法一样 |
| 装 Tomcat → 打 WAR → 部署 | 内嵌 Tomcat，`java -jar` 直接跑 | ❌ |

> **金句：** "上节课教的 @Controller 怎么用——照用。web.xml 怎么配——不用了。"

### 2.4 第一个 REST API

```java
@RestController  // = @Controller + @ResponseBody，返回 JSON
public class GreetingController {

    private final GreetingService greetingService;  // 构造器注入，复用 demo1 的 Service

    @GetMapping("/api/greeting")
    public Result<String> greet(@RequestParam(defaultValue = "世界") String name) {
        return Result.success(greetingService.greet(name));
    }
}
```

### 2.5 统一响应 Result\<T\>

写了 Controller 自然引入——"但返回格式不统一怎么办？"

```json
{"code": 200, "message": "操作成功", "data": {...}}
```

所有接口统一这个格式。前端只需判断 `code === 200`。

### 2.6 统一异常处理

"如果用户查了一个不存在的资源怎么办？"

```java
@RestControllerAdvice  // 一个注解，全局处理异常
public class GlobalExceptionHandler {
    @ExceptionHandler(BusinessException.class)
    public Result<?> handle(BusinessException e) {
        return Result.error(e.getCode(), e.getMessage());
    }
}
```

Controller 里不用写任何 try-catch。一个类全局搞定。

### 2.7 运行验证

```bash
cd demo2-web && mvn spring-boot:run
# 日志：Tomcat started on port 8080  ← Tomcat 自动启动了！

curl 'http://localhost:8080/api/greeting?name=产品团队'
# → {"code":200,"message":"操作成功","data":"你好，产品团队！"}
```

---

## 第3章：数据层实战 + demo3-data（~30min）

> 只讲数据层。再加两个依赖，Web 服务变完整后端。

### 3.1 demo3 比 demo2 多了什么？

pom.xml 多了两行：`mybatis-plus-spring-boot3-starter` + `h2`。

### 3.2 MyBatis Plus —— 数据层的"约定大于配置"

**Entity —— 一个类对应一张表：**

```java
@Data
@TableName("employee")
public class Employee {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private BigDecimal salary;       // 金额用 BigDecimal
    private LocalDateTime createTime; // 日期用 LocalDateTime
}
```

**Mapper —— 空接口 = 全套 CRUD：**

```java
@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {
    // 空接口！但自动拥有 insert、deleteById、updateById、
    // selectById、selectList、selectPage、selectCount……
}
```

> 传统 MyBatis：每表 10 条 SQL。MyBatis Plus：继承一个接口，完事。

### 3.3 条件查询与分页

```java
LambdaQueryWrapper<Employee> wrapper = new LambdaQueryWrapper<>();
wrapper.like(name != null, Employee::getName, name)
       .eq(departmentId != null, Employee::getDepartmentId, departmentId)
       .orderByDesc(Employee::getId);

Page<Employee> page = new Page<>(1, 10);
employeeMapper.selectPage(page, wrapper);
// 自动返回 records、total、current、pages
```

### 3.4 配置管理 —— 21 行 XML → 5 行 YAML

```yaml
# Spring Boot：5 行
spring:
  datasource:
    url: jdbc:h2:mem:demo3;MODE=MYSQL     # 零安装，换 MySQL 改这一行
    username: sa
  sql.init.mode: always                    # 启动自动执行建表脚本
```

对比传统 Spring 配置同一个数据源需要 21 行 XML `<bean>` + `<property>`。

**换 MySQL？** 改 3 行配置，代码不动。**多环境？** `application-dev.yml` / `application-prod.yml`，一行切换。

### 3.5 完整代码结构

```
demo3-data/
├── pom.xml                          ← 比 demo2 多 mybatis-plus + h2
└── src/main/java/com/example/demo3/
    ├── entity/Employee.java         ← @TableName @TableId
    ├── mapper/EmployeeMapper.java   ← extends BaseMapper（空接口）
    ├── service/EmployeeService.java + impl/  ← @Transactional + LambdaQueryWrapper
    ├── controller/EmployeeController.java    ← RESTful CRUD + 分页
    ├── common/                      ← Result、BusinessException、GlobalExceptionHandler
    ├── config/MyBatisPlusConfig.java         ← 分页插件 + 自动填充
    └── resources/
        ├── application.yml
        └── db/schema.sql + data.sql
```

### 3.6 运行验证

```bash
cd demo3-data && mvn spring-boot:run     # < 1 秒，SQL 自动执行
```

```bash
curl localhost:8080/api/employees                         # 查全部
curl 'localhost:8080/api/employees?current=1&size=5'      # 分页
curl 'localhost:8080/api/employees?name=张'               # 模糊搜索
curl -X POST localhost:8080/api/employees \
  -H 'Content-Type: application/json' \
  -d '{"name":"新员工","age":26,"departmentId":1,"salary":15000}'  # 新增
curl localhost:8080/api/employees/999                     # 异常处理
```

打开 `http://localhost:8080/h2-console` → 看到自动建表和 9 条测试数据。

### 3.7 三阶段小结

| | demo1 | demo2 | demo3 |
|------|------|------|------|
| 新增依赖 | 核心 starter | + starter-web | + mybatis-plus + h2 |
| 新增文件 | 4 个 | +3 个 | +7 个 |
| 能力 | IoC + AOP | + REST API + 统一响应 | + 完整 CRUD + 配置管理 |

> **每个 Starter 加一种能力。你不需要配——约定已经做好了。**

---

# 下半场：Spring Cloud 理论（~48min，共4章）

## 第4章：从单体到微服务——为什么需要 Spring Cloud（~12min）

### 4.1 一个电商系统的一生

**创业第一天（单体）：** 订单、用户、商品全在一个工程。`java -jar shop.jar`，真爽。

**公司做大了：**
- 200 人改一个工程 → 合并冲突地狱
- 订单扩容 → 整个系统部署 N 份，浪费资源
- 改一行代码 → 全站重新部署（"换个灯泡拉整栋楼电闸"）
- 一个模块挂了 → 全站崩溃（"一个房间短路，整栋楼停电"）

### 4.2 微服务的答案——拆

> "把一个超级工程拆成几十个小工程，每个独立开发、独立部署、独立扩容。"

**类比：** 单体 = 所有人一个大开间；微服务 = 按部门分楼层，各管各的。

### 4.3 拆完的新问题

- 订单服务要调用户服务——**怎么找到它在哪？**
- 前端要调 30 个服务——**难道记 30 个地址？**
- 用户服务挂了——**怎么不把订单服务也拖死？**
- 一个请求跨 5 个服务——**慢了怎么排查？**

> Spring Boot 让一个服务跑起来。**Spring Cloud 让一堆服务能协作。**

---

## 第5章：Spring Cloud 六大组件（~28min）

> 贯穿比喻：外卖平台——用户/订单/商家/配送/支付各是独立微服务。每个组件解决平台中的一个真实问题。

### 5.1 注册中心 Nacos ——「配送调度中心」📒

**外卖场景：** 用户下单后，订单服务需要通知配送服务找骑手。配送服务跑在 3 台机器上，订单服务怎么知道该找哪台？写死 IP？扩容就炸。

**解法：** 配送服务启动时去 Nacos 登记，订单服务去 Nacos 查询可用实例。机器挂了自动剔除，新增自动加入。

> 骑手上线打卡——平台知道谁在线。你下单，平台自动分配空闲骑手。Nacos 就是配送调度中心。

### 5.2 API 网关 Gateway ——「外卖 App 统一后台」🏢

**外卖场景：** App 首页要调商家服务、购物车调订单服务、结算调支付服务。让 App 记住 3 个地址？而且每个服务都要做登录验证——重复、不一致。

**解法：** App 只跟 Gateway 打交道。Gateway 根据路径转发到对应服务。鉴权统一在 Gateway 做——没登录的直接拦在门口。

> 你打开外卖 App，不会直接连商家数据库和支付系统——你只跟 App 后台交互。Gateway 就是这个统一后台。

### 5.3 配置中心 Nacos Config ——「商家后台统一改价」☁️

**外卖场景：** 配送费规则从"3 公里内 5 元"改成"3 公里内 4 元，每超 1 公里加 1 元"。订单服务和配送服务都在用这个规则——改两个配置、重启两个服务？

**解法：** 配送费规则放在配置中心。运营改一次，所有服务自动推送，**不重启**生效。

> 商家在后台改菜价——App 立刻显示新价格。配置中心就是这个后台管理系统。

### 5.4 服务调用 Feign ——「一键支付」📞

**外卖场景：** 用户点"提交订单"，订单服务要依次调用用户服务（验证地址）、商家服务（确认库存）、支付服务（扣款）。手写三次 HTTP 调用——每次十几行代码。

**解法：** Feign 声明接口 + 注解 = 自动发 HTTP。像调本地方法一样调远程服务。URL、序列化、超时全部自动处理。

> 你在 App 点"微信支付"，App 帮你调起微信——不用自己打开微信输金额。Feign 就是那个一键调用。

### 5.5 熔断降级 Sentinel ——「支付挂了，货到付款兜底」⚡

**外卖场景：** 支付服务挂了。订单服务还在不停调它——每次超时等 3 秒，1000 个请求全堵在等待。订单服务也被拖死。商家服务调订单——也挂了。**级联故障，全站崩溃。** 商家正常营业，骑手也在线，但 App 白屏。

**解法：** Sentinel 监控调用成功率。支付服务连续失败 → **熔断**（切断调用）+ **降级**（切换货到付款）。支付恢复了 → 自动切回在线支付。

> 一家店停电关门——平台暂时下架，你不会点进去发现点不了。Sentinel 就是这个自动下架机制。

### 5.6 链路追踪 Sleuth / Zipkin ——「外卖物流追踪」📦

**外卖场景：** 用户投诉"下单等了 10 秒"。一个请求跨了 Gateway → 订单 → 用户 → 商家 → 支付。5 个服务，到底谁慢了？每个服务有自己的日志，但没有关联——拼不出完整链路。

**解法：** 每个请求一个全局 TraceID（快递单号），在 5 个服务间传递。Zipkin 界面拼接完整调用树，每个节点耗时一目了然。

> 快递慢了——看物流轨迹："到达分拨中心 10:00，离开 18:00"。卡了 8 小时，破案。Zipkin 就是给每个请求发的快递单号。

### 5.7 速查表

| 外卖场景 | 对应组件 | 一句话 |
|---------|---------|------|
| 骑手上线打卡，平台自动分配 | 📒 Nacos | 配送调度中心 |
| App 统一后台，不管后面有几个服务 | 🏢 Gateway | 外卖 App 后台 |
| 商家改配送费，App 立刻生效 | ☁️ Nacos Config | 商家后台改价 |
| 一键支付，不用自己打开微信 | 📞 Feign | 一键支付 |
| 支付挂了，自动切换货到付款 | ⚡ Sentinel | 故障自动兜底 |
| 下单慢了，看哪个环节卡了 | 📦 Sleuth/Zipkin | 外卖物流追踪 |

---

## 第6章：产品视角——微服务不是免费的午餐（~8min）

### 6.1 拆服务的代价

| 好处 | 代价 |
|------|------|
| 独立部署 | 运维复杂度 × N |
| 独立扩容 | 分布式事务，复杂 10 倍 |
| 团队自治 | HTTP 调用有延迟 |

### 6.2 拆之前，先问三个问题

1. ❓ **有独立的业务边界吗？** 不是"功能多"就该拆。
2. ❓ **需要独立扩容吗？** 双十一只有订单要加机器——才值得拆。
3. ❓ **有独立团队维护吗？** 一个团队管 3 个微服务 = 分布式单体，更糟。

> **每拆一个服务，运维成本就多一份。**

---

## 第7章：课程总结 + Q&A（~10min）

### 今天讲了什么

| 模块 | 核心 |
|------|------|
| Spring Boot | 自动挡。约定大于配置。 |
| 三阶段实战 | demo1(IoC/AOP) → demo2(Web+承上启下) → demo3(数据+CRUD) |
| Spring Cloud | 六大组件——六个生活比喻 |

### 你最该带走的三件事
1. 🚗 **Spring Boot 是自动挡**——学过的 Spring 全有效，只是不需要 XML
2. 🔌 **每个 Starter 加一种能力**——加依赖就能用，约定已经配好了
3. 🏗️ **Cloud 六道保险 + 拆前三问**——微服务不是免费的

---

## 📝 课后资料

- Demo 源码：`demo1-initializer/` `demo2-web/` `demo3-data/`
- 启动命令：`cd demoX && mvn spring-boot:run`
- 大纲 & 逐字稿：`docs/`
- Spring Initializr：https://start.spring.io
