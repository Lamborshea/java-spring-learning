package com.learning;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * L16: Spring Data JPA 入门演示
 *
 * @author Java Learning Course
 */
@SpringBootApplication
public class JpaDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(JpaDemoApplication.class, args);
    }

    // ===== 1. 实体类 =====

    @Entity
    @Table(name = "t_user")
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class User {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(nullable = false, unique = true, length = 50)
        private String username;

        @Column(length = 100)
        private String email;

        @Column(name = "created_at")
        private java.time.LocalDateTime createdAt;

        @PrePersist
        protected void onCreate() {
            createdAt = java.time.LocalDateTime.now();
        }
    }

    // ===== 2. Repository 接口 =====

    /**
     * Repository 接口 - 数据访问层
     *
     * 继承层次：
     * Repository (标记接口)
     *   → CrudRepository (CRUD基本操作)
     *     → PagingAndSortingRepository (分页排序)
     *       → JpaRepository (JPA增强)
     */
    @Repository
    public interface UserRepository extends JpaRepository<User, Long> {

        // ===== 派生查询方法 =====
        // 根据方法名自动生成 SQL

        // SELECT * FROM t_user WHERE username = ?
        Optional<User> findByUsername(String username);

        // SELECT * FROM t_user WHERE email = ?
        Optional<User> findByEmail(String email);

        // SELECT * FROM t_user WHERE username LIKE %?%
        List<User> findByUsernameContaining(String keyword);

        // SELECT * FROM t_user WHERE age > ?
        List<User> findByEmailContaining(String email);

        // SELECT * FROM t_user WHERE username = ? AND email = ?
        Optional<User>findByUsernameAndEmail(String username, String email);

        // SELECT COUNT(*) FROM t_user WHERE username = ?
        boolean existsByUsername(String username);

        // SELECT COUNT(*) FROM t_user
        long count();

        // DELETE FROM t_user WHERE username = ?
        void deleteByUsername(String username);

        // ===== @Query 自定义查询 =====
        // 使用 JPQL 或原生 SQL

        @Query("SELECT u FROM User u WHERE u.username = :name")
        Optional<User> findByUsernameJPQL(@Param("name") String username);

        @Query(value = "SELECT * FROM t_user WHERE email LIKE %:email%", nativeQuery = true)
        List<User> findByEmailNative(@Param("email") String email);

        // ===== @Modifying 修改查询 =====
        @Query("UPDATE User u SET u.email = :email WHERE u.username = :username")
        @Modifying
        int updateEmailByUsername(@Param("username") String username, @Param("email") String email);
    }

    // ===== 3. Service 层 =====

    @Service
    @Transactional
    public static class UserService {

        private final UserRepository userRepository;

        public UserService(UserRepository userRepository) {
            this.userRepository = userRepository;
        }

        // 保存用户
        public User save(User user) {
            return userRepository.save(user);
        }

        // 根据ID查询
        public Optional<User> findById(Long id) {
            return userRepository.findById(id);
        }

        // 查询所有
        public List<User> findAll() {
            return userRepository.findAll();
        }

        // 根据用户名查询
        public Optional<User> findByUsername(String username) {
            return userRepository.findByUsername(username);
        }

        // 关键字搜索
        public List<User> search(String keyword) {
            return userRepository.findByUsernameContaining(keyword);
        }

        // 删除
        public void delete(Long id) {
            userRepository.deleteById(id);
        }

        // 批量保存
        public List<User> batchSave(List<User> users) {
            return userRepository.saveAll(users);
        }

        // 分页查询
        public org.springframework.data.domain.Page<User> findAll(
                org.springframework.data.domain.Pageable pageable) {
            return userRepository.findAll(pageable);
        }
    }

    // ===== 4. Entity 生命周期回调 =====

    /**
     * JPA 实体生命周期事件：
     *
     * @PrePersist - 保存前
     * @PostPersist - 保存后
     * @PreUpdate - 更新前
     * @PostUpdate - 更新后
     * @PreRemove - 删除前
     * @PostRemove - 删除后
     * @PostLoad - 加载后
     */

    @Entity
    @Data
    public static class Product {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private String name;
        private Double price;

        @PrePersist
        public void prePersist() {
            System.out.println("Product: 即将保存 - " + name);
        }

        @PostPersist
        public void postPersist() {
            System.out.println("Product: 已保存 - id=" + id);
        }

        @PostLoad
        public void postLoad() {
            System.out.println("Product: 已加载 - " + name);
        }
    }
}
