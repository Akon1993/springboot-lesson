# Spring Boot + Spring Cloud 产品团队培训大纲

> **前置知识（上节课已讲）：** Spring IoC/DI、Spring MVC 请求处理流程、Maven 基础
> **本次定位：** 承上启下——Spring Boot 让单个服务变简单，Spring Cloud 让一堆服务能协作
> **总时长：** 约 2 小时 35 分钟（Boot 实操 ~95min + Cloud 理论 ~48min + Q&A ~10min）

---

# 上半场：Spring Boot 实操（~95min）

## 第1章：从 Spring MVC 到 Spring Boot —— 演进之路（~15min）

> 先看结果，再讲原理。打开 demo1，让它跑起来，然后对比传统方式——"你省掉了什么"。

### 1.1 快速回顾（1min）
- Spring = IoC 容器；Spring MVC = Web 框架；你认识了 `@Controller`、`@Service`、`@Autowired`

### 1.2 🔌 先看结果——打开 demo1-initializer

[打开 IDE，展示 demo1 目录结构和关键代码]

**依赖：** 只有核心 starter，不含 Web，不含数据库。

**4 个文件：**
```
Demo1Application.java        ← @SpringBootApplication + main + CommandLineRunner
GreetingService.java         ← 接口
ChineseGreetingService.java  ← @Service 实现
LoggingAspect.java           ← @Aspect 切面
```

**代码要点快速过：**
- `ChineseGreetingService` 加了 `@Service`，通过构造器注入到 `Demo1Application`
- `LoggingAspect` 加了 `@Aspect`，拦截 service 方法打印耗时
- 没有任何 XML，没有 `web.xml`，没有 `applicationContext.xml`

[终端运行]
```bash
mvn spring-boot:run   # 0.4 秒启动
```
控制台输出：
```
你好，产品团队！欢迎来到 Spring Boot 的世界。
[AOP] greet 执行耗时: 2ms
```

IoC 在工作。AOP 也在工作。0.4 秒。

### 1.3 对比——以前你要写什么？

**同样的 IoC + AOP 效果，传统 Spring 需要在 XML 里写：**

```xml
<!-- applicationContext.xml：必须显式告诉 Spring 去哪扫、开启注解 -->
<context:component-scan base-package="com.example.service"/>
<context:annotation-config/>
```

就这两行，但每个项目都要写。而且包名一改、路径一变，就得跟着改。

**而 demo1：0 行 XML。** `@SpringBootApplication` 一个注解，默认从启动类所在包开始扫描，`@Autowired` 自动生效。0.4 秒跑起来。

> 至于 `web.xml`、`spring-mvc.xml` 那些——那是 Web 层的东西。demo1 根本没有 Web 模块，当然不需要。等到了 demo2，我们会看到 Spring Boot 怎么干掉那 80+ 行 XML。

### 1.4 Spring Boot 的答案："约定大于配置"

**"配置"** = 你告诉框架每一个细节。**"约定"** = 框架说"你按规矩来，细节不用告诉我"。

> 你引入 Spring Boot，不写端口配置。启动——8080 已经在监听了。你想改？`server.port=9090` 一行。你没写？默认 8080。
>
> **把每个人都要写的东西变成默认就有的。想改可以改，不改就能用。** 这就是约定大于配置。

### 1.5 Spring Boot 是什么

| 痛点 | 解法 |
|------|------|
| 手写 XML 配置 | **自动配置** — 约定好了，不用你写 |
| 6~8 个依赖 | **Starter 套餐** — 一个依赖搞定一组功能 |
| 装 Tomcat 打 WAR | **内嵌 Tomcat** — `java -jar` 直接跑 |
| 搭环境半天 | **Spring Initializr** — 30 秒生成项目 |

> **Spring Boot = Spring + Spring MVC + 自动配置 + 内嵌服务器。** 它是 Spring 的"自动挡"。

### 1.6 演进全景图

```
Java Servlet → Spring Framework → Spring MVC → Spring Boot → Spring Cloud
                                    ↑ 上节课         ↑ 本节课上半场      ↑ 本节课下半场
```

---

## 第2章：核心机制 + demo1 —— IoC / AOP 层（~20min）

> 只讲容器层。用 demo1 验证：不配任何 XML，IoC 和 AOP 照常工作。

### 2.1 一个注解背后的魔法

```java
@SpringBootApplication  // ← 三合一
public class DemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }
}
```

