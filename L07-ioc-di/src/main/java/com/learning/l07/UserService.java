package com.learning.l07;

import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 业务层：被 Controller 调用
 * 演示 @Service 注解和构造方法注入
 */
@Service
public class UserService {

    private final UserRepository userRepository;

    // 构造方法注入（推荐）
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
        System.out.println("[UserService] 构造：注入了 UserRepository");
    }

    public void createUser(String name, String email) {
        userRepository.save(new User(name, email));
    }

    public List<User> listUsers() {
        return userRepository.findAll();
    }
}
