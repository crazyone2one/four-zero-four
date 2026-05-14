package cn.master.autoconfigure;

import cn.master.util.MultiCacheManager;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJackson3Codec;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * @author : 11's papa
 * @since : 2026/5/12, 星期二
 **/
@Configuration
@EnableConfigurationProperties(RedisProperties.class)
@ConditionalOnClass(name = "org.springframework.data.redis.connection.RedisConnectionFactory")
@ConditionalOnProperty(prefix = "malo.redis", name = "enabled", havingValue = "true", matchIfMissing = true)
public class RedissonAutoConfiguration {
    @Bean(destroyMethod = "shutdown")
    @ConditionalOnMissingBean
    public RedissonClient redissonClient(RedisProperties properties) {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://" + properties.getHost() + ":" + properties.getPort());
        if (properties.getPassword() != null) {
            config.setPassword(properties.getPassword());
        }
        config.setCodec(new JsonJackson3Codec());
        return Redisson.create(config);
    }

    /**
     * 配置 Caffeine 本地缓存管理器
     */
    @Bean("caffeineCacheManager")
    @ConditionalOnMissingBean(name = "caffeineCacheManager")
    public CacheManager caffeineCacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager("localCache");
        cacheManager.setCaffeine(Caffeine.newBuilder()
                // 设置最后一次写入或访问后30分钟过期
                .expireAfterWrite(30, TimeUnit.MINUTES)
                // 设置最大缓存条目数
                .maximumSize(10000));
        return cacheManager;
    }

    @Bean("redisCacheManager")
    @ConditionalOnMissingBean(name = "redisCacheManager")
    public CacheManager redisCacheManager(RedisConnectionFactory factory) {
        // 配置序列化方式，避免乱码
        StringRedisSerializer stringSerializer = new StringRedisSerializer();
        GenericJacksonJsonRedisSerializer jsonSerializer = new GenericJacksonJsonRedisSerializer(new ObjectMapper());

        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofHours(2)) // 默认2小时过期
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(stringSerializer))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(jsonSerializer))
                .disableCachingNullValues(); // 不缓存null值

        // 可以为不同业务设置不同的过期时间
        // Map<String, RedisCacheConfiguration> initialCacheConfigs = new HashMap<>();
        // initialCacheConfigs.put("product", config.entryTtl(Duration.ofHours(1)));

        return RedisCacheManager.builder(factory)
                .cacheDefaults(config)
                // .withInitialCacheConfigurations(initialCacheConfigs)
                .build();
    }

    @Bean
    @ConditionalOnMissingBean(name = "multiCacheManager")
    public MultiCacheManager multiCacheManager(@Qualifier("redisCacheManager") CacheManager redisCacheManager,
                                               @Qualifier("caffeineCacheManager") CacheManager caffeineCacheManager,
                                               RedissonClient redissonClient) {
        return new MultiCacheManager(redisCacheManager, caffeineCacheManager, redissonClient);
    }
}