| 拆解 | 作用 | 学过？ |
|------|------|--------|
| `@Configuration` | 可以定义 Bean | ✅ 学过的 |
| `@ComponentScan` | 扫描当前包及子包下所有组件 | ✅ 学过的 |
| `@EnableAutoConfiguration` | **自动配置——Spring Boot 的灵魂** | 🆕 |

### 2.2 自动配置——「精装修公寓」

> 传统 Spring = 毛坯房（墙自己刷、线自己铺）；Spring Boot = 精装修（拎包入住，不满意可以改）。

你不用告诉 Spring Boot "帮我开启 IoC 容器"、"帮我扫描 @Service 注解"——`@SpringBootApplication` 一个注解，这些全自动做了。

**约定一：默认扫描位置。** `@ComponentScan` 默认从启动类所在包开始，向下扫描所有子包。demo1 的启动类在 `com.example.demo1`，所以 `service/`、`aspect/` 下的 `@Service`、`@Aspect` 自动被发现。你把类放到 `com.example.other` 下面？那就扫不到了——除非你显式配置。

**约定二：注解自动生效。** 传统 Spring 需要在 XML 里显式开启 `<context:annotation-config/>`，`@Autowired`、`@Service` 这些注解才能工作。Spring Boot 自动帮你开了——你写 `@Autowired`，它就直接生效，不需要任何 XML 开关。

条件装配的规则：你需要 + 你没自己配 → 我来。你不需要 / 你配了 → 我靠边站。

### 2.3 🔌 打开 demo1-initializer —— 验证

**依赖：** 仅 `spring-boot-starter` + `spring-boot-starter-aop`（核心 Starter，不含 Web，不含数据库）

**代码结构：**
```
demo1-initializer/
├── pom.xml                          ← 只有核心 starter，没有 web，没有数据库
└── src/main/java/com/example/demo1/
    ├── Demo1Application.java        ← @SpringBootApplication + CommandLineRunner
    ├── service/
    │   ├── GreetingService.java     ← 接口
    │   └── impl/
    │       └── ChineseGreetingService.java ← @Service 实现
    └── aspect/
        └── LoggingAspect.java       ← @Aspect + @Around
```

**演示要点：**

| 知识点 | 怎么验证 | 可见输出 |
|--------|---------|---------|
| IoC 容器 | `GreetingService` 接口 + `@Service` 实现 + 构造器注入 | 控制台打印问候语 |
| AOP 切面 | `@Aspect` 拦截 service 方法，打印执行耗时 | `[AOP] greet 执行耗时: 2ms` |

**讲解节奏：**
1. "上节课讲过 IoC 和 AOP。在 Spring Boot 里照常工作——但注意：你写 `web.xml` 了吗？没有。你配 `applicationContext.xml` 了吗？也没有。`@SpringBootApplication` 一个注解全搞定了。"
2. `mvn spring-boot:run` → 0.4 秒启动 → 控制台看到注入结果 + AOP 日志
3. "这个工程不能对外提供 HTTP 服务。下一步：加一个依赖会怎样？"

---

## 第3章：Web 层实战 + demo2-web（~30min）⭐ 承上启下核心章

> 只讲 Web 层。**这里是与上节课 Spring MVC 内容的衔接点。** 控制台程序 → Web 服务，全靠一个 starter。

### 3.1 demo2 比 demo1 多了什么？

demo2 的 pom.xml 只比 demo1 多了一行：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

**这一行 = Tomcat + Spring MVC + JSON 序列化。** 不用装、不用配。

demo2 新增代码只有 controller 和 common 包；service 和 aspect 跟 demo1 一模一样。

### 3.2 上节课回顾——传统 Spring MVC 要写什么

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

### 3.3 ⭐ 承上启下——上节课讲的 vs Spring Boot 做的

| 上节课讲过（传统 Spring MVC） | Spring Boot | 还需要吗？ |
|------|------|------|
| `web.xml` 配置 `DispatcherServlet` | 引入 starter-web → 自动配好 | ❌ |
| `spring-mvc.xml` 配置视图解析器、注解驱动 | `WebMvcAutoConfiguration` 自动配好 | ❌ |
| `applicationContext.xml` 配置组件扫描 | `@SpringBootApplication` 自带扫描 | ❌ |
| 写 `@Controller` + `@RequestMapping` | 写 `@RestController` + `@GetMapping` | ✅ 写法一样 |
| 装 Tomcat → 打 WAR → 部署 | 内嵌 Tomcat，`java -jar` 直接跑 | ❌ |

