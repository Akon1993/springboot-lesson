# Spring Boot + Spring Cloud 产品团队培训 —— 逐字稿

> **讲前提示：** `[演示]` 切换 IDE/Demo；`[互动]` 与学员交流；`[停顿]` 给消化时间。
> 上半场 Boot 实操 ~95min，下半场 Cloud 理论 ~48min，Q&A ~10min。

---

## 开场白（~3min）

大家好。

上节课我们讲了 Spring 和 Spring MVC。IoC 容器、DispatcherServlet、@Controller、@Service——这些概念你们应该还有印象。

今天这节课要做一件事：告诉你们，上节课学的那些东西还在，但你不用再写那些 XML 了。

我们会用一个递进的方式——从三个工程入手。第一个工程只有 IoC 和 AOP，没有 Web，没有数据库。第二个工程加一个依赖，变成 Web 服务。第三个工程再加两个依赖，变成完整后端。

下半场我们不讲代码，纯讲故事——当一个公司有几十个这样的服务时，怎么管理。

[互动]

先问一下，上节课听完，你们最大的感受是什么？

[等待回应——预期有人说"配置好多"、"XML 很烦"]

好，如果你们的感受是"配置好多"——那今天你会很开心。

---

# 上半场：Spring Boot 实操

---

## 第1章：从 Spring MVC 到 Spring Boot —— 演进之路

[预计 15 分钟]

### 1.1 快速回顾（1min）

上节课讲了什么？三件事：Spring = IoC 容器、Spring MVC = Web 框架、你认识了 `@Controller`、`@Service`、`@Autowired`。够了。

### 1.2 🔌 先看结果——打开 demo1-initializer

[演示——打开 demo1-initializer 目录]

我不讲理论，先给你看一个东西。

这是从 start.spring.io 生成的项目。勾了零额外依赖。你看它的 pom.xml——没有 web，没有数据库，只有核心 starter。

我在里面写了四个文件。

[演示——逐个打开]

**GreetingService.java** —— 一个接口，一个方法 `greet(name)`。

**ChineseGreetingService.java** —— 实现类，加了 `@Service`：

```java
@Service
public class ChineseGreetingService implements GreetingService {
    public String greet(String name) {
        return "你好，" + name + "！欢迎来到 Spring Boot 的世界。";
    }
}
```

**LoggingAspect.java** —— 加了 `@Aspect`，拦截 service 方法打印耗时：

```java
@Aspect
@Component
public class LoggingAspect {
    @Around("execution(* com.example.demo1.service..*.*(..))")
    public Object log(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        System.out.println("[AOP] " + joinPoint.getSignature().toShortString()
                + " 执行耗时: " + (System.currentTimeMillis() - start) + "ms");
        return result;
    }
}
```

**Demo1Application.java** —— 启动类，构造器注入 GreetingService，启动后自动调用：

```java
@SpringBootApplication
public class Demo1Application implements CommandLineRunner {
    private final GreetingService greetingService;
    public Demo1Application(GreetingService greetingService) {
        this.greetingService = greetingService;
    }
    public static void main(String[] args) {
        SpringApplication.run(Demo1Application.class, args);
    }
    public void run(String... args) {
        System.out.println(greetingService.greet("产品团队"));
    }
}
```

[停顿]

注意：这四个文件没有一行 XML。没有 `web.xml`，没有 `applicationContext.xml`。纯 Java 代码 + 注解。

[演示——终端运行]

```bash
mvn spring-boot:run
```

控制台输出：

```
你好，产品团队！欢迎来到 Spring Boot 的世界。
[AOP] ChineseGreetingService.greet(..) 执行耗时: 2ms
```

0.4 秒启动。IoC 在工作——`GreetingService` 自动注入。AOP 也在工作——每个方法自动计时。**没有一行 XML 配置。**

### 1.3 对比——以前你要写什么？

同样的 IoC + AOP 效果，传统 Spring 需要在 XML 里写：

```xml
<!-- applicationContext.xml：显式告诉 Spring 去哪扫、开启注解 -->
<context:component-scan base-package="com.example.service"/>
<context:annotation-config/>
```

