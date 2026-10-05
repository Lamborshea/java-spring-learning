package com.learning.webapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring Boot 应用程序入口
 *
 * 类似于 Node.js 的:
 * const app = require('express')();
 * app.listen(3000);
 *
 * @SpringBootApplication 是三个注解的组合:
 * 1. @Configuration: 标记该类是配置类，可以定义 Bean
 * 2. @EnableAutoConfiguration: 启用自动配置
 * 3. @ComponentScan: 自动扫描当前包及子包中的组件
 *
 * 运行方式:
 * - IDEA: 右键 -> Run 'WebAppApplication'
 * - 命令行: mvn spring-boot:run
 * - 打包运行: mvn package && java -jar target/spring-boot-webapp-1.0.0.jar
 */
@SpringBootApplication
public class WebAppApplication {

    public static void main(String[] args) {
        // SpringApplication.run() 会启动嵌入式 Tomcat 服务器
        // 并自动配置 Spring 容器中的所有 Bean
        SpringApplication.run(WebAppApplication.class, args);
    }
}
