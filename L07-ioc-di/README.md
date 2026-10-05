# L07: IoC 容器与依赖注入

> 理解 Spring 的核心思想：控制反转与依赖注入

## 本节课目标

- 理解 IoC（控制反转）的概念
- 掌握 DI（依赖注入）的几种方式
- 学会使用 @Component、@Service、@Repository
- 理解 Bean 的生命周期

## 学习问答（Q&A）

> 以下问题来自真实学习过程中的疑问，附对应代码位置。

### Q1: `@Bean` 和 `@Scope` 注解的编写有先后顺序吗？

没有要求，**谁写在上面完全等价**（`AppConfig.java` 第 24-25 行）。原因：

- 语法层面：Java 规范里注解属于修饰符序列，修饰符之间顺序自由（`public static` 与 `static public` 都合法）
- Spring 层面：反射读取方法上的注解**集合**，按注解类型查找而非位置查找，只关心"有没有 @Bean、有没有 @Scope"

唯一会报错的情况：同一位置重复**同类型**注解（非 `@Repeatable` 注解）。顺序纯属风格问题，惯例是"核心注解在前"——`@Bean` 定义"这是什么"，`@Scope` 是附加属性。类比 HTML 属性顺序：解析器按名字取值，不按出现位置。

### Q2: `NotificationService.java` 在这节课里有什么作用？

角色：**"被 @Bean 方法收编的普通类"的教具**。本课文件分两组对照：

- 组A 注解自注册：`UserRepository`(@Repository)、`UserService`(@Service)——类自己贴 Spring 注解，靠扫描发现
- 组B 配置类注册：`AppConfig`(@Configuration + @Bean) + `NotificationService`——**自身零 Spring 依赖的纯 POJO**，注册逻辑写在第三方配置类里

三个教学点：

1. **@Bean 是唯一注册手段的场景**：`RestTemplate`、`DataSource`、第三方 SDK 客户端——改不了源码没法贴 @Service，只能 @Bean 注册
2. **同一类注册多个实例**：`new NotificationService("email")` 和 `("sms")` 两个 Bean，靠方法名区分名字——@Service 做不到
3. **演示作用域差异**：@Scope("prototype") 挂在 smsNotification 上，启动后控制台能看到两个 Bean 都注册进了容器

一句话：它是"依赖倒置"的活教材——IoC 容器不只收纳贴注解的类，任何对象都能经 @Bean 之手成为受管组件。类比：@Service 像组件自声明，@Bean 像 `main.ts` 里 `app.provide('http', axios.create(...))`——axios 自己不知道 Vue 的存在。

### Q3: 怎么理解 Bean 这个概念？

**Bean = 交给 Spring 容器创建和管理的对象**。你 `new` 出来的不叫 Bean，容器"造"出来的才叫。

- 没有 Bean 的世界：自己 new、自己传递、自己保证唯一——对象一多就是意大利面条
- Bean 的世界：你只做两件事——**登记**（@Service/@Bean）和**声明需求**（构造参数），容器负责实例化、默认单例保管、按类型装配（DI）

```java
@Service                                        // ① 登记
public class UserService {
    public UserService(UserRepository repo) {}  // ② 声明需求，容器从仓库找现成的塞进来
}
```

`context.getBean(UserService.class)` 不是 new，是"向容器要一个保管好的实例"。

类比 npm：登记 Bean ≈ package.json 声明依赖；容器 ≈ node_modules + 解析器；注入 ≈ import 时解析器自动匹配版本。区别是 Spring 的依赖是运行期的活对象。

易混澄清：JavaBeans 规范的"JavaBean"（get/set 命名的 POJO）和"Spring Bean"（被容器托管的身份）是两回事，本课语境都是后者。

### Q4: `@Configuration` 注解是干什么用的？

声明**这个类是 Bean 定义源**。干两件事：

