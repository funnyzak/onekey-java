package com.github.funnyzak.onekey.biz.config.redis;

/**
 * @author Potato (silenceace@gmail.com)
 * @date 2020/10/22 5:59 下午
 * @description StringRedisSerializer
 */

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.apache.commons.lang3.SerializationException;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.nio.charset.Charset;

/**
 * 必须重写序列化器，否则@Cacheable注解的key会报类型转换错误
 *
 * @authors leon
 */

public class FastJsonRedisSerializer<T> implements RedisSerializer<T> {

    public static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

    private Class<T> clazz;

    public FastJsonRedisSerializer(Class<T> clazz) {
        super();
        this.clazz = clazz;
    }

    @Override
    public byte[] serialize(T t) throws SerializationException {
        if (t == null) {
            return new byte[0];
        }
        return JSON.toJSONString(t, SerializerFeature.WriteClassName).getBytes(DEFAULT_CHARSET);
    }

    @Override
    public T deserialize(byte[] bytes) throws SerializationException {
        if (bytes == null || bytes.length <= 0) {
            return null;
        }
        String str = new String(bytes, DEFAULT_CHARSET);
        return (T) JSON.parseObject(str, clazz);
    }

}