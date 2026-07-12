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

> 贯穿比喻：外卖平台——用户/订单/商家/配送/支付各是独立微服务。

### 6.1 注册中心 Nacos ——「公司通讯录」📒

**问题：** 订单服务不知道用户服务的 IP。写死？扩容就炸。

**解法：** 每个服务启动时去 Nacos 登记，调用方去 Nacos 查询。

> 换手机号 → 更新通讯录就行，不用群发通知 500 人。

### 6.2 API 网关 Gateway ——「写字楼前台」🏢

**问题：** 前端记 30 个地址？每个服务都做鉴权？

**解法：** 前端只调 Gateway。统一入口 + 统一鉴权。没 token 门都进不来。

> 去写字楼，前台帮你刷卡、告诉你几楼。

### 6.3 配置中心 Nacos Config ——「iCloud 同步」☁️

**问题：** 数据库地址变了 → 改 10 份配置、重启 10 个服务。

**解法：** 配置统一放配置中心。一处修改，全部推送，**不重启**生效。

> 换新手机，iCloud 自动同步。不用一个一个重新设置。

### 6.4 服务调用 Feign ——「快捷拨号」📞

**问题：** 每次调远程服务都要手写 HTTP 请求。

**解法：** 写接口 + 注解 = 自动发 HTTP。像调本地方法一样。

> 设一个"老婆"快捷拨号，说名字就拨。不用管号码。

### 6.5 熔断降级 Sentinel ——「保险丝」⚡

**问题：** 用户服务挂了 → 订单不停重试 → 订单也挂 → **级联故障，全站崩溃。**

**解法：** 熔断（切断调用）+ 降级（返回兜底结果）。恢复后自动重连。

> 电饭煲短路 → 保险丝只断厨房。客厅灯还亮着。

### 6.6 链路追踪 Sleuth/Zipkin ——「快递单号」📦

**问题：** 请求跨 5 个服务，慢了——哪一步的问题？

**解法：** 每个请求一个 TraceID，串联完整调用树。每步耗时一目了然。

> 快递慢了？看物流轨迹——卡在分拨中心 5 小时。破案。

### 6.7 速查表

| 组件 | 解决什么 | 比喻 |
|------|---------|------|
| Nacos | 服务互相找到 | 📒 公司通讯录 |
| Gateway | 统一入口+鉴权 | 🏢 写字楼前台 |
| Nacos Config | 配置统一管理 | ☁️ iCloud 同步 |
| Feign | 服务间调用 | 📞 快捷拨号 |
| Sentinel | 防止级联崩溃 | ⚡ 保险丝 |
| Sleuth/Zipkin | 排查慢请求 | 📦 快递单号 |

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
