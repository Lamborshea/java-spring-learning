# L06: Spring Boot 入门

> 第一个 Spring Boot 项目，理解项目结构和启动流程

## 本节目标

- 理解 Spring Boot 是什么、解决了什么问题
- 掌握标准项目结构
- 能用 Spring Initializr 创建一个项目
- 成功启动第一个 Spring Boot Web 应用

## 学习问答（Q&A）

> 以下问题来自真实学习过程中的疑问，附对应代码位置。

### Q1: "约定优于配置"在现实生活中有什么可以类比的？

本质：**用普遍默认的共识替代逐个决策，只在偏离共识时才需要声明**。生活类比：

- **交通规则**：默认靠右行驶，没人出发前"配置"方向；只有单行道、环岛这类特殊情况才有额外标志（= 配置项）
- **餐厅套餐**："商务套餐 = 一荤一素一汤+米饭"，不用逐个点菜；想换饮料才需额外说（覆盖配置）
- **酒店标准间**：床、牙刷、热水壶全行业有"默认房间"共识，90% 需求被默认满足，只有"要高楼层"这种偏离才需备注
- **项目目录结构**：看到 `src/`、`README.md`、`.gitignore` 就知道去哪找代码——约定形成后新人上手成本趋近于零
- **红绿灯**："默认停下"是约定，绿灯是例外放行；若每个路口都要交警现场配置规则（纯配置），效率归零

对应 Spring Boot：默认内嵌 Tomcat、默认扫描启动类所在包 ≈ 默认靠右行驶；`spring-boot-starter-web` 一键带来 MVC+Jackson+Tomcat ≈ 套餐默认配菜；`application.yml` 只写你要偏离的项 ≈ 特殊路况标志牌。反面典型是老式 Spring XML 时代——像每条规则都要跟交警现场谈判的路口，80% 的配置是重复劳动。

代价提醒：CoC 放弃了"默认之外的自由"（套餐便宜但不好挑食）。最好的系统是**约定打底 + 例外可配置**。

### Q2: 请求是怎么走到 `DemoController` 的？

关键在 `@SpringBootApplication` 的**组件扫描**，分两个阶段：

**启动阶段：**

```
main() → SpringApplication.run(HelloSpringBootApplication.class)
  ├─ 1. @SpringBootApplication 内含 @ComponentScan
  │      默认扫描"启动类所在包及子包" → com.learning.l06.**
  │      DemoController 正好在同包 → 被扫到
  ├─ 2. 看到 @RestController → 创建其 Bean 放入 IoC 容器
  ├─ 3. RequestMappingHandlerMapping 收集 @RequestMapping("/api/demo")
  │      + @GetMapping("/greet") → 注册路由表：GET /api/demo/greet → greet()
  └─ 4. 启动内嵌 Tomcat 监听 8080，所有请求统一交给 DispatcherServlet
```

**请求阶段：**

```
GET /api/demo/greet?name=Java
  → Tomcat 接收 → DispatcherServlet（前端控制器）
  → HandlerMapping 查路由表命中
  → @RequestParam 解析参数（缺省用 defaultValue="World"）
  → 反射调用容器里 DemoController Bean 的 greet("Java")
  → 返回 Map → Jackson 序列化成 JSON 响应
```

类比前端：`@ComponentScan` 像 Vite 的 glob 自动导入，路由表像 vue-router 的 `routes`，`DispatcherServlet` 是唯一入口的路由分发器。

两个要点：

1. **DemoController 能被发现纯属"同包"**。若把 Controller 放到启动类包之外（如 `com.other`）就会 404——新手最常见的坑。后续课程把它拆进 controller/service/repository **子包**，仍在扫描范围内，所以安全。
2. **本启动类自己也标了 `@RestController`**，同时承担"应用入口 + /hello、/info 接口"两个身份。最小示例可以这样，真实项目入口类从不写业务接口（L07+ 分层会纠正）。

### Q3: `mvn spring-boot:run` 是如何标记和识别，最终执行启动代码的？

分三段：**命令找插件 → 插件找 main 类 → fork JVM 执行**。

1. **解析命令**：`mvn` 命令格式是 `前缀:目标`。`spring-boot` 前缀在 pom 的 `<build><plugins>` 里匹配到 `spring-boot-maven-plugin`（去掉 `-maven-plugin` 后缀得前缀），于是执行其 `RunMojo`。
2. **识别启动类**（优先级从高到低）：
   - pom 里显式配置的 `<mainClass>`
   - 扫描 `target/classes` 字节码，找带 `public static void main(String[])` 的类（优先标了 `@SpringBootApplication` 的）。本课只有 `HelloSpringBootApplication` 有 main，唯一命中；**若两个类都有 main 且未配置，构建直接报错**，不是玄学自动选择
   - 打成 fat jar 后读 `META-INF/MANIFEST.MF` 的 `Start-Class`
