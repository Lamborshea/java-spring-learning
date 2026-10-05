package com.learning.webapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 统一 API 响应结构
 *
 * 类似于许多 JS API 框架的标准响应格式:
 * {
 *   "code": 200,
 *   "message": "success",
 *   "data": {...}
 * }
 *
 * 使用泛型 T 来支持不同类型的 data
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiResponse<T> {

    // 响应码，200 表示成功
    private int code;

    // 响应消息
    private String message;

    // 响应数据（泛型，支持任意类型）
    private T data;

    // ===== 静态工厂方法 =====

    /**
     * 成功响应
     */
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .code(200)
                .message("success")
                .data(data)
                .build();
    }

    /**
     * 成功响应（无数据）
     */
    public static <T> ApiResponse<T> success() {
        return success(null);
    }

    /**
     * 错误响应
     */
    public static <T> ApiResponse<T> error(int code, String message) {
        return ApiResponse.<T>builder()
                .code(code)
                .message(message)
                .data(null)
                .build();
    }

    /**
     * 业务错误响应（400 系列）
     */
    public static <T> ApiResponse<T> badRequest(String message) {
        return error(400, message);
    }

    /**
     * 服务器错误响应（500 系列）
     */
    public static <T> ApiResponse<T> serverError(String message) {
        return error(500, message);
    }
}