> **金句：** "上节课教的 @Controller 怎么用——照用。web.xml 怎么配——不用了。"

### 3.4 第一个 REST API

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

### 3.5 统一响应 Result\<T\>

写了 Controller 自然引入——"但返回格式不统一怎么办？"

```json
{"code": 200, "message": "操作成功", "data": {...}}
```

所有接口统一这个格式。前端只需判断 `code === 200`。

### 3.6 统一异常处理

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

### 3.7 运行验证

```bash
cd demo2-web && mvn spring-boot:run
# 日志：Tomcat started on port 8080  ← Tomcat 自动启动了！

curl 'http://localhost:8080/api/greeting?name=产品团队'
# → {"code":200,"message":"操作成功","data":"你好，产品团队！"}
```

---

## 第4章：数据层实战 + demo3-data（~30min）

> 只讲数据层。再加两个依赖，Web 服务变完整后端。

### 4.1 demo3 比 demo2 多了什么？

pom.xml 多了两行：`mybatis-plus-spring-boot3-starter` + `h2`。

### 4.2 MyBatis Plus —— 数据层的"约定大于配置"

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

### 4.3 条件查询与分页

```java
LambdaQueryWrapper<Employee> wrapper = new LambdaQueryWrapper<>();
wrapper.like(name != null, Employee::getName, name)
       .eq(departmentId != null, Employee::getDepartmentId, departmentId)
       .orderByDesc(Employee::getId);

Page<Employee> page = new Page<>(1, 10);
employeeMapper.selectPage(page, wrapper);
// 自动返回 records、total、current、pages
```

### 4.4 配置管理 —— 21 行 XML → 5 行 YAML

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

### 4.5 完整代码结构

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

### 4.6 运行验证

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

### 4.7 三阶段小结

| | demo1 | demo2 | demo3 |
|------|------|------|------|
| 新增依赖 | 核心 starter | + starter-web | + mybatis-plus + h2 |
| 新增文件 | 4 个 | +3 个 | +7 个 |
| 能力 | IoC + AOP | + REST API + 统一响应 | + 完整 CRUD + 配置管理 |

> **每个 Starter 加一种能力。你不需要配——约定已经做好了。**

---

# 下半场：Spring Cloud 理论（~48min）

## 第5章：从单体到微服务——为什么需要 Spring Cloud（~12min）

### 5.1 一个电商系统的一生

**创业第一天（单体）：** 订单、用户、商品全在一个工程。`java -jar shop.jar`，真爽。

**公司做大了：**
- 200 人改一个工程 → 合并冲突地狱
- 订单扩容 → 整个系统部署 N 份，浪费资源
- 改一行代码 → 全站重新部署（"换个灯泡拉整栋楼电闸"）
- 一个模块挂了 → 全站崩溃（"一个房间短路，整栋楼停电"）

### 5.2 微服务的答案——拆

> "把一个超级工程拆成几十个小工程，每个独立开发、独立部署、独立扩容。"

**类比：** 单体 = 所有人一个大开间；微服务 = 按部门分楼层，各管各的。

### 5.3 拆完的新问题

- 订单服务要调用户服务——**怎么找到它在哪？**
- 前端要调 30 个服务——**难道记 30 个地址？**
- 用户服务挂了——**怎么不把订单服务也拖死？**
- 一个请求跨 5 个服务——**慢了怎么排查？**

> Spring Boot 让一个服务跑起来。**Spring Cloud 让一堆服务能协作。**

---

## 第6章：Spring Cloud 六大组件（~28min）

> 贯穿比喻：外卖平台——用户/订单/商家/配送/支付各是独立微服务。每个组件解决平台中的一个真实问题。

### 6.1 注册中心 Nacos ——「配送调度中心」📒

**外卖场景：** 用户下单后，订单服务需要通知配送服务找骑手。配送服务跑在 3 台机器上，订单服务怎么知道该找哪台？写死 IP？扩容就炸。

**解法：** 配送服务启动时去 Nacos 登记，订单服务去 Nacos 查询可用实例。机器挂了自动剔除，新增自动加入。

> 骑手上线打卡——平台知道谁在线。你下单，平台自动分配空闲骑手。Nacos 就是配送调度中心。

### 6.2 API 网关 Gateway ——「外卖 App 统一后台」🏢

