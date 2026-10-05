# L10: 单元测试

> JUnit 5 + Mockito + Spring Boot Test

## 本节课目标

- 掌握 JUnit 5 常用注解
- 学会使用 Mockito 模拟对象
- 掌握 Spring Boot 集成测试
- 理解测试金字塔

## 核心概念

### 1. JUnit 5 注解

| 注解 | 说明 |
|------|------|
| @Test | 标记测试方法 |
| @BeforeEach | 每个测试方法前执行 |
| @AfterEach | 每个测试方法后执行 |
| @BeforeAll | 所有测试方法前执行（static） |
| @AfterAll | 所有测试方法后执行（static） |
| @Disabled | 禁用测试 |

### 2. 断言

```java
// 断言
assertEquals(expected, actual);
assertTrue(condition);
assertFalse(condition);
assertNull(object);
assertNotNull(object);
assertThrows(Exception.class, () -> {});

// 断言消息
assertEquals(expected, actual, "消息");
```

### 3. Mockito

```java
// 模拟对象
UserRepository mockRepo = mock(UserRepository.class);

// 设置行为
when(mockRepo.findById(1L)).thenReturn(Optional.of(user));

// 验证调用
verify(mockRepo, times(1)).findById(1L);
```

## 运行方式

```bash
# 运行所有测试
mvn test

# 运行单个测试类
mvn test -Dtest=UserServiceTest
```

## 下节课预告

L11: RESTful API 设计 - 资源命名、HTTP 方法、状态码
