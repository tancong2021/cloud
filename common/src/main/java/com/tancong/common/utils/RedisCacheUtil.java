package com.tancong.common.utils;

import com.tancong.common.utils.CacheHandler;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * ===================================
 * Redis缓存工具类
 * ===================================
 *
 * @author tancong
 * @version 1.0.0
 * @create 2025/11/27
 */
@Component
public class RedisCacheUtil implements CacheHandler {
    // 时间单位是毫秒
    private static long DEFAULT_EXP_MILLIS = TimeUnit.MINUTES.toMillis(30);

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * Redis里面判断这个缓存对象在不在
     * @param key
     * @return
     */
    @Override
    public boolean hasKey(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    /**
     * Redis缓存一个普通对象
     * @param key
     * @param value
     * @param expireMillis
     * @return
     */
    @Override
    public boolean set(String key, Object value, long expireMillis) {
        if (expireMillis <= 0) redisTemplate.opsForValue().set(key, value);
        else redisTemplate.opsForValue().set(key, value, expireMillis, TimeUnit.MILLISECONDS);
        return true;
    }

    @Override
    public boolean set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);

        return true;
    }
    /**
     * Redis缓存一个Map对象
     * @param key
     * @param value
     * @param expireMillis
     * @return
     */
    @Override
    public boolean put(String key, Map<String, Object> value, long expireMillis) {
        redisTemplate.opsForHash().putAll(key, value);
        redisTemplate.expire(key, expireMillis, TimeUnit.MILLISECONDS);
        return true;
    }

    /**
     * Redis缓存中根据key查询对象
     * @param key
     * @return
     */
    @Override
    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * 从Redis缓存中获取对象，并且是否改变缓存时间
     * @param key
     * @param updateExpire
     * @return
     */
    @Override
    public Object get(String key, boolean updateExpire) {
        if (key == null) return null;
        Object value = redisTemplate.opsForValue().get(key);
        if (value != null && updateExpire) {
            // 将 TTL 刷新为默认值（秒）
            redisTemplate.expire(key, DEFAULT_EXP_MILLIS, TimeUnit.MILLISECONDS);
        }
        return value;
    }

    /**
     * 从Redis缓存中删除指定 key 对应的数据
     * @param key
     */
    @Override
    public boolean delete(String key) {
       return redisTemplate.delete(key);
    }
}
