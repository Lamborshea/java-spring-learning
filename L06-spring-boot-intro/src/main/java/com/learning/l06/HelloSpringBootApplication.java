package com.learning.l06;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Spring Boot 第一个应用
 *
 * 启动方式：
 * 1. mvn spring-boot:run
 * 2. 直接运行 main 方法
 * 3. 打包后 java -jar target/L06-spring-boot-intro-1.0-SNAPSHOT.jar
 *
 * 访问：
 * - http://localhost:8080/hello
 * - http://localhost:8080/info
 * - http://localhost:8080/api/demo/greet?name=Java
 */
@SpringBootApplication
@RestController
public class HelloSpringBootApplication {

    public static void main(String[] args) {
        // SpringApplication 启动 Spring 上下文
        // 类似前端：new Vue({...}).$mount('#app')
        SpringApplication.run(HelloSpringBootApplication.class, args);
    }

    @GetMapping("/hello")
    public String hello() {
        return "Hello, Spring Boot!";
    }

    @GetMapping("/info")
    public String info() {
        return """
                Spring Boot Info:
                - 自动配置生效
                - 内嵌 Tomcat 启动
                - 默认端口 8080
                """;
    }
}