**外卖场景：** App 首页要调商家服务、购物车调订单服务、结算调支付服务。让 App 记住 3 个地址？而且每个服务都要做登录验证——重复、不一致。

**解法：** App 只跟 Gateway 打交道。Gateway 根据路径转发到对应服务。鉴权统一在 Gateway 做——没登录的直接拦在门口。

> 你打开外卖 App，不会直接连商家数据库和支付系统——你只跟 App 后台交互。Gateway 就是这个统一后台。

### 6.3 配置中心 Nacos Config ——「商家后台统一改价」☁️

**外卖场景：** 配送费规则从"3 公里内 5 元"改成"3 公里内 4 元，每超 1 公里加 1 元"。订单服务和配送服务都在用这个规则——改两个配置、重启两个服务？

**解法：** 配送费规则放在配置中心。运营改一次，所有服务自动推送，**不重启**生效。

> 商家在后台改菜价——App 立刻显示新价格。配置中心就是这个后台管理系统。

### 6.4 服务调用 Feign ——「一键支付」📞

**外卖场景：** 用户点"提交订单"，订单服务要依次调用用户服务（验证地址）、商家服务（确认库存）、支付服务（扣款）。手写三次 HTTP 调用——每次十几行代码。

**解法：** Feign 声明接口 + 注解 = 自动发 HTTP。像调本地方法一样调远程服务。URL、序列化、超时全部自动处理。

> 你在 App 点"微信支付"，App 帮你调起微信——不用自己打开微信输金额。Feign 就是那个一键调用。

### 6.5 熔断降级 Sentinel ——「支付挂了，货到付款兜底」⚡

**外卖场景：** 支付服务挂了。订单服务还在不停调它——每次超时等 3 秒，1000 个请求全堵在等待。订单服务也被拖死。商家服务调订单——也挂了。**级联故障，全站崩溃。** 商家正常营业，骑手也在线，但 App 白屏。

**解法：** Sentinel 监控调用成功率。支付服务连续失败 → **熔断**（切断调用）+ **降级**（切换货到付款）。支付恢复了 → 自动切回在线支付。

> 一家店停电关门——平台暂时下架，你不会点进去发现点不了。Sentinel 就是这个自动下架机制。

### 6.6 链路追踪 Sleuth / Zipkin ——「外卖物流追踪」📦

**外卖场景：** 用户投诉"下单等了 10 秒"。一个请求跨了 Gateway → 订单 → 用户 → 商家 → 支付。5 个服务，到底谁慢了？每个服务有自己的日志，但没有关联——拼不出完整链路。

**解法：** 每个请求一个全局 TraceID（快递单号），在 5 个服务间传递。Zipkin 界面拼接完整调用树，每个节点耗时一目了然。

> 快递慢了——看物流轨迹："到达分拨中心 10:00，离开 18:00"。卡了 8 小时，破案。Zipkin 就是给每个请求发的快递单号。

### 6.7 速查表

| 外卖场景 | 对应组件 | 一句话 |
|---------|---------|------|
| 骑手上线打卡，平台自动分配 | 📒 Nacos | 配送调度中心 |
| App 统一后台，不管后面有几个服务 | 🏢 Gateway | 外卖 App 后台 |
| 商家改配送费，App 立刻生效 | ☁️ Nacos Config | 商家后台改价 |
| 一键支付，不用自己打开微信 | 📞 Feign | 一键支付 |
| 支付挂了，自动切换货到付款 | ⚡ Sentinel | 故障自动兜底 |
| 下单慢了，看哪个环节卡了 | 📦 Sleuth/Zipkin | 外卖物流追踪 |

---

## 第7章：产品视角——微服务不是免费的午餐（~8min）

### 7.1 拆服务的代价

| 好处 | 代价 |
|------|------|
| 独立部署 | 运维复杂度 × N |
| 独立扩容 | 分布式事务，复杂 10 倍 |
| 团队自治 | HTTP 调用有延迟 |

### 7.2 拆之前，先问三个问题

1. ❓ **有独立的业务边界吗？** 不是"功能多"就该拆。
2. ❓ **需要独立扩容吗？** 双十一只有订单要加机器——才值得拆。
3. ❓ **有独立团队维护吗？** 一个团队管 3 个微服务 = 分布式单体，更糟。

> **每拆一个服务，运维成本就多一份。**

---

## 第8章：课程总结 + Q&A（~10min）

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
