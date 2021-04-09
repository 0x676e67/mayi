package com.zf1976.ant.common.security.support.session;

import com.power.common.util.StringUtil;
import com.zf1976.ant.common.core.constants.AuthConstants;
import com.zf1976.ant.common.core.util.RequestUtils;
import com.zf1976.ant.common.security.property.SecurityProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 身份验证会话管理器 sessionId即为用户id
 *
 * @author mac
 * Create by Ant on 2020/10/4 14:11
 */
@Component
public class DistributedSessionManager {

    private static final Logger LOG = LoggerFactory.getLogger(DistributedSessionManager.class);
    private static SecurityProperties properties;
    private static RedisTemplate<Object, Object> redisTemplate;

    public DistributedSessionManager(RedisTemplate<Object, Object> template, SecurityProperties properties) {
        DistributedSessionManager.redisTemplate = template;
        DistributedSessionManager.properties = properties;
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
                         .set(formatToken(token),
                                 session.getId(),
                                 getExpiredIn(),
                                 TimeUnit.SECONDS);
            // id指向session
            redisTemplate.opsForValue()
                         .set(formatId(session.getId()),
                                 session,
                                 getExpiredIn(),
                                 TimeUnit.SECONDS);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e.getCause());
        }
    }

    public static List<Session> selectListByIds(Collection<Long> ids) {
        List<Session> sessions = new ArrayList<>();
        if (!CollectionUtils.isEmpty(ids)) {
            ids.forEach(aLong -> {
                sessions.add(getSession(aLong));
            });

        }
        return sessions;
    }

    /**
     * 读取当前请求session
     *
     * @date 2021-03-23 12:19:55
     * @return {@link Session}
     */
    public static Session getSession(){
        try {
            final String token = getToken();
            Object o = redisTemplate.opsForValue().get(formatToken(token));
            if (ObjectUtils.isEmpty(o)) {
                return null;
            }
            // 抛出NumberFormatException
            final long sessionId = Long.parseLong(o.toString());
            return getSession(sessionId);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e.getCause());
            throw e;
        }
    }

    /**
     * 根据id读取会话
     *
     * @param sessionId session id
     * @return session
     */
    public static Session getSession(Long sessionId) {
        try {
            return (Session) redisTemplate.opsForValue().get(formatId(sessionId));
        } catch (Exception e) {
            LOG.error(e.getMessage(), e.getCause());
            throw e;
        }
    }

    /**
     * 根据id强制下线
     *
     * @param sessionId 用户id
     */
    public static void removeSession(Long sessionId) {
        try {
            Optional.ofNullable(getSession(sessionId))
                    .ifPresent(session -> {
                        redisTemplate.delete(formatId(sessionId));
                        redisTemplate.delete(formatToken(session.getToken()));
                    });
        } catch (Exception e) {
            LOG.info("user not online", e);
        }
    }

    /**
     * 强制当前用户下限
     */
    public static void removeSession(){
        try {
            final String token = getToken();
            Object o = redisTemplate.opsForValue().get(formatToken(token));
            if (ObjectUtils.isEmpty(o)) {
                return;
            }
            // 抛出NumberFormatException
            final long sessionId = Long.parseLong(o.toString());
            redisTemplate.delete(formatId(sessionId));
            redisTemplate.delete(formatToken(token));
        } catch (Exception e) {
            LOG.info("user not online", e);
        }
    }

    /**
     * 查询session过期时间
     *
     * @param sessionId token
     * @return timestamp
     */
    public static Long getExpiredTime(Long sessionId) {
        return Objects.requireNonNull(getSession(sessionId)).getExpiredTime().getTime();
    }

    /**
     * 获取当前会话id
     *
     * @return session
     */
    public static Long getSessionId(){
        return (Long) redisTemplate.opsForValue().get(formatToken(getToken()));
    }
    /**
     * owner
     *
     * @return boolean
     */
    public static boolean isOwner(){
        return Objects.requireNonNull(getSession()).getOwner();
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
    private static String getToken(){
        final String token = getAuthenticationForHeader();
        var header = token == null ? getAuthenticationForAttribute() : token;
        if (header == null) {
            throw new UnsupportedOperationException(HttpStatus.UNAUTHORIZED.getReasonPhrase());
        }
        return extractToken(header);
    }

    private static String getAuthenticationForHeader(){
        return RequestUtils.getRequest().getHeader(HttpHeaders.AUTHORIZATION);
    }

    private static String getAuthenticationForAttribute() {
        return (String) RequestUtils.getRequest().getAttribute(HttpHeaders.AUTHORIZATION);
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

    private static String formatId(Object id) {
        return properties.getPrefixSessionId() + "["+ id +"]";
    }

    private static String formatToken(Object token) {
        return properties.getPrefixSessionToken() + "[" + token + "]";
    }

    private static long getExpiredIn() { return (Integer) RequestUtils.getRequest().getAttribute(AuthConstants.SESSION_EXPIRED); }

}
