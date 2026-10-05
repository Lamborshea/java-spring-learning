# L09: 日志实战

> SLF4J + Logback 日志配置详解

## 本节课目标

- 掌握 SLF4J 日志 API
- 配置 Logback 日志级别和格式
- 理解日志输出到文件和控制台
- 学会使用日志占位符

## 核心概念

### 1. 日志级别

| 级别 | 说明 | 数值 |
|------|------|------|
| TRACE | 最详细，通常用于调试 | 0 |
| DEBUG | 调试信息 | 1 |
| INFO | 一般信息 | 2 |
| WARN | 警告信息 | 3 |
| ERROR | 错误信息 | 4 |

### 2. 占位符 vs 字符串拼接

```java
// ❌ 错误：字符串拼接，即使日志级别不输出也会执行
logger.debug("用户信息: " + user);

// ✅ 正确：占位符，未启用级别不执行拼接
logger.debug("用户信息: {}", user);

// ✅ 正确：条件日志
if (logger.isDebugEnabled()) {
    logger.debug("用户信息: " + user);
}
```

### 3. 配置示例

```yaml
logging:
  level:
    root: INFO
    com.learning: DEBUG
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
```

## 运行方式

```bash
cd L09-logging
mvn spring-boot:run
```

## 下节课预告

L10: 单元测试 - JUnit 5、Mock、Spring Boot Test
