package com.tancong.security.entity;


import com.tancong.security.entity.enums.TokenType;
import com.tancong.security.utils.DefaultSecurityUtils;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

/**
 * ===================================
 * 共享用户实体类
 * ===================================
 *
 * @author tancong
 * @version 1.0.0
 * @create 2025/12/03
 */
@Data
public class ShareUser implements AuthUser {

    private TokenType tokenType = TokenType.SHARE;
    private String shareId;
    private Collection<SimpleGrantedAuthority> authorities;
    private Date expireTime = null;

    @Override
    public void setExpireTime(Date expireTime) {
        this.expireTime = expireTime;
    }
    public ShareUser(String shareId) {
        this.shareId = shareId;
        authorities = new ArrayList<String>(){{
            add("ROLE_share");
        }}.stream().map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
        authorities.addAll(DefaultSecurityUtils.getDefaultAuthorities());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    public String getLoginId() {
        return this.shareId;
    }

    public long expired() {
        return this.expireTime.getTime() - System.currentTimeMillis();
    }

    @Override
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }
}
