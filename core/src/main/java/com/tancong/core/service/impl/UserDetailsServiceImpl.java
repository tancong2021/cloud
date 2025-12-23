package com.tancong.core.service.impl;

import com.tancong.core.entity.Role;
import com.tancong.core.entity.Menu;
import cn.hutool.core.util.ArrayUtil;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.tancong.core.entity.LoginUser;
import com.tancong.core.entity.User;
import com.tancong.core.entity.dto.UserDTO;
import com.tancong.core.entity.enums.StatusEnum;
import com.tancong.core.service.MenuService;
import com.tancong.core.service.RoleService;
import com.tancong.core.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.*;

import java.util.stream.Collectors;

/**
 * ===================================
 * 自定义用户加载器服务类
 * ===================================
 *
 * @author tancong
 * @version 1.0.0
 * @create 2025/11/30
 */
@Slf4j
@Primary
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    // ===== 常量定义 =====
    private static final Long SUPER_ADMIN_ID = 1L;
    private static final String ADMIN_ROLE = "ROLE_ADMIN";

    @Autowired
    private UserService userService;
    @Autowired
    private MenuService menuService;
    @Autowired
    private RoleService roleService;

    /**
     * Spring Security 登录时调用
     * @param username 用户名
     * @return UserDetails
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 1. 查询用户
        UserDTO userDTO = userService.getByUsername(username);

        if (userDTO == null) {
            log.warn("用户不存在: {}", username);
            throw new UsernameNotFoundException("用户名或密码不正确");
        }

        // 2. 验证用户状态
        validateUser(userDTO);

        // 3. 创建 LoginUser 对象
        LoginUser loginUser = new LoginUser();
        BeanUtils.copyProperties(userDTO, loginUser);

        // 4. 不在此处加载详细信息，留到认证成功后在 LoginController 中加载
        // loadUserInfo(loginUser);

        log.info("用户 {} 基本信息加载完成", username);
        return loginUser;
    }

    /**
     * 加载用户详细信息（权限、菜单、角色等）
     * @param loginUser 登录用户对象
     */
    public void loadUserInfo(LoginUser loginUser) {
        // 1. 获取当前用户的完整数据（包含角色信息）
        UserDTO userDTO = userService.getByUsername(loginUser.getUsername());

        // 2. 特殊处理：超级管理员拥有所有角色
        if (isSuperAdmin(userDTO)){
            log.info("加载超级管理员权限: {}", loginUser.getUsername());
            userDTO.setRoles(roleService.list());
        }

        // 3. 复制用户信息（主要是角色信息）
        if (userDTO != null && userDTO.getRoles() != null) {
            loginUser.setRoles(userDTO.getRoles());
        }

        // 5. 判断是否管理员角色
        boolean isAdmin = hasAdminRole(loginUser.getRoles());

        // 6. 设置角色 ID 字符串
        setRoleIds(loginUser);

        // 7. 加载菜单
        loadMenus(loginUser, isAdmin);

        // 8. 设置权限
        setAuthorities(loginUser);

        // 9. 构建菜单树
        loginUser.setMenus(menuService.buildTree(loginUser.getMenusOriginalOfList()));

        log.debug("用户 {} 权限加载完成，拥有 {} 个权限，{} 个菜单",
                loginUser.getUsername(),
                loginUser.getAuthorities().size(),
                loginUser.getMenus().size());
    }

    /**
     * ✅ 验证用户状态
     * 异常抛出后将会由 AuthenticationException 处理
     */
    private void validateUser(User user) {
        if (Objects.isNull(user)) {
            throw new AuthenticationCredentialsNotFoundException("用户名或密码不正确");
        }

        switch (user.getStatus()) {
            case ENABLED -> {
                // 用户正常，继续
                log.debug("用户 {} 状态正常", user.getUsername());
            }
            case DISABLED -> {
                log.warn("用户已被禁用: {}", user.getUsername());
                throw new DisabledException("用户已被禁用，请联系管理员");
            }
            case DELETED -> {
                log.warn("用户已被删除: {}", user.getUsername());
                throw new DisabledException("用户已被删除，请联系管理员");
            }
            default -> {
                log.error("用户状态异常: {}, status={}", user.getUsername(), user.getStatus());
                throw new DisabledException("用户状态异常，请联系管理员");
            }
        }
    }

    /**
     * 判断是否超级管理员
     */
    private boolean isSuperAdmin(UserDTO userDTO) {
        // 方式一：通过 ID 判断
        if (SUPER_ADMIN_ID.equals(userDTO.getId())) {
            return true;
        }

        // 方式二：通过角色判断（可选，更灵活）
        // return hasAdminRole(userDTO.getRoles());

        return false;
    }

    /**
     * 判断是否有管理员角色
     */
    private boolean hasAdminRole(List<Role> roles) {
        if (roles == null || roles.isEmpty()) {
            return false;
        }

        return roles.stream()
                .filter(role -> StatusEnum.ENABLED.equals(role.getStatus()))
                .anyMatch(role -> ADMIN_ROLE.equals(role.getName()));
    }

    /**
     * 设置用户的角色 ID 字符串
     */
    private void setRoleIds(LoginUser loginUser) {
        String roleIds = ArrayUtil.join(
                loginUser.getRoles().stream()
                        .filter(role -> StatusEnum.ENABLED.equals(role.getStatus()))
                        .map(Role::getId)
                        .toArray(),
                ","
        );
        loginUser.setRoleIds(roleIds);
    }

    /**
     * 加载用户菜单
     */
    private void loadMenus(LoginUser loginUser, boolean isAdmin) {
        if (isAdmin) {
            // 管理员：加载所有启用的菜单
            loginUser.setMenusOriginal(menuService.getAllEnable(true));
        } else {
            // 普通用户：根据角色加载菜单
            loginUser.setMenusOriginal(menuService.getByRoleIds(loginUser.getRoleIds()));
        }
    }

    /**
     * 设置用户权限
     */
    private void setAuthorities(LoginUser loginUser) {
        Set<SimpleGrantedAuthority> authorities = new HashSet<>();

        // 1. 菜单权限
        authorities.addAll(
                loginUser.getMenusOriginalOfList().stream()
                        .map(Menu::getPermission)
                        .filter(StringUtils::isNotBlank)
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toSet())
        );

        // 2. 默认权限
        authorities.addAll(SecurityUtils.getDefaultAuthorities());

        // 3. 角色权限
        authorities.addAll(
                loginUser.getRoles().stream()
                        .filter(role -> StatusEnum.ENABLED.equals(role.getStatus()))
                        .map(Role::getName)
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toSet())
        );

        loginUser.setAuthorities(authorities);
    }
}
