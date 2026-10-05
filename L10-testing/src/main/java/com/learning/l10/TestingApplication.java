package com.learning.l10;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * L10: 单元测试演示 - 服务类和实体
 *
 * @author Java Learning Course
 */
public class TestingApplication {

    // ===== 实体类 =====

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class User {
        private Long id;
        private String username;
        private String email;
        private int status; // 0: 禁用, 1: 正常
    }

    // ===== Repository 接口 =====

    @Repository
    public interface UserRepository {
        Optional<User> findById(Long id);
        List<User> findAll();
        User save(User user);
        void deleteById(Long id);
        boolean existsByUsername(String username);
    }

    // ===== Service 类 =====

    @Service
    @AllArgsConstructor
    public static class UserService {

        private final UserRepository userRepository;

        /**
         * 创建用户
         * 业务规则：用户名不能重复
         */
        public User createUser(String username, String email) {
            // 检查用户名是否已存在
            if (userRepository.existsByUsername(username)) {
                throw new IllegalArgumentException("用户名已存在: " + username);
            }

            // 创建用户
            User user = User.builder()
                    .username(username)
                    .email(email)
                    .status(1)
                    .build();

            return userRepository.save(user);
        }

        /**
         * 获取用户
         */
        public User getUser(Long id) {
            return userRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("用户不存在: " + id));
        }

        /**
         * 获取所有用户
         */
        public List<User> getAllUsers() {
            return userRepository.findAll();
        }

        /**
         * 删除用户
         */
        public void deleteUser(Long id) {
            if (!userRepository.findById(id).isPresent()) {
                throw new IllegalArgumentException("用户不存在: " + id);
            }
            userRepository.deleteById(id);
        }

        /**
         * 激活用户
         */
        public User activateUser(Long id) {
            User user = getUser(id);
            user.setStatus(1);
            return userRepository.save(user);
        }

        /**
         * 禁用用户
         */
        public User deactivateUser(Long id) {
            User user = getUser(id);
            user.setStatus(0);
            return userRepository.save(user);
        }
    }

    // ===== 主方法（演示）=====

    public static void main(String[] args) {
        System.out.println("单元测试示例项目");
        System.out.println("运行: mvn test -Dtest=UserServiceTest");
    }
}
