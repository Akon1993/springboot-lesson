# Spring Boot + Spring Cloud 产品团队培训 —— 逐字稿

> 上半场 Boot 实操 ~95min，下半场 Cloud 理论 ~48min，Q&A ~10min。
> `[演示]` 切换 IDE/Demo；`[互动]` 与学员交流；`[停顿]` 给消化时间。

---

## 开场白（~3min）

大家好。

上节课我们讲了 Spring 和 Spring MVC——IoC 容器、DispatcherServlet、@Controller、@Service 这些概念。

今天这节课要做两件事。第一，告诉你们 Spring Boot 如何消除了上节课那些繁琐的 XML 配置。第二，当服务数量从 1 个变成 20 个、50 个的时候，Spring Cloud 如何管理它们。

上半场我们用三个递进工程来演示。第一个工程只有 IoC 和 AOP，没有 Web、没有数据库。第二个工程加一个依赖，变成 Web 服务。第三个工程再加两个依赖，变成完整后端。每加一个依赖，能力就多一层。

下半场不讲代码，纯讲架构——用外卖平台做例子，把 Spring Cloud 六个组件串起来。

[互动]

上节课听完，你们最大的感受是什么？

[等待回应]

如果感受是"配置很多"——今天会看到这些配置是如何被消除的。

---

# 上半场：Spring Boot 实操

---

## 第1章：从 Spring MVC 到 Spring Boot —— 演进之路

[预计 15 分钟]

### 1.1 快速回顾

上节课三个要点：

- Spring 是 IoC 容器，管理对象的创建和注入，不需要手动 new。
- Spring MVC 是 Web 框架，请求通过 DispatcherServlet 分发到 Controller。
- @Controller、@Service、@Autowired 这些注解，标记了框架需要认识的类。

### 1.2 先看结果——打开 demo1-initializer

[演示——打开 demo1-initializer 目录]

这个工程来自 Spring Initializr，没有勾选任何额外依赖。pom.xml 里只有核心 starter，没有 Web，没有数据库。

里面写了四个文件：

[演示——逐个打开]

**GreetingService.java** —— 接口，定义一个 greet 方法。

**ChineseGreetingService.java** —— 实现类，加了 @Service 注解：

```java
@Service
public class ChineseGreetingService implements GreetingService {
    public String greet(String name) {
        return "你好，" + name + "！欢迎来到 Spring Boot 的世界。";
    }
}
```

**LoggingAspect.java** —— AOP 切面，加了 @Aspect 和 @Component，拦截 service 方法并打印执行耗时。

**Demo1Application.java** —— 启动类，通过构造器注入 GreetingService，启动后自动执行：

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

注意两点。第一，这四个文件没有一行 XML。第二，Demo1Application 里只声明了 `GreetingService` 接口，没有 `new ChineseGreetingService()`——Spring 自动找到了实现类并注入。

[演示——终端运行]

```bash
mvn spring-boot:run
```

控制台输出：

```
你好，产品团队！欢迎来到 Spring Boot 的世界。
[AOP] greet 执行耗时: 2ms
```

0.4 秒启动。IoC 在工作——依赖自动注入。AOP 在工作——方法自动计时。零行 XML。

### 1.3 对比——传统方式需要写什么

同样的 IoC + AOP 效果，传统 Spring 需要在 XML 中写：

```xml
<!-- applicationContext.xml -->
<context:component-scan base-package="com.example.service"/>
<context:annotation-config/>
```

每个项目都要写这两行。包名变化时要跟着改。而 demo1：零行 XML，@SpringBootApplication 一个注解替代了这些配置。

至于 web.xml、spring-mvc.xml 那 80 多行——那是 Web 层的配置。demo1 没有 Web 模块，不需要它们。下一章加了 Web Starter 之后再对比。

### 1.4 拆解 @SpringBootApplication

[演示——打开 Demo1Application.java]

```java
@SpringBootApplication
public class Demo1Application {
    public static void main(String[] args) {
        SpringApplication.run(Demo1Application.class, args);
    }
}
```

@SpringBootApplication 包含三个注解：

