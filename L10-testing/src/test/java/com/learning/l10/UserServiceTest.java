package com.learning.l10;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * L10: 单元测试演示 - Service 层测试
 *
 * 使用 Mockito 进行单元测试，不依赖真实数据库
 *
 * @author Java Learning Course
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UserService 单元测试")
public class UserServiceTest {

    // ===== Mock 依赖 =====
    // 使用 @Mock 创建模拟对象
    @Mock
    private TestingApplication.UserRepository userRepository;

    // ===== 注入被测对象 =====
    // 使用 @InjectMocks 将 Mock 注入到被测试的 Service
    @InjectMocks
    private TestingApplication.UserService userService;

    // ===== 测试数据 =====
    private TestingApplication.User testUser;

    @BeforeEach
    void setUp() {
        // 准备测试数据
        testUser = TestingApplication.User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .status(1)
                .build();
    }

    // ===== 1. 成功场景测试 =====

    @Test
    @DisplayName("创建用户 - 成功")
    void testCreateUser_Success() {
        // 准备：设置 Mock 行为
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.save(any(TestingApplication.User.class)))
                .thenAnswer(invocation -> {
                    TestingApplication.User user = invocation.getArgument(0);
                    user.setId(1L);  // 模拟数据库生成 ID
                    return user;
                });

        // 执行：调用被测试的方法
        TestingApplication.User result = userService.createUser("newuser", "new@example.com");

        // 验证：断言结果
        assertNotNull(result);
        assertEquals("newuser", result.getUsername());
        assertEquals("new@example.com", result.getEmail());
        assertEquals(1, result.getStatus());

        // 验证：确认 Mock 被调用
        verify(userRepository, times(1)).existsByUsername("newuser");
        verify(userRepository, times(1)).save(any(TestingApplication.User.class));
    }

    // ===== 2. 业务异常测试 =====

    @Test
    @DisplayName("创建用户 - 用户名已存在")
    void testCreateUser_UsernameExists() {
        // 准备：用户名已存在
        when(userRepository.existsByUsername("existing")).thenReturn(true);

        // 执行 & 验证：应该抛出异常
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.createUser("existing", "test@example.com")
        );

        // 验证异常消息
        assertTrue(exception.getMessage().contains("用户名已存在"));

        // 验证：save 方法不应该被调用
        verify(userRepository, never()).save(any());
    }

    // ===== 3. 查询测试 =====

    @Test
    @DisplayName("获取用户 - 成功")
    void testGetUser_Found() {
        // 准备
        when(userRepository.findById(1L)).Return(Optional.of(testUser));

        // 执行
        TestingApplication.User result = userService.getUser(1L);

        // 验证
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("testuser", result.getUsername());
    }

    @Test
    @DisplayName("获取用户 - 不存在")
    void testGetUser_NotFound() {
        // 准备
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // 执行 & 验证
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.getUser(999L)
        );

        assertTrue(exception.getMessage().contains("用户不存在"));
    }

    // ===== 4. 删除测试 =====

    @Test
    @DisplayName("删除用户 - 成功")
    void testDeleteUser_Success() {
        // 准备
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        doNothing().when(userRepository).deleteById(1L);

        // 执行
        assertDoesNotThrow(() -> userService.deleteUser(1L));

        // 验证
        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("删除用户 - 用户不存在")
    void testDeleteUser_NotFound() {
        // 准备
        when(userRepository.findById(999L)).Return(Optional.empty());

        // 执行 & 验证
        assertThrows(IllegalArgumentException.class,
                () -> userService.deleteUser(999L));

        verify(userRepository, never()).deleteById(any());
    }

    // ===== 5. 状态变更测试 =====

    @Nested
    @DisplayName("用户状态变更测试")
    class UserStatusTest {

        @Test
        @DisplayName("激活用户")
        void testActivateUser() {
            // 准备
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(userRepository.save(any())).thenReturn(testUser);

            // 执行
            TestingApplication.User result = userService.activateUser(1L);

            // 验证
            assertEquals(1, result.getStatus());
            verify(userRepository).save(testUser);
        }

        @Test
        @DisplayName("禁用用户")
        void testDeactivateUser() {
            // 准备
            when(userRepository.findById(1L)).Return(Optional.of(testUser));
            when(userRepository.save(any())).thenReturn(testUser);

            // 执行
            TestingApplication.User result = userService.deactivateUser(1L);

            // 验证
            assertEquals(0, result.getStatus());
            verify(userRepository).save(testUser);
        }
    }

    // ===== 6. 查询所有测试 =====

    @Test
    @DisplayName("获取所有用户")
    void testGetAllUsers() {
        // 准备
        List<TestingApplication.User> users = List.of(
                TestingApplication.User.builder().id(1L).username("user1").build(),
                TestingApplication.User.builder().id(2L).username("user2").build()
        );
        when(userRepository.findAll()).thenReturn(users);

        // 执行
        List<TestingApplication.User> result = userService.getAllUsers();

        // 验证
        assertEquals(2, result.size());
        verify(userRepository, times(1)).findAll();
    }

    // ===== 7. 验证调用次数 =====

    @Test
    @DisplayName("验证 Mock 调用次数")
    void testVerifyCallCount() {
        // 准备
        when(userRepository.existsByUsername("user")).thenReturn(false);
        when(userRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        // 执行：创建多个用户
        userService.createUser("user1", "user1@test.com");
        userService.createUser("user2", "user2@test.com");
        userService.createUser("user3", "user3@test.com");

        // 验证：检查调用次数
        verify(userRepository, times(3)).save(any(TestingApplication.User.class));
        verify(userRepository, times(3)).existsByUsername(anyString());
    }
}
