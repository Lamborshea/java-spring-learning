# L08: 配置管理

> Spring Boot 配置详解：application.yml、环境配置、@Value、@ConfigurationProperties

## 本节课目标

- 掌握 application.yml 配置语法
- 理解多环境配置
- 学会使用 @Value 和 @ConfigurationProperties
- 理解配置加载顺序

## 核心概念

### 1. application.yml 语法

```yaml
# 注释
server:
  port: 8080          # 嵌套配置
  servlet:
    context-path: /api

spring:
  datasource:
    url: jdbc:h2:mem:testdb
    username: sa
    password:
```

### 2. 环境配置

```
application.yml          # 默认配置
application-dev.yml      # 开发环境
application-prod.yml     # 生产环境

# 激活方式：
# 1. 命令行: java -jar app.jar --spring.profiles.active=dev
# 2. IDE: Configuration -> Active profiles -> dev
```

### 3. 配置注入

```java
// @Value: 单个属性
@Value("${app.name}")
private String appName;

// @ConfigurationProperties: 批量属性
@ConfigurationProperties(prefix = "app")
public class AppProperties {
    private String name;
    private int version;
    private Map<String, String> config;
}
```

## 运行方式

```bash
cd L08-config
mvn spring-boot:run
```

## 下节课预告

L09: 日志实战 - SLF4J + Logback、配置日志级别、日志格式
