# L22: API文档

> Swagger、OpenAPI、Knife4j

## 本节课目标

- 理解 OpenAPI 规范
- 掌握 Swagger/OpenAPI 注解
- 学会使用 Knife4j 增强文档
- 理解 API 版本管理

## 核心概念

### 1. OpenAPI 规范

```
┌─────────────────────────────────────────────────┐
│ OpenAPI 3.0 规范                                 │
├─────────────────────────────────────────────────┤
│ openapi: 3.0.0                                  │
│ info:                                           │
│   title: 用户管理API                             │
│   version: 1.0.0                                │
│ paths:                                          │
│   /users:                                       │
│     get:                                        │
│       summary: 获取用户列表                      │
│       responses:                                │
│         200:                                    │
│           description: 成功                     │
└─────────────────────────────────────────────────┘
```

### 2. 常用注解

| 注解 | 说明 |
|------|------|
| @Api | 控制器说明 |
| @ApiOperation | 接口说明 |
| @ApiParam | 参数说明 |
| @ApiModel | 实体类说明 |
| @ApiModelProperty | 字段说明 |

### 3. 访问地址

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- Knife4j: `http://localhost:8080/doc.html`

## 课程总结

完成本课程后，你将掌握：
- Java 基础语法和面向对象
- Spring Boot 核心概念
- RESTful API 设计
- 数据库访问（JPA）
- 事务管理
- 异步编程
- 缓存
- 安全认证
- API 文档
