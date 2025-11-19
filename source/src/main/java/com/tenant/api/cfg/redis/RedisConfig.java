package com.tenant.api.cfg.redis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Configuration
@Slf4j
public class RedisConfig {
    @Value("#{'${redis.sentinel.hosts}'.split(',')}")
    private List<String> sentinelHosts;
    @Value("${redis.master.name}")
    private String masterName;
    @Value("${redis.host}")
    private String hostPort;
    @Value("${redis.type}")
    private int type;
    @Value("${redis.password}")
    private String password;

    @Bean
    public JedisConnectionFactory redisConnectionFactory() {
        if (type == 1) {
            return createStandaloneConnection();
        } else if (type == 2) {
            return createSentinelConnection();
        } else {
            throw new IllegalArgumentException("Unsupported Redis type: " + type);
        }
    }

    private JedisConnectionFactory createSentinelConnection() {
        Set<String> sentinels = new HashSet<>(sentinelHosts);
        RedisSentinelConfiguration config = new RedisSentinelConfiguration(masterName, sentinels);
        config.setPassword(password);
        log.info("Redis Sentinel config with master: {}, sentinels: {}", masterName, sentinels);
        return new JedisConnectionFactory(config);
    }

    private JedisConnectionFactory createStandaloneConnection() {
        String[] hostAndPort = hostPort.split(":");
        if (hostAndPort.length != 2) {
            throw new IllegalArgumentException("Invalid Redis hostPort config: " + hostPort);
        }
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(hostAndPort[0], Integer.parseInt(hostAndPort[1]));
        config.setPassword(password);
        config.setPassword(password);
        log.info("Redis Standalone config with host: {}, port: {}", hostAndPort[0], hostAndPort[1]);
        return new JedisConnectionFactory(config);
    }

    @Bean(name = "customRedisTemplate")
    public RedisTemplate<String, Object> redisTemplate(JedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);

        GenericJackson2JsonRedisSerializer jackson2JsonRedisSerializer = new GenericJackson2JsonRedisSerializer();

        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(jackson2JsonRedisSerializer);

        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(jackson2JsonRedisSerializer);

        template.setEnableTransactionSupport(true);

        log.info("RedisTemplate initialized with Redis type: {}", type);
        return template;
    }
}