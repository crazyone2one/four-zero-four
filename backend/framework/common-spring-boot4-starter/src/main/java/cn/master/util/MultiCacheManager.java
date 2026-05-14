package cn.master.util;

import cn.master.exception.FZFException;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;
import tools.jackson.core.type.TypeReference;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * @author : 11's papa
 * @since : 2026/5/12, 星期二
 **/
@Component
public class MultiCacheManager {
    /**
     * 配置 Caffeine 本地缓存管理器
     */
    private final CacheManager redisCacheManager;
    private final CacheManager caffeineCacheManager;
    private final RedissonClient redissonClient;

    public MultiCacheManager(@Qualifier("redisCacheManager") CacheManager redisCacheManager,
                             @Qualifier("caffeineCacheManager") CacheManager caffeineCacheManager,
                             RedissonClient redissonClient) {
        this.redisCacheManager = redisCacheManager;
        this.caffeineCacheManager = caffeineCacheManager;
        this.redissonClient = redissonClient;
    }

    public <T> T get(String key, TypeReference<T> targetType, Callable<T> dbLoader) {
        // 1. 尝试从 L1 (Caffeine) 获取
        Cache caffeineCache = caffeineCacheManager.getCache("localCache");
        assert caffeineCache != null;
        Cache.ValueWrapper localValue = caffeineCache.get(key);
        if (localValue != null && localValue.get() != null) {
            LogUtils.debug("L1 Cache Hit: " + key);
            return JsonUtils.objectToType(targetType).apply(localValue.get());
        }
        // 2. L1 未命中，尝试从 L2 (Redisson) 获取
        Cache redisCache = redisCacheManager.getCache("redisCache");
        assert redisCache != null;
        Cache.ValueWrapper redisValue = redisCache.get(key);
        if (redisValue != null && redisValue.get() != null) {
            LogUtils.debug("L2 Cache Hit: " + key);
            T value = JsonUtils.objectToType(targetType).apply(redisValue.get());
            caffeineCache.put(key, value);
            return value;
        }
        // 4. L1, L2 均未命中，调用 loader 从数据库加载
        RLock lock = redissonClient.getLock("cache:lock:" + key);
        try {
            if (lock.tryLock(3, 10, TimeUnit.SECONDS)) {
                try {
                    redisValue = redisCache.get(key);
                    if (redisValue != null && redisValue.get() != null) {
                        T value = JsonUtils.objectToType(targetType).apply(redisValue.get());
                        caffeineCache.put(key, value);
                        return value;
                    }
                    T dbValue = dbLoader.call();
                    if (dbValue != null) {
                        // 数据库有结果，更新各级缓存
                        redisCache.put(key, dbValue);
                        caffeineCache.put(key, dbValue);
                    } else {
                        // 数据库无结果，缓存空值，防止缓存穿透
                        // 空值缓存时间较短，例如5分钟
                        redisCache.put(key, NullValue.INSTANCE);
                        caffeineCache.put(key, NullValue.INSTANCE);
                    }
                    return dbValue;
                } finally {
                    lock.unlock();
                }
            } else {
                return dbLoader.call();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new FZFException("缓存查询被中断", e);
        } catch (Exception e) {
            throw new FZFException("缓存查询异常", e);
        }
    }

    private record NullValue() {
        public static final NullValue INSTANCE = new NullValue();
    }

    public void evict(String key) {
        // 1. 清除本地缓存
        Cache caffeineCache = caffeineCacheManager.getCache("localCache");
        assert caffeineCache != null;
        caffeineCache.evict(key);
        // 2. 清除Redis缓存
        Cache redisCache = redisCacheManager.getCache("redisCache");
        assert redisCache != null;
        redisCache.evict(key);
    }
}