1. 自己先成为 Bean（元注解含 @Component，能被扫描到）——这是其 @Bean 方法被容器执行的前提
2. **核心魔法：把类 CGLIB 代理化，保证 @Bean 方法互调不破坏单例**

```java
@Bean
public SomeConsumer consumer() {
    return new SomeConsumer(email());  // 直接调用另一个 @Bean 方法？
}
```

- 有 @Configuration：调用被代理拦截 → 返回容器里的**已有单例** ✓
- 没有（lite 模式）：普通方法调用 → **真的再 new 一个**，内存出现两个实例 ✗

由此推出边界：随手 `new AppConfig().email()` 拿到的是裸对象，与容器无关——"Bean 身份"只在容器执行方法的流程中生效。进阶：`@Configuration(proxyBeanMethods = false)` 关掉代理（Boot 内部 AutoConfiguration 大量使用，无互调时代理纯属开销），业务配置类保持默认即可。类比：像 `nuxt.config.ts`——文件不跑业务，框架加载它、执行定义、产出进入运行体系，且只执行一次。

### Q5: `@Repository` 的作用是什么？

三层作用（从浅到深）：

1. **让类成为 Bean**：它是 @Component 的变体，被扫描进容器——这是 `UserService` 构造注入能拿到它的前提
2. **表达架构分层**：@Repository=数据访问层、@Service=业务层、@Controller=接口层。写 @Component 也能跑，但用对标签代码一眼辨层、架构测试工具可检查分层依赖
3. **异常翻译（@Repository 独有的技术差异）**：类中方法抛出的数据库原生异常（如 `SQLIntegrityConstraintViolationException`）被自动转译为 Spring 统一的 `DataAccessException` 体系（如 `DuplicateKeyException`）——上层 catch 统一异常族，换数据库不用改异常处理。本课用内存 List 模拟数据库看不到此能力，L16 接真数据库时才会体会

易混点（为 L16 铺路）：本课是**手写类 + @Repository 标签**；Spring Data JPA 里是 `interface UserRepository extends JpaRepository<User, Long>`——只声明接口，运行期动态生成实现，连 @Repository 都不用写。名字相同、思想一致（隔离数据访问），实现方式完全不同。

类比：@Repository 像快递的"易碎品"标签——你照样自己打包（写代码），但标签让处理流程对你启用特殊通道（异常翻译）。

## 核心概念

### 1. 什么是 IoC？

**传统方式**（主动创建依赖）：
```javascript
// JS/Node.js 中
class UserService {
    constructor() {
        this.userRepository = new UserRepository();  // 主动创建
    }
}
```

**IoC 方式**（被动接收依赖）：
```java
// Java Spring 中
@Service
public class UserService {
    private final UserRepository userRepository;  // 由容器注入

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;  // 注入
    }
}
```

### 2. 依赖注入方式

| 方式 | 说明 | 示例 |
|------|------|------|
| 构造方法注入 | 推荐，final 不可变 | `@RequiredArgsConstructor` |
| Setter 注入 | 可选，灵活 | `@Autowired setXxx()` |
| 字段注入 | 简洁但不推荐 | `@Autowired private Xxx xxx` |

### 3. Bean 作用域

| 作用域 | 说明 |
|--------|------|
| singleton | 默认，整个应用只有一个实例 |
| prototype | 每次获取创建新实例 |
| request | 每个 HTTP 请求一个实例 |
| session | 每个 HTTP 会话一个实例 |

## 运行方式

由于这是 Spring Boot 项目，需要使用：

```bash
# 在 spring-boot-webapp 目录下已有完整示例
# 启动应用后访问 http://localhost:8080/api/hello
mvn spring-boot:run
```

## 本课代码说明

本节课的代码已经集成到 `spring-boot-webapp` 项目中：

- `UserService` - 演示构造方法注入
- `UserRepository` - 演示 @Repository
- 启动 `WebAppApplication` 即可看到效果

## 下节课预告

L08: 配置管理 - application.yml、环境配置、@Value、@ConfigurationProperties
