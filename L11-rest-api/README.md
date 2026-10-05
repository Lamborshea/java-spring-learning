# L11: RESTful API 设计

> REST 架构风格与 Spring Boot REST API 最佳实践

## 本节课目标

- 理解 REST 架构风格
- 掌握资源命名规范
- 正确使用 HTTP 方法
- 返回合适的 HTTP 状态码

## REST 核心原则

### 1. 资源命名

| 场景 | 错误方式 | 正确方式 |
|------|----------|----------|
| 用户列表 | /getUsers | /users |
| 用户详情 | /getUserById/{id} | /users/{id} |
| 创建用户 | /createUser | POST /users |
| 删除用户 | /deleteUser/{id} | DELETE /users/{id} |

### 2. HTTP 方法对应

| 方法 | 用途 | 幂等 |
|------|------|------|
| GET | 查询 | 是 |
| POST | 创建 | 否 |
| PUT | 完整更新 | 是 |
| PATCH | 部分更新 | 否 |
| DELETE | 删除 | 是 |

### 3. 状态码

| 状态码 | 含义 |
|--------|------|
| 200 | 成功 |
| 201 | 创建成功 |
| 204 | 删除成功（无返回内容）|
| 400 | 请求参数错误 |
| 401 | 未认证 |
| 403 | 无权限 |
| 404 | 资源不存在 |
| 500 | 服务器错误 |

## 本课代码说明

已在 `spring-boot-webapp` 项目中实现了完整的 REST API：
- `UserController` - 用户管理接口
- `ApiResponse` - 统一响应格式

## 运行方式

```bash
cd spring-boot-webapp
mvn spring-boot:run

# 测试 API
curl http://localhost:8080/api/users
```

## 下节课预告

L12: 请求参数处理 - @PathVariable、@RequestParam、@RequestBody
