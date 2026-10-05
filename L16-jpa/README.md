# L16: Spring Data JPA 入门

> 快速掌握 JPA + Hibernate 数据访问

## 本节课目标

- 理解 JPA 和 Hibernate 关系
- 掌握 Repository 接口定义
- 学会使用派生查询方法
- 理解 Entity 生命周期

## 核心概念

### 1. Repository 层次

```
CrudRepository → PagingAndSortingRepository → JpaRepository
```

### 2. 派生查询方法命名规范

```java
findByUsername(String username)        // WHERE username = ?
findByAgeGreaterThan(int age)          // WHERE age > ?
findByNameContaining(String keyword)   // WHERE name LIKE %?%
findByAgeBetween(int min, int max)     // WHERE age BETWEEN ? AND ?
```

### 3. Entity 注解

```java
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;
}
```

## 本课代码说明

已在 `spring-boot-webapp` 项目中实现：
- `User` - 实体类
- `UserRepository` - Repository 接口

## 下节课预告

L17: 实体映射与关系 - @OneToMany、@ManyToOne、@ManyToMany
