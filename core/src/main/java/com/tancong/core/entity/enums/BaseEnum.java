package com.tancong.core.entity.enums;

/**
 * ===================================
 * 统一枚举实现的接口类，这样所有实现这个接口的枚举都可以统一方法
 * ===================================
 *
 * @author tancong
 * @version 1.0.0
 * @create 2025/11/26
 */
public interface BaseEnum<T>{
    // 获取枚举值
    T getValue();

}
