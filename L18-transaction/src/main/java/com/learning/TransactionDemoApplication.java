package com.learning;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * L18: 事务管理演示
 *
 * 本节课内容：
 * 1. 事务基本概念和ACID特性
 * 2. @Transactional注解使用
 * 3. 事务传播行为（Propagation）
 * 4. 事务隔离级别（Isolation）
 * 5. 事务回滚规则
 *
 * @author Java Learning Course
 */
@SpringBootApplication
public class TransactionDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(TransactionDemoApplication.class, args);
    }

    // ===== 1. 实体类 =====

    /**
     * 账户实体 - 用于演示转账事务
     */
    @Entity
    @Table(name = "t_account")
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Account {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(nullable = false, unique = true, length = 50)
        private String accountNo;

        @Column(nullable = false)
        private String holderName;

        @Column(nullable = false, precision = 19, scale = 2)
        private BigDecimal balance;
    }

    /**
     * 订单实体 - 用于演示业务事务
     */
    @Entity
    @Table(name = "t_order")
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Order {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(nullable = false, unique = true, length = 50)
        private String orderNo;

        @Column(nullable = false)
        private String productName;

        @Column(nullable = false, precision = 19, scale = 2)
        private BigDecimal amount;

        private String status;  // PENDING, COMPLETED, CANCELLED
    }

    // ===== 2. Repository =====

    @Repository
    public interface AccountRepository extends JpaRepository<Account, Long> {
        Optional<Account> findByAccountNo(String accountNo);
    }

    @Repository
    public interface OrderRepository extends JpaRepository<Order, Long> {
        Optional<Order> findByOrderNo(String orderNo);
    }

    // ===== 3. Service层 - 事务传播行为演示 =====

    /**
     * 转账服务 - 演示基本事务
     *
     * JS对比: 数据库事务类似于 Promise.all() 的原子性
     * const [result1, result2] = await Promise.all([op1, op2]);
     * 如果op2失败，op1的结果也会回滚
     */
    @Service
    public static class TransferService {

        private final AccountRepository accountRepository;

        public TransferService(AccountRepository accountRepository) {
            this.accountRepository = accountRepository;
        }

        /**
         * 转账操作 - REQUIRED（默认）
         * 有事务则加入，没有则创建新事务
         *
         * JS对比: 类似 async/await 的同步执行，要么全成功要么全失败
         */
        @Transactional
        public void transfer(String fromAccountNo, String toAccountNo, BigDecimal amount) {
            // 1. 查询转出账户
            Account fromAccount = accountRepository.findByAccountNo(fromAccountNo)
                    .orElseThrow(() -> new RuntimeException("转出账户不存在"));

            // 2. 查询转入账户
            Account toAccount = accountRepository.findByAccountNo(toAccountNo)
                    .orElseThrow(() -> new RuntimeException("转入账户不存在"));

            // 3. 检查余额
            if (fromAccount.getBalance().compareTo(amount) < 0) {
                throw new RuntimeException("余额不足");
            }

            // 4. 扣减转出账户余额
            fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
            accountRepository.save(fromAccount);
            System.out.println("转出: " + fromAccount.getHolderName() + " -" + amount);

            // 5. 增加转入账户余额
            toAccount.setBalance(toAccount.getBalance().add(amount));
            accountRepository.save(toAccount);
            System.out.println("转入: " + toAccount.getHolderName() + " +" + amount);

            // 如果这里抛异常，之前的操作都会回滚
        }
    }

    /**
     * 订单服务 - 演示REQUIRES_NEW传播行为
     */
    @Service
    public static class OrderService {

        private final OrderRepository orderRepository;
        private final AuditService auditService;

        public OrderService(OrderRepository orderRepository, AuditService auditService) {
            this.orderRepository = orderRepository;
            this.auditService = auditService;
        }

        /**
         * 创建订单 - REQUIRED（默认）
         */
        @Transactional
        public Order createOrder(String orderNo, String productName, BigDecimal amount) {
            Order order = Order.builder()
                    .orderNo(orderNo)
                    .productName(productName)
                    .amount(amount)
                    .status("PENDING")
                    .build();

            order = orderRepository.save(order);
            System.out.println("订单已创建: " + orderNo);

            // 即使订单创建失败，审计日志也应该记录
            // 使用 REQUIRES_NEW 创建独立事务
            try {
                auditService.logOrderCreated(orderNo);
            } catch (Exception e) {
                // 审计失败不应影响主业务
                System.err.println("审计日志失败: " + e.getMessage());
            }

            return order;
        }

        /**
         * 完成订单
         */
        @Transactional
        public void completeOrder(String orderNo) {
            Order order = orderRepository.findByOrderNo(orderNo)
                    .orElseThrow(() -> new RuntimeException("订单不存在"));

            if ("COMPLETED".equals(order.getStatus())) {
                throw new RuntimeException("订单已完成，不能重复确认");
            }

            order.setStatus("COMPLETED");
            orderRepository.save(order);
            System.out.println("订单已完成: " + orderNo);
        }

        /**
         * 取消订单
         */
        @Transactional
        public void cancelOrder(String orderNo) {
            Order order = orderRepository.findByOrderNo(orderNo)
                    .orElseThrow(() -> new RuntimeException("订单不存在"));

            order.setStatus("CANCELLED");
            orderRepository.save(order);
            System.out.println("订单已取消: " + orderNo);
        }
    }

    /**
     * 审计服务 - 演示REQUIRES_NEW
     *
     * JS对比: 类似于日志记录，使用独立的日志系统
     * 不应因为日志记录失败而影响主业务
     */
    @Service
    public static class AuditService {

        /**
         * 记录订单创建日志 - REQUIRES_NEW
         * 总是创建新事务，即使调用方有事务也独立执行
         *
         * 使用场景: 审计日志、消息发送等即使业务失败也要记录的操作
         */
        @Transactional(propagation = Propagation.REQUIRES_NEW)
        public void logOrderCreated(String orderNo) {
            // 模拟审计日志记录
            System.out.println("[AUDIT] 订单已创建: " + orderNo + ", 时间: " + java.time.LocalDateTime.now());
            // 即使这里抛异常，也不会影响调用方的事务
        }

        /**
         * 记录订单完成日志
         */
        @Transactional(propagation = Propagation.REQUIRES_NEW)
        public void logOrderCompleted(String orderNo) {
            System.out.println("[AUDIT] 订单已完成: " + orderNo + ", 时间: " + java.time.LocalDateTime.now());
        }
    }

    /**
     * 报告服务 - 演示SUPPORTS和NOT_SUPPORTED
     */
    @Service
    public static class ReportService {

        private final AccountRepository accountRepository;
        private final OrderRepository orderRepository;

        public ReportService(AccountRepository accountRepository, OrderRepository orderRepository) {
            this.accountRepository = accountRepository;
            this.orderRepository = orderRepository;
        }

        /**
         * 统计账户数量 - SUPPORTS
         * 有事务则加入，没有则以非事务运行
         *
         * JS对比: 类似于只读查询，可以在事务内运行也可以独立运行
         */
        @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
        public long countAccounts() {
            return accountRepository.count();
        }

        /**
         * 统计订单数量
         */
        @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
        public long countOrders() {
            return orderRepository.count();
        }

        /**
         * 生成汇总报告 - NOT_SUPPORTED
         * 以非事务运行，挂起外层事务（如果有）
         *
         * 适用于: 统计分析、报表生成等耗时操作
         */
        @Transactional(propagation = Propagation.NOT_SUPPORTED)
        public String generateSummaryReport() {
            long accountCount = accountRepository.count();
            long orderCount = orderRepository.count();
            return String.format("账户数: %d, 订单数: %d", accountCount, orderCount);
        }
    }

    // ===== 4. Service层 - 隔离级别演示 =====

    /**
     * 库存服务 - 演示隔离级别
     */
    @Service
    public static class InventoryService {

        private final EntityManager entityManager;

        public InventoryService(EntityManager entityManager) {
            this.entityManager = entityManager;
        }

        /**
         * 演示 READ_COMMITTED 隔离级别
         * 防止脏读：只能读取已提交的数据
         *
         * JS对比: 类似于乐观锁版本控制
         */
        @Transactional(isolation = Isolation.READ_COMMITTED)
        public void checkAndDecreaseStock(String productId, int quantity) {
            // 查询库存（读取已提交的数据）
            Query query = entityManager.createQuery(
                "SELECT i.stock FROM Inventory i WHERE i.productId = :productId");
            query.setParameter("productId", productId);

            Integer stock = (Integer) query.getSingleResult();
            System.out.println("当前库存: " + stock);

            if (stock < quantity) {
                throw new RuntimeException("库存不足");
            }

            // 模拟扣减库存
            Query updateQuery = entityManager.createQuery(
                "UPDATE Inventory i SET i.stock = i.stock - :quantity WHERE i.productId = :productId");
            updateQuery.setParameter("quantity", quantity);
            updateQuery.setParameter("productId", productId);
            updateQuery.executeUpdate();

            System.out.println("库存已扣减: " + quantity);
        }

        /**
         * 演示 REPEATABLE_READ 隔离级别
         * 防止不可重复读：同一事务内多次读取数据一致
         *
         * JS对比: 类似于数据库的乐观锁
         */
        @Transactional(isolation = Isolation.REPEATABLE_READ)
        public BigDecimal queryBalance(String accountNo) {
            Query query = entityManager.createQuery(
                "SELECT a.balance FROM Account a WHERE a.accountNo = :accountNo");
            query.setParameter("accountNo", accountNo);

            BigDecimal balance = (BigDecimal) query.getSingleResult();
            System.out.println("第一次查询余额: " + balance);

            // 模拟其他事务修改（在这个事务内看不到变化）
            // 实际测试时可以在另一个事务中修改

            BigDecimal balance2 = (BigDecimal) query.getSingleResult();
            System.out.println("第二次查询余额: " + balance2);

            return balance;
        }
    }

    /**
     * 简单的库存实体（用于演示）
     */
    @Entity
    @Table(name = "t_inventory")
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Inventory {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(nullable = false, unique = true, length = 50)
        private String productId;

        @Column(nullable = false)
        private Integer stock;
    }

    // ===== 5. Service层 - 回滚规则演示 =====

    /**
     * 支付服务 - 演示回滚规则
     *
     * JS对比: 类似于 try-catch 的错误处理
     * throw new Error() 会触发回滚
     */
    @Service
    public static class PaymentService {

        private final AccountRepository accountRepository;
        private final OrderRepository orderRepository;

        public PaymentService(AccountRepository accountRepository, OrderRepository orderRepository) {
            this.accountRepository = accountRepository;
            this.orderRepository = orderRepository;
        }

        /**
         * 支付订单 - 默认回滚规则
         * RuntimeException 及其子类会自动回滚
         *
         * JS对比: 类似于 throw Error 会中断执行
         */
        @Transactional
        public void payOrder(String orderNo, String accountNo) {
            // 1. 检查订单状态
            Order order = orderRepository.findByOrderNo(orderNo)
                    .orElseThrow(() -> new RuntimeException("订单不存在"));

            if ("COMPLETED".equals(order.getStatus())) {
                throw new RuntimeException("订单已支付");  // 会回滚
            }

            // 2. 检查账户余额
            Account account = accountRepository.findByAccountNo(accountNo)
                    .orElseThrow(() -> new RuntimeException("账户不存在"));

            if (account.getBalance().compareTo(order.getAmount()) < 0) {
                throw new RuntimeException("余额不足");  // 会回滚
            }

            // 3. 扣款
            account.setBalance(account.getBalance().subtract(order.getAmount()));
            accountRepository.save(account);

            // 4. 更新订单状态
            order.setStatus("COMPLETED");
            orderRepository.save(order);

            System.out.println("支付成功: " + orderNo);
        }

        /**
         * 支付订单 - 自定义回滚规则
         * 对于检查型异常（checked exception），默认不回滚
         * 可以通过 rollbackFor 指定回滚条件
         */
        @Transactional(rollbackFor = Exception.class)  // 任何异常都回滚
        public void payOrderWithCustomRollback(String orderNo, String accountNo) throws Exception {
            Order order = orderRepository.findByOrderNo(orderNo)
                    .orElseThrow(() -> new Exception("订单不存在"));

            Account account = accountRepository.findByAccountNo(accountNo)
                    .orElseThrow(() -> new Exception("账户不存在"));

            if (account.getBalance().compareTo(order.getAmount()) < 0) {
                throw new Exception("余额不足");
            }

            account.setBalance(account.getBalance().subtract(order.getAmount()));
            accountRepository.save(account);

            order.setStatus("COMPLETED");
            orderRepository.save(order);
        }

        /**
         * 支付订单 - 指定不回滚的异常
         * 某些异常不应导致回滚，如业务提示
         */
        @Transactional(noRollbackFor = InsufficientBalanceException.class)
        public void payOrderWithNonRollback(String orderNo, String accountNo)
                throws InsufficientBalanceException {

            Order order = orderRepository.findByOrderNo(orderNo)
                    .orElseThrow(() -> new RuntimeException("订单不存在"));

            Account account = accountRepository.findByAccountNo(accountNo)
                    .orElseThrow(() -> new RuntimeException("账户不存在"));

            if (account.getBalance().compareTo(order.getAmount()) < 0) {
                // 抛出自定义异常，但不会回滚事务
                throw new InsufficientBalanceException("余额不足，当前余额: " + account.getBalance());
            }

            account.setBalance(account.getBalance().subtract(order.getAmount()));
            accountRepository.save(account);

            order.setStatus("COMPLETED");
            orderRepository.save(order);
        }

        /**
         * 自定义异常 - 检查型异常（业务异常）
         */
        public static class InsufficientBalanceException extends Exception {
            public InsufficientBalanceException(String message) {
                super(message);
            }
        }
    }

    // ===== 6. 演示运行器 =====

    /**
     * 配置一个 CommandLineRunner 来运行演示
     */
    @Bean
    public org.springframework.boot.CommandLineRunner demoRunner(
            TransferService transferService,
            OrderService orderService,
            ReportService reportService,
            AccountRepository accountRepository,
            OrderRepository orderRepository) {

        return args -> {
            System.out.println("\n========== 事务管理演示 ==========\n");

            // 初始化测试数据
            initTestData(accountRepository, orderRepository);

            try {
                // 1. 基本转账事务
                demonstrateTransfer(transferService, accountRepository);

                // 2. 订单事务
                demonstrateOrder(orderService);

                // 3. 报告服务
                demonstrateReport(reportService);

                // 4. 回滚演示
                demonstrateRollback(transferService, accountRepository);

            } catch (Exception e) {
                System.err.println("演示异常: " + e.getMessage());
            }

            System.out.println("\n========== 演示完成 ==========");
        };
    }

    private void initTestData(AccountRepository accountRepository, OrderRepository orderRepository) {
        // 创建测试账户
        if (accountRepository.count() == 0) {
            accountRepository.save(Account.builder()
                    .accountNo("A001")
                    .holderName("张三")
                    .balance(new java.math.BigDecimal("1000.00"))
                    .build());

            accountRepository.save(Account.builder()
                    .accountNo("A002")
                    .holderName("李四")
                    .balance(new java.math.BigDecimal("500.00"))
                    .build());

            System.out.println("测试账户已创建");
        }

        // 创建测试订单
        if (orderRepository.count() == 0) {
            orderRepository.save(Order.builder()
                    .orderNo("O001")
                    .productName("Java书籍")
                    .amount(new java.math.BigDecimal("99.00"))
                    .status("PENDING")
                    .build());

            System.out.println("测试订单已创建");
        }
    }

    private void demonstrateTransfer(TransferService transferService, AccountRepository accountRepository) {
        System.out.println("\n--- 1. 基本转账事务 ---");

        // 查看转账前余额
        System.out.println("转账前:");
        accountRepository.findByAccountNo("A001").ifPresent(a ->
            System.out.println("  A001 (张三): " + a.getBalance()));
        accountRepository.findByAccountNo("A002").ifPresent(a ->
            System.out.println("  A002 (李四): " + a.getBalance()));

        // 执行转账
        transferService.transfer("A001", "A002", new java.math.BigDecimal("100.00"));

        // 查看转账后余额
        System.out.println("转账后:");
        accountRepository.findByAccountNo("A001").ifPresent(a ->
            System.out.println("  A001 (张三): " + a.getBalance()));
        accountRepository.findByAccountNo("A002").ifPresent(a ->
            System.out.println("  A002 (李四): " + a.getBalance()));
    }

    private void demonstrateOrder(OrderService orderService) {
        System.out.println("\n--- 2. 订单创建事务 ---");

        Order order = orderService.createOrder(
            "O002",
            "Spring Boot实战",
            new java.math.BigDecimal("128.00")
        );
        System.out.println("创建订单: " + order.getOrderNo());

        orderService.completeOrder("O002");
    }

    private void demonstrateReport(ReportService reportService) {
        System.out.println("\n--- 3. 报告服务（只读事务） ---");

        long accountCount = reportService.countAccounts();
        long orderCount = reportService.countOrders();
        System.out.println("账户数: " + accountCount + ", 订单数: " + orderCount);

        String report = reportService.generateSummaryReport();
        System.out.println("汇总报告: " + report);
    }

    private void demonstrateRollback(TransferService transferService, AccountRepository accountRepository) {
        System.out.println("\n--- 4. 事务回滚演示 ---");

        // 查看转账前余额
        accountRepository.findByAccountNo("A001").ifPresent(a ->
            System.out.println("转账前 A001 余额: " + a.getBalance()));

        try {
            // 尝试转账（余额不足，应该回滚）
            transferService.transfer("A001", "A002", new java.math.BigDecimal("99999.00"));
        } catch (Exception e) {
            System.out.println("转账失败（预期）: " + e.getMessage());
        }

        // 验证余额未变（事务已回滚）
        accountRepository.findByAccountNo("A001").ifPresent(a ->
            System.out.println("转账失败后 A001 余额: " + a.getBalance() + " (应该未变)"));
    }
}
