package com.zf1976.ant.common.security.support.session.manager;

import com.zf1976.ant.common.security.property.SecurityProperties;
import com.zf1976.ant.common.security.support.session.Session;
import com.zf1976.ant.common.security.support.session.exception.SessionException;
import com.zf1976.ant.common.security.support.session.repository.AbstractSessionRepository;
import com.zf1976.ant.common.security.support.session.repository.RedisSessionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.*;

/**
 * 在同一个操作中需要开启事务 或默认SessionCallable支持，在当前session完成所有操作
 * 身份验证会话管理器 sessionId即为用户id
 *
 * @author mac
 * Create by Ant on 2020/10/4 14:11
 */
@Component
public final class SessionManagement {

    private static final Logger log = LoggerFactory.getLogger("[SessionManagement]");
    private static SecurityProperties properties;
    private static RedisConnectionFactory redisConnectionFactory;
    private static AbstractSessionRepository repository;

    public SessionManagement(RedisConnectionFactory redisConnectionFactory, SecurityProperties properties) {
        SessionManagement.properties = properties;
        SessionManagement.redisConnectionFactory = redisConnectionFactory;
        this.checkStatus();
        repository = new RedisSessionRepository(redisConnectionFactory, properties);
    }

    /**
     * 根据session ID集合查询Session列表
     *
     * @param ids id集合
     * @return {@link List<Session>}
     */
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
     * 获取当前用户名
     *
     * @return {@link String}
     */
    public static String getCurrentUsername() {
        Session session = repository.getSession();
        if (session == null) {
            throw new SessionException("session expired");
        }
        return session.getUsername();
    }

    /**
     * 读取当前请求session
     *
     * @return {@link Session}
     * @date 2021-03-23 12:19:55
     */
    public static Session getSession() {
        Session session = repository.getSession();
        if (session == null) {
            throw new SessionException("session expired");
        }
        return session;
    }

    /**
     * 根据token获取Session
     *
     * @param token 令牌
     * @return {@link Session}
     */
    public static Session getSession(String token) {
        Session session = repository.getSession(token);
        if (session == null) {
            throw new SessionException("session expired");
        }
        return session;
    }

    /**
     * 根据id读取会话
     *
     * @param sessionId session id
     * @return session
     */
    public static Session getSession(Long sessionId) {
        Session session = repository.getSession(sessionId);
        if (session == null) {
            throw new SessionException("session expired");
        }
        return session;
    }

    /**
     * 强制当前用户下限
     */
    public static void removeSession() {
        try {
            repository.removeSession();
        } catch (SessionException | IOException e) {
            log.error(e.getMessage(), e.getCause());
        }
    }

    /**
     * 根据token强制下限
     *
     * @param token 令牌
     */
    public static void removeSession(String token) {
        try {
            repository.removeSession(token);
        } catch (IOException e) {
            log.error(e.getMessage(), e.getCause());
        }
    }

    /**
     * 根据id强制下线
     *
     * @param sessionId 用户id
     */
    public static void removeSession(Long sessionId) {
        if (sessionId != null) {
            try {
                repository.removeSession(sessionId);
            } catch (SessionException | IOException e) {
                log.error(e.getMessage(), e.getCause());
            }
        }
    }

    /**
     * 根据session ID查询session过期时间
     *
     * @param sessionId token
     * @return timestamp
     */
    public static Long getExpiredTime(Long sessionId) {
        return getSession(sessionId).getExpiredTime()
                                    .getTime();
    }

    /**
     * 获取当前会话有效期限
     *
     * @return {@link Long}
     */
    public static Long getExpiredTime() {
        return getSession().getExpiredTime()
                           .getTime();
    }

    /**
     * 获取当前会话id
     *
     * @return session
     */
    public static Long getSessionId() {
        return getSession().getId();
    }

    /**
     * owner
     *
     * @return boolean
     */
    public static boolean isOwner() {
        return getSession().getOwner();
    }

    private void checkStatus() {
        Assert.notNull(properties, "security properties is null!");
        Assert.notNull(redisConnectionFactory, "redis connection factory is null");
    }

}
