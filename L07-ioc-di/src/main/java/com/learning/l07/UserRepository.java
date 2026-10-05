package com.learning.l07;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 数据访问层：操作"数据库"
 * 这里用内存 List 模拟数据库
 *
 * 演示 @Repository 注解
 */
@Repository
public class UserRepository {

    private final List<User> users = new ArrayList<>();
    private final AtomicLong idGen = new AtomicLong(1);

    public UserRepository() {
        System.out.println("[UserRepository] 构造：创建了一个 Repository");
    }

    public User save(User user) {
        if (user.getId() == null) {
            user.setId(idGen.getAndIncrement());
            users.add(user);
        } else {
            users.removeIf(u -> u.getId().equals(user.getId()));
            users.add(user);
        }
        return user;
    }

    public Optional<User> findById(Long id) {
        return users.stream().filter(u -> u.getId().equals(id)).findFirst();
    }

    public List<User> findAll() {
        return new ArrayList<>(users);
    }
}
