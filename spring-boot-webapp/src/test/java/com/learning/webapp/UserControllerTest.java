package com.learning.webapp;

import com.learning.webapp.entity.User;
import com.learning.webapp.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 用户控制器集成测试
 *
 * @SpringBootTest: 启动完整的 Spring 上下文进行测试
 * @AutoConfigureMockMvc: 自动配置 MockMvc，用于模拟 HTTP 请求
 *
 * 类似于 Node.js 的 supertest:
 * const request = require('supertest');
 * request(app).get('/api/users').expect(200);
 */
@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;  // 模拟 HTTP 请求

    @Autowired
    private UserRepository userRepository;  // 直接操作数据库验证

    /**
     * 测试创建用户接口
     *
     * mockMvc.perform(): 执行请求
     * .contentType(): 设置请求内容类型
     * .content(): 设置请求体
     * .andExpect(): 断言响应
     */
    @Test
    public void testCreateUser() throws Exception {
        // 清理测试数据
        userRepository.deleteAll();

        // 准备请求体
        String requestBody = """
                {
                    "username": "testuser",
                    "email": "test@example.com"
                }
                """;

        // 执行 POST 请求
        ResultActions result = mockMvc.perform(
                post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
        );

        // 验证响应
        result.andExpect(status().isOk())                    // HTTP 200
                .andExpect(jsonPath("$.code").value(200))    // 响应码 200
                .andExpect(jsonPath("$.data.username").value("testuser"))
                .andExpect(jsonPath("$.data.email").value("test@example.com"))
                .andExpect(jsonPath("$.data.id").exists());  // ID 应该自动生成

        // 验证数据库
        assertEquals(1, userRepository.count());
        assertTrue(userRepository.existsByUsername("testuser"));
    }

    /**
     * 测试查询所有用户
     */
    @Test
    public void testGetAllUsers() throws Exception {
        // 准备测试数据
        userRepository.deleteAll();
        userRepository.save(User.builder()
                .username("user1")
                .email("user1@example.com")
               .build());
        userRepository.save(User.builder()
                .username("user2")
                .email("user2@example.com")
                .build());

        // 执行 GET 请求
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].username").value("user1"))
                .andExpect(jsonPath("$.data[1].username").value("user2"));
    }

    /**
     * 测试参数校验 - 用户名为空
     */
    @Test
    public void testCreateUserWithEmptyUsername() throws Exception {
        String requestBody = """
                {
                    "username": "",
                    "email": "test@example.com"
                }
                """;

        mockMvc.perform(
                post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
        )
        .andExpect(status().isBadRequest())  // HTTP 400
        .andExpect(jsonPath("$.code").value(400));
    }

    /**
     * 测试参数校验 - 邮箱格式错误
     */
    @Test
    public void testCreateUserWithInvalidEmail() throws Exception {
        String requestBody = """
                {
                    "username": "testuser",
                    "email": "not-an-email"
                }
                """;

        mockMvc.perform(
                post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
        )
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value(400));
    }

    /**
     * 测试删除用户
     */
    @Test
    public void testDeleteUser() throws Exception {
        // 准备测试数据
        userRepository.deleteAll();
        User user = userRepository.save(User.builder()
                .username("todelete")
                .email("delete@example.com")
                .build());
        Long userId = user.getId();

        // 执行 DELETE 请求
        mockMvc.perform(delete("/api/users/" + userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        // 验证数据库
        assertFalse(userRepository.existsById(userId));
    }
}
