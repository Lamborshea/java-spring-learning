# Java Spring Boot 学习课程体系

> 从 JavaScript 开发者角度出发，面向 Spring Boot Web 开发

## 🎯 学习入口

**👉 [打开课程总览 INDEX.html](./INDEX.html)**（22 节课程进度面板 + 一键跳转）

或者直接阅读 [学习路线图](./SpringBoot学习路线图.md)（12 周计划）

## 课程目标

帮助有经验的 JavaScript 开发者快速掌握 Java Spring Boot 开发能力，能够独立构建 Web 项目。

**3 个月后能做什么**：

- ✅ 独立搭建 Spring Boot 项目
- ✅ 设计并实现 RESTful API（含参数校验、统一响应）
- ✅ 用 JPA / MyBatis 完成数据库 CRUD
- ✅ 用 Spring Security + JWT 实现鉴权
- ✅ Docker 化部署上线
- ✅ 应对 Java 后端面试

## 学习路径（22 节）

```
阶段一: Java 基础（JS 开发者视角）         5 节
├── L01: 开发环境搭建
├── L02: 基础语法与数据类型
├── L03: 面向对象编程
├── L04: 集合与泛型
└── L05: 函数式编程（Stream API）

阶段二: Spring Boot 核心                   5 节
├── L06: Spring Boot 入门
├── L07: IoC 容器与依赖注入
├── L08: 配置管理
├── L09: 日志实战
└── L10: 单元测试

阶段三: Web 开发                           5 节
├── L11: RESTful API 设计
├── L12: 请求参数处理
├── L13: 响应统一封装
├── L14: 异常处理
└── L15: 参数校验

阶段四: 数据访问                           3 节
├── L16: Spring Data JPA 入门
├── L17: 实体映射与关系
└── L18: 事务管理

阶段五: 进阶功能                           4 节
├── L19: 异步编程
├── L20: 缓存
├── L21: Spring Security + JWT
└── L22: API 文档自动生成

阶段六: 综合项目                           1 个
└── spring-boot-webapp: 任务管理系统（综合实战）
```

## 每个课程的目录结构

```
L01-environment/
├── README.md          ← 速查笔记
├── lesson.html        ← HTML 教程（双击浏览器打开）
├── pom.xml
└── src/
    └── main/
        └── java/
            └── com/
                └── learning/
                    └── l01/
                        └── HelloWorld.java
```

## 快速开始

```bash
# 1. 克隆项目
git clone https://github.com/your-repo/java-spring-learning.git
cd java-spring-learning

# 2. 安装 JDK 17+
# Mac: brew install openjdk@17
# Windows: winget install Oracle.JDK.17

# 3. 验证环境
java -version
mvn -version

# 4. 学习方式
# - 浏览器打开 INDEX.html 看课程列表
# - 点击进入某一节的 lesson.html
# - 按教程指引运行对应课程目录下的代码
```

## 环境要求

- **JDK 17+**（LTS 版本）
- **Maven 3.8+**
- **IntelliJ IDEA**（推荐社区版）或 VS Code
- 浏览器（Chrome / Edge / Safari）

## 推荐学习方式

1. **打开 [INDEX.html](./INDEX.html)**，看课程全貌
2. **进入 L01**，先看 `lesson.html` 教程（HTML 格式，可打印）
3. **运行代码**：在 IDEA 中打开 `L##-course-name` 目录
4. **做动手练习**：每节末尾有练习题
5. **回答思考题**：内化知识
6. **遇到问题**：直接问我（你的 Java 私教）

## JS vs Java 对照速查

| 概念 | JavaScript | Java |
|------|------------|------|
| 变量声明 | `let x = 1` | `int x = 1`（必须声明类型） |
| 字符串 | `'hello'` / `"hello"` | `"hello"`（只有双引号） |
| 数组 | `let arr = [1, 2, 3]` | `int[] arr = {1, 2, 3}` 或 `List<Integer>` |
| 对象 | `{ name: 'Tom', age: 20 }` | `class Person { String name; int age; }` |
| 函数 | `const add = (a, b) => a + b` | `BinaryOperator<Integer> add = (a, b) -> a + b;` |
| 类定义 | `class Dog extends Animal {}` | `class Dog extends Animal {}`（更严格） |
| 接口 | TypeScript 有 `interface` | `interface Drawable { void draw(); }` |
| 包管理 | npm / yarn | Maven / Gradle |
| 异步 | `async/await` / Promise | 多线程 / CompletableFuture / @Async |
| 空值 | `null` / `undefined` | `null` |
| 类型检查 | 运行时（动态） | 编译时（静态） |

## 前端工程师类比速查（Spring Boot 部分）

| 前端概念 | Spring Boot 对应概念 |
|----------|---------------------|
| `npm` / `yarn` | Maven / Gradle |
| `package.json` | `pom.xml` |
| Express / Koa | Spring MVC |
| 中间件（middleware） | 过滤器（Filter） / 拦截器（Interceptor） |
| `req.body` / `req.params` | `@RequestBody` / `@PathVariable` |
| Sequelize / Prisma (ORM) | JPA / MyBatis |
| JWT（前端使用） | Spring Security + JWT（后端签发验证） |
| `.env` 配置文件 | `application.yml` / `application.properties` |
| ESLint 校验 | `@Valid` + Bean Validation |
| Express error handler | `@ControllerAdvice` + `@ExceptionHandler` |
| PM2 部署 | Docker / `java -jar` |

## 配套资源

- [Java 官方文档](https://docs.oracle.com/en/java/javase/17/)
- [Spring Boot 官方文档](https://spring.io/projects/spring-boot)
- [Baeldung Java 教程](https://www.baeldung.com/)
- [Spring Initializr](https://start.spring.io/) - 项目脚手架
- [完整资源列表](./RESOURCES.md)

## 工作区文件

- [MISSION.md](./MISSION.md) - 学习目标与成功标准
- [NOTES.md](./NOTES.md) - 学习偏好与决策记录
- [RESOURCES.md](./RESOURCES.md) - 高质量学习资源
- [SpringBoot学习路线图.md](./SpringBoot学习路线图.md) - 12 周详细计划

---

**开始学习**: [L01: 开发环境搭建](./L01-environment/lesson.html) 🚀
