package com.tancong.core.service;

import com.tancong.core.entity.User;
import com.tancong.core.entity.dto.UserDTO;
import com.tancong.core.entity.enums.StatusEnum;
import com.tancong.core.mapper.UserMapper;
import com.tancong.core.service.impl.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * UserService 单元测试
 */
@ExtendWith(MockitoExtension.class)  // 启用 Mockito
@DisplayName("用户服务测试")
class UserServiceTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private UserDTO testUserDTO;

    @BeforeEach
    void setUp() {
        // 准备测试数据
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setPassword("$2a$10$N.zmdr9k7uOCQb376NoUnu");  // BCrypt 加密后的密码
        testUser.setStatus(StatusEnum.ENABLED);

        testUserDTO = new UserDTO();
        testUserDTO.setId(1L);
        testUserDTO.setUsername("testuser");
        testUserDTO.setPassword("$2a$10$N.zmdr9k7uOCQb376NoUnu");
        testUserDTO.setStatus(StatusEnum.ENABLED);
    }

    @Test
    @DisplayName("测试：密码加密")
    void testEncodePassword() {
        System.out.println("\n========================================");
        System.out.println("开始测试：密码加密");
        System.out.println("========================================");

        // Given
        String rawPassword = "123456";
        String encodedPassword = "$2a$10$N.zmdr9k7uOCQb376NoUnu";
        System.out.println("1. 准备数据");
        System.out.println("   原始密码: " + rawPassword);
        System.out.println("   期望密码: " + encodedPassword);

        when(passwordEncoder.encode(rawPassword)).thenReturn(encodedPassword);
        System.out.println("2. Mock 设置完成");

        // When
        System.out.println("3. 执行方法...");
        String result = userService.encodePassword(rawPassword);
        System.out.println("4. 得到结果: " + result);

        // Then
        System.out.println("5. 开始验证...");
        assertNotNull(result);
        System.out.println("   ✅ 结果不为 null");

        assertEquals(encodedPassword, result);
        System.out.println("   ✅ 结果正确");

        verify(passwordEncoder, times(1)).encode(rawPassword);
        System.out.println("   ✅ 方法被调用了 1 次");

        System.out.println("\n🎉 测试通过！");
        System.out.println("========================================\n");
    }

    @Test
    @DisplayName("测试：密码验证 - 成功")
    void testEqualsPassword_Success() {
        // Given
        String rawPassword = "123456";
        String encodedPassword = "$2a$10$N.zmdr9k7uOCQb376NoUnu";

        // 创建测试用户
        User user = new User();
        user.setPassword(encodedPassword);

        // Mock passwordEncoder.matches() 返回 true
        when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(true);

        // When
        boolean result = userService.equalsPassword(rawPassword, user);

        // Then
        assertTrue(result);
        verify(passwordEncoder).matches(rawPassword, encodedPassword);
    }

    @Test
    @DisplayName("测试：密码验证 - 失败")
    void testEqualsPassword_Fail() {
        // Given
        String rawPassword = "wrongpassword";
        String encodedPassword = "$2a$10$N.zmdr9k7uOCQb376NoUnu";

        // 创建测试用户
        User user = new User();
        user.setPassword(encodedPassword);

        // Mock passwordEncoder.matches() 返回 false
        when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(false);

        // When
        boolean result = userService.equalsPassword(rawPassword, user);

        // Then
        assertFalse(result);
        verify(passwordEncoder).matches(rawPassword, encodedPassword);
    }

    @Test
    @DisplayName("测试：根据用户名查询用户 - 成功")
    void testGetByUsername_Success() {
        // Given
        when(userMapper.selectByUsername("testuser")).thenReturn(testUserDTO);

        // When
        UserDTO result = userService.getByUsername("testuser");

        // Then
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        verify(userMapper).selectByUsername("testuser");
    }

    @Test
    @DisplayName("测试：根据用户名查询用户 - 用户不存在")
    void testGetByUsername_NotFound() {
        // Given
        when(userMapper.selectByUsername("notexist")).thenReturn(null);

        // When
        UserDTO result = userService.getByUsername("notexist");

        // Then
        assertNull(result);
    }

}
