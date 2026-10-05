# L18: 事务管理

> @Transactional、传播行为、隔离级别

## 本节课目标

- 理解事务的 ACID 特性
- 掌握 @Transactional 注解使用
- 理解事务传播行为（Propagation）
- 理解事务隔离级别（Isolation）
- 学会处理事务异常和回滚

## 核心概念

### 1. 事务特性（ACID）

| 特性 | 说明 | JS中的类似概念 |
|------|------|---------------|
| Atomic（原子性） | 全部成功或全部回滚 | 事务操作要么全做要么全不做 |
| Consistency（一致性） | 事务前后数据一致 | 数据库约束保证数据有效 |
| Isolation（隔离性） | 并发事务互不干扰 | 锁机制防止脏读 |
| Durability（持久性） | 提交后数据永久保存 | 数据写入磁盘 |

### 2. @Transactional 注解

```java
@Service
@Transactional  // 类级别：所有方法开启事务
public class UserService {

    @Transactional  // 方法级别：只对该方法生效
    public void transferMoney() {
        // 事务逻辑
    }
}
```

### 3. 传播行为（Propagation）

| 传播行为 | 说明 | 场景 |
|---------|------|------|
| REQUIRED（默认） | 有事务则加入，没有则创建 | 大多数场景 |
| REQUIRES_NEW | 总是创建新事务，挂起外层事务 | 独立日志、审计 |
| SUPPORTS | 有事务则加入，没有则以非事务运行 | 查询操作 |
| NOT_SUPPORTED | 以非事务运行，挂起外层事务 | 统计类操作 |
| MANDATORY | 必须在事务中运行，否则抛异常 | 核心业务 |
| NEVER | 必须不在事务中运行，否则抛异常 | 只读查询 |
| NESTED | 嵌套事务（Savepoint） | 局部回滚 |

### 4. 隔离级别（Isolation）

| 隔离级别 | 脏读 | 不可重复读 | 幻读 | 并发性能 |
|---------|------|-----------|------|---------|
| READ_UNCOMMITTED | ✓ | ✓ | ✓ | 最高 |
| READ_COMMITTED | ✗ | ✓ | ✓ | 中等 |
| REPEATABLE_READ | ✗ | ✗ | ✓ | 较低 |
| SERIALIZABLE | ✗ | ✗ | ✗ | 最低 |

### 5. 事务回滚规则

- 默认：运行时异常（RuntimeException）会回滚
- 检查型异常（Checked Exception）默认不回滚
- 可通过 `rollbackFor` 和 `noRollbackFor` 自定义

## 本课代码说明

- `TransactionDemoApplication.java` - 演示事务各种特性

## 下节课预告

L19: 异步编程 - @Async、线程池、CompletableFuture
