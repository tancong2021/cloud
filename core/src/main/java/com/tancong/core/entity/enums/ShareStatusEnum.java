package com.tancong.core.entity.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * ===================================
 * 分享状态枚举类
 * ===================================
 *
 * @author tancong
 * @version 1.0.0
 * @create 2025/12/25
 */
public enum ShareStatusEnum implements BaseEnum<Integer> {
    CANCELLED(0, "已取消"),
    NORMAL(1, "正常"),
    EXPIRED(2, "已过期");

    @EnumValue // 指定持久化到数据库时候显示的值
    @JsonValue // 返回给前端Json数据时显示的值
    private final int status;

    private final String label;

    ShareStatusEnum(int status, String label) {
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
    public static ShareStatusEnum valueOf(Integer value) {
        for (ShareStatusEnum enums : ShareStatusEnum.values()) {
            if (enums.getValue().equals(value)) {
                return enums;
            }
        }
        throw new IllegalArgumentException("Unexpected ShareStatusEnum value: " + value);
    }
}
