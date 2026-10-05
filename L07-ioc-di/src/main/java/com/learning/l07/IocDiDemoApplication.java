package com.learning.l07;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

/**
 * Spring Boot IoC 容器演示
 *
 * 运行后查看控制台输出，可以看到：
 * 1. Spring 容器中所有 Bean 的名字
 * 2. UserService 自动注入了 UserRepository
 */
@SpringBootApplication
public class IocDiDemoApplication {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(IocDiDemoApplication.class, args);

        // 从容器中获取 Bean
        UserService userService = context.getBean(UserService.class);
        userService.createUser("Lambor", "lambor@example.com");
        userService.listUsers().forEach(System.out::println);
    }

    /**
     * 容器启动后执行这个
     */
    @Bean
    public CommandLineRunner printBeans(ApplicationContext context) {
        return args -> {
            System.out.println("\n=== Spring 容器中的所有 Bean ===");
            String[] beanNames = context.getBeanDefinitionNames();
            for (String name : beanNames) {
                System.out.println(" - " + name);
            }
            System.out.println("=== 共 " + beanNames.length + " 个 Bean ===\n");
        };
    }
}
