package com.tancong.security.entity.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * ===================================
 * 枚举类型说明Token的类型的
 * ===================================
 *
 * @author tancong
 * @version 1.0.0
 * @create 2025/11/27
 */
public enum TokenType {

    LOGIN( "用户登录"),
    SHARE( "访问分享资源" ),
    DEFAULT("默认用户");

    private final String name;

    TokenType(String name) {
        this.name = name;
    }

}
