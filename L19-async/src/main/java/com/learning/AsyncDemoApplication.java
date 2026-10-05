package com.learning;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.concurrent.*;

/**
 * L19: 异步编程演示
 *
 * 本节课内容：
 * 1. @Async 注解异步执行
 * 2. CompletableFuture 异步编程
 * 3. 线程池配置
 * 4. 异步任务调度
 *
 * @author Java Learning Course
 */
@Slf4j
@SpringBootApplication
// 开启异步支持 - 必须！
@EnableAsync
public class AsyncDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(AsyncDemoApplication.class, args);
    }

    // ===== 1. 配置线程池 =====

    /**
     * 自定义异步线程池
     *
     * JS对比: 类似于 worker pool 或 thread pool
     * - 核心线程：常驻线程
     * - 最大线程：高峰期扩容上限
     * - 队列：缓存待执行任务
     * - 拒绝策略：队列满时的处理方式
     */
    @Bean
    public TaskExecutor asyncTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        // 核心线程数：一直保持活跃的线程数
        // JS对比: 类似于最小 worker 数量
        executor.setCorePoolSize(4);

        // 最大线程数：高峰期扩容上限
        executor.setMaxPoolSize(16);

        // 队列容量：等待执行的任务队列大小
        executor.setQueueCapacity(100);

        // 线程名前缀：便于日志追踪
        executor.setThreadNamePrefix("async-");

        // 拒绝策略：队列满时的处理方式
        // - AbortPolicy: 抛异常（默认）
        // - CallerRunsPolicy: 由调用方线程执行
        // - DiscardPolicy: 丢弃任务
        // - DiscardOldestPolicy: 丢弃最老的任务
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

        // 等待线程终止的超时时间
        executor.setAwaitTerminationSeconds(60);

        // 初始化
        executor.initialize();

        return executor;
    }

    // ===== 2. @Async 异步服务 =====

    /**
     * 邮件服务 - 演示 @Async 异步调用
     *
     * JS对比: 类似于发送邮件后不等待，直接返回
     * email.send().then(() => console.log('邮件已发送'))
     */
    @Service
    public static class EmailService {

        /**
         * 发送邮件 - 异步执行
         *
         * @Async 注解的方法会在独立线程中执行
         * 调用方不会阻塞，立即返回
         *
         * JS对比: 相当于返回一个 Promise
         */
        @Async("asyncTaskExecutor")  // 指定线程池（可选，默认使用SimpleAsyncTaskExecutor）
        public void sendEmail(String to, String subject, String content) {
            // 模拟耗时操作
            try {
                log.info("开始发送邮件 to: {}", to);
                Thread.sleep(2000);  // 模拟SMTP发送延迟
                log.info("邮件发送成功 to: {}, subject: {}", to, subject);
            } catch (InterruptedException e) {
                log.error("邮件发送失败: {}", e.getMessage());
                Thread.currentThread().interrupt();
            }
        }

        /**
         * 发送邮件 - 有返回值
         *
         * 返回 Future/CompletableFuture，可以在调用方获取结果
         */
        @Async
        public Future<String> sendEmailWithResult(String to, String subject) {
            try {
                log.info("异步发送邮件 to: {}", to);
                Thread.sleep(1500);
                return new AsyncResult<>("邮件发送成功: " + to);
            } catch (InterruptedException e) {
                return new AsyncResult<>("邮件发送失败: " + e.getMessage());
            }
        }
    }

    /**
     * 订单服务 - 演示异步任务链
     */
    @Service
    public static class OrderService {

        private final EmailService emailService;
        private final NotificationService notificationService;
        private final AuditService auditService;

        public OrderService(EmailService emailService,
                           NotificationService notificationService,
                           AuditService auditService) {
            this.emailService = emailService;
            this.notificationService = notificationService;
            this.auditService = auditService;
        }

        /**
         * 创建订单 - 同步入口，触发多个异步任务
         *
         * JS对比: 类似于异步触发多个操作
         * await createOrder();
         * Promise.all([sendEmail, notify, audit]);
         */
        public String createOrder(String productName, String userEmail) {
            log.info("开始创建订单: {}", productName);

            // 创建订单（同步逻辑）
            String orderId = "ORD-" + System.currentTimeMillis();
            log.info("订单创建成功: {}", orderId);

            // 触发多个异步任务（不等待结果）
            emailService.sendEmail(userEmail, "订单确认", "您的订单 " + orderId + " 已确认");

            // 短信通知（异步）
            notificationService.sendSms("13800000000", "订单已创建");

            // 审计日志（异步）
            auditService.logOrderCreated(orderId);

            // 即使上面三个异步任务还在执行，方法也会立即返回
            return orderId;
        }
    }

    /**
     * 通知服务 - 演示异步短信发送
     */
    @Service
    public static class NotificationService {

        /**
         * 发送短信 - 异步
         */
        @Async
        public void sendSms(String phone, String message) {
            try {
                log.info("开始发送短信 to: {}", phone);
                Thread.sleep(1000);  // 模拟短信网关延迟
                log.info("短信发送成功 to: {}", phone);
            } catch (InterruptedException e) {
                log.error("短信发送失败: {}", e.getMessage());
            }
        }
    }

    /**
     * 审计服务 - 演示异步日志记录
     */
    @Service
    public static class AuditService {

        /**
         * 记录审计日志 - 异步
         *
         * 即使主业务失败，审计日志也应该记录
         * 使用 REQUIRES_NEW 传播行为（在事务Demo中已演示）
         */
        @Async
        public void logOrderCreated(String orderId) {
            log.info("[AUDIT] 订单已创建: {}, 时间: {}",
                    orderId, java.time.LocalDateTime.now());
        }

        @Async
        public void logOrderPaid(String orderId) {
            log.info("[AUDIT] 订单已支付: {}, 时间: {}",
                    orderId, java.time.LocalDateTime.now());
        }
    }

    // ===== 3. CompletableFuture 异步编程 =====

    /**
     * 支付服务 - 演示 CompletableFuture
     *
     * JS对比: 类似于 Promise 的完整API
     * - thenApply: map
     * - thenCompose: flatMap
     * - handle: catch + then
     * - allOf: Promise.all
     * - anyOf: Promise.race
     */
    @Service
    public static class PaymentService {

        /**
         * 模拟支付网关调用
         */
        public CompletableFuture<String> callPaymentGateway(String orderId, BigDecimal amount) {
            return CompletableFuture.supplyAsync(() -> {
                log.info("调用支付网关, 订单: {}, 金额: {}", orderId, amount);
                try {
                    Thread.sleep(2000);  // 模拟网络调用
                    // 模拟返回结果
                    return "PAY-" + System.currentTimeMillis();
                } catch (InterruptedException e) {
                    throw new CompletionException(e);
                }
            });
        }

        /**
         * 异步调用外部API - 串行
         *
         * JS对比: async/await 串行执行
         * const result1 = await api1();
         * const result2 = await api2(result1);
         */
        public CompletableFuture<String> processPaymentChain(String orderId) {
            return callPaymentGateway(orderId, new BigDecimal("100.00"))
                    .thenApply(transactionId -> {
                        log.info("支付成功, 交易号: {}", transactionId);
                        return "SUCCESS-" + transactionId;
                    })
                    .exceptionally(ex -> {
                        log.error("支付失败: {}", ex.getMessage());
                        return "FAILED-" + ex.getMessage();
                    });
        }

        /**
         * 并行调用多个服务
         *
         * JS对比: Promise.all([api1(), api2(), api3()])
         */
        public CompletableFuture<String> processOrderComplete(String orderId) {
            // 并行执行三个任务
            CompletableFuture<String> paymentFuture = callPaymentGateway(orderId, new BigDecimal("100.00"));
            CompletableFuture<String> inventoryFuture = CompletableFuture.supplyAsync(() -> {
                log.info("检查库存...");
                try { Thread.sleep(500); } catch (InterruptedException e) {}
                return "INVENTORY-OK";
            });
            CompletableFuture<String> logisticsFuture = CompletableFuture.supplyAsync(() -> {
                log.info("检查物流...");
                try { Thread.sleep(800); } catch (InterruptedException e) {}
                return "LOGISTICS-OK";
            });

            // 等待所有任务完成
            // JS对比: Promise.all()
            return CompletableFuture.allOf(paymentFuture, inventoryFuture, logisticsFuture)
                    .thenApply(v -> {
                        // 所有任务都成功
                        log.info("订单处理完成: {}", orderId);
                        return "ORDER-COMPLETE";
                    })
                    .exceptionally(ex -> {
                        log.error("订单处理失败: {}", ex.getMessage());
                        return "ORDER-FAILED";
                    });
        }

        /**
         * 竞态：任一服务完成即可
         *
         * JS对比: Promise.race()
         */
        public String checkMultiChannel(String orderId) {
            CompletableFuture<String> channel1 = CompletableFuture.supplyAsync(() -> {
                try { Thread.sleep(1000); } catch (InterruptedException e) {}
                return "CHANNEL1";
            });
            CompletableFuture<String> channel2 = CompletableFuture.supplyAsync(() -> {
                try { Thread.sleep(500); } catch (InterruptedException e) {}
                return "CHANNEL2";
            });

            // 哪个先完成就返回哪个
            // JS对比: Promise.race()
            return CompletableFuture.anyOf(channel1, channel2)
                    .thenApply(Object::toString)
                    .join();  // 阻塞等待结果
        }

        /**
         * 回调式异步处理
         *
         * JS对比: promise.then(success).catch(failure)
         */
        public void processWithCallback(String orderId, Callback callback) {
            CompletableFuture.supplyAsync(() -> {
                log.info("处理订单: {}", orderId);
                try { Thread.sleep(1000); } catch (InterruptedException e) {}
                return "OK";
            }).thenAccept(result -> callback.onSuccess(result))
              .exceptionally(ex -> {
                  callback.onFailure(ex.getMessage());
                  return null;
              });
        }

        public interface Callback {
            void onSuccess(String result);
            void onFailure(String error);
        }
    }

    // ===== 4. 响应式编程示例（简要） =====

    /**
     * 响应式服务 - Spring WebFlux 基础
     *
     * JS对比: 类似于 RxJS 或 React Query
     * - Mono: 0或1个元素
     * - Flux: 0或N个元素
     *
     * 注：这里只是示例，实际需要添加 spring-boot-starter-webflux 依赖
     */
    @Service
    public static class ReactiveService {

        /**
         * 响应式返回 - 不阻塞线程
         *
         * JS对比: 类似于 Observable 或 AsyncIterable
         * 客户端可以订阅数据流变化
         */
        // @Autowired
        // private ProductRepository productRepository;
        //
        // public Flux<Product> getAllProducts() {
        //     return productRepository.findAll()
        //             .delayElements(Duration.ofMillis(100));  // 流式返回
        // }

        /**
         * 响应式查询 - 按需获取
         */
        // public Mono<Product> getProductById(Long id) {
        //     return productRepository.findById(id);  // 返回Mono
        // }

        /**
         * 响应式事务
         */
        // public Mono<Void> saveProduct(Product product) {
        //     return productRepository.save(product)
        //             .then();  // 转换为Void
        // }
    }

    // ===== 5. 演示运行器 =====

    @Bean
    public org.springframework.boot.CommandLineRunner demoRunner(
            OrderService orderService,
            PaymentService paymentService) {

        return args -> {
            System.out.println("\n========== 异步编程演示 ==========\n");

            try {
                // 1. @Async 示例
                demonstrateAsync(orderService);

                // 2. CompletableFuture 串行
                demonstrateCompletableFutureSerial(paymentService);

                // 3. CompletableFuture 并行
                demonstrateCompletableFutureParallel(paymentService);

                // 4. 回调式异步
                demonstrateCallback(paymentService);

                // 等待异步任务完成
                Thread.sleep(5000);

            } catch (Exception e) {
                System.err.println("演示异常: " + e.getMessage());
            }

            System.out.println("\n========== 演示完成 ==========");
            System.exit(0);
        };
    }

    private void demonstrateAsync(OrderService orderService) {
        System.out.println("\n--- 1. @Async 异步执行 ---");

        long start = System.currentTimeMillis();

        // 触发异步任务，立即返回
        String orderId = orderService.createOrder("iPhone 15", "user@example.com");

        long elapsed = System.currentTimeMillis() - start;
        System.out.println("createOrder 方法执行时间: " + elapsed + "ms");
        System.out.println("  返回订单ID: " + orderId);
        System.out.println("  注意：邮件/短信/日志是异步发送的，方法立即返回");
    }

    private void demonstrateCompletableFutureSerial(PaymentService paymentService) {
        System.out.println("\n--- 2. CompletableFuture 串行 ---");

        long start = System.currentTimeMillis();

        String result = paymentService.processPaymentChain("ORD-001").join();

        long elapsed = System.currentTimeMillis() - start;
        System.out.println("串行执行时间: " + elapsed + "ms");
        System.out.println("  结果: " + result);
    }

    private void demonstrateCompletableFutureParallel(PaymentService paymentService) {
        System.out.println("\n--- 3. CompletableFuture 并行 ---");

        long start = System.currentTimeMillis();

        String result = paymentService.processOrderComplete("ORD-002").join();

        long elapsed = System.currentTimeMillis() - start;
        System.out.println("并行执行时间: " + elapsed + "ms (并行，应该比串行快)");
        System.out.println("  结果: " + result);
    }

    private void demonstrateCallback(PaymentService paymentService) {
        System.out.println("\n--- 4. 回调式异步 ---");

        paymentService.processWithCallback("ORD-003", new PaymentService.Callback() {
            @Override
            public void onSuccess(String result) {
                System.out.println("  回调成功: " + result);
            }

            @Override
            public void onFailure(String error) {
                System.out.println("  回调失败: " + error);
            }
        });
    }

    // 辅助类
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Order {
        private String orderId;
        private String productName;
        private BigDecimal amount;
    }
}
