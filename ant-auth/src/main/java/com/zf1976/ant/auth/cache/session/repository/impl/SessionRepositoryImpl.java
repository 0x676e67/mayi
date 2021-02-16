package com.zf1976.ant.auth.cache.session.repository.impl;

import com.zf1976.ant.auth.AuthorizationConstants;
import com.zf1976.ant.auth.cache.session.Session;
import com.zf1976.ant.auth.cache.session.repository.SessionRepository;
import com.zf1976.ant.common.core.dev.SecurityProperties;
import com.zf1976.ant.common.core.util.RequestUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisConnectionUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.NumberUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author mac
 * Create by Ant on 2020/9/29 07:20
 */
@Slf4j
@Service
public class SessionRepositoryImpl implements SessionRepository {

    private final SecurityProperties securityProperties;
    private final RedisTemplate<Object, Object> redisTemplate;


    private SessionRepositoryImpl(RedisTemplate<Object, Object> template, SecurityProperties securityProperties) {
        this.redisTemplate = template;
        this.securityProperties = securityProperties;
        this.checkState();
    }

    private void checkState() {
        Assert.notNull(redisTemplate, "repository cannot be null");
        Assert.notNull(securityProperties, "config cannot be null");
    }

    @Override
    public void saveSessionById(Long id, Session session) {
        redisTemplate.opsForValue()
                     .set(this.formatSessionId(id),
                             session,
                             this.getTokenExpired(),
                             TimeUnit.SECONDS);
    }

    @Override
    public void savaIdByToken(String token, Long id) {
        redisTemplate.opsForValue()
                     .set(this.formatSessionToken(token),
                             id,
                             this.getTokenExpired(),
                             TimeUnit.SECONDS);
    }

    @Override
    public void updateSessionById(Long id, Session session, long expired) {
        redisTemplate.opsForValue()
                     .set(this.formatSessionId(id),
                             session,
                             expired,
                             TimeUnit.SECONDS);
        redisTemplate.opsForValue()
                     .set(this.formatSessionToken(session.getToken()),
                             id,
                             expired,
                             TimeUnit.SECONDS);
    }

    @Override
    public Long selectIdByToken(String token) {
        Object o = redisTemplate.opsForValue().get(this.formatSessionToken(token));
        if (o == null) {
            return -1L;
        }
        return NumberUtils.parseNumber(o.toString(), Long.class);
    }

    @Override
    public Session selectSessionById(Long id) {
        return (Session) redisTemplate.opsForValue().get(this.formatSessionId(id));
    }

    @Override
    public List<Session> selectSessionByIds(Collection<Long> ids) {
        List<Session> sessionList = new ArrayList<>();
        for (Long id : ids) {
            sessionList.add(this.selectSessionById(id));
        }
        return sessionList;
    }

    @Override
    public Long selectSessionExpiredById(Long id) {
        return redisTemplate.getExpire(this.formatSessionId(id), TimeUnit.SECONDS);
    }

    @Override
    public Long selectSessionExpiredById(Long id, TimeUnit timeUnit) {
        return redisTemplate.getExpire(this.formatSessionId(id), timeUnit);
    }

    @Override
    public void deleteIdByToken(String token) {
        redisTemplate.delete(this.formatSessionToken(token));
    }

    @Override
    public void deleteSessionById(Long id) {
        redisTemplate.delete(this.formatSessionId(id));
    }

    @Override
    public Boolean hasSession(Long token) {
        return redisTemplate.hasKey(this.formatSessionId(token));
    }

    private String formatSessionId(Object id) {
        return securityProperties.getPrefixSessionId() + "["+ id +"]";
    }

    private String formatSessionToken(Object token) {
        return securityProperties.getPrefixSessionToken() + "[" + token + "]";
    }

    private long getTokenExpired() {
        return (Integer) RequestUtils.getRequest()
                                     .getAttribute(AuthorizationConstants.EXPIRED);
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
