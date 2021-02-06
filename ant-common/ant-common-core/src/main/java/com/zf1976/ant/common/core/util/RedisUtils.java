package com.zf1976.ant.common.core.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author mac
 */
public class RedisUtils {

    private final static Logger LOG = LoggerFactory.getLogger(RedisUtils.class);

    private final static StringRedisTemplate REDIS_TEMPLATE = SpringContextHolder.getBean(StringRedisTemplate.class);


    public static String get(String key) {
        return REDIS_TEMPLATE.opsForValue().get(key);
    }

    public static void set(String key, String value,long expired, TimeUnit timeUnit) {
        REDIS_TEMPLATE.opsForValue().set(key, value, expired, timeUnit);
    }

    public static Boolean delete(String key) {
        return REDIS_TEMPLATE.delete(key);
    }

    public static void set(String prefix, String key, String value, long expired, TimeUnit timeUnit) {
        REDIS_TEMPLATE.opsForValue().set(prefix + key, value, expired , timeUnit);
    }

    public static String get(String prefix, String key) {
        return REDIS_TEMPLATE.opsForValue().get(prefix + key);
    }

    public static Boolean delete(String prefix, String key) {
        return REDIS_TEMPLATE.delete(prefix + key);
    }

    public static Long delete(Collection<String> keys) {
        return REDIS_TEMPLATE.delete(keys);
    }

    /**
     * 获取所有模式匹配key
     *
     * @param pattern 模式
     * @return keys
     */
    public static Set<String> scanKeys(String pattern) {
        final ScanOptions scanOptions = ScanOptions.scanOptions()
                                             .match(pattern)
                                             .build();
        Set<String> keys = new HashSet<>();
        final RedisConnectionFactory connectionFactory = REDIS_TEMPLATE.getRequiredConnectionFactory();
        try (RedisConnection connection = connectionFactory.getConnection()) {
            final Cursor<byte[]> cursor = connection.scan(scanOptions);
            while (cursor.hasNext()) {
                keys.add(new String(cursor.next()));
            }
            return keys;
        }catch (Exception e) {
            LOG.error(e.getMessage(), e.getCause());
        }
        return keys;
    }

    /**
     * 根据模式匹配 分页查询key
     *
     * @param pattern 模式
     * @param page 页
     * @param pageSize 页大小
     * @return /
     */
    public static Set<String> scanKeysForPage(String pattern, int page, int pageSize) {
        final ScanOptions scanOptions = ScanOptions.scanOptions()
                                             .match(pattern)
                                             .build();
        Set<String> keys = new HashSet<>();
        final RedisConnectionFactory connectionFactory = REDIS_TEMPLATE.getRequiredConnectionFactory();
        try (RedisConnection connection = connectionFactory.getConnection()) {
            final Cursor<byte[]> cursor = connection.scan(scanOptions);
            int tempIndex = 0;
            int fromIndex = page * pageSize;
            int toIndex = fromIndex + pageSize;
            while (cursor.hasNext()) {
                if (tempIndex >= toIndex) {
                    break;
                }
                if (tempIndex >= fromIndex) {
                    keys.add(new String(cursor.next()));
                    ++tempIndex;
                    continue;
                }
                ++tempIndex;
            }
            return keys;
        } catch (Exception e) {
            LOG.error(e.getMessage(), e.getCause());
        }
        return keys;
    }

}
