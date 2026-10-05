package com.learning.l08;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.List;
import java.util.Map;

/**
 * L08: 配置管理演示
 *
 * 本示例展示 Spring Boot 的各种配置方式：
 * 1. application.yml 配置
 * 2. @Value 单个属性注入
 * 3. @ConfigurationProperties 批量注入
 * 4. 多环境配置
 * 5. 配置加载顺序
 *
 * @author Java Learning Course
 */
@SpringBootApplication
public class ConfigApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConfigApplication.class, args);
    }

    // ===== 1. @Value 单个属性注入 =====

    /**
     * @Value 注入单个属性
     *
     * 语法：@Value("${property.path:defaultValue}")
     * - ${property.path}: 从配置中读取
     * - :defaultValue: 默认值（可选）
     */
    @org.springframework.stereotype.Service
    public static class ValueInjectionService {

        // 注入简单值
        @Value("${app.name}")
        private String appName;

        @Value("${app.version}")
        private String appVersion;

        // 注入带默认值的配置
        @Value("${app.description:默认描述}")
        private String description;

        // 注入数字（自动转换类型）
        @Value("${custom.timeout:60}")
        private int timeout;

        // 注入布尔值
        @Value("${custom.features.enable-cache:true}")
        private boolean enableCache;

        // 注入系统属性
        @Value("${java.home}")
        private String javaHome;

        // 注入环境变量
        @Value("${USER:default-user}")
        private String user;

        public void printConfig() {
            System.out.println("\n=== @Value 注入示例 ===");
            System.out.println("app.name: " + appName);
            System.out.println("app.version: " +appVersion);
            System.out.println("description: " + description);
            System.out.println("timeout: " + timeout);
            System.out.println("enableCache: " + enableCache);
            System.out.println("java.home: " + javaHome);
            System.out.println("user: " + user);
        }
    }

    // ===== 2. @ConfigurationProperties 批量注入 =====

    /**
     * 配置属性类
     * 使用 @ConfigurationProperties 绑定配置前缀
     *
     * 必须配合 @EnableConfigurationProperties 或 @EnableConfigurationProperties 启用
     */
    @Data
    @ConfigurationProperties(prefix = "app")
    public static class AppProperties {
        private String name;
        private String version;
        private String author;
        private String description;
    }

    /**
     * 自定义配置类
     */
    @Data
    @ConfigurationProperties(prefix = "custom")
    public static class CustomProperties {
        private int timeout;
        private int retry;
        private Features features = new Features();

        @Data
        public static class Features {
            private boolean enableCache;
            private boolean enableSecurity;
        }
    }

    // ===== 3. 配置类中使用 @ConfigurationProperties =====

    @org.springframework.stereotype.Service
    @EnableConfigurationProperties({AppProperties.class, CustomProperties.class})
    public static class PropertiesInjectionService {

        private final AppProperties appProperties;
        private final CustomProperties customProperties;

        // 构造方法注入
        public PropertiesInjectionService(AppProperties appProperties,
                                          CustomProperties customProperties) {
            this.appProperties = appProperties;
            this.customProperties = customProperties;
        }

        public void printConfig() {
            System.out.println("\n=== @ConfigurationProperties 注入示例 ===");
            System.out.println("App名称: " + appProperties.getName());
            System.out.println("App版本: " + appProperties.getVersion());
            System.out.println("作者: " + appProperties.getAuthor());
            System.out.println("描述: " + appProperties.getDescription());
            System.out.println("超时: " + customProperties.getTimeout());
            System.out.println("重试: " + customProperties.getRetry());
            System.out.println("缓存: " + customProperties.getFeatures().isEnableCache());
            System.out.println("安全: " + customProperties.getFeatures().isEnableSecurity());
        }
    }

    // ===== 4. 多环境配置演示 =====

    /**
     * 使用 @Profile 区分不同环境
     */
    @Configuration
    @Profile("dev")
    public static class DevConfig {
        @Bean
        public String environment() {
            return "开发环境配置";
        }
    }

    @Configuration
    @Profile("prod")
    public static class ProdConfig {
        @Bean
        public String environment() {
            return "生产环境配置";
        }
    }

    // ===== 5. 配置加载顺序演示 =====

    /**
     * Spring Boot 配置加载顺序（后者覆盖前者）：
     * 1. jar 包内的 application.properties
     * 2. jar 包内的 application.yml
     * 3. jar 包外的 application.properties
     * 4. jar 包外的 application.yml
     * 5. @PropertySource 指定的配置
     * 6. 命令行参数 --server.port=9090
     *
     * 同级配置覆盖规则：
     * - application-dev.yml 覆盖 application.yml
     * - 命令行参数优先级最高
     */
}
