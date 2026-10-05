package com.learning.webapp.exception;

import com.learning.webapp.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * 全局异常处理器
 *
 * 作用：统一捕获和处理整个应用的异常，返回规范的错误响应
 *
 * 类似于 Node.js Express 的错误中间件:
 * app.use((err, req, res, next) => {
 *     console.error(err.stack);
 *     res.status(500).json({ code: 500, message: err.message });
 * });
 *
 * @RestControllerAdvice: 相当于 @ControllerAdvice + @ResponseBody
 *                        对所有 @RestController 生效
 */
@Slf4j  // Lombok: 自动生成 log 字段，用于日志记录
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理业务异常（如用户名已存在）
     *
     * @ExceptionHandler: 指定要处理的异常类型
     * @ResponseStatus: 指定返回的 HTTP 状态码
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handleIllegalArgumentException(IllegalArgumentException e) {
        log.warn("业务异常: {}", e.getMessage());
        return ApiResponse.badRequest(e.getMessage());
    }

    /**
     * 处理参数校验异常
     *
     * 当 DTO 上的 @NotBlank、@Email 等校验失败时抛出
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Map<String, String>> handleValidationException(
            MethodArgumentNotValidException e) {

        Map<String, String> errors = new HashMap<>();

        // 收集所有校验错误
        e.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        log.warn("参数校验失败: {}", errors);
        return ApiResponse.<Map<String, String>>builder()
                .code(400)
                .message("参数校验失败")
                .data(errors)
                .build();
    }

    /**
     * 处理所有未捕获的异常（兜底）
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse<Void> handleException(Exception e) {
        log.error("系统异常", e);  // 打印完整堆栈信息
        return ApiResponse.serverError("系统繁忙，请稍后重试");
    }
}
