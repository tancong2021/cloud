package com.tancong.core.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tancong.security.entity.enums.TokenType;
import com.tancong.security.entity.AuthUser;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.*;
import java.util.stream.Collectors;

/**
 * ===================================
 * 登入用户业务实体类
 * ===================================
 *
 * @author tancong
 * @version 1.0.0
 * @create 2025/11/27
 */
@Data
@EqualsAndHashCode(callSuper = true)  // ✅ 正确继承父类的 equals 和 hashCode
@Accessors(chain = true)  // ✅ 添加链式调用，与父类保持一致
public class LoginUser extends User implements AuthUser {
    private TokenType tokenType = TokenType.LOGIN;
    @JsonIgnore
    private String loginId;
    @JsonIgnore
    private String password;
    @JsonIgnore
    private String roleIds;
    @JsonIgnore
    private List<Role> roles;
    private List<? extends Menu> menus; // 树结构的菜单列表（前端用）
    @JsonIgnore
    private List<Menu> menusOriginal;   // 原始菜单列表（后端内部用）
    private Collection<SimpleGrantedAuthority> authorities;
    private Date expireTime = null;


    @Override
    public void setExpireTime(Date expireTime) {
        this.expireTime = expireTime;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @JsonIgnore // 不显示给前端，给后端使用属性
    public List<Menu> getMenusOriginalOfList() {
        return this.menusOriginal;
    }

    public void setMenusOriginal(List<Menu> menusOriginal) {
        if (menusOriginal == null) menusOriginal = new ArrayList<>();
        this.menusOriginal = menusOriginal.stream()
                .distinct() // 去重
                .sorted(Comparator.comparingInt(Menu::getOrder)) // 排序
                .collect(Collectors.toList()); // 收集成集合
    }

    public long expired() {
        return this.expireTime.getTime() - System.currentTimeMillis();
    }
}