3. **执行**：先编译（process-classes），再用 Maven 依赖树算出完整 classpath（`target/classes` + 全部传递依赖），最后 **fork 新 JVM 进程**：`java -cp <classpath> com.learning.l06.HelloSpringBootApplication` → 进入 `SpringApplication.run()` 链路。

`spring-boot:run` 本质就是"帮你用正确的 classpath 敲了那条 java 命令"，与 IDE 点 run、`java -jar` 殊途同归。类比：`spring-boot:run` ≈ `vite dev`，`repackage` ≈ `vite build` 产出 dist。细节：fork 的是独立进程，改代码**不会自动重启**——这是 L10 的 `spring-boot-devtools` 存在的理由。

### Q4: Spring Boot 的"自动配置"具体自动配了什么？

本质：**根据你引入了哪些依赖，反推你需要哪些 Bean**。机制：`@EnableAutoConfiguration` 读取各 starter jar 的 `META-INF/spring/...AutoConfiguration.imports` 清单，逐个加载配置类，每个都带条件注解（`@ConditionalOnClass` 有该类才生效、`@ConditionalOnMissingBean` 你没配我才配）。

三个例子：

1. **Web 层**（`spring-boot-starter-web`）：注册 `DispatcherServlet`、创建内嵌 Tomcat（默认 8080）、注册 Jackson 消息转换器——所以 Controller 返回 `Map` 自动变 JSON，一行配置没写
2. **数据层**（`starter-data-jpa` + 驱动）：按 `spring.datasource.*` 创建 HikariCP 连接池、`EntityManagerFactory`、事务管理器、自动识别数据库方言——老 Spring 时代要手写几十行 XML
3. **横切能力**：`JacksonAutoConfiguration` 配好 `ObjectMapper`（日期 ISO-8601 等默认值，一行 yml 即可覆盖）；`WebMvcAutoConfiguration` 把 `classpath:/static/` 注册为静态资源目录

亲眼验证：启动加 `--debug` → 打印 CONDITIONS EVALUATION REPORT，列出每个自动配置类生效/被排除的原因。**关键认知：自动配置从不覆盖你的手动配置**，随时可声明同名 Bean 夺回控制权；它配的全是"合理默认值"，可逐条追溯到源码。

### Q5: 为什么 Spring Boot 要"内嵌"Tomcat，而不是要求单独装？

本质是部署模型变迁：**应用自带服务器，一个 jar 就是完整可执行单元**。四个动机：

1. **环境一致性**：Tomcat 版本锁死在 pom 依赖树里，随代码构建、版本控制，开发/测试/生产跑字节级相同的产物——消灭"我机器上是好的"。类比 npm：服务器是依赖，不是环境
2. **部署成本**：从"装 JDK→装 Tomcat→改配置→丢 war→启动"变成 `java -jar` 一步；Docker 镜像只需 JDK + jar
3. **微服务粒度**：50 个服务各带一个外部 Tomcat 就是 50 份运维负担；内嵌后"Tomcat"退化成库，每个进程自给自足
4. **可编程控制**：服务器成为代码里的对象，才能随机端口测试、代码调优线程数、一行依赖换 Jetty

代价（内嵌不是纯赢）：每个进程各带一套容器（几十个服务时内存开销大）、失去"统一升级 Tomcat"抓手（安全补丁须随每个应用重新构建）、老派 Java EE 环境仍要求 war。Boot 保留了 `<packaging>war</packaging>` 退路，但新项目 99% 用不到——这正是 jar 被立为默认的理由。

## 运行方式

```bash
cd L06-spring-boot-intro
mvn spring-boot:run

# 浏览器访问
# http://localhost:8080/hello
# http://localhost:8080/info
# http://localhost:8080/api/demo/greet?name=Lambor
```

## 核心概念

### 1. Spring Boot 是什么？

Spring Boot 是 Spring 框架的"脚手架"工具，把"繁琐配置"变成"约定优于配置"。
- **自动配置**：根据依赖猜测你想要的配置
- **内嵌服务器**：自带 Tomcat，不用单独部署
- **starter 依赖**：一组依赖打包好，一行加进去

### 2. 对比前端

| 前端 | Spring Boot |
|------|-------------|
| `create-vite` 脚手架 | `start.spring.io` 脚手架 |
| `package.json` | `pom.xml` |
| `npm install` 一键装依赖 | Maven 依赖管理 |
| `vite dev` 启动开发服务器 | `mvn spring-boot:run` |
| 部署到 Vercel/Netlify | 打包 jar 直接 `java -jar` |

## 代码说明

- `HelloSpringBootApplication.java` - 主类，包含 main 入口
- `DemoController.java` - 演示一个简单的 REST 接口

## 下节课预告

L07: IoC 容器与依赖注入
