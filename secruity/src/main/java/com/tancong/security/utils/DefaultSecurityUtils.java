package com.tancong.security.utils;

import com.tancong.common.utils.CacheManagers;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import java.util.ArrayList;
import java.util.Objects;

/**
 * ===================================
 * 默认安全工具类
 * 1.为用户添加默认权限
 * 2.验证UUID token是否有效
 * ===================================
 *
 * @author tancong
 * @version 1.0.0
 * @create 2025/11/30
 */
public class DefaultSecurityUtils {

    private static ArrayList<SimpleGrantedAuthority> authorities = new ArrayList<>() {{
        add(new SimpleGrantedAuthority("resource:preview"));
    }};

    public static ArrayList<SimpleGrantedAuthority> getDefaultAuthorities() {
        return authorities;
    }

    public static boolean verifyUUIDToken(String uuidToken) {
        return !Objects.isNull(CacheManagers.get(uuidToken));
    }

}