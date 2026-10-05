# Spring Boot Web 应用

> 一个完整的 Spring Boot Web 项目模板，用于学习

## 项目结构

```
spring-boot-webapp/
├── pom.xml                      # Maven 依赖配置
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── learning/
│   │   │           └── webapp/
│   │   │               ├── WebAppApplication.java    # 启动类
│   │   │               ├── controller/               # Controller 层
│   │   │               │   ├── HelloController.java
│   │   │               │   └── UserController.java
│   │   │               ├── service/                  # Service 层
│   │   │               │   └── UserService.java
│   │   │               ├── repository/               # Repository 层
│   │   │               │   └── UserRepository.java
│   │   │               ├── entity/                   # 实体类
│   │   │               │   └── User.java
│   │   │               ├── dto/                      # 数据传输对象
│   │   │               │   └── ApiResponse.java
│   │   │               ├── exception/                # 异常处理
│   │   │               │   └── GlobalExceptionHandler.java
│   │   │               └── config/                  # 配置类
│   │   └── resources/
│   │       └── application.yml               # 应用配置
│   └── test/
│       └── java/.../UserControllerTest.java  # 单元测试
```

## 快速开始

### 1. 启动应用

```bash
cd spring-boot-webapp

# 方式一：Maven 运行
mvn spring-boot:run

# 方式二：打包后运行
mvn package
java -jar target/spring-boot-webapp-1.0.0.jar

# 方式三：IDEA 中运行
# 右键 WebAppApplication.java -> Run
```

### 2. 访问 API

应用启动后，访问 http://localhost:8080

#### 2.1 Hello 接口

```bash
# 基础问候
curl http://localhost:8080/api/hello

# 带路径参数
curl http://localhost:8080/api/hello/John

# 带查询参数
curl "http://localhost:8080/api/greet?name=John&age=25"
```

#### 2.2 用户管理接口

```bash
# 创建用户
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{"username": "john", "email": "john@example.com"}'

# 查询所有用户
curl http://localhost:8080/api/users

# 根据 ID 查询
curl http://localhost:8080/api/users/1

# 更新用户
curl -X PUT http://localhost:8080/api/users/1 \
  -H "Content-Type: application/json" \
  -d '{"email": "newemail@example.com"}'

# 删除用户
curl -X DELETE http://localhost:8080/api/users/1

# 统计用户数
curl http://localhost:8080/api/users/count
```

#### 2.3 H2 控制台

开发时可直接访问 H2 内存数据库：
- URL: http://localhost:8080/h2-console
- JDBC URL: `jdbc:h2:mem:webapp_db`
- Username: `sa`
- Password: (空)

## 架构说明

### 分层架构

```
┌─────────────────────────────────────────┐
│           Controller (层)               │
│  处理 HTTP 请求，返回 JSON 响应          │
├─────────────────────────────────────────┤
│            Service (层)                 │
│  业务逻辑处理，事务管理                  │
├─────────────────────────────────────────┤
│          Repository (层)                │
│  数据访问，数据库操作                    │
├─────────────────────────────────────────┤
│            Entity (层)                  │
│  数据库实体映射                          │
└─────────────────────────────────────────┘
```

### JS 对比

| Java Spring Boot | Node.js |
|-----------------|---------|
| @RestController | Express Router |
| @Service | Service 类 |
| @Repository | Mongoose Model |
| @Entity | Mongoose Schema |
| JPA/Hibernate | Mongoose |
| H2/MySQL | MongoDB/MySQL |

## 技术栈

- Spring Boot 3.2
- Spring Data JPA
- H2 内存数据库
- Lombok
- Validation
- Spring Security (已引入，未配置)

## 测试

```bash
# 运行单元测试
mvn test

# 运行特定测试类
mvn test -Dtest=UserControllerTest

# 生成测试报告
mvn test -DgenerateReports=true
```

## 后续课程

| 课程 | 内容 |
|------|------|
| L12 | RESTful API 设计原则 |
| L13 | 请求参数处理 |
| L14 | 响应统一封装 |
| L15 | 异常处理 |
| L16 | 参数校验 |
| L17 | Spring Data JPA 入门 |
| L21 | 异步编程 |
| L23 | Spring Security 入门 |

## 常见问题

### Q: 端口被占用

修改 `application.yml` 中的 `server.port`

### Q: 数据库连接失败

确保 H2 控制台可用，或配置外部数据库

### Q: Lombok 不生效

安装 IDEA Lombok 插件：Settings → Plugins → 搜索 Lombok

---

**项目启动成功！现在可以开始学习后续课程了。**
