package com.learning.l22;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI 文档配置
 *
 * 访问地址：
 * - Swagger UI: http://localhost:8080/swagger-ui.html
 * - OpenAPI JSON: http://localhost:8080/v3/api-docs
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("示例 API 文档")
                        .version("1.0.0")
                        .description("演示 SpringDoc / OpenAPI 3 自动生成接口文档")
                        .contact(new Contact()
                                .name("Lambor")
                                .email("lambor@example.com"))
                        .license(new License()
                                .name("MIT")
                                .url("https://opensource.org/licenses/MIT")))
                // 配置 JWT 认证
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("在请求头添加：Authorization: Bearer xxx")));
    }
}
