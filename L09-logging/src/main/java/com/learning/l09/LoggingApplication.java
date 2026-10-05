package com.learning.l09;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * L09: 日志实战演示
 *
 * 本示例展示：
 * 1. SLF4J 日志 API 使用
 * 2. 占位符 vs 字符串拼接
 * 3. 不同日志级别
 * 4. Lombok @Slf4j 简化
 *
 * @author Java Learning Course
 */
@SpringBootApplication
public class LoggingApplication {

    // ===== 1. 传统 Logger 创建 =====
    // private static final Logger log = LoggerFactory.getLogger(LoggingApplication.class);

    // ===== 2. 使用 Lombok @Slf4j 简化 =====
    // Lombok 自动生成: private static final org.slf4j.Logger log = LoggerFactory.getLogger(LoggingApplication.class);

    // 为了演示，我们手动创建多个 Logger
    private static final Logger logger = LoggerFactory.getLogger(LoggingApplication.class);
    private static final Logger userLogger = LoggerFactory.getLogger("com.learning.l09.user");
    private static final Logger orderLogger = LoggerFactory.getLogger("com.learning.l09.order");

    public static void main(String[] args) {
        SpringApplication.run(LoggingApplication.class, args);

        demonstrateLogLevels();
        demonstratePlaceholders();
        demonstrateConditionalLogging();
    }

    /**
     * 日志级别演示
     *
     * 日志级别顺序：TRACE < DEBUG < INFO < WARN < ERROR
     * 输出某个级别时，会同时输出更高级别的日志
     */
    private static void demonstrateLogLevels() {
        System.out.println("\n=== 日志级别演示 ===");

        logger.trace("TRACE - 最详细的日志，通常用于追踪执行流程");
        logger.debug("DEBUG - 调试信息，用于排查问题");
        logger.info("INFO - 一般信息，记录重要业务事件");
        logger.warn("WARN - 警告信息，不影响运行但需要注意");
        logger.error("ERROR - 错误信息，记录异常和错误");

        // 模拟业务日志
        userLogger.info("用户登录: userId=123, ip=192.168.1.1");
        orderLogger.info("订单创建: orderId=456, amount=99.99");
    }

    /**
     * 占位符 vs 字符串拼接
     */
    private static void demonstratePlaceholders() {
        System.out.println("\n=== 占位符演示 ===");

        String userName = "Alice";
        int userAge = 25;
        double accountBalance = 1234.56;

        // ❌ 错误：字符串拼接（即使日志级别不输出，也会执行拼接）
        // 性能问题：每次都会执行字符串拼接
        logger.debug("用户信息: " + userName + ", 年龄: " + userAge + ", 余额: " + accountBalance);

        // ✅ 正确：占位符 {}
        // 优点：只有当日志级别启用时才会执行拼接
        logger.debug("用户信息: {}, 年龄: {}, 余额: {}", userName, userAge, accountBalance);

        // ✅ 正确：条件日志
        if (logger.isDebugEnabled()) {
            // 复杂逻辑可以在这里执行
            String complexInfo = buildComplexInfo(userName, userAge, accountBalance);
            logger.debug(complexInfo);
        }

        // 多参数占位符
        logger.info("订单{} 已{}，金额: {:.2f}", "1001", "支付", 99.99);
    }

    /**
     * 条件日志
     * 复杂的日志内容应该先判断日志级别
     */
    private static void demonstrateConditionalLogging() {
        System.out.println("\n=== 条件日志演示 ===");

        // 场景：记录用户的详细信息
        User user = new User(1L, "Bob", "bob@example.com", 30);

        // 简单情况：使用占位符
        logger.info("用户: {}", user);

        // 复杂情况：先判断级别
        if (logger.isDebugEnabled()) {
            StringBuilder sb = new StringBuilder();
            sb.append("用户详情:\n");
            sb.append("  ID: ").append(user.getId()).append("\n");
            sb.append("  名称: ").append(user.getName()).append("\n");
            sb.append("  邮箱: ").append(user.getEmail()).append("\n");
            sb.append("  年龄: ").append(user.getAge()).append("\n");
            sb.append("  注册时间: ").append(user.getRegisterTime()).append("\n");
            logger.debug(sb.toString());
        }

        // 记录异常
        try {
            int result = 10 / 0;
        } catch (Exception e) {
            // 只记录错误信息
            logger.error("除法运算失败: {}", e.getMessage());

            // 记录完整堆栈信息
            logger.error("除法运算失败", e);
        }
    }

    // ===== 辅助方法 =====

    private static String buildComplexInfo(String name, int age, double balance) {
        // 模拟复杂的字符串构建过程
        return String.format("User{name=%s, age=%d, balance=%.2f}", name, age, balance);
    }

    // ===== 内部类 =====

    @lombok.Data
    @lombok.AllArgsConstructor
    public static class User {
        private Long id;
        private String name;
        private String email;
        private int age;
        private java.time.LocalDateTime registerTime = java.time.LocalDateTime.now();

        public User(Long id, String name, String email, int age) {
            this.id = id;
            this.name = name;
            this.email = email;
            this.age = age;
            this.registerTime = java.time.LocalDateTime.now();
        }
    }
}
