package com.learning;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * L12: 请求参数处理演示
 *
 * @author Java Learning Course
 */
@RestController
@RequestMapping("/api/demo")
public class ParamsController {

    // ===== 1. 路径参数 @PathVariable =====

    /**
     * GET /api/demo/users/{id}
     * 访问: http://localhost:8080/api/demo/users/123
     */
    @GetMapping("/users/{id}")
    public Map<String, Object> pathVariable(@PathVariable Long id) {
        return Map.of(
            "type", "PathVariable",
            "paramName", "id",
            "value", id,
            "description", "路径参数，用于唯一标识资源"
        );
    }

    /**
     * GET /api/demo/users/{userId}/orders/{orderId}
     * 访问: http://localhost:808/api/demo/users/1/orders/100
     */
    @GetMapping("/users/{userId}/orders/{orderId}")
    public Map<String, Object> multiplePathVariables(
            @PathVariable Long userId,
            @PathVariable Long orderId) {
        return Map.of(
            "userId", userId,
            "orderId", orderId
        );
    }

    // ===== 2. 查询参数 @RequestParam =====

    /**
     * GET /api/demo/search?name=john&age=25
     * 访问: http://localhost:8080/api/demo/search?name=john&age=25
     */
    @GetMapping("/search")
    public Map<String, Object> requestParam(
            @RequestParam String name,
            @RequestParam(defaultValue = "0") int age) {
        return Map.of(
            "type", "RequestParam",
            "name", name,
            "age", age,
            "description", "查询参数，用于筛选/分页"
        );
    }

    /**
     * 可选查询参数
     * GET /api/demo/list?page=0&size=10
     */
    @GetMapping("/list")
    public Map<String, Object> optionalParams(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam(required = false) String sort) {
        return Map.of(
            "page", page,
            "size", size,
            "sort", sort != null ? sort : "id"
        );
    }

    /**
     * Map 接收所有查询参数
     * GET /api/demo/map?a=1&b=2&c=3
     */
    @GetMapping("/map")
    public Map<String, Object> paramMap(@RequestParam Map<String, String> params) {
        return Map.of(
            "type", "Map with @RequestParam",
            "params", params
        );
    }

    // ===== 3. 请求体 @RequestBody =====

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateUserRequest {
        private String username;
        private String email;
        private Integer age;
    }

    /**
     * POST /api/demo/users
     * Content-Type: application/json
     * Body: {"username": "john", "email": "john@example.com", "age": 25}
     */
    @PostMapping("/users")
    public Map<String, Object> requestBody(@RequestBody CreateUserRequest request) {
        return Map.of(
           "type", "RequestBody",
            "received", request,
            "username", request.getUsername(),
            "email", request.getEmail()
        );
    }

    /**
     * 接收 Map 请求体
     */
    @PostMapping("/json")
    public Map<String, Object> jsonBody(@RequestBody Map<String, Object> body) {
        return Map.of(
            "type", "Map as request body",
            "received", body
        );
    }

    // ===== 4. 请求头 @RequestHeader =====

    /**
     * 获取单个请求头
     * GET /api/demo/header
     * Header: Authorization: Bearer token123
     */
    @GetMapping("/header")
    public Map<String, Object> requestHeader(
            @RequestHeader("Authorization") String auth,
            @RequestHeader(value = "X-Custom-Header", required = false) String custom) {
        return Map.of(
            "type", "RequestHeader",
            "authorization", auth,
            "customHeader", custom != null ? custom : "not provided"
        );
    }

    /**
     * 获取所有请求头
     */
    @GetMapping("/headers")
   public Map<String, Object> allHeaders(@RequestHeader Map<String, String> headers) {
        return Map.of(
            "type", "All headers",
            "headers", headers
        );
    }

    // ===== 5. 综合示例 =====

    /**
     * 完整示例：结合所有参数类型
     *
     * POST /api/demo/complex/{type}
     * Header: X-Request-ID: abc123
     * Query: debug=true
     * Body: {"data": "test"}
     */
    @PostMapping("/complex/{type}")
    public Map<String, Object> complexParams(
            @PathVariable String type,
            @RequestHeader("X-Request-ID") String requestId,
            @RequestParam(defaultValue = "false") boolean debug,
            @RequestBody Map<String, Object> body) {

        return Map.of(
            "type", type,
            "requestId", requestId,
            "debug", debug,
            "body", body
        );
    }
}