| 注解 | 作用 | 上节课是否涉及 |
|------|------|-------------|
| @Configuration | 标记配置类，可以定义 Bean | 涉及 |
| @ComponentScan | 扫描当前包及子包下的组件 | 涉及 |
| @EnableAutoConfiguration | 自动配置——Spring Boot 的核心 | 本节课新内容 |

前两个上节课学过。第三个是 Spring Boot 独有的——它就是约定大于配置在代码层面的实现。

### 1.5 自动配置与两条约定

传统 Spring 像毛坯房：墙自己刷，电线自己铺。Spring Boot 像精装修公寓：拎包入住，不满意的地方可以改，你的配置优先级高于默认配置。

具体到 demo1，体现为两条约定：

**约定一：默认扫描位置。** @ComponentScan 从启动类所在包开始，向下扫描所有子包。Demo1Application 在 `com.example.demo1`，所以 `service/`、`aspect/` 子包下的 @Service、@Aspect 自动被发现。不需要在 XML 里写 `<context:component-scan base-package="..."/>`。把类放到包外则扫不到，需要显式配置。

**约定二：注解自动生效。** 传统 Spring 需要在 XML 中显式写 `<context:annotation-config/>`，@Autowired、@Service 等注解才能工作。Spring Boot 自动开启了注解处理——写 @Autowired 就直接生效，无需任何 XML 开关。

两条约定合在一起：Spring Boot 知道去哪里扫描、扫到之后怎么处理。开发者只需要写注解。

### 1.6 "约定大于配置"总结 & Spring Boot 是什么

**"配置"** = 你告诉框架每一个细节。**"约定"** = 框架预设默认行为——按规矩来就自动生效。

> 不写端口配置→8080 已在监听。想改 `server.port=9090`，没写就默认。
> 把每个人都要写的东西变成默认就有的。想改可以改，不改就能用。

Spring Boot = Spring + Spring MVC + 自动配置 + 内嵌服务器。它是 Spring 的"自动挡"。

### 1.7 演进全景图

```
Java Servlet → Spring Framework → Spring MVC → Spring Boot → Spring Cloud
                                    ↑ 上节课         ↑ 本节课上半场      ↑ 本节课下半场
```

demo1 验证了容器层的约定。但它没有 Web 能力。加一个依赖会怎样？

---

## 第2章：Web 层实战 + demo2-web（~30min）

> 这一章是与上节课 Spring MVC 内容的衔接点。控制台程序变为 Web 服务，全靠一个 Starter。

### 2.1 demo2 比 demo1 多了什么

[演示——并排打开 demo1 和 demo2 的 pom.xml]

demo2 的 pom.xml 只比 demo1 多了一个依赖：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

这一个依赖包含：Tomcat（Web 服务器）、Spring MVC（Web 框架）、Jackson（JSON 序列化）。不需要安装、不需要配置。

demo2 新增的代码全在 controller 和 common 包中。service 和 aspect 与 demo1 完全相同——代码复用，没有改动。

### 2.2 上节课回顾——传统 Spring MVC 要写什么

[演示——逐段展示三个 XML]

同样的 Web 能力，传统 Spring MVC 需要三个 XML 文件，80 多行配置。第一章我们只看了容器层的两行配置，因为 demo1 没有 Web 模块。现在 demo2 有了 Web 层，来看全貌。

**web.xml —— 配置 DispatcherServlet（约 30 行）：**

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

**applicationContext.xml —— 数据源、事务、Service 扫描（约 20 行）：**

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

**spring-mvc.xml —— 视图解析器、注解驱动（约 30 行）：**

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

然后安装 Tomcat、打 WAR 包、部署到 webapps 目录、重启。

而 demo2：一个 starter-web 依赖加一个 @RestController。没有一行 XML。

[停顿]

### 2.3 承上启下——上节课讲的 vs Spring Boot 做的

[演示——投屏对照表]

把上节课学的 Spring MVC 配置项和 Spring Boot 的做法做一次一一映射：

