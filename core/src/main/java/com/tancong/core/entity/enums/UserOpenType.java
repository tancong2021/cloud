package com.tancong.core.entity.enums;

import com.baomidou.mybatisplus.annotation.IEnum;

/**
 * ===================================
 *   定义第三方登录类型
 *   - 目前定义了 QQ 登录方式
 *   - 可以扩展其他登录方式（如微信、微博等）
 * ===================================
 *
 * @author tancong
 * @version 1.0.0
 * @create 2025/12/02
 */
public enum UserOpenType implements IEnum<String> {
    QQ("QQ", "QQ快捷登录"),
    WECHAT("WECHAT", "微信快捷登录");


    private final String type;
    private final String desc;

    UserOpenType(String type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    @Override
    public String getValue() {
        return type;
    }

    public String getDesc() {
        return desc;
    }
}