就这两行，但每个项目都要写。包名一改、路径一变，还得跟着改。而且这只是容器层的配置——还没到 Web 层呢。

**而 demo1：0 行 XML。** `@SpringBootApplication` 默认从启动类所在包开始扫描，`@Autowired` 自动生效。0.4 秒跑起来。

至于 `web.xml`、`spring-mvc.xml` 那 80+ 行——那是 Web 层的东西，demo1 根本没有 Web 模块。等第三章加了 Web Starter，我们再看 Spring Boot 怎么干掉它们。

### 1.4 Spring Boot 的答案："约定大于配置"

Spring 团队 2014 年出了 Spring Boot。核心理念就四个字——**约定大于配置**。

**"配置"** = 你告诉框架每一个细节。**"约定"** = 框架说"你按规矩来，细节不用我说"。

> 你引入 Spring Boot，不写端口配置。启动——8080 已经在监听了。想改？`server.port=9090` 一行。没写？默认 8080。
>
> **把每个人都要写的东西变成默认就有的。想改可以改，不改就能用。**

### 1.5 Spring Boot 是什么

| 痛点 | 解法 |
|------|------|
| 80+ 行 XML | **自动配置** — 约定好了，不用写 |
| 6~8 个依赖 | **Starter 套餐** — 一个依赖一组功能 |
| 装 Tomcat 打 WAR | **内嵌 Tomcat** — `java -jar` 直接跑 |
| 搭环境半天 | **Spring Initializr** — 30 秒 |

> **Spring Boot = Spring + Spring MVC + 自动配置 + 内嵌服务器。** 它是 Spring 的"自动挡"。

### 1.6 演进全景图

```
Java Servlet → Spring Framework → Spring MVC → Spring Boot → Spring Cloud
                                    ↑ 上节课         ↑ 本节课上半场      ↑ 本节课下半场
```

好。你看到了结果，也看到了对比。下一章我们钻进 demo1 内部——看看 `@SpringBootApplication` 到底做了什么。

---

## 第2章：核心机制 + demo1 —— IoC / AOP 层

[预计 20 分钟]

### 2.1 一个注解背后的魔法

[演示——打开 demo1-initializer 的 Demo1Application.java]

整个 Spring Boot 的入口就这一个注解：

```java
@SpringBootApplication  // ← 三合一
public class Demo1Application {
    public static void main(String[] args) {
        SpringApplication.run(Demo1Application.class, args);
    }
}
```

这个注解其实包含了三个注解：

| 拆开看 | 作用 | 学过吗？ |
|------|------|--------|
| `@Configuration` | "我是一个配置类，可以定义 Bean" | ✅ 上节课学过的 |
| `@ComponentScan` | "扫描我当前包下面所有组件" | ✅ 上节课学过的 |
| `@EnableAutoConfiguration` | **"自动配置！"** | 🆕 **这是 Spring Boot 的灵魂** |

前两个你上节课见过。第三个是新的——它就是"约定大于配置"在代码层面的实现。

### 2.2 自动配置——「精装修公寓」

自动配置干了什么？一句话：你需要的，我帮你配好；你没需要的，我不动。

打个比方：

> **传统 Spring = 毛坯房。** 拿到钥匙，墙没刷、电线没铺。你自己买油漆、自己拉电线。好处是想怎么装怎么装，坏处是累。
>
> **Spring Boot = 精装修公寓。** 拎包入住。墙刷好了、空调装好了。不满意的地方你可以改——你的配置优先级比默认配置高。

你不用告诉 Spring Boot "帮我开启 IoC 容器"、"帮我扫描 @Service"——`@SpringBootApplication` 一个注解，这些全自动做了。

这里面有两个非常典型的**约定**。

**第一个：默认扫描位置。** `@ComponentScan` 默认从启动类所在的包开始，向下扫描所有子包。demo1 的启动类在 `com.example.demo1`，所以它下面的 `service/`、`aspect/` 里的 `@Service`、`@Aspect` 自动被发现。不需要在 XML 里写 `<context:component-scan base-package="..."/>`。你把类放 `com.example.other` 下面？扫不到，得自己配。

