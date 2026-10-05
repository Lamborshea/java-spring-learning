# L12: 请求参数处理

> @PathVariable、@RequestParam、@RequestBody、@RequestHeader

## 本节课目标

- 掌握路径参数 @PathVariable
- 掌握查询参数 @RequestParam
- 掌握请求体 @RequestBody
- 掌握请求头 @RequestHeader

## 核心概念

### 1. 路径参数

```java
@GetMapping("/users/{id}")
public User getUser(@PathVariable Long id) { ... }
```

### 2. 查询参数

```java
@GetMapping("/users")
public List<User> searchUsers(
    @RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "10") int size
) { ... }
```

### 3. 请求体

```java
@PostMapping("/users")
public User createUser(@RequestBody CreateUserRequest request) { ... }
```

### 4. 请求头

```
@PostMapping("/users")
public User createUser(
    @RequestHeader("Authorization") String token,
    @RequestBody CreateUserRequest request
) { ... }
```

## 本课代码说明

已在 `spring-boot-webapp` 项目中实现：
- `HelloController` - 演示各种参数绑定
- `UserController` - 完整 CRUD 参数处理

## 运行方式

```bash
cd spring-boot-webapp
mvn spring-boot:run
```

## 下节课预告

L13: 响应统一封装
