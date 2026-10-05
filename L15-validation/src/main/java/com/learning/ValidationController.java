package com.learning;

import jakarta.validation.Constraint;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * L15: 参数校验演示
 *
 * @author Java Learning Course
 */
@Slf4j
@RestController
@RequestMapping("/api/validation")
@Validated  // 开启方法参数校验
public class ValidationController {

    // ===== 1. 基础校验注解 =====

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CreateUserRequest {

        @NotBlank(message = "用户名不能为空")
        @Size(min = 3, max = 20, message = "用户名长度3-20")
        private String username;

        @NotBlank(message = "邮箱不能为空")
        @Email(message = "邮箱格式不正确")
        private String email;

        @NotNull(message = "年龄不能为空")
        @Min(value = 18, message = "年龄最小18岁")
        @Max(value = 100, message = "年龄最大100岁")
        private Integer age;

        @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
        private String phone;
    }

    // ===== 2. 嵌套校验 =====

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateOrderRequest {

        @NotNull(message = "订单项不能为空")
        @Size(min = 1, message = "至少有一个订单项")
        @Valid  // 嵌套对象校验
        private List<OrderItem> items;

        @NotBlank(message = "收货地址不能为空")
        private String address;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItem {

        @NotBlank(message = "商品名称不能为空")
        private String productName;

        @NotNull(message = "数量不能为空")
        @Min(value = 1, message = "数量至少为1")
        private Integer quantity;

        @NotNull(message = "价格不能为空")
        @DecimalMin(value = "0.01", message = "价格最小0.01")
        private Double price;
    }

    // ===== 3. 自定义校验注解 =====

    /**
     * 自定义校验注解：校验密码强度
     */
    @Documented
    @Constraint(validatedBy = PasswordValidator.class)
    @Target({ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface StrongPassword {
        String message() default "密码强度不足";
        Class<?>[] groups() default {};
        Class<? extends jakarta.validation.Payload> [] payload() default {};
    }

    public static class PasswordValidator implements jakarta.validation.ConstraintValidator<StrongPassword, String> {
        @Override
        public boolean isValid(String password, jakarta.validation.ConstraintValidatorContext context) {
            if (password == null) return false;
            // 至少8位，包含数字和字母
            return password.length() >= 8
                    && password.matches(".*\\d.*")
                    && password.matches(".*[a-zA-Z].*");
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RegisterRequest {
        @NotBlank(message = "用户名不能为空")
        private String username;

        @NotBlank(message = "密码不能为空")
        @StrongPassword  // 使用自定义校验
        private String password;
    }

    // ===== 4. 全局异常处理 =====

    @org.springframework.web.bind.annotation.RestControllerAdvice
    public static class ValidationExceptionHandler {

        @org.springframework.web.bind.annotation.ExceptionHandler(jakarta.validation.ConstraintViolationException.class)
        public ResponseEntity<ErrorResponse> handleConstraintViolation(
                jakarta.validation.ConstraintViolationException e) {

            List<String> errors = e.getConstraintViolations().stream()
                    .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                    .toList();

            return ResponseEntity
                    .badRequest()
                    .body(new ErrorResponse(400, "参数校验失败", errors));
        }

        @org.springframework.web.bind.annotation.ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
        public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(
                org.springframework.web.bind.MethodArgumentNotValidException e) {

            List<String> errors = e.getBindingResult().getFieldErrors().stream()
                    .map(f -> f.getField() + ": " + f.getDefaultMessage())
                    .toList();

            return ResponseEntity
                    .badRequest()
                    .body(new ErrorResponse(400, "参数校验失败", errors));
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ErrorResponse {
        private int code;
        private String message;
        private List<String> errors;
    }

    // ===== 5. 测试接口 =====

    /**
     * POST /api/validation/user - 创建用户
     * 使用 @Valid 触发校验
     */
    @PostMapping("/user")
    public ResponseEntity<ApiResponse<CreateUserRequest>> createUser(
            @Valid @RequestBody CreateUserRequest request) {
        log.info("创建用户: {}", request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
               .body(ApiResponse.success(request));
    }

    /**
     * POST /api/validation/order - 创建订单
     * 测试嵌套对象校验
     */
    @PostMapping("/order")
    public ResponseEntity<ApiResponse<CreateOrderRequest>> createOrder(
            @Valid @RequestBody CreateOrderRequest request) {
        log.info("创建订单: {}", request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(request));
    }

    /**
     * POST /api/validation/register - 注册
     * 测试自定义校验注解
     */
    @PostMapping("/register")
   public ResponseEntity<ApiResponse<RegisterRequest>> register(
            @Valid @RequestBody RegisterRequest request) {
        log.info("注册: {}", request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(request));
    }

    /**
     * GET /api/validation/user/{id} - 路径参数校验
     */
    @GetMapping("/user/{id}")
    public ResponseEntity<ApiResponse<String>> getUser(
            @PathVariable @Min(1) Long id) {  // 路径参数校验
        return ResponseEntity.ok(ApiResponse.success("User: " + id));
    }

    /**
     * GET /api/validation/search - 查询参数校验
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<String>> search(
            @RequestParam @Size(min = 2, max = 50) String keyword,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size) {
        return ResponseEntity.ok(ApiResponse.success("Search: " + keyword));
    }

    // 辅助类
    @lombok.Data
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    @lombok.Builder
    public static class ApiResponse<T> {
        private int code;
        private String message;
        private T data;
        private LocalDateTime timestamp;

        public static <T> ApiResponse<T> success(T data) {
            return ApiResponse.<T>builder()
                    .code(200)
                   .message("success")
                    .data(data)
                    .timestamp(LocalDateTime.now())
                    .build();
        }
    }
}