**第二个：注解自动生效。** 传统 Spring 需要在 XML 里显式写 `<context:annotation-config/>`，`@Autowired`、`@Service` 这些注解才能工作。Spring Boot 自动帮你开了——你写 `@Autowired`，它就直接生效。不需要任何 XML 开关。

两个约定合在一起：**Spring Boot 知道去哪扫、扫到了怎么处理。你只需要写注解，不需要开开关。**

### 2.3 回顾 demo1 —— 验证这两条约定

回到第一章我们看过的 demo1。启动类在 `com.example.demo1`，`@Service` 在 `com.example.demo1.service.impl`——在启动类的子包下，自动被扫描到。`@Autowired`（通过构造器注入）自动生效，不需要 `<context:annotation-config/>`。

跑起来：`mvn spring-boot:run`，0.4 秒，IoC 和 AOP 全在工作。

**现在问题来了——这个工程不能对外提供 HTTP 服务。加一个依赖会怎样？**

---

## 第3章：Web 层实战 + demo2-web（~30min）⭐

> 这一章是整个培训的衔接点——把上节课讲的 Spring MVC 和今天的 Spring Boot 连起来。

### 3.1 demo2 比 demo1 多了什么？

[演示——并排打开 demo1 和 demo2 的 pom.xml]

看两个 pom.xml。demo2 只比 demo1 多了一行：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

**就这一行。** 它带来了什么？Tomcat + Spring MVC + JSON 序列化。不用装，不用配。

demo2 新增的代码全在 controller 和 common 包里。service 和 aspect 跟 demo1 一模一样——代码复用，没有改动。

### 3.2 上节课回顾——传统 Spring MVC 要写什么

[演示——逐段展示三个 XML]

现在我们把镜头拉回到上节课。同样的 Web 能力，传统 Spring MVC 需要写 3 个 XML 文件。第一章我们只看了容器层的两行配置——因为 demo1 没有 Web。现在 demo2 有了 Web 层，来看看那 80+ 行的全貌。

**web.xml —— 配置 DispatcherServlet（~30 行）：**

```xml
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
```

**applicationContext.xml —— 数据源、事务、扫描（~20 行）：**

```xml
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
```

**spring-mvc.xml —— 视图解析器、注解驱动（~30 行）：**

```xml
<beans>
  <mvc:annotation-driven/>
  <context:component-scan base-package="com.example.controller"/>
  <bean class="...InternalResourceViewResolver">
    <property name="prefix" value="/WEB-INF/views/"/>
    <property name="suffix" value=".jsp"/>
  </bean>
</beans>
```

然后安装 Tomcat → 打 WAR → 丢 webapps → 重启。

**而 demo2：1 个 starter-web 依赖 + 1 个 @RestController。没有一行 XML。**

[停顿]

### 3.3 ⭐ 承上启下——上节课讲的 vs Spring Boot 做的

[演示——投屏对照表，逐行指着讲]

好，看完 XML，我们再做一个一一映射。上节课哪些东西还在？哪些不用了？

| 上节课（传统 Spring MVC） | Spring Boot | 还需要吗？ |
|------|------|------|
| `web.xml` 配 `DispatcherServlet` | starter-web → 自动配好 | ❌ |
| `spring-mvc.xml` 配视图解析器、注解驱动 | `WebMvcAutoConfiguration` 自动配好 | ❌ |
| `applicationContext.xml` 配组件扫描 | `@SpringBootApplication` 自带扫描 | ❌ |
| 写 `@Controller` + `@RequestMapping` | 写 `@RestController` + `@GetMapping` | ✅ 写法一样！ |
| 装 Tomcat → 打 WAR → 部署 | 内嵌 Tomcat，`java -jar` 直接跑 | ❌ |

[停顿，语气加重]

**上节课教的 @Controller 怎么用——写法照用。web.xml 怎么配——不用了。Starter 替你配好了。**

以前 3 个 XML + 装 Tomcat + 打 WAR = 现在 1 个依赖 + 1 个注解。这就是 Spring Boot 的价值。

### 3.4 第一个 REST API

[演示——打开 GreetingController.java]

