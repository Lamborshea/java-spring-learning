package com.learning.l22;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * L22: API 文档自动生成演示
 *
 * 启动后访问：
 * - http://localhost:8080/swagger-ui.html  (Swagger UI)
 * - http://localhost:8080/v3/api-docs       (OpenAPI JSON)
 */
@SpringBootApplication
public class ApiDocsDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiDocsDemoApplication.class, args);
    }
}
