package com.learning.webapp.repository;

import com.learning.webapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 用户 Repository 接口
 *
 * 类似于 Node.js 中使用 Mongoose 的 Model:
 * const User = mongoose.model('User', UserSchema);
 * User.find(), User.findById(), User.save()...
 *
 * Spring Data JPA 的强大之处:
 * 只需要定义接口，Spring 会自动实现！
 * 不需要写任何 SQL 或实现类
 *
 * JpaRepository<T, ID> 提供的常用方法:
 * - save(entity): 保存或更新
 * - findById(id): 根据 ID 查询
 * - findAll(): 查询所有
 * - deleteById(id): 删除
 * - count(): 计数
 *
 * @Repository: 标记为 Spring 容器中的 Repository 组件
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * 根据用户名查询用户
     *
     * 这是一个"派生查询"（Derived Query）
     * Spring Data JPA 会根据方法名自动生成 SQL:
     * SELECT * FROM t_user WHERE username = ?
     *
     * 命名规范支持:
     * - findBy + 字段名: 根据字段查询
     * - findBy + 字段名 + And + 字段名: 多条件查询
     * - findBy + 字段名 + In: IN 查询
     * - countBy + 字段名: 计数
     * - existsBy + 字段名: 判断是否存在
     *
     * 类似于 Mongoose 的:
     * User.findOne({ username: 'xxx' })
     */
    Optional<User> findByUsername(String username);

    /**
     * 判断用户名是否存在
     * 生成的 SQL: SELECT COUNT(*) > 0 FROM t_user WHERE username = ?
     */
    boolean existsByUsername(String username);

    /**
     * 判断邮箱是否存在
     */
    boolean existsByEmail(String email);

    /**
     * 根据邮箱查询用户
     */
    Optional<User> findByEmail(String email);
}
