package com.learning;

import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.*;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * L20: 缓存演示
 *
 * 本节课内容：
 * 1. Spring Cache 抽象（@Cacheable, @CachePut, @CacheEvict）
 * 2. Caffeine 本地缓存配置
 * 3. 多级缓存策略
 * 4. 缓存key设计
 *
 * @author Java Learning Course
 */
@Slf4j
@SpringBootApplication
// 开启Spring Cache支持 - 必须！
@EnableCaching
public class CacheDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(CacheDemoApplication.class, args);
    }

    // ===== 1. 缓存配置 =====

    /**
     * Caffeine 缓存配置
     *
     * JS对比: 类似于 LRU cache 实现
     * - maxSize: 最大条目数
     * - expireAfterWrite: 写入后过期时间
     * - weakKeys/weakValues: 弱引用，防止内存泄漏
     */
    @Configuration
    public static class CacheConfig {

        @Bean
        @Primary  // 默认缓存管理器
        public CacheManager caffeineCacheManager() {
            CaffeineCacheManager cacheManager = new CaffeineCacheManager();

            // 配置缓存规格
            cacheManager.setCaffeine(caffeineCacheBuilder());

            // 或者为不同缓存设置不同配置
            // cacheManager.setCacheNames(Arrays.asList("users", "products"));
            // cacheManager.setCaffeineSpec(CaffeineSpec.from("maximumSize=100,expireAfterWrite=10m"));

            return cacheManager;
        }

        private com.github.benmanes.caffeine.cache.Caffeine<Object, Object> caffeineCacheBuilder() {
            return Caffeine.newBuilder()
                    // 最大缓存条目数
                    .maximumSize(100)
                    // 写入后过期时间
                    .expireAfterWrite(Duration.ofMinutes(10))
                    // 访问后过期时间
                    // .expireAfterAccess(Duration.ofMinutes(5))
                    // 写入后刷新时间（保证缓存一致性）
                    // .refreshAfterWrite(Duration.ofMinutes(1))
                    // 监听缓存事件
                    .removalListener((key, value, cause) -> {
                        log.info("缓存移除 - key: {}, value: {}, 原因: {}", key, value, cause);
                    })
                    // 统计功能
                    .recordStats();
        }
    }

    // ===== 2. 实体类 =====

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class User {
        private Long id;
        private String username;
        private String email;
        private int age;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Product {
        private Long id;
        private String name;
        private Double price;
        private String category;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Order {
        private Long id;
        private String orderNo;
        private Long userId;
        private Double totalAmount;
    }

    // ===== 3. 模拟数据库 =====

    /**
     * 模拟数据库 - 简单内存存储
     *
     * JS对比: 类似于 mock database 或 in-memory store
     */
    static class MockDatabase {

        private static final Map<Long, User> users = new ConcurrentHashMap<>();
        private static final Map<Long, Product> products = new ConcurrentHashMap<>();
        private static final Map<Long, Order> orders = new ConcurrentHashMap<>();
        private static final AtomicLong userIdGen = new AtomicLong(1);
        private static final AtomicLong productIdGen = new AtomicLong(1);
        private static final AtomicLong orderIdGen = new AtomicLong(1);

        // 查询计数器（用于验证缓存效果）
        private static final Map<String, AtomicLong> queryCount = new ConcurrentHashMap<>();

        static {
            // 初始化测试数据
            users.put(1L, User.builder().id(1L).username("alice").email("alice@example.com").age(25).build());
            users.put(2L, User.builder().id(2L).username("bob").email("bob@example.com").age(30).build());
            users.put(3L, User.builder().id(3L).username("charlie").email("charlie@example.com").age(35).build());
            userIdGen.set(4L);

            products.put(1L, Product.builder().id(1L).name("iPhone 15").price(6999.0).category("手机").build());
            products.put(2L, Product.builder().id(2L).name("MacBook Pro").price(14999.0).category("电脑").build());
            productIdGen.set(3L);

            orders.put(1L, Order.builder().id(1L).orderNo("O001").userId(1L).totalAmount(6999.0).build());
            orderIdGen.set(2L);
        }

        // 记录查询次数
        static void recordQuery(String queryName) {
            queryCount.computeIfAbsent(queryName, k -> new AtomicLong(0)).incrementAndGet();
        }

        static long getQueryCount(String queryName) {
            return queryCount.getOrDefault(queryName, new AtomicLong(0)).get();
        }

        static void resetQueryCount() {
            queryCount.clear();
        }

        // User CRUD
        static User findUserById(Long id) {
            recordQuery("findUserById-" + id);
            log.info("【数据库查询】findUserById: {}", id);
            return users.get(id);
        }

        static User saveUser(User user) {
            if (user.getId() == null) {
                user.setId(userIdGen.getAndIncrement());
            }
            users.put(user.getId(), user);
            return user;
        }

        static void deleteUser(Long id) {
            users.remove(id);
        }

        // Product CRUD
        static Product findProductById(Long id) {
            recordQuery("findProductById-" + id);
            log.info("【数据库查询】findProductById: {}", id);
            return products.get(id);
        }

        // Order CRUD
        static Order findOrderById(Long id) {
            recordQuery("findOrderById-" + id);
            log.info("【数据库查询】findOrderById: {}", id);
            return orders.get(id);
        }

        static Order saveOrder(Order order) {
            if (order.getId() == null) {
                order.setId(orderIdGen.getAndIncrement());
            }
            orders.put(order.getId(), order);
            return order;
        }
    }

    // ===== 4. Service层 - 缓存注解使用 =====

    /**
     * 用户服务 - 演示 @Cacheable
     *
     * JS对比: 类似于 memoize 高阶函数
     * const getUser = memoize(async (id) => db.findUser(id));
     */
    @Service
    public static class UserService {

        /**
         * 查询用户 - @Cacheable
         *
         * 第一次调用：查询数据库，结果存入缓存
         * 后续调用：直接从缓存返回，不执行方法体
         *
         * JS对比:
         * // 第一次调用
         * await getUser(1);  // 查询数据库
         * // 后续调用
         * await getUser(1);  // 直接从缓存返回
         *
         * @Cacheable 属性：
         * - value/cacheNames: 缓存名称（必填）
         * - key: 缓存key，支持SpEL表达式
         * - condition: 缓存条件，满足才缓存
         * - unless: 不缓存条件
         * - sync: 同步模式，防止缓存击穿
         */
        @Cacheable(value = "users", key = "#id", unless = "#result == null")
        public User getUserById(Long id) {
            log.info("执行 getUserById 方法，id: {}", id);
            // 这里是实际业务逻辑，只在缓存未命中时执行
            return MockDatabase.findUserById(id);
        }

        /**
         * 根据用户名查询 - 自定义key
         */
        @Cacheable(value = "users", key = "'username:' + #username", unless = "#result == null")
        public User getUserByUsername(String username) {
            log.info("执行 getUserByUsername 方法，username: {}", username);
            return MockDatabase.findUserById(
                MockDatabase.users.values().stream()
                    .filter(u -> u.getUsername().equals(username))
                    .findFirst()
                    .map(User::getId)
                    .orElse(null)
            );
        }

        /**
         * 条件缓存 - 只有满足条件才缓存
         *
         * 示例：只缓存年龄大于18岁的用户
         */
        @Cacheable(value = "adultUsers", key = "#id", condition = "#age > 18")
        public User getAdultUserById(Long id, int age) {
            log.info("执行 getAdultUserById 方法，id: {}", id);
            return MockDatabase.findUserById(id);
        }

        /**
         * 缓存查询结果集合
         */
        @Cacheable(value = "userList", key = "'all'", unless = "#result.isEmpty()")
        public java.util.List<User> getAllUsers() {
            log.info("执行 getAllUsers 方法");
            return new java.util.ArrayList<>(MockDatabase.users.values());
        }

        /**
         * 更新用户 - @CachePut
         *
         * 每次执行后更新缓存（不管缓存有没有）
         * 用于数据更新后需要立即刷新缓存的场景
         *
         * JS对比:
         * const updateUser = async (user) => {
         *     const result = await db.updateUser(user);
         *     cache.set(`user:${user.id}`, result);  // 刷新缓存
         *     return result;
         * };
         */
        @CachePut(value = "users", key = "#result.id")
        public User updateUser(User user) {
            log.info("执行 updateUser 方法，id: {}", user.getId());
            return MockDatabase.saveUser(user);
        }

        /**
         * 创建用户 - 同时放入多个缓存
         */
        @Caching(put = {
            @CachePut(value = "users", key = "#result.id"),
            @CachePut(value = "users", key = "'username:' + #result.username")
        })
        public User createUser(User user) {
            log.info("执行 createUser 方法");
            return MockDatabase.saveUser(user);
        }

        /**
         * 删除用户 - @CacheEvict
         *
         * 执行后删除缓存，用于数据删除时同步清除缓存
         *
         * JS对比:
         * const deleteUser = async (id) => {
         *     await db.deleteUser(id);
         *     cache.delete(`user:${id}`);  // 删除缓存
         * };
         *
         * @CacheEvict 属性：
         * - value: 缓存名
         * - key: 要删除的key
         * - allEntries: 是否删除所有条目
         * - beforeInvocation: 是否在方法执行前删除
         */
        @CacheEvict(value = "users", key = "#id")
        public void deleteUser(Long id) {
            log.info("执行 deleteUser 方法，id: {}", id);
            MockDatabase.deleteUser(id);
        }

        /**
         * 清除所有用户缓存
         */
        @CacheEvict(value = {"users", "userList", "adultUsers"}, allEntries = true)
        public void clearAllUserCache() {
            log.info("清除所有用户缓存");
        }
    }

    /**
     * 商品服务 - 演示缓存击穿和过期策略
     */
    @Service
    public static class ProductService {

        /**
         * 查询商品 - 防止缓存击穿（sync模式）
         *
         * sync=true：多个并发请求同时查询时，只有一个请求去查数据库
         * 其他请求等待缓存结果
         *
         * JS对比: 类似于 mutex 锁或 Promise cache
         */
        @Cacheable(value = "products", key = "#id", sync = true)
        public Product getProductById(Long id) {
            log.info("执行 getProductById 方法，id: {}", id);
            // 模拟数据库查询延迟
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return MockDatabase.findProductById(id);
        }

        /**
         * 批量查询 - 使用 keyGenerator
         */
        @Cacheable(value = "products", keyGenerator = "customKeyGenerator")
        public Product getProductByIdWithGenerator(Long id) {
            return MockDatabase.findProductById(id);
        }
    }

    /**
     * 订单服务 - 演示缓存多级查找
     */
    @Service
    public static class OrderService {

        private final UserService userService;

        public OrderService(UserService userService) {
            this.userService = userService;
        }

        /**
         * 查询订单（带用户信息）
         *
         * 场景：查询订单时需要展示用户信息
         * 优化：先从缓存查用户，缓存未命中再查数据库
         */
        public Order getOrderWithUser(Long orderId) {
            log.info("执行 getOrderWithUser 方法，orderId: {}", orderId);

            Order order = MockDatabase.findOrderById(orderId);
            if (order != null && order.getUserId() != null) {
                // 从缓存获取用户信息
                User user = userService.getUserById(order.getUserId());
                log.info("订单 {} 的用户: {}", orderId, user.getUsername());
            }

            return order;
        }
    }

    // ===== 5. 自定义Key生成器 =====

    /**
     * 自定义缓存Key生成器
     *
     * JS对比: 类似于自定义缓存key策略
     * const customKey = (fn, args) => `${fn.name}:${args.join(':')}`;
     */
    // @Component
    // public static class CustomKeyGenerator implements KeyGenerator {
    //     @Override
    //     public Object generate(Object target, Method method, Object... params) {
    //         return target.getClass().getSimpleName() + ":" + method.getName() + ":" +
    //                 Arrays.stream(params).map(Object::toString).collect(Collectors.joining(":"));
    //     }
    // }

    // ===== 6. 演示运行器 =====

    @Bean
    public org.springframework.boot.CommandLineRunner demoRunner(
            UserService userService,
            ProductService productService,
            OrderService orderService) {

        return args -> {
            System.out.println("\n========== 缓存演示 ==========\n");

            try {
                // 1. 基础缓存演示
                demonstrateBasicCache(userService);

                // 2. 缓存更新演示
                demonstrateCacheUpdate(userService);

                // 3. 缓存删除演示
                demonstrateCacheEvict(userService);

                // 4. 缓存击穿演示
                demonstrateCachePenetration(productService);

                // 5. 缓存统计
                demonstrateCacheStats(userService);

            } catch (Exception e) {
                System.err.println("演示异常: " + e.getMessage());
                e.printStackTrace();
            }

            System.out.println("\n========== 演示完成 ==========");
            System.exit(0);
        };
    }

    private void demonstrateBasicCache(UserService userService) {
        System.out.println("\n--- 1. @Cacheable 基础演示 ---");

        MockDatabase.resetQueryCount();

        // 第一次查询 - 应该查询数据库
        System.out.println("第1次查询用户ID=1:");
        User user1 = userService.getUserById(1L);
        System.out.println("  结果: " + user1.getUsername());
        System.out.println("  数据库查询次数: " + MockDatabase.getQueryCount("findUserById-1"));

        // 第二次查询 - 应该从缓存返回
        System.out.println("第2次查询用户ID=1:");
        User user2 = userService.getUserById(1L);
        System.out.println("  结果: " + user2.getUsername());
        System.out.println("  数据库查询次数: " + MockDatabase.getQueryCount("findUserById-1"));
        System.out.println("  预期：数据库查询次数仍为1，说明使用了缓存");

        // 查询不存在的用户 - 不缓存
        System.out.println("查询不存在的用户ID=999:");
        User user999 = userService.getUserById(999L);
        System.out.println("  结果: " + user999);
    }

    private void demonstrateCacheUpdate(UserService userService) {
        System.out.println("\n--- 2. @CachePut 更新缓存 ---");

        MockDatabase.resetQueryCount();

        // 查询用户（会缓存）
        System.out.println("查询用户ID=2:");
        User user = userService.getUserById(2L);
        System.out.println("  当前用户名: " + user.getUsername());

        // 更新用户（会刷新缓存）
        user.setUsername("bob-updated");
        User updated = userService.updateUser(user);
        System.out.println("  更新后: " + updated.getUsername());

        // 再次查询（应该从缓存获取更新后的值）
        System.out.println("再次查询用户ID=2:");
        User userAgain = userService.getUserById(2L);
        System.out.println("  用户名: " + userAgain.getUsername());
        System.out.println("  预期：用户名应该是更新后的值（从缓存获取）");
    }

    private void demonstrateCacheEvict(UserService userService) {
        System.out.println("\n--- 3. @CacheEvict 删除缓存 ---");

        MockDatabase.resetQueryCount();

        // 查询用户（会缓存）
        System.out.println("查询用户ID=3:");
        userService.getUserById(3L);
        System.out.println("  数据库查询次数: " + MockDatabase.getQueryCount("findUserById-3"));

        // 删除用户（会删除缓存）
        System.out.println("删除用户ID=3:");
        userService.deleteUser(3L);

        // 再次查询（应该重新查询数据库）
        System.out.println("再次查询用户ID=3:");
        userService.getUserById(3L);
        System.out.println("  数据库查询次数: " + MockDatabase.getQueryCount("findUserById-3"));
        System.out.println("  预期：数据库查询次数为1（缓存已删除）");
    }

    private void demonstrateCachePenetration(ProductService productService) {
        System.out.println("\n--- 4. 缓存击穿防护（sync=true）---");

        // sync=true 可以防止缓存击穿
        // 当多个线程同时查询同一个key时，只有一个线程去查数据库
        // 其他线程等待缓存结果

        // 实际测试需要并发调用，这里仅作演示
        System.out.println("使用 sync=true 防止缓存击穿");
        System.out.println("  并发请求同一key时，只有1个请求查数据库");
    }

    private void demonstrateCacheStats(UserService userService) {
        System.out.println("\n--- 5. 缓存统计信息 ---");

        // 查询一些数据触发缓存
        userService.getUserById(1L);
        userService.getUserById(1L);  // 缓存命中
        userService.getUserById(2L);
        userService.getAllUsers();

        // 获取缓存统计（需要开启 recordStats()）
        // Cache cache = cacheManager.getCache("users");
        // if (cache instanceof CaffeineCache) {
        //     CaffeineCache caffeineCache = (CaffeineCache) cache;
        //     CacheStats stats = caffeineCache.getNativeCache().stats();
        //     System.out.println("缓存命中率: " + stats.hitRate());
        //     System.out.println("命中次数: " + stats.hitCount());
        //     System.out.println("未命中次数: " + stats.missCount());
        // }

        System.out.println("注意: 需要通过 CaffeineCache 获取 stats");
    }
}
