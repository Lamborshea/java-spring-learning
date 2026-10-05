package com.learning.webapp.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * REST 控制器示例
 *
 * 关键注解说明（类似于 JS 框架中的装饰器）:
 * - @RestController: 标记为 REST 控制器，返回 JSON（类似 Express 的 router）
 * - @RequestMapping: 请求路径前缀（类似 Express 的 app.use('/api')）
 * - @GetMapping: 处理 GET 请求（类似 Express 的 app.get()）
 * - @PathVariable: 路径参数（类似 Express 的 /:id）
 *
 * 响应直接作为 JSON 返回，Spring 自动序列化
 */
@RestController
@RequestMapping("/api")
public class HelloController {

    /**
     * 简单 GET 请求
     * 访问: GET http://localhost:8080/api/hello
     *
     * 类似于 Node.js 的:
     * app.get('/api/hello', (req, res) => res.json({message: 'Hello'}));
     */
    @GetMapping("/hello")
    public Map<String, Object> hello() {
        return Map.of(
            "message", "Hello, Spring Boot!",
            "timestamp", LocalDateTime.now()
        );
    }

    /**
     * 带路径参数的 GET 请求
     * 访问: GET http://localhost:8080/api/hello/John
     *
     * 类似于 Node.js 的:
     * app.get('/api/hello/:name', (req, res) => res.json({message: `Hello, ${req.params.name}`}));
     */
    @GetMapping("/hello/{name}")
    public Map<String, String> helloWithName(@PathVariable String name) {
        return Map.of(
            "message", "Hello, " + name + "!",
            "timestamp", LocalDateTime.now().toString()
        );
    }

    /**
     * 带查询参数的 GET 请求
     * 访问: GET http://localhost:8080/api/greet?name=John&age=25
     *
     * 类似于 Node.js 的:
     * app.get('/api/greet', (req, res) => res.json({message: `Hello, ${req.query.name}`}));
     */
    @GetMapping("/greet")
    public Map<String, Object> greet(
            java.util.Optional<String> name,
            java.util.Optional<Integer> age) {
        String greetingName = name.orElse("Guest");
        Integer greetingAge = age.orElse(0);

        return Map.of(
            "message", "Hello, " + greetingName + "!",
            "age", greetingAge,
            "timestamp", LocalDateTime.now().toString()
        );
    }
}
