package com.tancong.common.utils;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * ===================================
 * 缓存总工具类
 * ===================================
 *
 * @author tancong
 * @version 1.0.0
 * @create 2025/11/28
 */
@Slf4j
@Component
public class CacheManagers {
    // 缓存处理器
    private static CacheHandler handler;

    @Autowired(required = false)
    private RedisCacheUtil redisCacheUtil;

    @Autowired
    private LocalCacheUtil localCacheUtil;

    @PostConstruct
    public void init() {
        if (redisCacheUtil != null) {
            handler = redisCacheUtil;
            log.info("============> Redis 缓存已启用");
        } else {
            handler = localCacheUtil;
            log.warn("============> 未配置 Redis，将使用 Local 内存缓存");
        }
    }

    public static boolean hasKey(String key) {
        return handler.hasKey(key);
    }

    public static boolean set(String key, Object value) {
        return handler.set(key, value);
    }

    /**
     * 保存对象
     * @param key
     * @param value
     * @param exp 到期时间 单位 毫秒
     */
    public static boolean set(String key, Object value, long exp) {
        return handler.set(key, value, exp);
    }

    /**
     * 获取对象
     * @param key
     * @return
     */
    public static Object get(String key) {
        return handler.get(key);
    }

    public static Object get(String key, boolean update) {
        return handler.get(key, update);
    }

    /**
     * 删除对象
     * @param key
     */
    public static boolean del(String key) {
        return handler.delete(key);
    }
    public static boolean put(String key, Map<String, Object> value, long exp) {
        return handler.put(key, value, exp);
    }
}
