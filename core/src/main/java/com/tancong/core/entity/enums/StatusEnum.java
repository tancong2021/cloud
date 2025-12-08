package com.tancong.core.entity.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * ===================================
 * 状态枚举类
 * ===================================
 *
 * @author tancong
 * @version 1.0.0
 * @create 2025/11/26
 */
public enum StatusEnum implements BaseEnum<Integer>{
    DISABLED(0, "用户被禁用"),
    ENABLED(1, "用户已启用"),
    DELETED(2, "用户已删除"),
    LOCKED(3, "锁定"),      // 新增：账号锁定
    EXPIRED(4, "已过期");    // 新增：账号过期
    @EnumValue // 指定持久化到数据库时候显示的值
    @JsonValue // 返回给前端Json数据时显示的值
    private final int status;


    private final String label;

    StatusEnum(int status, String label) {
        this.status = status;
        this.label = label;
    }
    public String getLabel() {
        return label;
    }

    @Override
    public Integer getValue() {
        return status;
    }

    // 根据value查找对应的枚举
    @JsonCreator
    public static StatusEnum valueOf(Integer value) {
        for (StatusEnum enums:StatusEnum.values()) {
            if (enums.getValue() == value) {
                return enums;
            }
        }
        throw new IllegalArgumentException("Unexpected StatusEnum value: " + value);
    }
}
