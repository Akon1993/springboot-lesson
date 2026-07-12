#!/usr/bin/env python3
"""Generate Spring Boot + Spring Cloud training PPT — 8-chapter structure."""

from pptx import Presentation
from pptx.util import Inches, Pt
from pptx.dml.color import RGBColor
from pptx.enum.text import PP_ALIGN
from pptx.enum.shapes import MSO_SHAPE

G = RGBColor(0x6D, 0xB3, 0x3F)  # Spring Green
D = RGBColor(0x34, 0x30, 0x2D)  # Dark
W = RGBColor(0xFF, 0xFF, 0xFF)
L = RGBColor(0xF8, 0xF9, 0xFA)
R = RGBColor(0xE7, 0x4C, 0x3C)
GR = RGBColor(0x66, 0x66, 0x66)
DB = RGBColor(0x1B, 0x1B, 0x1B)
LG = RGBColor(0xF0, 0xFD, 0xF4)
CODE_BG = RGBColor(0x27, 0x28, 0x22)
CODE_FG = RGBColor(0xE6, 0xDB, 0x74)

prs = Presentation()
prs.slide_width = Inches(13.333)
prs.slide_height = Inches(7.5)

def bg(slide, color):
    slide.background.fill.solid()
    slide.background.fill.fore_color.rgb = color

def tb(slide, l, t, w, h, text, fs=18, bold=False, color=D, align=PP_ALIGN.LEFT):
    box = slide.shapes.add_textbox(Inches(l), Inches(t), Inches(w), Inches(h))
    tf = box.text_frame; tf.word_wrap = True
    p = tf.paragraphs[0]; p.text = text
    p.font.size = Pt(fs); p.font.bold = bold
    p.font.color.rgb = color; p.font.name = 'Microsoft YaHei'; p.alignment = align
    return tf

def line(slide, l, t):
    ln = slide.shapes.add_shape(MSO_SHAPE.RECTANGLE, Inches(l), Inches(t), Inches(1.2), Inches(.05))
    ln.fill.solid(); ln.fill.fore_color.rgb = G; ln.line.fill.background()

def code(slide, l, t, w, h, text, fs=10):
    shape = slide.shapes.add_shape(MSO_SHAPE.ROUNDED_RECTANGLE, Inches(l), Inches(t), Inches(w), Inches(h))
    shape.fill.solid(); shape.fill.fore_color.rgb = CODE_BG; shape.line.fill.background()
    tf = shape.text_frame; tf.word_wrap = True
    tf.margin_left = Inches(.15); tf.margin_right = Inches(.15)
    tf.margin_top = Inches(.12); tf.margin_bottom = Inches(.12)
    lines = text.strip().split('\n')
    for i, ln_text in enumerate(lines):
        p = tf.paragraphs[0] if i == 0 else tf.add_paragraph()
        p.text = ln_text
        p.font.size = Pt(fs); p.font.name = 'Courier New'; p.font.color.rgb = CODE_FG
        p.space_after = Pt(1)
    return tf

def table(slide, l, t, w, h, rows, col_widths=None):
    nr, nc = len(rows), len(rows[0])
    ts = slide.shapes.add_table(nr, nc, Inches(l), Inches(t), Inches(w), Inches(h))
    tbl = ts.table
    if col_widths:
        for i, cw in enumerate(col_widths): tbl.columns[i].width = Inches(cw)
    for r, row in enumerate(rows):
        for c, val in enumerate(row):
            cell = tbl.cell(r, c); cell.text = str(val)
            for p in cell.text_frame.paragraphs:
                p.font.size = Pt(12); p.font.name = 'Microsoft YaHei'
                p.font.bold = (r == 0); p.font.color.rgb = W if r == 0 else D
            cell.fill.solid()
            cell.fill.fore_color.rgb = G if r == 0 else (W if r % 2 == 0 else L)
    return tbl

def ch(title, num, sub=""):
    s = prs.slides.add_slide(prs.slide_layouts[6]); bg(s, DB)
    tb(s, .8, 1.2, 4, 2, num, 96, True, G)
    ln = s.shapes.add_shape(MSO_SHAPE.RECTANGLE, Inches(.8), Inches(3.2), Inches(1.5), Inches(.06))
    ln.fill.solid(); ln.fill.fore_color.rgb = G; ln.line.fill.background()
    tb(s, .8, 3.5, 10, 1.5, title, 38, True, W)
    if sub: tb(s, .8, 4.8, 10, .8, sub, 18, False, RGBColor(0xAA,0xAA,0xAA))
    return s

