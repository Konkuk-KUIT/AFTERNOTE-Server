package com.example.afternote.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {
    
    @Bean
    public RedisTemplate<String, Long> redisTemplateLong(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Long> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        
        // Key는 String으로 저장 (redis-cli에서 읽을 수 있게)
        template.setKeySerializer(new StringRedisSerializer());
        
        // Value는 Long을 문자열로 변환해서 저장
        template.setValueSerializer(new GenericToStringSerializer<>(Long.class));
        
        return template;
    }
}
