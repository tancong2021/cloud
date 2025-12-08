package com.tancong.core.utils;

import com.tancong.core.entity.LoginUser;
import com.tancong.security.entity.AuthUser;
import com.tancong.security.utils.DefaultSecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Objects;
/**
 * ===================================
 * 安全工具类
 * 1.获取当前认证用户（AuthUser 或 LoginUser）
 * 2.获取当前认证用户的 ID
 * 3.检查用户是否已登录
 * 4.封装获取 Authentication 对象的逻辑
 * ===================================
 *
 * @author tancong
 * @version 1.0.0
 * @create 2025/11/30
 */
@Slf4j
public class SecurityUtils extends DefaultSecurityUtils {

    public static AuthUser getUser() {
        Object principal = getAuthentication().getPrincipal();
        if (principal instanceof AuthUser authUser) {
            return authUser;
        } else {
            return null;
        }
    }

    public static LoginUser getLoginUser() {
        Object principal = getAuthentication().getPrincipal();
        if (principal instanceof LoginUser loginUser) {
            return loginUser;
        } else {
            return null;
        }
    }

    public static Long getLoginUserId() {
        return Objects.requireNonNull(SecurityUtils.getLoginUser()).getId();
    }

    public static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    public static boolean isLogin() {
        return SecurityUtils.getAuthentication().isAuthenticated();
    }
}