def box(slide, l, t, w, h, fill, border):
    s = slide.shapes.add_shape(MSO_SHAPE.ROUNDED_RECTANGLE, Inches(l), Inches(t), Inches(w), Inches(h))
    s.fill.solid(); s.fill.fore_color.rgb = fill
    s.line.color.rgb = border; s.line.width = Pt(2)
    return s

# ===== TITLE =====
s = prs.slides.add_slide(prs.slide_layouts[6]); bg(s, DB)
tb(s, 1, 1.5, 11, 1.5, "Spring Boot + Spring Cloud", 48, True, W, PP_ALIGN.CENTER)
tb(s, 1, 3.2, 11, .8, "产品团队技术培训", 28, False, RGBColor(0xCC,0xCC,0xCC), PP_ALIGN.CENTER)
tb(s, 1, 4.3, 11, .6, "Spring Boot 3.x  ·  MyBatis Plus  ·  Spring Cloud", 16, False, G, PP_ALIGN.CENTER)
tb(s, 1, 5.5, 11, .5, "~2h35min  ·  上半场实操 + 下半场理论", 14, False, RGBColor(0x88,0x88,0x88), PP_ALIGN.CENTER)

# ===== AGENDA =====
s = prs.slides.add_slide(prs.slide_layouts[6]); bg(s, W)
tb(s, .8, .4, 11, .8, "课程安排", 30, True); line(s, .8, 1.15)
box(s, .8, 1.8, 5.5, 4.5, LG, RGBColor(0xBB,0xF7,0xD0))
tb(s, 1.2, 2.1, 5, .5, "上半场：Spring Boot 实操", 22, True, G)
tb(s, 1.2, 2.9, 5, 2, "Ch1 演进之路 → Ch2 核心机制+demo1\n→ Ch3 Web层+demo2 → Ch4 数据层+demo3", 14, False, D)
tb(s, 1.2, 5.2, 5, .4, "~95 min", 16, False, GR)
box(s, 7.0, 1.8, 5.5, 4.5, LG, RGBColor(0xBB,0xF7,0xD0))
tb(s, 7.4, 2.1, 5, .5, "下半场：Spring Cloud 理论", 22, True, G)
tb(s, 7.4, 2.9, 5, 2, "Ch5 为什么微服务 → Ch6 六大组件\n→ Ch7 产品视角 → Ch8 总结", 14, False, D)
tb(s, 7.4, 5.2, 5, .4, "~48 min", 16, False, GR)

# ===== CH1: 演进之路 =====
ch("从 Spring MVC 到 Spring Boot", "01", "演进之路 · ~15min")

s = prs.slides.add_slide(prs.slide_layouts[6]); bg(s, W)
tb(s, .8, .4, 11, .8, "1.2 先看结果——demo1-initializer", 28, True); line(s, .8, 1.15)
code(s, .8, 1.6, 11.5, 3.2,
"""// Demo1Application.java：构造器注入 + 启动后自动执行
@SpringBootApplication
public class Demo1Application implements CommandLineRunner {
    private final GreetingService greetingService;
    public Demo1Application(GreetingService greetingService) { this.greetingService = greetingService; }
    public static void main(String[] args) { SpringApplication.run(Demo1Application.class, args); }
    public void run(String... args) { System.out.println(greetingService.greet("产品团队")); }
}
// ChineseGreetingService.java：@Service 标记，Spring 自动发现
@Service public class ChineseGreetingService implements GreetingService {
    public String greet(String name) { return "你好，" + name + "！"; }
}
// LoggingAspect.java：@Aspect，所有 service 方法自动加计时日志
@Aspect @Component public class LoggingAspect {
    @Around("execution(* com.example.demo1.service..*.*(..))")
    public Object log(ProceedingJoinPoint jp) throws Throwable {
        long s=System.currentTimeMillis(); Object r=jp.proceed();
        System.out.println("[AOP] "+jp.getSignature().toShortString()+" 耗时:"+(System.currentTimeMillis()-s)+"ms");
        return r;
    }
}""")
tb(s, .8, 5.2, 11, 1.5, "mvn spring-boot:run → 0.4 秒启动 → IoC 注入 + AOP 日志全在工作。零行 XML。", 18, True, G)

