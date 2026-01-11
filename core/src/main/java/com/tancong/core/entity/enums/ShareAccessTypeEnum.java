package com.tancong.core.entity.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * ===================================
 * 分享访问类型枚举类
 * ===================================
 *
 * @author tancong
 * @version 1.0.0
 * @create 2025/12/25
 */
public enum ShareAccessTypeEnum implements BaseEnum<Integer> {
    VIEW(1, "查看"),
    DOWNLOAD(2, "下载"),
    VERIFY_FAILED(3, "验证失败");

    @EnumValue // 指定持久化到数据库时候显示的值
    @JsonValue // 返回给前端Json数据时显示的值
    private final int type;

    private final String label;

    ShareAccessTypeEnum(int type, String label) {
        this.type = type;
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public Integer getValue() {
        return type;
    }

    // 根据value查找对应的枚举
    @JsonCreator
    public static ShareAccessTypeEnum valueOf(Integer value) {
        for (ShareAccessTypeEnum enums : ShareAccessTypeEnum.values()) {
            if (enums.getValue().equals(value)) {
                return enums;
            }
        }
        throw new IllegalArgumentException("Unexpected ShareAccessTypeEnum value: " + value);
    }
}
