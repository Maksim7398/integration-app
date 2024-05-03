package com.example.servicesecond.integrationapp.configuration;

import com.example.servicesecond.integrationapp.configuration.properties.AppCacheProperties;
import com.google.common.cache.CacheBuilder;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@EnableCaching
@EnableConfigurationProperties(AppCacheProperties.class)
public class CacheConfiguration {

    @Bean
    @ConditionalOnExpression("'${app.cache.cacheType}'.equals('inMemory')")
    public ConcurrentMapCacheManager inMemoryCacheManager(AppCacheProperties appCacheProperties) {
        ConcurrentMapCacheManager concurrentMapCacheManager = new ConcurrentMapCacheManager() {
            @NotNull
            @Override
            protected Cache createConcurrentMapCache(@NotNull String name) {
                return new ConcurrentMapCache(
                        name,
                        CacheBuilder.newBuilder()
                                .expireAfterWrite(appCacheProperties.getCashes().get(name).getExpiry())
                                .build()
                                .asMap(),
                        true
                );
            }
        };

        final List<String> cacheNames = appCacheProperties.getCacheNames();
        if (!cacheNames.isEmpty()) {
            concurrentMapCacheManager.setCacheNames(cacheNames);
        }

        return concurrentMapCacheManager;
    }

    @Bean
    @ConditionalOnExpression("'${app.cache.cacheType}'.equals('redis')")
    public CacheManager redisCacheManager(AppCacheProperties appCacheProperties, LettuceConnectionFactory lettuceConnectionFactory) {
        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig();
        Map<String, RedisCacheConfiguration> redisCacheConfigurationMap = new HashMap<>();
        appCacheProperties.getCacheNames().forEach(cacheName -> {
            redisCacheConfigurationMap.put(cacheName, redisCacheConfiguration.entryTtl(
                    appCacheProperties.getCashes().get(cacheName).getExpiry()
            ));
        });

        return RedisCacheManager.builder(lettuceConnectionFactory)
                .cacheDefaults(redisCacheConfiguration)
                .withInitialCacheConfigurations(redisCacheConfigurationMap)
                .build();
    }
}