s = prs.slides.add_slide(prs.slide_layouts[6]); bg(s, W)
tb(s, .8, .4, 11, .8, "1.3 对比——传统方式 vs Spring Boot", 28, True); line(s, .8, 1.15)
box(s, .8, 1.8, 5.5, 3.5, RGBColor(0xFF,0xF5,0xF5), RGBColor(0xFE,0xCA,0xCA))
tb(s, 1.1, 2.0, 5, .5, "传统 Spring", 22, True, R)
code(s, 1.1, 2.6, 5, 1.5,
"""<context:component-scan base-package="com.example.service"/>
<context:annotation-config/>""", 11)
tb(s, 1.1, 4.3, 5, .7, "每个项目都要写这两行。包名变化时跟着改。", 14, False, D)
box(s, 7.0, 1.8, 5.5, 3.5, LG, RGBColor(0xBB,0xF7,0xD0))
tb(s, 7.3, 2.0, 5, .5, "Spring Boot (demo1)", 22, True, G)
tb(s, 7.3, 2.8, 5, 2, "0 行 XML。@SpringBootApplication 默认从启动类包开始扫描，@Autowired 自动生效。0.4 秒跑起来。", 16, False, D)
tb(s, .8, 5.8, 11, 1, "web.xml、spring-mvc.xml 那 80+ 行是 Web 层配置——demo1 没有 Web 模块。第三章加了 Web Starter 之后再对比。", 15, False, GR)

s = prs.slides.add_slide(prs.slide_layouts[6]); bg(s, W)
tb(s, .8, .4, 11, .8, "1.4 约定大于配置 & 1.5 Spring Boot 是什么", 28, True); line(s, .8, 1.15)
tb(s, .8, 1.6, 11, 1.2, '配置 = 你告诉框架每一个细节。约定 = 框架预设默认行为，按规矩来就自动生效。\n举例：不写端口配置→8080 已在监听。想改 server.port=9090，不改就默认。', 17, False, D)
table(s, .8, 3.2, 11.5, 2.5, [
    ["痛点", "解法"],
    ["手写 XML 配置", "自动配置——约定好了，不用写"],
    ["6~8 个依赖版本对齐", "Starter 套餐——一个依赖一组功能"],
    ["装 Tomcat 打 WAR 部署", "内嵌 Tomcat——java -jar 直接跑"],
    ["搭环境半天", "Spring Initializr——30 秒生成项目"],
], col_widths=[4, 7.5])
tb(s, .8, 6.0, 11, .8, "Spring Boot = Spring + Spring MVC + 自动配置 + 内嵌服务器。它是 Spring 的自动挡。", 18, True, G)

s = prs.slides.add_slide(prs.slide_layouts[6]); bg(s, W)
tb(s, .8, .4, 11, .8, "1.6 演进全景图", 28, True); line(s, .8, 1.15)
tb(s, 1, 2.5, 11, 1.5, "Java Servlet → Spring Framework → Spring MVC → Spring Boot → Spring Cloud", 24, True, D, PP_ALIGN.CENTER)
tb(s, 1, 4.0, 11, 1, "↑ 上节课                    ↑ 本节课上半场                    ↑ 本节课下半场", 18, False, G, PP_ALIGN.CENTER)
tb(s, 1, 5.5, 11, .8, "Spring Initializr: start.spring.io — Maven + Java 21 + Spring Boot 3.3.x → Generate → 解压 → 直接跑", 16, False, GR, PP_ALIGN.CENTER)

# ===== CH2: 核心机制 + demo1 =====
ch("核心机制 + demo1", "02", "IoC / AOP 层 · ~20min")

s = prs.slides.add_slide(prs.slide_layouts[6]); bg(s, W)
tb(s, .8, .4, 11, .8, "2.1 @SpringBootApplication 三合一", 28, True); line(s, .8, 1.15)
code(s, .8, 1.6, 11.5, 1.2,
"""@SpringBootApplication  // ← 三合一注解
public class Demo1Application {
    public static void main(String[] args) { SpringApplication.run(Demo1Application.class, args); }
}""")
table(s, .8, 3.2, 11.5, 1.8, [
    ["注解", "作用", "上节课"],
    ["@Configuration", "标记配置类，可定义 Bean", "涉及"],
    ["@ComponentScan", "扫描当前包及子包下组件", "涉及"],
    ["@EnableAutoConfiguration", "自动配置——Spring Boot 的灵魂", "本节课新内容"],
], col_widths=[3.5, 4.5, 3.5])

