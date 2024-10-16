package jpabook.trello_project.config.redis;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@EnableCaching
public class RedisManagerConfig {

    @Value("${REDIS_HOST}")
    private String host;

    @Value("${REDIS_PORT}")
    private int port;

    @Value("${REDIS_PW}")
    private String password;

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        // 키 직렬화 설정: 문자열 직렬화 사용
        RedisSerializationContext.SerializationPair<String> keySerializer = RedisSerializationContext
                .SerializationPair
                .fromSerializer(new StringRedisSerializer());

        // 값 직렬화 설정: JSON 직렬화 사용
        RedisSerializationContext.SerializationPair<Object> valueSerializer = RedisSerializationContext
                .SerializationPair
                .fromSerializer(new GenericJackson2JsonRedisSerializer());

        // RedisCacheConfiguration에 직렬화 방식 설정
        RedisCacheConfiguration cacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(keySerializer)  // 키 직렬화 설정
                .serializeValuesWith(valueSerializer);  // 값 직렬화 설정

        return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(cacheConfiguration)  // 캐시 설정 적용
                .build();
    }

    // RedisTemplate 빈 설정 추가
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);

        // 키와 값의 직렬화 방식 설정
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());

        return redisTemplate;
    }
}