| 上节课（传统 Spring MVC） | Spring Boot | 还需要吗 |
|------|------|------|
| web.xml 配置 DispatcherServlet | starter-web 自动配置 | 不需要 |
| spring-mvc.xml 配置视图解析器、注解驱动 | WebMvcAutoConfiguration 自动配置 | 不需要 |
| applicationContext.xml 配置组件扫描 | @SpringBootApplication 自带扫描 | 不需要 |
| 写 @Controller + @RequestMapping | 写 @RestController + @GetMapping | 写法一样 |
| 装 Tomcat → 打 WAR → 部署 | 内嵌 Tomcat，java -jar 直接跑 | 不需要 |

上节课教的 @Controller 用法照旧，web.xml 的配置不再需要。Starter 已经替代了那三个 XML 文件。

### 2.4 第一个 REST API

[演示——打开 GreetingController.java]

```java
@RestController  // = @Controller + @ResponseBody，返回值自动转 JSON
public class GreetingController {

    private final GreetingService greetingService;  // 构造器注入，复用 demo1 的 Service

    @GetMapping("/api/greeting")
    public Result<String> greet(@RequestParam(defaultValue = "世界") String name) {
        return Result.success(greetingService.greet(name));
    }
}
```

@RestController 等于上节课的 @Controller 加上自动 JSON 序列化。@GetMapping 等于 @RequestMapping 的 GET 限定版。GreetingService 通过构造器注入——和 demo1 中完全相同的 IoC 机制。

### 2.5 统一响应 Result\<T\>

Controller 写完后自然产生一个问题：每个接口返回格式不统一怎么办？有的返回对象，有的返回字符串，有的返回空 Body——前端无法统一处理。

解决方案：所有接口统一返回以下格式：

```json
{"code": 200, "message": "操作成功", "data": {...}}
```

三个字段：code（状态码，200 表示成功）、message（提示信息）、data（实际数据，泛型）。前端只需判断 `code === 200`。

### 2.6 统一异常处理

另一个问题：当用户请求不存在的资源时，如何处理？

```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(BusinessException.class)
    public Result<?> handle(BusinessException e) {
        return Result.error(e.getCode(), e.getMessage());
    }
}
```

@RestControllerAdvice 一个注解，全局处理所有异常。Controller 方法中不需要写 try-catch。上节课讲异常处理时，要么每个方法写 try-catch，要么在 web.xml 中配置 error-page——现在一个类全局完成。

### 2.7 运行验证

[演示——终端]

```bash
cd demo2-web && mvn spring-boot:run
# 日志关键行：Tomcat started on port 8080

curl 'http://localhost:8080/api/greeting?name=产品团队'
# → {"code":200,"message":"操作成功","data":"你好，产品团队！"}
```

Tomcat 自动启动，不需要安装和配置。一个依赖加一个 Controller，控制台程序变为 Web 服务。

---

## 第3章：数据层实战 + demo3-data（~30min）

### 3.1 demo3 比 demo2 多了什么

[演示——打开 demo3 的 pom.xml]

demo3 的 pom.xml 比 demo2 多了两个依赖：mybatis-plus-spring-boot3-starter 和 h2。

H2 是内存数据库——不需要安装 MySQL，不需要建表，应用停止数据自动清空，适合演示。生产环境换成 MySQL 只需修改 application.yml 中的几行配置，代码不变。

### 3.2 MyBatis Plus —— 数据层的约定大于配置

[演示——打开 Employee.java]

**Entity —— 一个类对应一张表：**

```java
@Data
@TableName("employee")
public class Employee {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private BigDecimal salary;         // 金额用 BigDecimal，不用 float
    private LocalDateTime createTime;  // 日期用 LocalDateTime，不用 Date
}
```

@TableName 指定对应的数据库表名，@TableId 标记主键及自增策略。

[演示——打开 EmployeeMapper.java]

**Mapper —— 空接口等于全套 CRUD：**

```java
@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {
    // 空接口。继承 BaseMapper 后自动拥有：
    // insert()、deleteById()、updateById()、selectById()
    // selectList()、selectPage()、selectCount()
}
```