s = prs.slides.add_slide(prs.slide_layouts[6]); bg(s, W)
tb(s, .8, .4, 11, .8, "2.2 自动配置——两条约定", 28, True); line(s, .8, 1.15)
tb(s, .8, 1.6, 11, 1.2, "约定一：默认扫描位置", 22, True, G)
tb(s, .8, 2.8, 11, 1.5, "@ComponentScan 从启动类所在包开始向下扫描所有子包。Demo1Application 在 com.example.demo1，所以 service/、aspect/ 下的 @Service、@Aspect 自动被发现。类放到包外则扫不到，需显式配置。不需要在 XML 里写 <context:component-scan base-package=\"...\"/>。", 16, False, D)
tb(s, .8, 4.3, 11, 1.2, "约定二：注解自动生效", 22, True, G)
tb(s, .8, 5.5, 11, 1.5, "传统 Spring 需要 <context:annotation-config/> 开启注解处理。Spring Boot 自动开启——写 @Autowired 即生效，无需任何 XML 开关。两条约定合在一起：知道去哪扫、扫到怎么处理。开发者只需写注解。", 16, False, D)

s = prs.slides.add_slide(prs.slide_layouts[6]); bg(s, W)
tb(s, .8, .4, 11, .8, "2.2 精装修公寓 & 2.3 Demo1 回顾", 28, True); line(s, .8, 1.15)
box(s, .8, 1.8, 5.5, 3.5, RGBColor(0xFF,0xF5,0xF5), RGBColor(0xFE,0xCA,0xCA))
tb(s, 1.1, 2.0, 5, .5, "传统 Spring = 毛坯房", 20, True, R)
tb(s, 1.1, 2.8, 5, 2, "墙自己刷、电线自己铺、地板自己装\n好处：想怎么装怎么装\n坏处：每个项目从头装一遍", 16, False, D)
box(s, 7.0, 1.8, 5.5, 3.5, LG, RGBColor(0xBB,0xF7,0xD0))
tb(s, 7.3, 2.0, 5, .5, "Spring Boot = 精装修公寓", 20, True, G)
tb(s, 7.3, 2.8, 5, 2, "拎包入住。不满意可以改——\n你的配置优先级高于默认配置。\n条件装配：你需要+没配→我来。", 16, False, D)
tb(s, .8, 5.8, 11, 1, "Demo1 回顾：启动类在 com.example.demo1，@Service 在子包下，自动扫描。构造器注入生效。mvn run → 0.4s，IoC+AOP 全在工作。没有 Web 能力——下一步加 starter-web。", 16, False, D)

# ===== CH3: Web 层 + demo2 =====
ch("Web 层实战 + demo2-web", "03", "承上启下核心章 · ~30min")

s = prs.slides.add_slide(prs.slide_layouts[6]); bg(s, W)
tb(s, .8, .4, 11, .8, "3.1 demo2 多了一个依赖 & 3.2 传统 Spring MVC 的 3 个 XML", 26, True); line(s, .8, 1.15)
code(s, .8, 1.5, 5.5, 1,
"""<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>""")
tb(s, .8, 2.7, 5.5, .5, "这一行 = Tomcat + Spring MVC + JSON", 14, True, G)
tb(s, 7.0, 1.5, 5.5, 1.5, "传统方式需要 3 个 XML 文件 80+ 行配置\n+ 装 Tomcat + 打 WAR + 部署", 16, False, R)
code(s, .8, 3.5, 11.5, 3.5,
"""// web.xml（~30行）：配置 DispatcherServlet
<web-app><servlet><servlet-name>dispatcher</servlet-name>
<servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
<init-param><param-name>contextConfigLocation</param-name>
<param-value>/WEB-INF/spring-mvc.xml</param-value></init-param></servlet>
<servlet-mapping><servlet-name>dispatcher</servlet-name><url-pattern>/</url-pattern></servlet-mapping></web-app>
// applicationContext.xml（~20行）：数据源、事务、扫描
<beans><context:component-scan base-package="com.example"/>
<bean id="dataSource" class="...BasicDataSource"><property name="driverClassName" value="com.mysql.cj.jdbc.Driver"/>
<property name="url" value="jdbc:mysql://localhost:3306/demo"/></bean>
<bean id="txManager" class="...DataSourceTransactionManager"><property name="dataSource" ref="dataSource"/></bean></beans>
// spring-mvc.xml（~30行）：视图解析器、注解驱动
<beans><mvc:annotation-driven/><context:component-scan base-package="com.example.controller"/>
<bean class="...InternalResourceViewResolver"><property name="prefix" value="/WEB-INF/views/"/>
<property name="suffix" value=".jsp"/></bean></beans>""", 9)
tb(s, 7.2, 3.8, 5.5, 1, "而 demo2：1 个 starter-web 依赖\n+ 1 个 @RestController。零行 XML。", 16, True, G)

