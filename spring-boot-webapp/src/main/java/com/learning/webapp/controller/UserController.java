package com.learning.webapp.controller;

import com.learning.webapp.dto.ApiResponse;
import com.learning.webapp.entity.User;
import com.learning.webapp.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户管理 REST API
 *
 * RESTful API 设计规范:
 * - GET    /api/users      -> 查询所有用户
 * - GET    /api/users/{id} -> 根据 ID 查询用户
 * - POST   /api/users      -> 创建用户
 * - PUT    /api/users/{id} -> 更新用户
 * - DELETE /api/users/{id} -> 删除用户
 *
 * 注解说明:
 * - @Validated: 开启方法参数校验
 * - @Valid: 触发嵌套对象的校验
 * - @NotBlank: 不能为空或空白
 * - @Email: 必须是邮箱格式
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Validated  // 开启参数校验
public class UserController {

    private final UserService userService;

    /**
     * GET /api/users - 查询所有用户
     *
     * 相当于 Express 的:
     * app.get('/api/users', async (req, res) => {
     *     const users = await User.find();
     *     res.json(ApiResponse.success(users));
     * });
     */
    @GetMapping
    public ApiResponse<List<User>> getAllUsers() {
        List<User> users = userService.findAll();
        return ApiResponse.success(users);
    }

    /**
     * GET /api/users/{id} - 根据 ID 查询用户
     *
     * @PathVariable: 从 URL 路径中获取参数
     */
    @GetMapping("/{id}")
    public ApiResponse<User> getUserById(@PathVariable Long id) {
        User user = userService.findById(id);
        return ApiResponse.success(user);
    }

    /**
     * POST /api/users - 创建用户
     *
     * @RequestBody: 将请求体 JSON 映射为对象
     * @Valid: 触发 DTO 中的校验注解
     */
    @PostMapping
    public ApiResponse<User> createUser(@Valid @RequestBody CreateUserRequest request) {
        User user = userService.createUser(request.getUsername(), request.getEmail());
        return ApiResponse.success(user);
    }

    /**
     * PUT /api/users/{id} - 更新用户
     */
    @PutMapping("/{id}")
    public ApiResponse<User> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRequest request) {
        User user = userService.updateUser(id, request.getEmail());
        return ApiResponse.success(user);
    }

    /**
     * DELETE /api/users/{id} - 删除用户
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ApiResponse.success();
    }

    /**
     * GET /api/users/count - 统计用户数量
     */
    @GetMapping("/count")
    public ApiResponse<Long> countUsers() {
        return ApiResponse.success(userService.count());
    }

    // ===== DTO 类（内部类）=====

    /**
     * 创建用户请求体
     *
     * 使用 DTO 的好处:
     * 1. 解耦：API 请求结构和内部实体分离
     * 2. 校验：可以在 DTO 上添加校验注解
     * 3. 安全：隐藏内部字段，只暴露需要的数据
     */
    @Data
    public static class CreateUserRequest {
        @NotBlank(message = "用户名不能为空")
        private String username;

        @NotBlank(message = "邮箱不能为空")
        @Email(message = "邮箱格式不正确")
        private String email;
    }

    /**
     * 更新用户请求体
     */
    @Data
    public static class UpdateUserRequest {
        @Email(message = "邮箱格式不正确")
        private String email;
    }
}
