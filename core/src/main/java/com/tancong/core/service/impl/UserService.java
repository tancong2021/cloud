package com.tancong.core.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tancong.core.entity.LoginUser;
import com.tancong.core.entity.User;
import com.tancong.core.entity.dto.UserDTO;
import com.tancong.core.entity.enums.StatusEnum;
import com.tancong.core.mapper.UserMapper;
import com.tancong.core.service.BaseService;
import lombok.val;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * ===================================
 * User服务实现类
 * ===================================
 *
 * @author tancong
 * @version 1.0.0
 * @create 2025/11/27
 */
@Service(value = "userService")
public class UserService extends ServiceImpl<UserMapper, User> implements BaseService<User> {
    public static final String DEFAULT_PASS = "123456";

    @Autowired
    UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder; // 这里使用了 PasswordEncoder 接口
    /**
     * 修改密码
     * @param userId 用户ID
     * @param rawPassword 原始密码（明文）
     * @return 是否更新成功
     */
    public boolean updatePassword(Long userId, String rawPassword) {
        String encodedPassword = encodePassword(rawPassword);

        UpdateWrapper<User> wrapper = new UpdateWrapper<>();
        wrapper.eq("id", userId)
                .set("password", encodedPassword);

        return userMapper.update(null, wrapper) == 1;
    }


    /**
     * 根据用户名查询用户信息
     */
    public UserDTO getByUsername(String username) {
        return userMapper.selectByUsername(username);
    }

    /**
     * 加密密码
     * @param rawPassword
     * @return
     */
    public String encodePassword(String rawPassword) {
        if (StrUtil.isBlank(rawPassword)) {
            throw new IllegalArgumentException("密码不能为空");
        }
        return passwordEncoder.encode(rawPassword);
    }

    /**
     * 检验用户密码
     * @param rawPassword
     * @param user
     * @return
     */
    public boolean equalsPassword(String rawPassword, User user) {
        return passwordEncoder.matches(
                rawPassword,
                user.getPassword()
        );
    }

    /**
     * 获取使用默认密码
     */
    public String getDefaultPassword() {
        return passwordEncoder.encode(DEFAULT_PASS);
    }

    /**
     * 删除用户的所有角色信息
     */
    public void removeUserRoles(Long userId) {
        userMapper.deleteUserRole(userId);
    }
}