s = prs.slides.add_slide(prs.slide_layouts[6]); bg(s, W)
tb(s, .8, .4, 11, .8, "3.3 承上启下对照表", 28, True); line(s, .8, 1.15)
table(s, .8, 1.8, 11.5, 3.5, [
    ["上节课（传统 Spring MVC）", "Spring Boot", "还需要吗"],
    ["web.xml 配置 DispatcherServlet", "starter-web 自动配置", "不需要"],
    ["spring-mvc.xml 配置视图解析器、注解驱动", "WebMvcAutoConfiguration 自动配置", "不需要"],
    ["applicationContext.xml 配置组件扫描", "@SpringBootApplication 自带扫描", "不需要"],
    ["写 @Controller + @RequestMapping", "写 @RestController + @GetMapping", "写法一样"],
    ["装 Tomcat → 打 WAR → 部署", "内嵌 Tomcat，java -jar 直接跑", "不需要"],
], col_widths=[4.5, 4.5, 2.5])
tb(s, .8, 5.8, 11, 1, "上节课教的 @Controller 怎么用——照用。web.xml 怎么配——不用了。Starter 已替代三个 XML 文件。", 18, True, G)

s = prs.slides.add_slide(prs.slide_layouts[6]); bg(s, W)
tb(s, .8, .4, 11, .8, "3.4 REST API & 3.5 统一响应 & 3.6 异常处理", 26, True); line(s, .8, 1.15)
code(s, .8, 1.6, 6, 2.5,
"""@RestController  // = @Controller + @ResponseBody
public class GreetingController {
    private final GreetingService greetingService;
    @GetMapping("/api/greeting")
    public Result<String> greet(@RequestParam String name) {
        return Result.success(greetingService.greet(name));
    }
}""", 10)
code(s, 7.2, 1.6, 5.3, 2.5,
"""// 统一响应格式
{"code":200,"message":"操作成功","data":{...}}

// 全局异常处理
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(BusinessException.class)
    public Result<?> handle(BusinessException e) {
        return Result.error(e.getCode(), e.getMessage());
    }
}""", 9)
tb(s, .8, 4.5, 11, 2.5, "@RestController = @Controller + 自动 JSON 序列化。@GetMapping = @RequestMapping 的 GET 限定版。\n统一响应：所有接口返回 {code, message, data}，前端只需判断 code===200。\n统一异常：@RestControllerAdvice 一个注解全局处理异常，Controller 里不需要 try-catch。\n运行：mvn run → Tomcat started on port 8080 → curl 返回 JSON。", 15, False, D)

# ===== CH4: 数据层 + demo3 =====
ch("数据层实战 + demo3-data", "04", "~30min")

s = prs.slides.add_slide(prs.slide_layouts[6]); bg(s, W)
tb(s, .8, .4, 11, .8, "4.2 MyBatis Plus —— 数据层的约定大于配置", 28, True); line(s, .8, 1.15)
code(s, .8, 1.6, 5.8, 3,
"""// Entity —— 一个类对应一张表
@Data @TableName("employee")
public class Employee {
    @TableId(type = IdType.AUTO) private Long id;
    private String name;
    private BigDecimal salary;       // 金额用 BigDecimal
    private LocalDateTime createTime;// 日期用 LocalDateTime
}""", 10)
code(s, 7.0, 1.6, 5.5, 3,
"""// Mapper —— 空接口 = 全套 CRUD
@Mapper
public interface EmployeeMapper
        extends BaseMapper<Employee> {
    // 空接口！继承 BaseMapper 自动拥有：
    // insert、deleteById、updateById
    // selectById、selectList
    // selectPage、selectCount ...
}""", 10)
tb(s, .8, 5.0, 11, 1.5, "传统 MyBatis：每张表约 10 条 SQL，十张表 100 条。MyBatis Plus：继承一个接口，完事。这是数据层的约定大于配置。", 17, True, G)

s = prs.slides.add_slide(prs.slide_layouts[6]); bg(s, W)
tb(s, .8, .4, 11, .8, "4.3 条件查询 & 4.4 配置管理", 28, True); line(s, .8, 1.15)
code(s, .8, 1.6, 7, 2.5,
"""LambdaQueryWrapper<Employee> wrapper = new LambdaQueryWrapper<>();
wrapper.like(name != null, Employee::getName, name)
       .eq(deptId != null, Employee::getDepartmentId, deptId)
       .orderByDesc(Employee::getId);
Page<Employee> page = new Page<>(1, 10);
employeeMapper.selectPage(page, wrapper);
// 返回: records、total、current、pages""", 10)
code(s, 8.2, 1.6, 4.3, 2.5,
"""# application.yml: 5 行
spring:
  datasource:
    url: jdbc:h2:mem:demo3
    username: sa
  sql.init.mode: always
# 换 MySQL 改 3 行，代码不动""", 10)
tb(s, .8, 4.5, 11, 2.5, "LambdaQueryWrapper 链式调用，字段名用方法引用——写错编译器直接报错。分页 count/limit 自动完成。\n5 行 YAML 替代传统 21 行 XML。换 MySQL 改 3 行配置，代码不变。多环境：application-dev.yml / prod.yml，一行切换。", 15, False, D)

