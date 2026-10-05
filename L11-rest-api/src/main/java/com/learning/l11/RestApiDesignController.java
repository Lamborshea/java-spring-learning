package com.learning.l11;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * L11: RESTful API 设计最佳实践演示
 *
 * 本示例展示：
 * 1. RESTful 资源命名
 * 2. HTTP 方法正确使用
 * 3. HTTP 状态码规范
 * 4. 统一响应格式
 *
 * @author Java Learning Course
 */
@RestController
@RequestMapping("/api/users")
public class RestApiDesignController {

    // ===== 实体类 =====

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class User {
        private Long id;
        private String username;
        private String email;
        private LocalDateTime createdAt;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateUserRequest {
        private String username;
        private String email;
    }

    @Data
   @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateUserRequest {
        private String email;
    }

    // ===== 统一响应包装 =====

    /**
     * 统一 API 响应
     *
     * RESTful 最佳实践：
     * - 始终返回 JSON 格式
     * - 包含状态信息（code/message）
     * - 数据放在 data 字段中
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ApiResponse<T> {
        private int code;
        private String message;
        private T data;

        // 成功响应
        public static <T> ApiResponse<T> success(T data) {
            return ApiResponse.<T>builder()
                    .code(200)
                    .message("success")
                    .data(data)
                   .build();
        }

        // 错误响应
        public static <T> ApiResponse<T> error(int code, String message) {
            return ApiResponse.<T>builder()
                    .code(code)
                    .message(message)
                    .build();
        }
    }

    // ===== RESTful API 实现 =====

    /**
     * GET /api/users - 获取所有用户
     *
     * RESTful 规范：
     * - 使用复数名词表示资源集合
     * - 返回 200 OK
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<User>>> getAllUsers() {
        List<User> users = List.of(
                User.builder().id(1L).username("alice").email("alice@example.com").build(),
                User.builder().id(2L).username("bob").email("bob@example.com").build()
        );

        return ResponseEntity.ok(ApiResponse.success(users));
    }

    /**
     * GET /api/users/{id} - 获取单个用户
     *
     * RESTful 规范：
     * - 使用路径参数表示资源 ID
     * - 资源不存在返回 404
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<User>> getUserById(@PathVariable Long id) {
        if (id <= 0) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "Invalid user ID"));
        }

        User user = User.builder()
                .id(id)
                .username("testuser")
                .email("test@example.com")
                .createdAt(LocalDateTime.now())
                .build();

        return ResponseEntity.ok(ApiResponse.success(user));
    }

    /**
     * POST /api/users - 创建用户
     *
     * RESTful 规范：
     * - 使用 POST 方法创建资源
     * - 成功返回 201 Created
     * - 返回创建的资源
     */
    @PostMapping
    public ResponseEntity<ApiResponse<User>> createUser(@RequestBody CreateUserRequest request) {
        // 验证请求
        if (request.getUsername() == null || request.getUsername().isBlank()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "Username is required"));
        }

        // 创建用户
        User user = User.builder()
                .id(1L)
                .username(request.getUsername())
                .email(request.getEmail())
                .createdAt(LocalDateTime.now())
                .build();

        // 返回 201 Created 和新创建的资源
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(user));
    }

    /**
     * PUT /api/users/{id} - 完整更新用户
     *
     * RESTful 规范：
     * - PUT 用于完整更新（替换整个资源）
     * - 幂等操作：多次调用结果相同
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<User>> updateUser(
            @PathVariable Long id,
            @RequestBody UpdateUserRequest request) {

        User user = User.builder()
                .id(id)
                .username("testuser")
               .email(request.getEmail())
                .createdAt(LocalDateTime.now())
                .build();

        return ResponseEntity.ok(ApiResponse.success(user));
    }

    /**
     * PATCH /api/users/{id} - 部分更新用户
     *
     * RESTful 规范：
     * - PATCH 用于部分更新（只更新指定字段）
     * - 非幂等操作
     */
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<User>> patchUser(
            @PathVariable Long id,
            @RequestBody UpdateUserRequest request) {

        // 只更新 email，其他字段保持不变
        User user = User.builder()
               .id(id)
                .username("testuser")
                .email(request.getEmail())
                .createdAt(LocalDateTime.now())
                .build();

        return ResponseEntity.ok(ApiResponse.success(user));
    }

    /**
     * DELETE /api/users/{id} - 删除用户
     *
     * RESTful 规范：
     * - 成功返回 204 No Content（无返回内容）
     * - 幂等操作：多次删除结果相同
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        // 执行删除逻辑
        // ...

        // 204 表示成功且无返回内容
        return ResponseEntity.noContent().build();
    }

    // ===== 其他常用端点 =====

    /**
     * HEAD /api/users/{id} - 检查资源是否存在
     *
     * 用途：不获取资源内容，只检查是否存在
     * 返回：200 OK 或 404 Not Found
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.HEAD)
    public ResponseEntity<Void> checkUserExists(@PathVariable Long id) {
        // 检查逻辑
        return ResponseEntity.ok().build();
    }

    /**
     * OPTIONS /api/users - 获取支持的 HTTP 方法
     *
     * 用途：获取资源支持的操作
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.OPTIONS)
    public ResponseEntity<Void> getAllowedMethods() {
        return ResponseEntity.ok()
                .header("Allow", "GET, POST, PUT, PATCH, DELETE, HEAD, OPTIONS")
                .build();
    }
}
