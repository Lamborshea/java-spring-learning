package com.learning.l07;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * 演示 @Configuration 和 Bean 作用域
 */
@Configuration
public class AppConfig {

    /**
     * 默认 singleton：整个应用只有一个实例
     */
    @Bean
    public NotificationService emailNotification() {
        return new NotificationService("email");
    }

    /**
     * prototype：每次获取都创建新实例
     */
    @Bean
    @Scope("prototype")
    public NotificationService smsNotification() {
        return new NotificationService("sms");
    }
}
