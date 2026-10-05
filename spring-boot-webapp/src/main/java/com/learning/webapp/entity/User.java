package com.learning.webapp.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户实体类
 *
 * 与 JavaScript 的对比:
 * JS: class User { constructor(data) { this.id = data.id; ... } }
 * Java: 使用 JPA 注解映射到数据库表
 *
 * Lombok 注解说明（自动生成样板代码）:
 * - @Data: 生成 getter/setter/toString/equals/hashCode
 * - @NoArgsConstructor: 生成无参构造方法
 * - @AllArgsConstructor: 生成全参构造方法
 * - @Builder: 生成 Builder 模式建造者
 *
 * JPA 注解说明:
 * - @Entity: 标记为 JPA 实体类
 * - @Table: 指定数据库表名
 * - @Id: 主键字段
 * - @GeneratedValue: 主键自动生成策略
 * - @Column: 映射到数据库列
 */
@Data               // 相当于同时添加 @Getter @Setter @ToString @EqualsAndHashCode
@NoArgsConstructor  // 生成: public User() {}
@AllArgsConstructor // 生成: public User(Long id, String name, ...) {}
@Builder            // 生成: User.builder().id(1L).name("John").build()
@Entity
@Table(name = "t_user")
public class User {

    /**
     * 主键 ID
     * 类似于 JS 中我们手动维护的 _id 字段
     *
     * @GeneratedValue(strategy = GenerationType.IDENTITY)
     * 表示使用数据库的自增主键（MySQL 的 AUTO_INCREMENT）
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 用户名
     * nullable = false 表示该字段不能为空
     * unique = true 表示该字段值必须唯一
     */
    @Column(nullable = false, unique = true, length = 50)
    private String username;

    /**
     * 邮箱
     */
    @Column(length = 100)
    private String email;

    /**
     * 创建时间
     * updatable = false 表示更新时不修改该字段
     */
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * JPA 持久化回调：插入前自动设置创建时间
     * 类似于 JS 中的 mongoose middleware: UserSchema.pre('save', ...)
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    /**
     * JPA 持久化回调：更新前自动设置更新时间
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
