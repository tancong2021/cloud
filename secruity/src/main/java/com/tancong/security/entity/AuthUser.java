package com.tancong.security.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tancong.security.entity.enums.TokenType;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;

/**
 * ===================================
 * 操作用户权限接口
 * ===================================
 *
 * @author tancong
 * @version 1.0.0
 * @create 2025/11/27
 */
public interface AuthUser extends UserDetails, Cloneable{
    @JsonIgnore
    TokenType tokenType = null;

    default TokenType getTokenType() {
        return tokenType;
    }

    String getLoginId();

    void setExpireTime(Date expireTime);
    long expired();

    Object clone();
}
