package com.example.backend.tools;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import com.alibaba.fastjson.parser.ParserConfig;
import org.springframework.util.Assert;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class FastJsonRedisSerializer<T> implements RedisSerializer<T> {

    // 默认字符集为UTF-8
    public static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

    // 泛型类的类型
    private Class<T> clazz;

    // 静态代码块，在类加载时执行，设置全局的FastJSON配置，支持自动类型
    static {
        ParserConfig.getGlobalInstance().setAutoTypeSupport(true);
    }

    // 构造函数，传入泛型类的类型
    public FastJsonRedisSerializer(Class<T> clazz) {
        super();
        this.clazz = clazz;
    }

    // 序列化对象为字节数组
    @Override
    public byte[] serialize(T t) throws SerializationException {
        if (t == null) {
            return new byte[0];
        }
        // 使用FastJSON将对象转换为JSON字符串，并添加类名信息，然后将字符串转换为字节数组
        return JSON.toJSONString(t, SerializerFeature.WriteClassName).getBytes(DEFAULT_CHARSET);
    }

    // 反序列化字节数组为对象
    @Override
    public T deserialize(byte[] bytes) throws SerializationException {
        if (bytes == null || bytes.length <= 0) {
            return null;
        }
        // 将字节数组转换为字符串
        String str = new String(bytes, DEFAULT_CHARSET);
        // 使用FastJSON将字符串解析为对象，并指定对象的类型
        return JSON.parseObject(str, clazz);
    }

    // 获取JavaType对象，用于FastJSON解析时指定对象的类型
    protected JavaType getJavaType(Class<?> clazz) {
        return TypeFactory.defaultInstance().constructType(clazz);
    }
}
