package com.tancong.core.entity.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * ===================================
 * 文件状态枚举类
 * ===================================
 *
 * @author tancong
 * @version 1.0.0
 * @create 2025/12/08
 */
public enum FileStatusEnum implements BaseEnum<Integer> {
    DELETED(0, "已删除"),
    NORMAL(1, "正常"),
    PENDING(2, "待审核");

    @EnumValue // 指定持久化到数据库时候显示的值
    @JsonValue // 返回给前端Json数据时显示的值
    private final int status;

    private final String label;

    FileStatusEnum(int status, String label) {
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
    public static FileStatusEnum valueOf(Integer value) {
        for (FileStatusEnum enums : FileStatusEnum.values()) {
            if (enums.getValue().equals(value)) {
                return enums;
            }
        }
        throw new IllegalArgumentException("Unexpected FileStatusEnum value: " + value);
    }
}