传统 MyBatis 中，每张表需要手写约 10 条 SQL，十张表就是 100 条。MyBatis Plus 中继承一个接口即拥有全部单表操作。这是数据层的约定大于配置。

### 3.3 条件查询与分页

[演示——打开 EmployeeServiceImpl.java]

```java
LambdaQueryWrapper<Employee> wrapper = new LambdaQueryWrapper<>();
wrapper.like(name != null, Employee::getName, name)
       .eq(departmentId != null, Employee::getDepartmentId, departmentId)
       .orderByDesc(Employee::getId);

Page<Employee> page = new Page<>(1, 10);
employeeMapper.selectPage(page, wrapper);
// 返回对象包含：records（数据列表）、total（总数）、current（当前页）、pages（总页数）
```

LambdaQueryWrapper 使用链式调用构建条件，字段名以方法引用方式书写。字段名写错时编译器直接报错，而非运行时才发现。分页所需的 count 查询和 limit 拼接全部自动完成。

### 3.4 配置管理 —— XML 与 YAML 的对比

[演示——打开 application.yml]

```yaml
spring:
  datasource:
    url: jdbc:h2:mem:demo3;MODE=MYSQL
    username: sa
  sql:
    init:
      mode: always
```

5 行 YAML 完成数据源配置。对比传统 Spring 中同一配置需要约 21 行 XML（bean 定义、property 标签等）。缩进表示层级，配置即文档。

切换到 MySQL 只需修改 url、username、password 三行。多环境部署时使用 application-dev.yml / application-prod.yml，一行 `spring.profiles.active=prod` 切换。

### 3.5 完整代码结构

[演示——IDE 展示 demo3-data 目录树]

```
demo3-data/
├── pom.xml
└── src/main/java/com/example/demo3/
    ├── entity/Employee.java              ← 实体类，映射数据库表
    ├── mapper/EmployeeMapper.java        ← 空接口，继承 BaseMapper
    ├── service/EmployeeService.java      ← 业务接口
    ├── service/impl/EmployeeServiceImpl.java ← 业务实现，@Transactional
    ├── controller/EmployeeController.java    ← RESTful CRUD + 分页
    ├── common/                           ← Result、BusinessException、Handler
    ├── config/MyBatisPlusConfig.java     ← 分页插件、自动填充
    └── resources/
        ├── application.yml               ← 数据源配置
        └── db/schema.sql + data.sql      ← 建表与测试数据
```

从 HTTP 请求到数据库返回的完整链路：Controller → Service → Mapper → Entity。四个层次，不到 15 个文件。

### 3.6 运行验证

[演示——终端]

```bash
cd demo3-data && mvn spring-boot:run     # 不到 1 秒，SQL 脚本自动执行
```

