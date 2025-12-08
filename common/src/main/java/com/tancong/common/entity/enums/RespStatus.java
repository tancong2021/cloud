package com.tancong.common.entity.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * ==============================
 * 〈枚举类：响应状态码所对应的信息〉
 * ==============================
 *
 * @author tancong
 * @version 1.0.0
 * @create 2025/11/25
 */
public enum RespStatus {

    SUCCESS(200, "成功"),
    FAIL(505, "失败"),
    NOT_LOGIN(401, "未登录"),
    HTTP_FORBIDDEN(403, "禁止访问"),
    HTTP_NOT_FOUND(404, "资源未找到"),
    // 鉴权相关
    TOKEN_EXPIRED(2001, "Token 已过期"),
    ACCESS_DENIED(2002, "无访问权限"),
    // 系统错误
    SYSTEM_ERROR(1000, "系统异常"),
    DATABASE_ERROR(1001, "数据库异常"),
    ;
    // 枚举值
    private final int code;

    private final String msg;

    RespStatus(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    // 根据状态码 code，找到对应的 RespStatus 枚举对象，如果找不到就默认返回 SUCCESS。
    @JsonCreator
    public static RespStatus valueOf(int code) {
        for (RespStatus val : RespStatus.values()) {
            if (val.code == code) {
                return val;
            }
        }
        return RespStatus.valueOf("SUCCESS");
    }
}
