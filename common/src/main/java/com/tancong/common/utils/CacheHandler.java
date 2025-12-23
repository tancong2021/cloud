package com.tancong.common.utils;

import jakarta.validation.constraints.NotNull;

import java.util.Map;

/**
 * ===================================
 * 统一规范缓存操作
 * ===================================
 *
 * @author tancong
 * @version 1.0.0
 * @create 2025/11/28
 */
public interface CacheHandler {
    boolean hasKey(String key); // ✅判断这个key是否存在

    boolean set(@NotNull String key, @NotNull Object value, long exp); // ✅缓存普通对象
    boolean set(@NotNull String key, @NotNull Object value); // ✅缓存普通对象

    boolean put(String key, Map<String, Object> value, long exp); // ✅缓存存Map结构的对象

    Object get(String key); // ✅根据key查询对象

    Object get(String key, boolean updateExpire); // ✅查询对象，决定了：缓存是"固定过期"还是"滑动过期"

    boolean delete(String key); // ✅从缓存中删除指定 key 对应的数据
}
