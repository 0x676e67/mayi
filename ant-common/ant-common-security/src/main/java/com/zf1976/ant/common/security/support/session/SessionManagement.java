package com.zf1976.ant.common.security.support.session;

import com.power.common.util.StringUtil;
import com.zf1976.ant.common.core.constants.AuthConstants;
import com.zf1976.ant.common.core.util.RequestUtil;
import com.zf1976.ant.common.security.property.SecurityProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 在同一个操作中需要开启事务 或默认SessionCallable支持，在当前session完成所有操作
 * 身份验证会话管理器 sessionId即为用户id
 *
 * @author mac
 * Create by Ant on 2020/10/4 14:11
 */
@Component
public class SessionManagement {

    private static final Logger LOG = LoggerFactory.getLogger("DistributedSession-[Log]");
    private static SecurityProperties properties;
    private static RedisTemplate<Object, Object> redisTemplate;

    public SessionManagement(RedisTemplate<Object, Object> template, SecurityProperties properties) {
        SessionManagement.redisTemplate = template;
        SessionManagement.properties = properties;
    }

    /**
     * 存储当前请求会话
     *
     * @param token              token
     * @param session        用户会话details
     */
    @SuppressWarnings("unchecked")
    public static void storeSession(String token, Session session) {
        redisTemplate.execute(new SessionCallback<>() {
            @Override
            public Object execute(@Nullable RedisOperations redisOperations) throws DataAccessException {
                Assert.notNull(redisOperations,"redis connection nullable in:" + SessionManagement.class.getName());
                try {
                    // token指向id
                    redisOperations.opsForValue()
                                 .set(formatToken(token),
                                         session.getId(),
                                         getExpiredIn(),
                                         TimeUnit.SECONDS);
                    // id指向session
                    redisOperations.opsForValue()
                                 .set(formatId(session.getId()),
                                         session,
                                         getExpiredIn(),
                                         TimeUnit.SECONDS);
                } catch (Exception e) {
                    LOG.error(e.getMessage(), e.getCause());
                }
                return null;
            }
        });
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

    public static String getUsername() {
        return Objects.requireNonNull(getSession()).getUsername();
    }

    /**
     * 读取当前请求session
     *
     * @date 2021-03-23 12:19:55
     * @return {@link Session}
     */
    @NonNull
    public static Session getSession(){
        try {
            final String token = getToken();
            Object o = redisTemplate.opsForValue().get(formatToken(token));
            if (ObjectUtils.isEmpty(o)) {
                throw new UnsupportedOperationException("session expired");
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
    @NonNull
    public static Session getSession(Long sessionId) {
        try {
            final Object o = redisTemplate.opsForValue().get(formatId(sessionId));
            if (o == null) {
                throw new UnsupportedOperationException("session expired");
            }
            return (Session) o;
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
    @SuppressWarnings("unchecked")
    public static void removeSession(Long sessionId) {
        redisTemplate.execute(new SessionCallback<>() {
            @Override
            public  Object execute(@Nullable RedisOperations redisOperations) throws DataAccessException {
                Assert.notNull(redisOperations,"redis connection nullable in:" + SessionManagement.class.getName());
                try {
                    Session session = getSession(sessionId);
                    redisOperations.delete(formatId(sessionId));
                    redisOperations.delete(formatToken(session.getToken()));
                } catch (Exception e) {
                    LOG.info("user not online", e);
                }
                return null;
            }
        });
    }

    /**
     * 强制当前用户下限
     */
    @SuppressWarnings("unchecked")
    public static void removeSession(){
        final String token = getToken();
        final Object obj = redisTemplate.opsForValue().get(formatToken(token));
        if (ObjectUtils.isEmpty(obj)) {
            return;
        }
        // 抛出NumberFormatException
        final long sessionId = Long.parseLong(obj.toString());
        redisTemplate.execute(new SessionCallback<>() {
            @Override
            public  Object execute(@Nullable RedisOperations redisOperations) throws DataAccessException {
                Assert.notNull(redisOperations,"redis connection nullable in:" + SessionManagement.class.getName());
                try {
                    redisOperations.delete(formatId(sessionId));
                    redisOperations.delete(formatToken(token));
                } catch (Exception e) {
                    LOG.info("user not online", e);
                }
                return null;
            }
        });
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
        final Object o = redisTemplate.opsForValue().get(formatToken(getToken()));
        Assert.isInstanceOf(Integer.class, o);
        Assert.notNull(o,"session id cannot been null");
        return Long.parseLong(o.toString());
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
        return RequestUtil.getRequest().getHeader(HttpHeaders.AUTHORIZATION);
    }

    private static String getAuthenticationForAttribute() {
        return (String) RequestUtil.getRequest().getAttribute(HttpHeaders.AUTHORIZATION);
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

    private static long getExpiredIn() { return (Integer) RequestUtil.getRequest().getAttribute(AuthConstants.SESSION_EXPIRED); }

}
