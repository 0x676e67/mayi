package com.zf1976.ant.common.security.support.session;

import com.power.common.util.StringUtil;
import com.zf1976.ant.common.core.constants.AuthConstants;
import com.zf1976.ant.common.core.util.RequestUtils;
import com.zf1976.ant.common.security.property.SecurityProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisConnectionUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 身份验证会话管理器 sessionId即为用户id
 *
 * @author mac
 * Create by Ant on 2020/10/4 14:11
 */
@Component
public class RedisSessionHolder {

    private static final Logger LOG = LoggerFactory.getLogger(RedisSessionHolder.class);
    private static SecurityProperties properties;
    private static RedisTemplate<Object, Object> redisTemplate;

    public RedisSessionHolder(RedisTemplate<Object, Object> template, SecurityProperties securityProperties) {
        RedisSessionHolder.redisTemplate = template;
        RedisSessionHolder.properties = securityProperties;
    }

    /**
     * 存储当前请求会话
     *
     * @param token              token
     * @param session        用户会话details
     */
    public static void storeSession(String token, Session session) {
        try {
            // token指向id
            redisTemplate.opsForValue()
                         .set(formatSessionToken(token),
                                 session.getId(),
                                 getExpired(),
                                 TimeUnit.SECONDS);
            // id指向session
            redisTemplate.opsForValue()
                         .set(formatSessionId(session.getId()),
                                 session,
                                 getExpired(),
                                 TimeUnit.SECONDS);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e.getCause());
        }
    }

    /**
     * 读取当前请求session
     *
     * @date 2021-03-23 12:19:55
     * @return {@link Session}
     */
    public static Session readSession(){
        return readSession(token());
    }

    /**
     * 根据id读取会话
     *
     * @param sessionId session id
     * @return session
     */
    public static Session readSession(Long sessionId) {
        try {
            return (Session) redisTemplate.opsForValue().get(formatSessionId(sessionId));
        } catch (Exception e) {
            LOG.error(e.getMessage(), e.getCause());
            return null;
        }
    }

    /**
     * 根据token读取会话
     *
     * @param token token
     * @return session
     */
    public static Session readSession(String token) {
        try {
            Object o = redisTemplate.opsForValue().get(formatSessionToken(token));
            if (ObjectUtils.isEmpty(o)) {
                return null;
            }
            // 抛出NumberFormatException
            final long sessionId = Long.parseLong(o.toString());
            return readSession(sessionId);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e.getCause());
            return null;
        }
    }


    /**
     * 强制下线
     *
     * @param token token
     */
    public static void removeSession(String token) {

        try {
            if (token != null) {
                Object o = redisTemplate.opsForValue().get(formatSessionToken(token));
                if (ObjectUtils.isEmpty(o)) {
                    return;
                }
                // 抛出NumberFormatException
                final long sessionId = Long.parseLong(o.toString());
                redisTemplate.delete(formatSessionId(sessionId));
                redisTemplate.delete(formatSessionToken(token));
            }
        } catch (Exception e) {
            LOG.info("user not online", e);
        }
    }

    /**
     * 根据id强制下线
     *
     * @param sessionId 用户id
     */
    public static void removeSession(Long sessionId) {
        try {
            Optional.ofNullable(readSession(sessionId))
                    .ifPresent(session -> {
                        redisTemplate.delete(formatSessionId(sessionId));
                        redisTemplate.delete(formatSessionToken(session.getToken()));
                    });
        } catch (Exception e) {
            LOG.info("user not online", e);
        }
    }

    /**
     * 强制当前用户下限
     */
    public static void removeSession(){
        removeSession(token());
    }

    /**
     * 查询session过期时间
     *
     * @param sessionId token
     * @return timestamp
     */
    public static Long getExpiredTime(Long sessionId) {
        return Objects.requireNonNull(readSession(sessionId)).getExpiredTime().getTime();
    }

    /**
     * 获取当前会话id
     *
     * @return session
     */
    public static Long getSessionId(){
        return (Long) redisTemplate.opsForValue().get(formatSessionToken(token()));
    }

    /**
     * 获取当前会话用户名
     *
     * @return username
     */
    public static String username() {
        return readSession().getUsername();
    }

    /**
     * owner
     *
     * @return boolean
     */
    public static boolean isOwner(){
        return readSession().getOwner();
    }

    /**
     * owner
     *
     * @param username 用户名
     * @return boolean
     */
    public static boolean isOwner(String username) {
        return ObjectUtils.nullSafeEquals(properties.getOwner(), username);
    }

    /**
     * 获取token
     *
     * @return token
     */
    private static String token(){
        var header = RequestUtils.getRequest().getHeader(HttpHeaders.AUTHORIZATION);
        if (header == null) {
            throw new UnsupportedOperationException(HttpStatus.UNAUTHORIZED.getReasonPhrase());
        }
        return extractToken(header);
    }

    /**
     * 提取token
     *
     * @param header request header
     * @return token
     */
    private static String extractToken(String header) {
        return header.replace("Bearer ", StringUtil.ENMPTY);
    }

    private static String formatSessionId(Object id) {
        return properties.getPrefixSessionId() + "["+ id +"]";
    }

    private static String formatSessionToken(Object token) {
        return properties.getPrefixSessionToken() + "[" + token + "]";
    }

    private static long getExpired() {
        return (Integer) RequestUtils.getRequest().getAttribute(AuthConstants.EXPIRED);
    }


    /**
     * 扫描所有包含关键字的条目
     *
     * @param keyword 关键字
     * @return entry
     */
    @Deprecated
    protected Set<String> scanKeys(String keyword) {
        ScanOptions options = ScanOptions.scanOptions()
                                         .match(keyword + "*")
                                         .build();
        RedisConnectionFactory factory = redisTemplate.getRequiredConnectionFactory();
        RedisConnection connection = factory.getConnection();
        try  {
            Cursor<byte[]> cursor = connection.scan(options);
            HashSet<String> result = new HashSet<>();
            while (cursor.hasNext()) {
                result.add(new String(cursor.next()));
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            RedisConnectionUtils.releaseConnection(connection, factory);
        }
        return new HashSet<>();
    }
}
