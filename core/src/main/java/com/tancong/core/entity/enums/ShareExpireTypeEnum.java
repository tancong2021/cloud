package com.tancong.core.entity.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * ===================================
 * 分享有效期类型枚举类
 * ===================================
 *
 * @author tancong
 * @version 1.0.0
 * @create 2025/12/25
 */
public enum ShareExpireTypeEnum implements BaseEnum<Integer> {
    SEVEN_DAYS(1, "7天", 7),
    THIRTY_DAYS(2, "30天", 30),
    PERMANENT(3, "永久", -1);

    @EnumValue // 指定持久化到数据库时候显示的值
    @JsonValue // 返回给前端Json数据时显示的值
    private final int type;

    private final String label;

    private final int days; // -1表示永久有效

    ShareExpireTypeEnum(int type, String label, int days) {
        this.type = type;
        this.label = label;
        this.days = days;
    }

    public String getLabel() {
        return label;
    }

    public int getDays() {
        return days;
    }

    @Override
    public Integer getValue() {
        return type;
    }

    // 根据value查找对应的枚举
    @JsonCreator
    public static ShareExpireTypeEnum valueOf(Integer value) {
        for (ShareExpireTypeEnum enums : ShareExpireTypeEnum.values()) {
            if (enums.getValue().equals(value)) {
                return enums;
            }
        }
        throw new IllegalArgumentException("Unexpected ShareExpireTypeEnum value: " + value);
    }
}
