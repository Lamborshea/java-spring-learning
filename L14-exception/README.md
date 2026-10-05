# L14: 异常处理

> 全局异常处理器 @ControllerAdvice

## 本节课目标

- 掌握 @ControllerAdvice 全局异常处理
- 自定义业务异常
- 统一错误响应格式
- 处理不同类型的异常

## 核心概念

### 全局异常处理

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ApiResponse<Void> handle(IllegalArgumentException e) {
        return ApiResponse.error(400, e.getMessage());
    }
}
```

## 本课代码说明

已在 `spring-boot-webapp` 项目中实现：
- `GlobalExceptionHandler` - 全局异常处理

## 下节课预告

L15: 参数校验 - @Valid、@NotBlank、@Email 等
