package com.learning.l22;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 用户管理 API - 演示完整的 OpenAPI 注解
 */
@RestController
@RequestMapping("/api/users")
@Tag(name = "用户管理", description = "用户的 CRUD 接口")
public class UserController {

    private final Map<Long, User> users = new HashMap<>();
    private long idGen = 1;

    public UserController() {
        // 初始化示例数据
        create(new CreateUserRequest("alice", "alice@example.com", 25));
        create(new CreateUserRequest("bob", "bob@example.com", 30));
    }

    @Operation(
            summary = "获取用户列表",
            description = "分页获取所有用户，支持按姓名过滤"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "成功"),
            @ApiResponse(responseCode = "500", description = "服务器异常", content = @Content)
    })
    @GetMapping
    public Map<String, Object> list(
            @Parameter(description = "页码，从 0 开始", example = "0")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "每页大小", example = "10")
            @RequestParam(defaultValue = "10") int size,

            @Parameter(description = "按姓名模糊搜索")
            @RequestParam(required = false) String keyword
    ) {
        List<User> filtered = users.values().stream()
                .filter(u -> keyword == null || u.name().contains(keyword))
                .toList();

        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "success");
        result.put("data", Map.of(
                "content", filtered,
                "page", page,
                "size", size,
                "total", filtered.size()
        ));
        result.put("timestamp", LocalDateTime.now().toString());
        return result;
    }

    @Operation(summary = "获取用户详情")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "成功"),
            @ApiResponse(responseCode = "404", description = "用户不存在", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<User> get(
            @Parameter(description = "用户 ID", example = "1")
            @PathVariable Long id
    ) {
        User user = users.get(id);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user);
    }

    @Operation(summary = "创建用户")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "创建成功"),
            @ApiResponse(responseCode = "400", description = "参数校验失败")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User create(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "创建用户的请求体",
                    required = true,
                    content = @Content(schema = @Schema(implementation = CreateUserRequest.class))
            )
            @RequestBody @Valid CreateUserRequest req
    ) {
        User user = new User(idGen++, req.name(), req.email(), req.age(), LocalDateTime.now());
        users.put(user.id(), user);
        return user;
    }

    @Operation(summary = "更新用户")
    @PutMapping("/{id}")
    public ResponseEntity<User> update(
            @PathVariable Long id,
            @RequestBody @Valid UpdateUserRequest req
    ) {
        User existing = users.get(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }
        User updated = new User(
                existing.id(),
                req.name() != null ? req.name() : existing.name(),
                req.email() != null ? req.email() : existing.email(),
                req.age() != null ? req.age() : existing.age(),
                existing.createdAt()
        );
        users.put(id, updated);
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "删除用户")
    @ApiResponse(responseCode = "204", description = "删除成功")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        users.remove(id);
        return ResponseEntity.noContent().build();
    }

    // ============= DTO =============

    @Schema(description = "用户实体")
    public record User(
            @Schema(description = "用户 ID", example = "1")
            Long id,

            @Schema(description = "用户名", example = "alice")
            String name,

            @Schema(description = "邮箱", example = "alice@example.com")
            String email,

            @Schema(description = "年龄", example = "25")
            Integer age,

            @Schema(description = "创建时间")
            LocalDateTime createdAt
    ) {}

    @Schema(description = "创建用户请求")
    public record CreateUserRequest(
            @Schema(description = "用户名", example = "alice", requiredMode = Schema.RequiredMode.REQUIRED)
            @NotBlank String name,

            @Schema(description = "邮箱", example = "alice@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
            @NotBlank @Email String email,

            @Schema(description = "年龄", example = "25", minimum = "0", maximum = "150")
            @Min(0) Integer age
    ) {}

    @Schema(description = "更新用户请求（字段都可选）")
    public record UpdateUserRequest(
            @Schema(description = "用户名") String name,
            @Schema(description = "邮箱") @Email String email,
            @Schema(description = "年龄", minimum = "0") @Min(0) Integer age
    ) {}
}