```java
@RestController  // = @Controller + @ResponseBody，返回 JSON
public class GreetingController {

    private final GreetingService greetingService;  // 构造器注入，复用 demo1 的 Service

    @GetMapping("/api/greeting")  // 上节课的 @RequestMapping 简化版
    public Result<String> greet(@RequestParam(defaultValue = "世界") String name) {
        return Result.success(greetingService.greet(name));
    }
}
```

注意几点：
- `@RestController` = 上节课的 `@Controller` + 自动转 JSON
- `@GetMapping` = 上节课的 `@RequestMapping` 简化版，限定 GET 请求
- `GreetingService` 通过构造器注入——和 demo1 一模一样，IoC 照常工作

### 3.5 统一响应 Result\<T\>

"但这样每个接口返回的格式不一样怎么办？" —— 引入统一响应。

```json
{"code": 200, "message": "操作成功", "data": {...}}
```

三个字段：code（状态码）、message（提示信息）、data（真正的数据）。所有接口统一这个格式，前端只需判断 `code === 200`。

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

Controller 里不需要写任何 try-catch。一个类全局搞定。上节课讲异常处理的时候，要么每个方法写 try-catch，要么在 web.xml 里配 error-page——现在一个注解。

### 3.7 运行验证

[演示——终端运行]

```bash
cd demo2-web
mvn spring-boot:run
```

看启动日志：**Tomcat started on port 8080**。

Tomcat 自己启动了。你没有安装它，没有配置它。一个 starter-web 依赖，它就在那了。

```bash
curl 'http://localhost:8080/api/greeting?name=产品团队'
# → {"code":200,"message":"操作成功","data":"你好，产品团队！"}
```

[停顿]

**一个依赖 + 一个 Controller。从控制台程序变成了 Web 服务。但现在数据还在代码里写死的——下一步，接数据库。**

---

## 第4章：数据层实战 + demo3-data（~30min）

### 4.1 demo3 比 demo2 多了什么？

[演示——打开 demo3 的 pom.xml]

pom.xml 多了两行：`mybatis-plus-spring-boot3-starter` + `h2`。

H2 是内存数据库——不需要你装 MySQL，不需要建表。应用一停数据就没，非常适合演示。生产环境换 MySQL？改几行配置，代码不动——我们后面会验证。

### 4.2 MyBatis Plus —— 数据层的"约定大于配置"

[演示——打开 Employee.java]

**Entity —— 一个类对应一张表：**

```java
@Data
@TableName("employee")   // ← 对应数据库的 employee 表
public class Employee {
    @TableId(type = IdType.AUTO)  // ← 主键自增
    private Long id;
    private String name;
    private BigDecimal salary;       // 金额用 BigDecimal，不是 float
    private LocalDateTime createTime; // 日期用 LocalDateTime，不是 Date
}
```

一个普通的 Java 类，加两个注解，就映射好了数据库表。

[演示——打开 EmployeeMapper.java]

**Mapper —— 空接口 = 全套 CRUD：**

```java
@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {
    // 空接口！
    // 但你现在拥有：insert、deleteById、updateById、
    //             selectById、selectList、selectPage、selectCount……
}
```

[停顿，制造效果]

什么都没写。但所有的单表 CRUD 已经全有了。传统 MyBatis 要写 10 条 SQL × 10 张表 = 100 条 SQL。MyBatis Plus：继承一个接口，完事。**数据层的"约定大于配置"。**

### 4.3 条件查询与分页

[演示——打开 EmployeeServiceImpl.java]

```java
// 条件查询 —— 链式调用，字段名写错了编译器直接报错
LambdaQueryWrapper<Employee> wrapper = new LambdaQueryWrapper<>();
wrapper.like(name != null, Employee::getName, name)
       .eq(departmentId != null, Employee::getDepartmentId, departmentId)
       .orderByDesc(Employee::getId);

// 分页 —— 一行搞定
Page<Employee> page = new Page<>(1, 10);
employeeMapper.selectPage(page, wrapper);
// 自动返回 records（数据列表）、total（总数）、current（当前页）、pages（总页数）
```

