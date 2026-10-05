package com.learning;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.MissingServletRequestParameterException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * L14: 异常处理演示
 *
 * @author Java Learning Course
 */
@RestController
@RequestMapping("/api/exception")
public class ExceptionController {

    // ===== 1. 自定义业务异常 =====

    /**
     * 自定义业务异常
     */
    public static class BusinessException extends RuntimeException {
        private final int code;

        public BusinessException(String message) {
            super(message);
            this.code = 400;
        }

        public BusinessException(int code, String message) {
            super(message);
            this.code = code;
        }

        public int getCode() { return code; }
    }

    // ===== 2. 全局异常处理器 =====

    /**
     * 全局异常处理器
     * 使用 @ControllerAdvice 拦截所有异常
     */
    @Slf4j
    @RestControllerAdvice
    public static class GlobalExceptionHandler {

        /**
         * 处理业务异常
         */
        @ExceptionHandler(BusinessException.class)
        public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException e) {
            log.warn("业务异常: {}", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getCode(), e.getMessage()));
        }

        /**
         * 处理参数校验异常
         */
        @ExceptionHandler(IllegalArgumentException.class)
        public ResponseEntity<ApiResponse<Void>> handleIllegalArgument(IllegalArgumentException e) {
            log.warn("参数异常: {}", e.getMessage());
            return ResponseEntity
                    .badRequest()
                    .body(ApiResponse.error(400, e.getMessage()));
        }

        /**
         * 处理参数缺失异常
         */
        @ExceptionHandler(MissingServletRequestParameterException.class)
        public ResponseEntity<ApiResponse<Void>> handleMissingParam(MissingServletRequestParameterException e) {
            log.warn("缺少参数: {}", e.getParameterName());
            return ResponseEntity
                    .badRequest()
                    .body(ApiResponse.error(400, "缺少参数: " + e.getParameterName()));
        }

        /**
         * 处理类型转换异常
         */
        @ExceptionHandler(MethodArgumentTypeMismatchException.class)
        public ResponseEntity<ApiResponse<Void>> handleTypeMismatch(MethodArgumentTypeMismatchException e) {
            log.warn("参数类型错误: {}", e.getMessage());
            return ResponseEntity
                    .badRequest()
                    .body(ApiResponse.error(400, "参数类型错误: " + e.getName()));
        }

        /**
         * 处理所有未捕获的异常
         */
        @ExceptionHandler(Exception.class)
        public ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
            log.error("系统异常", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "系统繁忙，请稍后重试"));
        }
    }

    // ===== 3. 统一响应格式 =====

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ApiResponse<T> {
        private int code;
        private String message;
        private T data;
        private LocalDateTime timestamp;

        public static <T> ApiResponse<T> error(int code, String message) {
            return ApiResponse.<T>builder()
                    .code(code)
                    .message(message)
                    .timestamp(LocalDateTime.now())
                   .build();
        }
    }

    // ===== 4. 测试接口 =====

    /**
     * 测试抛出业务异常
     */
    @GetMapping("/business-error")
    public void throwBusinessException() {
        throw new BusinessException("业务处理失败");
    }

    /**
     * 测试抛出参数异常
     */
    @GetMapping("/illegal-argument")
    public void throwIllegalArgument(@RequestParam(required = false) String param) {
        if (param == null) {
            throw new IllegalArgumentException("参数不能为空");
        }
    }

    /**
     * 测试触发运行时异常
     */
    @GetMapping("/runtime-error")
    public void throwRuntimeException() {
        throw new NullPointerException("空指针异常");
    }

    /**
     * 测试404异常
     */
    @GetMapping("/not-found")
    public void throwNotFound() {
        throw new ResourceNotFoundException("资源未找到");
    }

    /**
     * 自定义 404 异常
     */
    public static class ResourceNotFoundException extends RuntimeException {
        public ResourceNotFoundException(String message) {
            super(message);
        }
    }

    /**
     * 全局异常处理器中添加 404 处理
     */
    @org.springframework.web.bind.annotation.ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotFound(ResourceNotFoundException e) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(404, e.getMessage()));
    }

    // 主方法
    public static void main(String[] args) {
        System.out.println("异常处理示例项目");
        System.out.println("启动后访问各个接口测试异常处理");
    }
}
