package com.zf1976.mayi.common.core.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.Map;


/**
 * @author mac
 * Create by Ant on 2020/10/3 14:41
 */
@Configuration
@EnableCaching
@ConditionalOnClass(RedisOperations.class)
@EnableConfigurationProperties(RedisProperties.class)
@SuppressWarnings("rawtypes, unchecked")
public class RedisConfiguration extends CachingConfigurerSupport {

    @Bean(name = "template")
    public RedisTemplate<Object, Object> template(RedisConnectionFactory factory) {
        RedisTemplate<Object, Object> template = new RedisTemplate<>();
        final Jackson2JsonRedisSerializer<Object> objectJackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
        return this.setRedisSerializer(template, factory, objectJackson2JsonRedisSerializer);
    }

    @Bean
    public RedisTemplate<Object, Map<Object, Object>> mapRedisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<Object, Map<Object, Object>> template = new RedisTemplate<>();
        Jackson2JsonRedisSerializer mapJackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Map.class);
        return this.setRedisSerializer(template, factory, mapJackson2JsonRedisSerializer);
    }


    private RedisTemplate setRedisSerializer(RedisTemplate<?,?> template, RedisConnectionFactory factory, Jackson2JsonRedisSerializer jackson2JsonRedisSerializer) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.activateDefaultTyping(new LaissezFaireSubTypeValidator(), ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(objectMapper);
        StringRedisSerializer stringSerial = new StringRedisSerializer();
        template.setConnectionFactory(factory);
        template.setKeySerializer(stringSerial);
        template.setValueSerializer(jackson2JsonRedisSerializer);
        template.setHashKeySerializer(stringSerial);
        template.setHashValueSerializer(jackson2JsonRedisSerializer);
        template.afterPropertiesSet();
        return template;
    }

}

