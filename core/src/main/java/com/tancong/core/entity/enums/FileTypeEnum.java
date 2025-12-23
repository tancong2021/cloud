package com.tancong.core.entity.enums;

/**
 * ===================================
 * 枚举类：判断是文件还是文件夹
 * ===================================
 *
 * @author tancong
 * @version 1.0.0
 * @create 2025/12/16
 */
public enum FileTypeEnum implements BaseEnum<Integer> {

    FILE(1, "文件"),
    FOLDER(2, "文件夹");

    private final int value;
    private final String description;

    FileTypeEnum(int value, String description) {
        this.value = value;
        this.description = description;
    }
    @Override
    public Integer getValue() {
        return value;
    }
}
