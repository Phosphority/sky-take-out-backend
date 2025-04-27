package com.sky.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;


@Configuration
@Slf4j
public class RedisTemplateConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public RedisTemplate redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        log.info("开始创建redisTemplate，{}",redisConnectionFactory);
        RedisTemplate redisTemplate = new RedisTemplate<>();
        // NOTICE 设置redis的序列化器,使用StringRedisSerializer在redis中是普通字符串类型，
        // 如果要使用的是GenericJackson2JsonRedisSerializer那就要指定java类型的对象
        // 而使用Jackson2JsonRedisSerializer在redis中是Json格式的字符串,
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        // NOTICE 如果将value的序列化器也设置为StringRedisSerializer这个序列化器的话，那就意味着在redis中只能String类型的value,不能存储Integer之类的
        // 总之如果不需要去看redis中的数据的话，直接不设就好了，会默认使用jdk的序列化器，将数据以二进制的字节数据存储并回显的
//        redisTemplate.setValueSerializer(new StringRedisSerializer());
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        return redisTemplate;
    }
}
