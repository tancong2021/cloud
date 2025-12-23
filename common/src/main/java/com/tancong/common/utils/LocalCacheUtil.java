package com.tancong.common.utils;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Expiry;
import com.tancong.common.utils.CacheHandler;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * ===================================
 * JVM缓存工具类
 * ===================================
 *
 * @author tancong
 * @version 1.0.0
 * @create 2025/11/27
 */
@Component
public class LocalCacheUtil implements CacheHandler {

    /**
     * 包装缓存值，用于携带每个 key 的过期时间
     */
    private static class ExpirableValue {
        private final Object value;
        private final long expireMillis; // <=0 表示永久

        public ExpirableValue(Object value, long expireMillis) {
            this.value = value;
            this.expireMillis = expireMillis;
        }
    }

    /**
     * Caffeine 缓存实例 - 支持动态 TTL
     */
    private static final Cache<String, ExpirableValue> CACHE = Caffeine.newBuilder()
            .maximumSize(10_000)
            .expireAfter(new Expiry<String, ExpirableValue>() {

                /* 创建时设置 TTL */
                @Override
                public long expireAfterCreate(String key, ExpirableValue value, long currentTime) {
                    return value.expireMillis <= 0
                            ? Long.MAX_VALUE
                            : TimeUnit.MILLISECONDS.toNanos(value.expireMillis);
                }

                /* 更新时重置 TTL */
                @Override
                public long expireAfterUpdate(String key, ExpirableValue value,
                                              long currentTime, long currentDuration) {
                    return value.expireMillis <= 0
                            ? Long.MAX_VALUE
                            : TimeUnit.MILLISECONDS.toNanos(value.expireMillis);
                }

                /* 读取时的 TTL 策略（固定） */
                @Override
                public long expireAfterRead(String key, ExpirableValue value,
                                            long currentTime, long currentDuration) {
                    return value.expireMillis <= 0
                            ? Long.MAX_VALUE
                            : TimeUnit.MILLISECONDS.toNanos(value.expireMillis);
                }
            })
            .build();

    /* =================== 写入 =================== */

    /**
     * 写入缓存，指定 TTL（毫秒）
     */
    public boolean set(String key, Object value, long expireMillis) {
        CACHE.put(key, new ExpirableValue(value, expireMillis));
        return true;
    }

    /**
     * 写入缓存（永久保存）
     */
    public boolean set(String key, Object value) {
        CACHE.put(key, new ExpirableValue(value, 0));
        return true;
    }

    /**
     * 写入 Map 类型缓存，指定 TTL（毫秒）
     */
    @Override
    public boolean put(String key, Map<String, Object> value, long expireMillis) {
        return set(key, value, expireMillis);
    }

    /* =================== 读取 =================== */

    /**
     * 获取缓存（不会手动刷新 TTL）
     */
    @SuppressWarnings("unchecked")
    public Object get(String key) {
        ExpirableValue ev = CACHE.getIfPresent(key);
        return ev == null ? null : ev.value;
    }

    /**
     * 获取缓存 - 手动控制是否刷新 TTL
     * update = true 时，强制刷新为初始 TTL
     */
    @SuppressWarnings("unchecked")
    public Object get(String key, boolean updateExpire) {
        ExpirableValue ev = CACHE.getIfPresent(key);
        if (ev == null) return null;

        if (updateExpire) {
            CACHE.put(key, ev); // 触发 expireAfterUpdate，TTL 重置
        }
        return ev.value;
    }

    /* =================== 其他操作 =================== */

    /**
     * 判断 key 是否存在
     */
    public boolean hasKey(String key) {
        return CACHE.getIfPresent(key) != null;
    }

    /**
     * 删除缓存
     */
    public boolean delete(String key) {

        if (key != null) {
            CACHE.invalidate(key);
            return true;
        }
        return false;
    }
}
