package com.tancong.core.entity.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * ===================================
 * 菜单类型枚举类
 * ===================================
 *
 * @author tancong
 * @version 1.0.0
 * @create 2025/11/26
 */
public enum MenuType implements BaseEnum<Integer> {
    LEVEL_1(1, "一级菜单"),
    PAGE(2, "页面"),
    BUTTON(3, "按钮操作");

    @EnumValue
    @JsonValue
    private final Integer value;
    private final String label;

    MenuType(Integer value, String label) {
        this.value = value;
        this.label = label;
    }
    public String getLabel() {
        return label;
    }
    @Override
    public Integer getValue() {
        return value;
    }

    // 根据value查找对应的枚举
    public static MenuType valueOf(Integer value) {
        for (MenuType enums:MenuType.values()) {
            if (enums.getValue() == value) {
                return enums;
            }
        }
        throw new IllegalArgumentException("Unexpected MenuType value: " + value);
    }
}
