package com.github.funnyzak.biz.config.redis;


import com.alibaba.fastjson.parser.ParserConfig;
import com.github.funnyzak.common.db.RedisOp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;

import java.net.InetAddress;

/**
 * @author silenceace@gmail.com
 */
@Configuration
@Component
@ConditionalOnClass(RedisOp.class)
public class RedisConfig {

    final Logger logger = LoggerFactory.getLogger(this.getClass());
    /**
     * 定义redisTemplate 序列化模板。一般不用直接使用 StringRedisTemplate 即可
     *
     * @param redisConnectionFactory
     * @return
     */

    @Bean
    public RedisTemplate<String, Object> redisTemplate(LettuceConnectionFactory redisConnectionFactory) {

        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);

        FastJsonRedisSerializer<Object> fastJsonRedisSerializer = new FastJsonRedisSerializer<>(Object.class);
        // 全局开启AutoType，不建议使用
        // ParserConfig.getGlobalInstance().setAutoTypeSupport(true);
        // 建议使用这种方式，小范围指定白名单
        ParserConfig.getGlobalInstance().addAccept("org.skyf.potato");

        // 设置值（value）的序列化采用FastJsonRedisSerializer。
        redisTemplate.setValueSerializer(fastJsonRedisSerializer);
        redisTemplate.setHashValueSerializer(fastJsonRedisSerializer);
        // 设置键（key）的序列化采用StringRedisSerializer。
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());

        redisTemplate.afterPropertiesSet();

        return redisTemplate;

    }

    @Bean
    public RedisOp redisOp(RedisTemplate<String, Object> template, LettuceConnectionFactory redisConnectionFactory) {
        if (template == null) return null;

        try {
            RedisCheckDB checkDB = new RedisCheckDB(InetAddress.getByName(redisConnectionFactory.getHostName()), redisConnectionFactory.getPort(), redisConnectionFactory.getPassword(), redisConnectionFactory.getDatabase());
            if (!checkDB.isConnected()) {
                return null;
            }
            checkDB.stop();
        } catch (Exception ex) {
            logger.error("获取RedisDB失败==>", ex);
        }

        RedisOp ru = new RedisOp();
        ru.setRedisTemplate(template);
        return ru;
    }

}