# L19: 异步编程

> @Async、CompletableFuture、消息队列

## 本节课目标

- 理解同步 vs 异步编程
- 掌握 @Async 注解使用
- 学会使用 CompletableFuture
- 了解消息队列基础

## 核心概念

### 1. 同步 vs 异步

| 模式 | 说明 | JS对比 |
|------|------|--------|
| 同步 | 阻塞等待结果 | async/await (等待) |
| 异步 | 不阻塞，立即返回 | Promise.then() |

### 2. @Async 注解

```java
@Service
public class EmailService {

    @Async  // 异步执行，不阻塞调用方
    public void sendEmail(String to, String content) {
        // 耗时操作
    }
}

@RestController
public class OrderController {

    @Autowired
    private EmailService emailService;

    @PostMapping("/order")
    public String createOrder() {
        emailService.sendEmail(...);  // 立即返回，不等待邮件发送
        return "订单创建成功";
    }
}
```

### 3. CompletableFuture

```java
// JS对比: 类似于 Promise.all()
CompletableFuture.allOf(future1, future2, future3).join();

// JS对比: 类似于 Promise.race()
CompletableFuture.anyOf(future1, future2).join();
```

### 4. 线程池配置

```yaml
spring:
  task:
    execution:
      pool:
        core-size: 4        # 核心线程数
        max-size: 16        # 最大线程数
        queue-capacity: 100 # 队列容量
      thread-name-prefix: async-
```

## 下节课预告

L20: 缓存 - @Cacheable、Redis、Caffeine
