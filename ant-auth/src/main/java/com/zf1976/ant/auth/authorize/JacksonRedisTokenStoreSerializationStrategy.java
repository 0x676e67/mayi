package com.zf1976.ant.auth.authorize;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStoreSerializationStrategy;

import java.nio.charset.StandardCharsets;

/**
 * @author mac
 * @date 2021/2/11
 **/
public class JacksonRedisTokenStoreSerializationStrategy implements RedisTokenStoreSerializationStrategy {

    protected static ObjectMapper mapper = new ObjectMapper();

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> aClass) {
        Preconditions.checkArgument(aClass != null, "clazz can't be null");
        if (bytes == null || bytes.length == 0) {
            return null;
        }

        try {
            return mapper.readValue(new String(bytes, StandardCharsets.UTF_8), aClass);
        } catch (Exception ex) {
            throw new SerializationException("Could not serialize: " + ex.getMessage(), ex);
        }
    }

    @Override
    public String deserializeString(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        return new String(bytes, StandardCharsets.UTF_8);
    }

    @Override
    public byte[] serialize(Object o) {
        if (o == null) {
            return new byte[0];
        }

        try {
            return mapper.writeValueAsBytes(o);
        } catch (Exception ex) {
            throw new SerializationException("Could not serialize: " + ex.getMessage(), ex);
        }
    }

    @Override
    public byte[] serialize(String s) {
        if (s == null) {
            return new byte[0];
        }

        try {
            return mapper.writeValueAsBytes(s);
        } catch (Exception ex) {
            throw new SerializationException("Could not serialize: " + ex.getMessage(), ex);
        }
    }
}