[演示——浏览器打开 http://localhost:8080/h2-console]

JDBC URL 填入 `jdbc:h2:mem:demo3`，可查看自动创建的 employee 表和 9 条测试数据。

[演示——curl 测试]

```bash
curl localhost:8080/api/employees                         # 查询全部
curl 'localhost:8080/api/employees?current=1&size=5'      # 分页，返回前5条
curl 'localhost:8080/api/employees?name=张'               # 按姓名模糊搜索
curl -X POST localhost:8080/api/employees \
  -H 'Content-Type: application/json' \
  -d '{"name":"新员工","age":26,"departmentId":1,"salary":15000}'  # 新增
curl localhost:8080/api/employees/999                     # 异常处理
# → {"code":400,"message":"员工不存在: id=999"}
```

### 3.7 三阶段小结

[演示——投屏小结表]

| | demo1 | demo2 | demo3 |
|------|------|------|------|
| 新增依赖 | 核心 starter | + starter-web | + mybatis-plus + h2 |
| 新增文件 | 4 个 | +3 个 | +7 个 |
| 能力 | IoC + AOP | + REST API + 统一响应 | + 完整 CRUD + 配置管理 |

每个 Starter 添加一种能力。从 demo1 到 demo3，只增加了三个依赖和若干 Java 文件，从控制台输出走到了完整的增删改查。Spring Boot 的核心不是魔法——是约定。

[停顿]

上半场结束。休息 5 分钟，回来后讨论一个问题：当公司有几十个这样的服务时，如何管理它们。

---

# 下半场：Spring Cloud 理论

[休息 5 分钟后]

---

## 第4章：从单体到微服务——为什么需要 Spring Cloud

[预计 12 分钟]

### 4.1 一个电商系统的发展过程

假设一家电商公司。

创业初期，只有一个应用——shop.jar。订单、用户、商品、库存全部在一个工程里。java -jar shop.jar 即上线。这是单体架构。

一年后，团队 200 人。问题开始出现：

200 人同时修改一个工程，代码合并冲突频繁。双十一订单模块需要扩容，但因为单体架构，只能将整个系统部署多份，每份都包含不需要扩容的模块。修改一行订单代码，整个系统需要全部重新部署——"换一个灯泡需要拉整栋楼的电闸"。用户模块出现内存泄漏，整个网站全部不可用——一个房间短路，整栋楼停电。

### 4.2 微服务的思路——拆分

解决方案：将一个超级工程拆分为几十个小工程。每个独立开发、独立部署、独立扩容。

类比：单体架构像所有人坐在一个大开间，一个人感冒全公司传染。微服务像按部门分楼层，技术部三楼、产品部五楼、市场部八楼，各管各的，互不干扰。

### 4.3 拆分后产生的新问题

拆分解决了部署和扩容问题，但引入了新的挑战：

订单服务需要调用用户服务——用户服务运行在多台机器上，订单服务如何知道应该连接哪一台？

前端页面需要调用订单、用户、商品、支付四个服务——让前端记住四个地址？后端扩缩容时地址变化了怎么办？

用户服务宕机。订单服务不断重试，超时等待积压，最终订单服务也被拖垮。调用订单服务的支付服务跟着宕机——级联故障，全站崩溃。

一个请求跨越五个服务，用户反馈响应缓慢——到底是哪一个环节的问题？

Spring Cloud 正是为解决这四个问题而设计的。Spring Boot 让一个服务运行起来，Spring Cloud 让一组服务能够协作。

---

## 第5章：Spring Cloud 六大组件

[预计 28 分钟]

用一个外卖平台作为贯穿案例。用户打开 App，看到商家列表，选店，下单，支付，等待骑手取餐配送。这背后是五个独立的微服务：用户服务、订单服务、商家服务、支付服务、配送服务。

### 5.1 注册中心 Nacos ——配送调度中心

用户下单后，订单服务需要通知配送服务："有新的外卖订单，请安排骑手。"

配送服务运行在三台机器上，IP 地址各不相同。订单服务如何知道该找哪一台？将 IP 地址写在配置文件里？配送服务扩容了一台新机器，地址变了，需要修改配置、重启服务。

Nacos 的解决方案：三台配送服务启动时都向 Nacos 登记——"我是配送服务，我的地址是 xxx"。订单服务需要调用时，向 Nacos 查询："配送服务在哪里？" Nacos 返回可用实例列表，并提示哪台机器当前负载较低。某台机器宕机后，Nacos 自动将其从列表中移除。新增机器后，自动加入列表。订单服务不需要感知任何变化。

一句话理解：骑手上班时打卡，平台即知道谁在线。用户下单后，平台自动分配空闲骑手。Nacos 的作用相当于这个调度中心。

### 5.2 API 网关 Gateway ——外卖 App 的统一后台

用户打开外卖 App。首页需要调用商家服务展示餐厅列表，购物车需要调用订单服务，结算需要调用支付服务。如果让 App 直接记住三个不同的服务器地址——后端扩缩容时地址变化，App 需要跟着更新。而且每个服务各自做登录验证，代码重复且容易出现不一致。

Gateway 的解决方案：App 只与 Gateway 一个入口通信。App 请求商家列表时，Gateway 根据路径 `/api/shops` 转发给商家服务。请求下单时，Gateway 转发给订单服务。App 完全不知道后端有几个服务、地址是什么。登录验证也在 Gateway 统一完成：没有 token 的请求在 Gateway 层即被拦截，不会到达后端服务。

一句话理解：用户打开外卖 App，不会直接连接商家的数据库或支付系统，只与 App 的统一后台交互。Gateway 就是这个统一后台。

### 5.3 配置中心 Nacos Config ——商家后台统一改价

外卖平台的配送费规则需要调整：从"3 公里内 5 元"改为"3 公里内 4 元，每超 1 公里加 1 元"。

这个规则订单服务在使用，配送服务也在使用。没有配置中心时，需要修改两个服务的配置文件，重启两个服务。如果只改了一个而忘记另一个，线上即出现不一致。

Nacos Config 的解决方案：配送费规则统一存放在配置中心。运营人员修改一次，订单服务和配送服务自动收到推送，不重启即生效。

一句话理解：商家在后台修改了菜品价格，App 上立即显示新价格，用户无需更新 App。配置中心相当于这个后台管理系统。

### 5.4 服务调用 Feign ——一键支付

用户点击"提交订单"后，订单服务需要依次调用用户服务验证地址、调用商家服务确认库存、调用支付服务完成扣款。

传统方式下，每次远程调用都需要手写 HTTP 请求：拼接 URL、设置 Header、发送请求、解析响应——每次十几行代码。三个调用就是几十行，五个服务互相调用时这段逻辑需要重复数十遍。

Feign 的解决方案：订单服务只需声明一个接口：

```java
@FeignClient("payment-service")
public interface PaymentClient {
    @PostMapping("/pay")
    PayResult pay(@RequestBody PayRequest request);
}
```

然后像调用本地方法一样使用：`paymentClient.pay(request)`。Feign 自动处理 HTTP 请求的发送、序列化、超时等细节。

一句话理解：用户在 App 中点击"微信支付"，App 自动调起微信，用户无需自己打开微信输入金额。Feign 相当于这个一键调用的机制。

### 5.5 熔断降级 Sentinel ——支付宕机后的兜底方案

这是微服务架构中最严重的风险——级联故障。

支付服务宕机。订单服务不知道，仍在不断调用它——每次超时等待 3 秒。1000 个用户同时下单，1000 个请求全部阻塞在等待支付响应上。线程池耗尽，订单服务也被拖垮。然后商家服务调用订单服务，跟着宕机。用户服务调用商家服务，也跟着宕机。连锁反应，全站崩溃。

此时商家正常营业，骑手也在线，但因为支付系统故障，整个 App 白屏——用户什么都做不了。

Sentinel 的解决方案：监控每次调用的成功率。支付服务连续失败后触发熔断——"订单服务不要再调支付服务了，直接切换为货到付款"。用户侧体验：下单成功，支付方式显示货到付款，骑手正常接单，商家正常出餐。支付服务恢复后，Sentinel 放少量请求试探，确认正常后自动恢复在线支付。

两个核心概念：熔断——切断对故障服务的调用，避免连锁反应；降级——提供备用方案，保证核心流程不受影响。

一句话理解：一家店停电关门，平台将其从列表中暂时下架，用户不会点进去才发现无法下单。Sentinel 相当于这个自动下架并推荐替代方案的机制。

### 5.6 链路追踪 Sleuth / Zipkin ——外卖物流追踪

用户投诉："下单过程等了 10 秒才成功，太慢了。"

一个下单请求跨越了五个服务：Gateway → 订单服务 → 用户服务（验证地址）→ 商家服务（确认库存）→ 支付服务（扣款）。到底哪个环节耗时过长？

没有链路追踪时，五个服务各自有日志，但日志之间没有关联——无法将同一个请求在五个服务上的片段拼接起来。

Sleuth / Zipkin 的解决方案：为每个请求分配一个全局唯一的 TraceID，相当于快递单号。这个 ID 在五个服务之间全程传递。在 Zipkin 界面上，可以按 TraceID 查询完整的调用链：

```
下单请求 TraceID: order-20260712-001
  Gateway        →   5ms
  订单服务        →  20ms
  用户服务        →  30ms
  商家服务        → 8000ms  ← 查库存的 SQL 缺少索引
  支付服务        →  15ms
```

商家服务的数据库缺一个索引，查库存时扫描了全表，耗时 8 秒。定位到了问题。

一句话理解：快递物流轨迹显示"到达上海分拨中心 10:00，离开 18:00"——卡了 8 小时。Zipkin 就是给每个请求分配的快递单号，慢了看轨迹即可定位瓶颈。

### 5.7 速查表——回到外卖平台

六个组件在外卖平台中各自扮演的角色：

| 外卖场景 | 对应组件 | 作用 |
|---------|---------|------|
| 骑手上线打卡，平台自动分配 | Nacos | 配送调度中心 |
| App 统一后台，不管后面有几个服务 | Gateway | 外卖 App 后台 |
| 商家改配送费，App 立刻生效 | Nacos Config | 商家后台改价 |
| 一键支付，不用自己打开微信 | Feign | 一键支付 |
| 支付宕机，自动切换货到付款 | Sentinel | 故障自动兜底 |
| 下单慢了，看哪个环节卡顿 | Sleuth/Zipkin | 外卖物流追踪 |

六个组件，六种场景。不需要会配置，但需要知道它们各自解决什么问题。

---

## 第6章：产品视角——微服务不是免费的午餐

[预计 8 分钟]

### 6.1 拆分的代价

每一个架构决策都有成本和收益。

| 收益 | 成本 |
|------|------|
| 独立部署 | 运维复杂度乘以 N。日志、监控、部署流水线从 1 份变为 20 份 |
| 独立扩容 | 分布式事务复杂度增加。原来一个数据库事务完成的操作，现在需要协调多个服务 |
| 团队自治 | 网络调用引入延迟。原来本地方法调用 1 毫秒，现在 HTTP 调用 10 毫秒起步 |

微服务不是银弹。它解决了一些问题，同时制造了新的问题。

### 6.2 拆分前的三个问题

产品人员不需要决定如何拆分，但可以向开发团队提出三个问题：

第一，这个模块有独立的业务边界吗？不是功能多就应该拆分。订单和订单详情属于同一个业务边界，拆分反而增加耦合。

第二，这个模块需要独立扩容吗？双十一只有订单服务需要增加机器，用户服务不需要。没有独立扩容需求的模块，拆分没有带来核心收益。

第三，这个模块有独立团队在维护吗？一个团队同时维护三个微服务，这被称为分布式单体——比单体架构更复杂却没有获得微服务的好处。

每拆分一个服务，运维成本就增加一份。拆分之前，先回答这三个问题。

---

## 第7章：课程总结 + Q&A

[预计 10 分钟]

### 今天的内容

| 模块 | 核心 |
|------|------|
| Spring Boot | 自动挡。约定大于配置。上节课学的全部有效，只是不需要 XML |
| 三阶段实战 | demo1(IoC/AOP) → demo2(Web+承上启下) → demo3(数据+CRUD) |
| Spring Cloud | 六大组件——六个外卖平台场景 |

### 三个关键结论

第一，Spring Boot 是自动挡。上节课学的 @Controller、@Service、@Autowired 全部有效，web.xml、spring-mvc.xml、applicationContext.xml 不再需要。一个 @SpringBootApplication 注解替代了三个 XML 文件。

第二，每个 Starter 添加一种能力。加依赖即获得能力，约定已经做好了配置。demo1 加 starter-web 变成 Web 服务，再加 mybatis-plus 变成完整后端。

第三，Spring Cloud 六个组件解决微服务协作问题。配送调度中心、统一后台、配置改价、一键支付、故障兜底、物流追踪——六个场景对应六个组件。拆分之前，先问三个问题：独立边界？独立扩容？独立团队？

[互动]

以上就是今天的内容。有什么问题可以现在交流。

---

> 大纲文档：`docs/springboot-teaching-outline.md`
> Demo 源码：`demo1-initializer/` `demo2-web/` `demo3-data/`
> 启动命令：`cd demoX && mvn spring-boot:run`
