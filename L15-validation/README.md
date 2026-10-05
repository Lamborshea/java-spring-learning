# L15: 参数校验

> Bean Validation (@Valid, @NotBlank, @Email 等)

## 本节课目标

- 掌握 JSR-380 注解
- 使用 @Valid 触发校验
- 自定义校验规则
- 分组校验

## 常用校验注解

| 注解 | 说明 |
|------|------|
| @NotNull | 不能为 null |
| @NotEmpty | 不能为空（字符串/集合）|
| @NotBlank | 不能为空字符串 |
| @Email | 邮箱格式 |
| @Size | 长度范围 |
| @Min/@Max | 数值范围 |
| @Pattern | 正则表达式 |
| @Valid | 嵌套对象校验 |

## 本课代码说明

已在 `spring-boot-webapp` 项目中实现：
- `UserController.CreateUserRequest` - DTO 校验

## 运行方式

```bash
cd spring-boot-webapp
mvn spring-boot:run

# 测试校验失败
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{"username":"", "email":"invalid"}'
```

## 下节课预告

L16: Spring Data JPA 入门 - Repository、CrudRepository、JPQL
