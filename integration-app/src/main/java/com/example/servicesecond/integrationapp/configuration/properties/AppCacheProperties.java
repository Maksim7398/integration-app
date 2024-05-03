package com.example.servicesecond.integrationapp.configuration.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@Data
@ConfigurationProperties(prefix = "app.cache")
public class AppCacheProperties {

    private List<String> cacheNames;

    private Map<String,CacheProperties> cashes;

    private CacheType cacheType;

    @Data
    public static class CacheProperties{
        private Duration expiry = Duration.ZERO;
    }

    public interface CacheNames{

        String DATABASE_ENTITIES = "databaseEntities";

        String DATABASE_ENTITY_BY_NAME = "databaseEntityByName";
    }

    public enum CacheType{

        IN_MEMORY,
        REDIS
    }
}
