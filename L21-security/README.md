# L21: 安全

> Spring Security、JWT、密码加密

## 本节课目标

- 理解 Spring Security 架构
- 掌握 JWT 工作原理
- 学会实现登录认证和授权
- 理解密码加密（BCrypt）

## 核心概念

### 1. Spring Security 核心组件

| 组件 | 说明 | JS对比 |
|------|------|--------|
| SecurityFilterChain | 安全过滤器链 | 类似于中间件 |
| UserDetailsService | 用户信息加载 | 类似于用户仓库 |
| AuthenticationProvider | 认证提供者 | 验证逻辑 |
| AccessDecisionManager | 授权决策 | 权限判断 |

### 2. JWT 结构

```
┌─────────────────────────────────────┐
│ Header (头部) - 算法和类型            │
├─────────────────────────────────────┤
│ Payload (载荷) - 用户信息             │
├─────────────────────────────────────┤
│ Signature (签名) - 验证令牌           │
└─────────────────────────────────────┘

Header.Payload.Signature
```

### 3. 认证流程

```
请求 ──> Filter ──> AuthenticationManager ──> UserDetailsService
                          │
                          ▼
                    Database ──> UserDetails
                          │
                          ▼
                    Authentication ──> SecurityContext
```

## 下节课预告

L22: API文档 - Swagger、OpenAPI
