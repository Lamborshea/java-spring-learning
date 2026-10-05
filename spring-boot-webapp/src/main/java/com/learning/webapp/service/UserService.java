package com.learning.webapp.service;

import com.learning.webapp.entity.User;
import com.learning.webapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 用户 Service 业务逻辑层
 *
 * 架构说明（典型的三层架构）:
 * Controller -> Service -> Repository
 *
 * 类似于 Node.js 中:
 * Controller: 处理 HTTP 请求，调用 Service
 * Service: 业务逻辑处理，调用 Repository
 * Repository: 数据访问，类似于 Mongoose Model
 *
 * @Service: 标记为 Spring 容器中的 Service 组件
 * @RequiredArgsConstructor: Lombok 注解，自动生成含参构造方法
 *                          （用于实现依赖注入，类似于 JS 的 constructor 注入）
 */
@Service
@RequiredArgsConstructor  // 生成: public UserService(UserRepository userRepository) { this.userRepository = userRepository; }
@Transactional           // 所有方法自动开启事务（类似数据库事务）
public class UserService {

    // 依赖注入：Repository 层
    // 类似于 JS 中的: const userRepository = require('./repository/UserRepository');
    private final UserRepository userRepository;

    /**
     * 创建用户
     *
     * 类似于 Mongoose 的:
     * async function createUser(data) {
     *     const existing = await User.findOne({ username: data.username });
     *     if (existing) throw new Error('用户名已存在');
     *     return await User.create(data);
     * }
     */
    public User createUser(String username, String email) {
        // 检查用户名是否存在
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("用户名已存在");
        }

        // 检查邮箱是否已被使用
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("邮箱已被使用");
        }

        // 构建用户对象并保存
        User user = User.builder()
                .username(username)
                .email(email)
                .build();

        return userRepository.save(user);
    }

    /**
     * 查询所有用户
     * 相当于: User.find()
     */
    public List<User> findAll() {
        return userRepository.findAll();
    }

    /**
     * 根据 ID 查询用户
     * 相当于: User.findById(id)
     */
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
    }

    /**
     * 根据用户名查询用户
     * 使用我们自定义的查询方法
     */
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
    }

    /**
     * 更新用户
     * 相当于: User.findByIdAndUpdate(id, data, { new: true })
     */
    public User updateUser(Long id, String email) {
        User user = findById(id);
        user.setEmail(email);
        return userRepository.save(user);  // save 会自动判断是插入还是更新
    }

    /**
     * 删除用户
     * 相当于: User.findByIdAndDelete(id)
     */
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new IllegalArgumentException("用户不存在");
        }
        userRepository.deleteById(id);
    }

    /**
     * 统计用户总数
     * 相当于: User.countDocuments()
     */
    public long count() {
        return userRepository.count();
    }
}