s = prs.slides.add_slide(prs.slide_layouts[6]); bg(s, W)
tb(s, .8, .4, 11, .8, "4.6 运行验证 & 4.7 三阶段小结", 28, True); line(s, .8, 1.15)
code(s, .8, 1.6, 6, 3.5,
"""cd demo3-data && mvn spring-boot:run  # < 1秒

curl localhost:8080/api/employees                # 查全部
curl 'localhost:8080/api/employees?current=1&size=5' # 分页
curl 'localhost:8080/api/employees?name=张'      # 模糊搜索
curl -X POST localhost:8080/api/employees \\
  -H 'Content-Type: application/json' \\
  -d '{"name":"新员工","age":26,"deptId":1,"salary":15000}'
curl localhost:8080/api/employees/999
# -> {"code":400,"message":"员工不存在: id=999"}""", 10)
table(s, 7.0, 1.6, 5.5, 2.5, [
    ["", "demo1", "demo2", "demo3"],
    ["新增依赖", "核心 starter", "+ starter-web", "+ mybatis-plus + h2"],
    ["新增文件", "4 个", "+3 个", "+7 个"],
    ["能力", "IoC+AOP", "+ REST API", "+ 完整 CRUD"],
], col_widths=[1.2, 1.5, 1.5, 1.5])
tb(s, .8, 5.5, 11, 1.5, "每个 Starter 添加一种能力。三个依赖、十几个文件，从控制台输出到完整增删改查。核心不是魔法——是约定。", 17, True, G)

# ===== CH5: 为什么微服务 =====
ch("从单体到微服务", "05", "为什么需要 Spring Cloud · ~12min")

s = prs.slides.add_slide(prs.slide_layouts[6]); bg(s, W)
tb(s, .8, .4, 11, .8, "5.1 单体困境 & 5.2 微服务思路", 28, True); line(s, .8, 1.15)
box(s, .8, 1.8, 5.5, 4, RGBColor(0xFF,0xF5,0xF5), RGBColor(0xFE,0xCA,0xCA))
tb(s, 1.1, 2.0, 5, .5, "单体架构的问题", 20, True, R)
tb(s, 1.1, 2.8, 5, 2.5, "• 200 人改一个工程→合并冲突\n• 订单扩容→整个系统部署 N 份\n• 改一行代码→全站重新部署\n• 一个模块挂了→全站崩溃", 16, False, D)
box(s, 7.0, 1.8, 5.5, 4, LG, RGBColor(0xBB,0xF7,0xD0))
tb(s, 7.3, 2.0, 5, .5, "微服务的答案", 20, True, G)
tb(s, 7.3, 2.8, 5, 2.5, "拆分为几十个小工程。每个独立开发、独立部署、独立扩容。\n类比：单体 = 所有人一个大开间\n微服务 = 按部门分楼层，各管各的", 16, False, D)
tb(s, .8, 6.3, 11, .6, "但拆分产生新问题——Spring Cloud 来解决。Spring Boot 让一个服务跑起来，Spring Cloud 让一组服务协作。", 16, True, G)

s = prs.slides.add_slide(prs.slide_layouts[6]); bg(s, W)
tb(s, .8, .4, 11, .8, "5.3 拆分后的四个新问题", 28, True); line(s, .8, 1.15)
qs = [("① 服务发现", "订单服务要调用户服务——怎么知道它在哪台机器？写死 IP 则扩容就炸。"),
      ("② 统一入口", "前端要调多个服务——难道记住所有地址？每个服务各自鉴权？"),
      ("③ 级联故障", "用户服务宕机→订单不停重试→订单也挂→全站崩溃。"),
      ("④ 链路排查", "一个请求跨 5 个服务——哪一步慢了？日志分散，拼不出全链路。")]
for i, (title, desc) in enumerate(qs):
    y = 1.6 + i * 1.3
    tb(s, 1.2, y, 10, .5, title, 20, True, G)
    tb(s, 1.2, y + .5, 10, .5, desc, 15, False, D)

# ===== CH6: 六大组件 =====
ch("Spring Cloud 六大组件", "06", "~28min · 贯穿比喻：外卖平台")