分页需要的 count 查询、limit 拼接——全自动。你不需要手动写 `SELECT COUNT(*)`。

### 4.4 配置管理 —— 21 行 XML → 5 行 YAML

[演示——打开 application.yml]

```yaml
spring:
  datasource:
    url: jdbc:h2:mem:demo3;MODE=MYSQL     # 零安装
    username: sa
  sql:
    init:
      mode: always  # 启动自动执行 schema.sql + data.sql
```

5 行。对比第一章我们看的那个 applicationContext.xml——21 行 `<bean>` + `<property>`。

**换 MySQL？** 改 3 行配置，代码一行不变。**多环境？** `application-dev.yml` / `application-prod.yml`，一行 `spring.profiles.active=prod` 切换。这就是 Spring Boot 的威力——改配置不改代码。

### 4.5 完整代码结构

[演示——IDE 展示 demo3-data 目录树]

```
demo3-data/
├── pom.xml                          ← 比 demo2 多 mybatis-plus + h2
└── src/main/java/com/example/demo3/
    ├── entity/Employee.java         ← 一个类对应一张表
    ├── mapper/EmployeeMapper.java   ← 空接口 = 全套 CRUD
    ├── service/... + impl/          ← @Transactional + 业务逻辑
    ├── controller/...               ← RESTful CRUD + 分页
    ├── common/                      ← Result、Exception、Handler
    ├── config/...                   ← 分页插件 + 自动填充
    └── resources/
        ├── application.yml          ← 5 行数据源配置
        └── db/schema.sql + data.sql ← 建表 + 9 条测试数据
```

从 URL 请求 → Controller → Service → Mapper → 数据库 → 返回 JSON。完整的四层架构，不到 15 个文件。

### 4.6 运行验证

[演示——终端]

```bash
cd demo3-data
mvn spring-boot:run     # < 1 秒，SQL 自动执行
```

