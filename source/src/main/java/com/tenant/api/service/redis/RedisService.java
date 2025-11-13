package com.tenant.api.service.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

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

    public <T> void put(String key, T value, String... ignoreFields) {
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> valueMap = objectMapper.convertValue(value, Map.class);

        for (String field : ignoreFields) {
            valueMap.remove(field);
        }

        map.putAll(valueMap);
        redisTemplate.opsForHash().putAll(key, map);
        // Set TTL for the Redis key
        redisTemplate.expire(key, TTL, TimeUnit.SECONDS);
    }

    public<T> T get(String key, Class<T> clazz) {
        Map<Object, Object> map = redisTemplate.opsForHash().entries(key);
        return objectMapper.convertValue(map, clazz);
    }

    public Set<String> getKeysByPrefix(String prefix) {
        return redisTemplate.keys(prefix + "*");
    }
}