comps = [
    ("📒", "Nacos 注册中心", "配送调度中心",
     "用户下单→订单服务通知配送服务找骑手。配送服务跑在 3 台机器上，订单服务怎么知道该找哪台？",
     "配送服务启动时向 Nacos 登记地址。订单服务去 Nacos 查询可用实例。宕机自动剔除，新增自动加入。",
     "骑手上线打卡→平台知道谁在线。下单→自动分配空闲骑手。"),
    ("🏢", "Gateway 网关", "外卖 App 统一后台",
     "App 首页调商家服务、购物车调订单服务、结算调支付服务。让 App 记多个地址？每个服务各自鉴权？",
     "App 只与 Gateway 通信。Gateway 按路径转发到对应服务。鉴权统一完成，没 token 直接拦截。",
     "你不会直接连商家数据库和支付系统——只跟 App 后台交互。Gateway = 统一后台。"),
    ("☁️", "Nacos Config 配置中心", "商家后台改价",
     "配送费规则调整。订单服务和配送服务都在用。需要改两个配置、重启两个服务。漏改一个线上不一致。",
     "规则统一放配置中心。运营改一次，所有服务自动推送，不重启生效。",
     "商家后台改菜价→App 立刻显示新价格，用户无需更新 App。"),
    ("📞", "Feign 服务调用", "一键支付",
     "提交订单后依次调用户服务、商家服务、支付服务。每次手写 HTTP 调用十几行，重复数十遍。",
     "声明接口 + @FeignClient 注解 = 自动发 HTTP。像调本地方法一样调远程服务。",
     "App 点微信支付→自动调起微信。不用自己打开微信输金额。"),
    ("⚡", "Sentinel 熔断降级", "故障自动兜底",
     "支付服务宕机。订单不停重试→1000 请求阻塞→订单也挂→商家也挂→级联故障全站崩溃。",
     "监控调用成功率。连续失败→熔断（切断）+降级（货到付款）。恢复后自动切回在线支付。",
     "一家店停电关门→平台暂时下架，推荐替代店铺。用户不会点进去才发现点不了。"),
    ("📦", "Sleuth/Zipkin 链路追踪", "外卖物流追踪",
     "用户投诉下单等了 10 秒。请求跨 5 个服务——哪个环节耗时过长？日志分散，拼不出全链路。",
     "每个请求分配全局唯一 TraceID（快递单号），在服务间传递。Zipkin 拼接完整调用树，每步耗时可见。",
     "快递物流：到达分拨中心 10:00，离开 18:00——卡了 8 小时。破案。"),
]

for emoji, name, metaphor, problem, solution, analogy in comps:
    s = prs.slides.add_slide(prs.slide_layouts[6]); bg(s, W)
    box(s, .8, .8, 5.5, 5.5, LG, RGBColor(0xBB,0xF7,0xD0))
    tb(s, 1.2, 1.2, 5, 1, emoji, 42, False, D, PP_ALIGN.CENTER)
    tb(s, 1.2, 2.4, 5, .6, name, 22, True, G, PP_ALIGN.CENTER)
    tb(s, 1.2, 3.1, 5, .5, f"「{metaphor}」", 18, False, D, PP_ALIGN.CENTER)
    tb(s, 1.2, 3.9, 5, 2.2, analogy, 14, False, GR, PP_ALIGN.CENTER)
    tb(s, 7.0, 1.2, 5.5, .5, "问题", 20, True, R)
    tb(s, 7.0, 1.9, 5.5, 2, problem, 15, False, D)
    tb(s, 7.0, 4.2, 5.5, .5, "解法", 20, True, G)
    tb(s, 7.0, 4.9, 5.5, 2, solution, 15, False, D)

s = prs.slides.add_slide(prs.slide_layouts[6]); bg(s, W)
tb(s, .8, .4, 11, .8, "6.7 六大组件速查", 28, True); line(s, .8, 1.15)
summary = [("外卖场景", "组件", "作用"),
    ("骑手上线打卡，平台自动分配", "Nacos", "配送调度中心"),
    ("App 统一后台，不管后面几个服务", "Gateway", "外卖 App 后台"),
    ("商家改配送费，App 立刻生效", "Nacos Config", "商家后台改价"),
    ("一键支付，不用自己打开微信", "Feign", "一键支付"),
    ("支付宕机，自动切换货到付款", "Sentinel", "故障自动兜底"),
    ("下单慢了，看哪个环节卡顿", "Sleuth/Zipkin", "外卖物流追踪")]
table(s, .8, 1.8, 11.5, 4.8, summary, col_widths=[4.5, 3, 4])

