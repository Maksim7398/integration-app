package com.example.servicesecond.integrationapp.service;

import com.example.servicesecond.integrationapp.configuration.properties.AppCacheProperties;
import com.example.servicesecond.integrationapp.entity.DatabaseEntity;
import com.example.servicesecond.integrationapp.repositoty.DatabaseEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@CacheConfig(cacheManager = "redisCacheManager")
public class DatabaseEntityService{

    private final DatabaseEntityRepository repository;

    @Cacheable(cacheNames = AppCacheProperties.CacheNames.DATABASE_ENTITY_BY_NAME)// для ппометки методов, результаты которых могут быть кэшированны,
    // метод не выполняется, если результат уже есть в кэше
    public List<DatabaseEntity> findAll() {
        return repository.findAll();
    }

    public DatabaseEntity findById(UUID id) {
        return repository.findById(id).orElseThrow();
    }

    @Cacheable(cacheNames = AppCacheProperties.CacheNames.DATABASE_ENTITY_BY_NAME,key = "#name")
    public DatabaseEntity findByName(String name) {
        DatabaseEntity probe = new DatabaseEntity();
        probe.setName(name);

        ExampleMatcher matcher = ExampleMatcher.matching()
                .withIgnoreNullValues()
                .withIgnorePaths("id", "date");
        Example<DatabaseEntity> example = Example.of(probe, matcher);

        return repository.findOne(example).orElseThrow();
    }

    @CacheEvict(value = "databaseEntities", allEntries = true)// используется для указания методов,
    // которые должны удалить значения из кэша(allEntries - говорит, что все значения должны быть удаленны из кэша
    public DatabaseEntity create(DatabaseEntity entity) {
        DatabaseEntity forSave = new DatabaseEntity();
        forSave.setName(entity.getName());
        forSave.setDate(entity.getDate());

        return repository.save(forSave);
    }

    @Caching(evict = {
            @CacheEvict(value = "databaseEntities", allEntries = true),
            @CacheEvict(value = "databaseEntityByName", allEntries = true)
    })
    public DatabaseEntity update(UUID id, DatabaseEntity entity) {
        DatabaseEntity entityForUpdate = repository.findById(id).orElseThrow();
        entityForUpdate.setName(entity.getName());
        entityForUpdate.setDate(entity.getDate());

        return repository.save(entityForUpdate);
    }

    @Caching(evict = {
            @CacheEvict(value = "databaseEntities", allEntries = true),
            @CacheEvict(value = "databaseEntityByName", allEntries = true)
    })
    public void deleteById(UUID id) {
        repository.deleteById(id);
    }
}
