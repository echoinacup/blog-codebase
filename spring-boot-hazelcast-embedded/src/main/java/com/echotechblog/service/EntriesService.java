package com.echotechblog.service;

import com.hazelcast.map.IMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class EntriesService {
    private static final Logger LOGGER = LogManager.getLogger(EntriesService.class);
    private static final Map<String, Long> DB_MOCK = new HashMap<>();
    private final CacheManager cacheManager;

    public EntriesService(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
        DB_MOCK.put("one", 1L);
        DB_MOCK.put("two", 2L);
        DB_MOCK.put("three", 3L);
    }

    public Map<String, Long> findAllEntries() {
        var cache = cacheManager.getCache("testEmbeddedCache").getNativeCache();

        if (cache instanceof IMap<?, ?>) {
            IMap<String, Long> nativeCacheImpl = (IMap<String, Long>) cache;
            if (!nativeCacheImpl.isEmpty()) {
                LOGGER.info("Fetching values from cache");
                return nativeCacheImpl.entrySet().stream().collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue
                ));
            } else {
                nativeCacheImpl.putAll(DB_MOCK);
                LOGGER.info("cache was filled with values.");
            }
        }
        LOGGER.info("fetching from db");
        return DB_MOCK;
    }

    @CachePut(value = "testEmbeddedCache", key = "#key")
    public Long addNewEntry(String key, Long value) {
        LOGGER.info("adding new value to db");
        DB_MOCK.put(key, value);
        return value;
    }
}