# ===== CH7: 产品视角 =====
ch("产品视角", "07", "微服务不是免费的午餐 · ~8min")

s = prs.slides.add_slide(prs.slide_layouts[6]); bg(s, W)
tb(s, .8, .4, 11, .8, "7.1 代价 & 7.2 拆分前三问", 28, True); line(s, .8, 1.15)
table(s, .8, 1.6, 11.5, 2, [
    ["收益", "成本"],
    ["独立部署", "运维复杂度 × N。日志、监控、部署流水线从 1 变 20"],
    ["独立扩容", "分布式事务复杂度增加。原来一个事务→需协调多个服务"],
    ["团队自治", "网络调用引入延迟。本地调用 1ms→HTTP 调用 10ms 起步"],
], col_widths=[3, 8.5])
qs2 = [("① 有独立的业务边界吗？", "不是功能多就该拆。订单和订单详情属同一边界，拆分增加耦合。"),
       ("② 需要独立扩容吗？", "双十一仅订单要加机器——才值得拆。没有独立扩容需求的模块，拆分没核心收益。"),
       ("③ 有独立团队维护吗？", "一个团队管 3 个微服务 = 分布式单体，比单体更复杂却没得到好处。")]
for i, (q, detail) in enumerate(qs2):
    y = 3.8 + i * 1.2
    tb(s, 1.2, y, 10, .5, q, 20, True, G)
    tb(s, 1.2, y + .5, 10, .5, detail, 15, False, GR)
tb(s, .8, 7.0, 11, .4, "每拆分一个服务，运维成本就增加一份。拆分之前，先回答这三个问题。", 16, True, G, PP_ALIGN.CENTER)

# ===== CH8: 总结 =====
ch("课程总结 + Q&A", "08", "~10min")

s = prs.slides.add_slide(prs.slide_layouts[6]); bg(s, W)
tb(s, .8, .4, 11, .8, "今天的内容 & 三个关键结论", 28, True); line(s, .8, 1.15)
table(s, .8, 1.6, 11.5, 1.8, [
    ["模块", "核心"],
    ["Spring Boot", "自动挡。约定大于配置。上节课学的全部有效，只是不需要 XML"],
    ["三阶段实战", "demo1(IoC/AOP) → demo2(Web+承上启下) → demo3(数据+CRUD)"],
    ["Spring Cloud", "六大组件——六个外卖平台场景"],
], col_widths=[3, 8.5])
cards_data = [
    ("🚗", "Spring Boot 是自动挡", "@Controller 照用，web.xml 不用了\n一个注解替代三个 XML"),
    ("🔌", "Starter 加一种能力", "加依赖即获得能力，约定已做好配置\ndemo1→demo2→demo3"),
    ("🏗️", "Cloud 六道保险", "配送调度·统一后台·配置改价\n一键支付·故障兜底·物流追踪"),
]
for i, (icon, title, desc) in enumerate(cards_data):
    x = .8 + i * 4.1
    box(s, x, 3.8, 3.8, 2.8, W, RGBColor(0xE0,0xE0,0xE0))
    tb(s, x+.3, 4.0, 3.2, .8, icon, 32, False, D, PP_ALIGN.CENTER)
    tb(s, x+.3, 4.8, 3.2, .6, title, 16, True, D, PP_ALIGN.CENTER)
    tb(s, x+.3, 5.4, 3.2, 1, desc, 13, False, GR, PP_ALIGN.CENTER)

s = prs.slides.add_slide(prs.slide_layouts[6]); bg(s, W)
tb(s, .8, .4, 11, .8, "课后资料", 28, True, D, PP_ALIGN.CENTER); line(s, 6, 1.15)
tb(s, 2, 2.0, 9, 4, "Demo 源码：demo1-initializer/  demo2-web/  demo3-data/\n启动命令：cd demoX && mvn spring-boot:run\n大纲与逐字稿：docs/springboot-teaching-outline.md\nSpring Initializr：https://start.spring.io", 20, False, D, PP_ALIGN.CENTER)

s = prs.slides.add_slide(prs.slide_layouts[6]); bg(s, DB)
tb(s, 1, 2.5, 11, 1.5, "Q & A", 64, True, W, PP_ALIGN.CENTER)
tb(s, 1, 4.5, 11, .8, "有什么问题？", 28, False, RGBColor(0xCC,0xCC,0xCC), PP_ALIGN.CENTER)

# Save
out = '/Users/lihg/MyOpenCodeProject/springboot-learning/docs/slides.pptx'
prs.save(out)
print(f"Saved: {out} ({len(prs.slides)} slides)")
