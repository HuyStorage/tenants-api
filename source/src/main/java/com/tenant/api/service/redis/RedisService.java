package com.tenant.api.service.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
public class RedisService {
    public static final int TTL = 7200; // 2 hours

    @Autowired
    @Qualifier("customRedisTemplate")
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private ObjectMapper objectMapper;

    public String buildKey(String... parts) {
        return String.join("::", parts);
    }

    public <T> void put(String key, T value, String... ignoreFields) {
        putInternal(key, value, TTL, ignoreFields);
    }

    public <T> void put(String key, T value, Integer ttl, String... ignoreFields) {
        putInternal(key, value, ttl, ignoreFields);
    }

    private <T> void putInternal(String key, T value, int ttl, String... ignoreFields) {
        if (value instanceof String || value instanceof Number || value instanceof Boolean) {
            redisTemplate.opsForValue().set(key, String.valueOf(value), ttl, TimeUnit.SECONDS);
            return;
        }

        Map<String, Object> valueMap = objectMapper.convertValue(value, new TypeReference<>() {});

        for (String field : ignoreFields) {
            valueMap.remove(field);
        }

        Map<String, Object> stringKeyMap = new HashMap<>();
        for (Map.Entry<String, Object> entry : valueMap.entrySet()) {
            stringKeyMap.put(entry.getKey(), entry.getValue());
        }

        redisTemplate.opsForHash().putAll(key, stringKeyMap);
        redisTemplate.expire(key, ttl, TimeUnit.SECONDS);
    }

    public <T> T get(String key, Class<T> clazz) {
        if (clazz == String.class || clazz == Integer.class || clazz == Long.class || clazz == Boolean.class) {
            Object raw = redisTemplate.opsForValue().get(key);
            if (raw == null) return null;
            return clazz.cast(raw);
        }

        Map<Object, Object> map = redisTemplate.opsForHash().entries(key);
        if (map.isEmpty()) {
            return null;
        }

        Map<String, Object> stringKeyMap = new HashMap<>();
        for (Map.Entry<Object, Object> entry : map.entrySet()) {
            stringKeyMap.put(String.valueOf(entry.getKey()), entry.getValue());
        }

        return objectMapper.convertValue(stringKeyMap, clazz);
    }

    public Set<String> getKeysByPrefix(String prefix) {
        return redisTemplate.keys(prefix + "*");
    }

    public void delete(String key) {
        redisTemplate.delete(key);
    }

    public void deleteKeys(Collection<String> keys) {
        redisTemplate.delete(keys);
    }

    public void deleteByPrefix(String prefix) {
        Set<String> keys = redisTemplate.keys(prefix + "*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }

    public void refreshTTL(String key, int ttl) {
        redisTemplate.expire(key, ttl, TimeUnit.SECONDS);
    }
}