[演示——打开 H2 控制台 http://localhost:8080/h2-console]

JDBC URL 填 `jdbc:h2:mem:demo3`。点进去，`SELECT * FROM employee`——9 条数据。

[演示——curl 逐条测试]

```bash
curl localhost:8080/api/employees                         # 查全部 9 条
curl 'localhost:8080/api/employees?current=1&size=5'      # 分页，返回前 5 条
curl 'localhost:8080/api/employees?name=张'               # 模糊搜索
curl -X POST localhost:8080/api/employees \
  -H 'Content-Type: application/json' \
  -d '{"name":"新员工","age":26,"departmentId":1,"salary":15000}'  # 新增
curl localhost:8080/api/employees/999                     # 异常处理
# → {"code":400,"message":"员工不存在: id=999"}
```

[停顿，让大家感受全链路]

### 4.7 三阶段小结

[演示——投屏小结表]

| | demo1 | demo2 | demo3 |
|------|------|------|------|
| 新增依赖 | 核心 starter | + starter-web | + mybatis-plus + h2 |
| 新增文件 | 4 个 | +3 个 | +7 个 |
| 能力 | IoC + AOP | + REST API | + 完整 CRUD |

**每一个 Starter 加一种能力。你不需要配——约定已经做好了。**

我们从最纯粹的 Spring Boot 开始，加了三个依赖，从控制台输出走到了完整的增删改查。反过来看——如果你一上来就看 demo3，你会觉得"东西好多"。但当你看到它只是 demo1 + 三个依赖 + 几个 Java 文件——你就明白了：Spring Boot 的核心不是魔法，是约定。

[停顿]

好了，上半场结束。休息 5 分钟。回来我们进入下半场——**当一个公司有几十个这样的服务时，怎么管理。**

---

# 下半场：Spring Cloud 理论

[休息 5 分钟后]

---

## 第5章：从单体到微服务——为什么需要 Spring Cloud

[预计 12 分钟]

### 5.1 一个电商公司的故事

[切换到讲故事的语气]

假设你开了一家电商公司。

**创业第一天。** 你只有一个应用——`shop.jar`。订单、用户、商品、库存全在里面。`java -jar shop.jar`，网站上线了。真爽。这就叫**单体架构**。

**一年后，公司 200 人。**

200 个人同时改一个工程——"昨天我写的代码被谁覆盖了？合并冲突 50 个文件。"

双十一，订单模块要扩容——但因为是单体，你只能把整个系统都部署 10 份。每份 4G 内存，40G。实际上只有订单模块需要扩容，用户模块根本没流量。

改了一个订单状态的颜色——整个系统全部重新部署。"为了换个灯泡，我把整栋楼的电闸拉了。"

用户模块有个 bug，内存泄漏——**整个网站全部崩溃。** 一个房间短路，整栋楼停电。

[停顿]

### 5.2 微服务的答案——拆

> "把一个超级工程拆成几十个小工程。每个独立开发、独立部署、独立扩容。"

**类比：** 单体 = 所有人坐一个大开间。吵、挤、一个人感冒全公司传染。微服务 = 按部门分楼层。技术部 3 楼、产品部 5 楼、市场部 8 楼。各管各的，互不干扰。

### 5.3 拆完之后的新问题

[语气转折]

但是——拆开之后，新问题来了。

订单服务要调用户服务。用户服务跑在 3 台机器上。**订单服务怎么知道该找哪台？**

前端页面要调订单、用户、商品、支付 4 个服务。**难道让前端记住 4 个地址？后端扩缩容了怎么办？**

用户服务挂了。订单服务还在不停调它——超时等 3 秒，1000 个请求全堵在队列里。最后订单服务也挂了。然后调订单服务的支付服务也挂了……**级联故障，全站崩溃。**

一个请求跨了 5 个服务。用户投诉"页面好慢"。**哪一步慢了？你完全不知道。**

[停顿]

**Spring Cloud 就是来解决这四个问题的。**

> Spring Boot 让一个服务跑起来。Spring Cloud 让一堆服务能协作。

---

## 第6章：Spring Cloud 六大组件

[预计 28 分钟]

好，回到刚才说的那四个问题。Spring Cloud 用六个组件来解决。我拿一个外卖平台串起来讲——你打开 App，看到商家列表，选了一家店，下单，支付，等骑手取餐配送。这背后是五个微服务：**用户服务、订单服务、商家服务、支付服务、配送服务。**

我们一个个来。看看每个组件在这个外卖平台里扮演什么角色。

### 6.1 注册中心 Nacos ——「配送调度中心」📒

你现在在 App 上下了一单。订单服务要通知配送服务："有新的外卖单，找个骑手。"

但配送服务跑在 3 台机器上，IP 分别是 192.168.1.5、192.168.1.8、192.168.1.12。订单服务怎么知道该找哪台？写死在配置文件里？配送服务扩容了一台新机器——地址变了，改配置，重启。运维疯了。

**Nacos 怎么做的？** 配送服务的 3 台机器启动时都去 Nacos 登记："我是配送服务，我住在 xxx"。订单服务要调用时，去 Nacos 问："配送服务在哪？" Nacos 说："3 台在线，第 2 台正在高峰期，你优先找 1 号和 3 号。"

某台机器挂了？Nacos 自动把它从列表里踢掉。新增一台？自动加进来。订单服务不需要知道任何变化。

**一句话记住：** 骑手上班打卡——平台知道谁在线。你下单，平台自动分配空闲骑手。Nacos 就是这个调度中心。

### 6.2 API 网关 Gateway ——「外卖 App 的统一后台」🏢

你打开外卖 App。首页要调商家服务（展示餐厅列表），购物车要调订单服务，结算要调支付服务。难道让 App 记住 3 个不同的服务器地址？

而且每个服务都要验证"你是不是登录用户"——商家服务验一次 token，支付服务又验一次。重复、不一致、有漏洞。

**Gateway 怎么做的？** App 只跟 Gateway 一个入口打交道。App 说"我要商家列表"，Gateway 看路径 `/api/shops` → 转发给商家服务。App 说"我要下单"，Gateway 转发给订单服务。App 不需要知道后面有几个服务、地址是什么。

鉴权也在这里统一做。没带 token？Gateway 直接返回"请先登录"，请求根本到不了后面的服务。

**一句话记住：** 你打开外卖 App，不会直接连"商家数据库"和"支付系统"——你只跟 App 的后台交互。Gateway 就是这个统一的 App 后台。

### 6.3 配置中心 Nacos Config ——「商家后台统一改价」☁️

外卖平台有个配送费规则。以前是"3 公里内 5 元"，运营说改成"3 公里内 4 元，每超 1 公里加 1 元"。

这个规则订单服务在用，配送服务也在用。没有配置中心的话，要改两个服务的 application.yml，重启两个服务。万一改了一个忘了一个？线上事故。

**Nacos Config 怎么做的？** 配送费规则统一放在配置中心。运营在后台改一次，订单服务和配送服务自动收到推送——不重启，直接生效。

**一句话记住：** 商家在后台改了一个菜的价格——App 上立刻显示新价格，不需要顾客更新 App。配置中心就是这个后台管理系统。

### 6.4 服务调用 Feign ——「App 里的一键支付」📞

你点了"提交订单"，订单服务要做三件事：调用户服务（验证地址）、调商家服务（确认库存）、调支付服务（扣款）。

传统方式，每个调用都要手写 HTTP：拼 URL、设 Header、发请求、解析响应——十几行代码。三个调用就是三四十行。而且每个调用方都要写一遍。

**Feign 怎么做的？** 订单服务只需要声明一个接口：

```java
@FeignClient("payment-service")
public interface PaymentClient {
    @PostMapping("/pay")
    PayResult pay(@RequestBody PayRequest request);
}
```

然后像调本地方法一样：`paymentClient.pay(request)`。Feign 自动帮你发 HTTP 请求、解析响应。URL 是什么？不用管。怎么序列化的？不用管。超时怎么处理？Feign 有默认配置。

**一句话记住：** 你在 App 上点"微信支付"，App 帮你调起微信——你不用自己打开微信、输入金额、输入密码。Feign 就是那个"一键调用"。

### 6.5 熔断降级 Sentinel ——「支付挂了，货到付款兜底」⚡

这是微服务最可怕的事——**级联故障。**

支付服务挂了。订单服务不知道，还在不停调它——每次超时等 3 秒。1000 个用户同时下单，1000 个请求全堵在等支付响应。线程池满了。**订单服务也挂了。** 然后商家服务调订单服务——商家服务也挂了。用户服务调商家服务——也挂了。连锁反应，全站崩溃。

[停顿]

商家正常营业，骑手也在线——**但因为支付系统挂了，整个 App 白屏。** 用户什么都做不了。

**Sentinel 怎么做的？** 它监控支付服务的调用。发现连续失败 → **熔断**："支付挂了，订单服务你别调它了，直接用货到付款。"

用户层面：下单成功，支付方式显示"货到付款"。骑手正常接单，商家正常出餐。支付服务恢复后，Sentinel 放几个请求试探 → 确认正常 → 自动恢复在线支付。

两个核心概念：
- **熔断：** 切断调用——"支付挂了，别等它了，用备选方案"
- **降级：** 兜底逻辑——"在线支付不可用？切换货到付款。页面其他功能正常用"

**一句话记住：** 一家店停电关门了——平台把它从列表里暂时下架，你不会点进去发现点不了。Sentinel 就是自动下架故障商家的那个机制。

### 6.6 链路追踪 Sleuth / Zipkin ——「外卖物流追踪」📦

用户投诉："我下单等了 10 秒才成功，太慢了。"

一个下单请求跨了 5 个服务：Gateway → 订单服务 → 用户服务（验证地址）→ 商家服务（确认库存）→ 支付服务（扣款）。到底哪一步慢了？

没有链路追踪的话，5 个服务各有自己的日志，但彼此没有关联——你没法把同一个请求的 5 段日志拼在一起。

**Sleuth / Zipkin 怎么做的？** 给每个请求分配一个全局唯一的 TraceID，就像快递单号。这个 ID 在 5 个服务之间一直传递。最后在 Zipkin 界面上，我们看到：

```
下单请求 TraceID: order-20260712-001
  Gateway        →   5ms ✓
  订单服务        →  20ms ✓
  用户服务        →  30ms ✓
  商家服务        → 8000ms 🚨  ← 查库存的 SQL 没建索引！
  支付服务        →  15ms ✓
```

破案。商家服务数据库缺一个索引，查库存扫了全表，耗时 8 秒。

**一句话记住：** 快递慢了——看物流轨迹："到达上海分拨中心 10:00" → "离开上海分拨中心 18:00"。卡了 8 小时。Zipkin 就是给每个请求发的快递单号，慢了看轨迹，秒级定位。

### 6.7 速查表——回到外卖平台

好了，我们用一个外卖平台把六个组件串了一遍。回顾一下：

| 外卖场景 | 对应组件 | 一句话 |
|---------|---------|------|
| 骑手上线打卡，平台自动分配 | 📒 Nacos | 配送调度中心 |
| App 统一后台，不管后面有几个服务 | 🏢 Gateway | 外卖 App 后台 |
| 商家改价，App 立刻生效 | ☁️ Nacos Config | 商家后台改价 |
| 一键支付，不用自己打开微信 | 📞 Feign | 一键支付 |
| 支付挂了，自动切换货到付款 | ⚡ Sentinel | 故障自动兜底 |
| 下单慢了，看哪个环节卡了 | 📦 Sleuth/Zipkin | 外卖物流追踪 |

六个组件，一个外卖平台全串起来了。你不需要会配置，但下次开会别人提到"熔断"，你知道那是"支付挂了用货到付款"的那个机制。

---

## 第7章：产品视角——微服务不是免费的午餐

[预计 8 分钟]

### 7.1 拆服务的代价

每一件事都有代价。

| 好处 | 代价 |
|------|------|
| 独立部署 | 运维复杂度 × N。日志、监控、部署流水线——1 份变 20 份 |
| 独立扩容 | 分布式事务，复杂 10 倍。以前一个数据库事务搞定的事，现在要协调多个服务 |
| 团队自治 | HTTP 调用有延迟。以前本地方法调用 1 毫秒，现在网络调用 10 毫秒起步 |

**微服务不是银弹。它解决了一些问题，制造了另一些问题。**

### 7.2 拆之前，先问三个问题

你不需要决定怎么拆——但你能问开发三个问题。

**第一，这个模块有独立的业务边界吗？** 不是"功能多"就该拆。订单和订单详情是同一个业务——拆开了反而增加耦合。

**第二，这个模块需要独立扩容吗？** 双十一只有订单要加机器，用户服务不用。没有独立扩容需求的模块，拆了干什么？

**第三，这个模块有独立团队在维护吗？** 一个团队同时维护 3 个微服务——那不是微服务，那叫分布式单体，更复杂还没拿到好处。

> **每拆一个服务，运维成本就多一份。拆之前，先问这三个问题。**

---

## 第8章：课程总结 + Q&A

[预计 10 分钟]

### 今天讲了什么

| 模块 | 核心 |
|------|------|
| Spring Boot | 自动挡。约定大于配置。 |
| 三阶段实战 | demo1(IoC/AOP) → demo2(Web+承上启下) → demo3(数据+CRUD) |
| Spring Cloud | 六大组件——六个生活比喻 |

### 你最该带走的三件事

**第一，Spring Boot 是自动挡。** 上节课学的 Spring 全部有效，只是不需要写 XML 了。@Controller 照用，web.xml 不用了。

**第二，每个 Starter 加一种能力。** 加依赖就能用。demo1 加 starter-web = Web 服务。再加 mybatis-plus = 完整后端。约定已经帮你配好了。

**第三，Cloud 六道保险 + 拆前三问。** 微服务不是免费的。通讯录、前台、iCloud、快捷拨号、保险丝、快递单号——六个比喻帮你记住六个组件。拆之前问：独立边界？独立扩容？独立团队？

[互动]

好，我就讲到这儿。有什么想问的？

---

> **讲后提示：**
> - 大纲文档：`docs/springboot-teaching-outline.md`
> - 逐字稿：`docs/springboot-teaching-script.md`
> - Demo 源码：`demo1-initializer/` `demo2-web/` `demo3-data/`
> - 启动命令：`cd demoX && mvn spring-boot:run`
