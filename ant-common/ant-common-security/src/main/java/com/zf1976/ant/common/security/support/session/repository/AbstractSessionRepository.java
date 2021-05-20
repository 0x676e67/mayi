package com.zf1976.ant.common.security.support.session.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zf1976.ant.common.core.util.JSONUtil;
import com.zf1976.ant.common.security.property.SecurityProperties;
import com.zf1976.ant.common.security.support.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.io.IOException;
import java.util.Optional;


/**
 * 回话存储接口
 *
 * @author mac
 * @date 2021/5/16
 */
public abstract class AbstractSessionRepository {

    private final static String ID_TO_SESSION = "id_to_session:";
    private final static String ACCESS_TO_SESSION = "access_to_session:";
    private final static String LOGOUT_URL = "http://localhost:8888/oauth/logout";
    protected final RedisConnectionFactory connectionFactory;
    protected final ObjectMapper objectMapper = JSONUtil.getJsonMapper();
    private final Logger logger = LoggerFactory.getLogger("[SessionStore]");
    private final StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
    private SecurityProperties properties;

    protected AbstractSessionRepository(RedisConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    protected AbstractSessionRepository(RedisConnectionFactory connectionFactory, SecurityProperties properties) {
        this(connectionFactory);
        this.properties = properties;
    }

    /**
     * 获取当前会话
     *
     * @return {@link Session}
     */
    public abstract Optional<Session> getSession();

    /**
     * 根据token获取会话
     *
     * @param token 令牌
     * @return {@link Session}
     */
    public abstract Optional<Session> getSession(String token);

    /**
     * 根据session id获取会话
     *
     * @param sessionId 会话ID
     * @return {@link Session}
     */
    public abstract Optional<Session> getSession(long sessionId);

    /**
     * 删除当前会话
     */
    public abstract void removeSession() throws IOException;

    /**
     * 根据token删除会话
     *
     * @param token 令牌
     */
    public abstract void removeSession(String token) throws IOException;

    /**
     * 根据会话ID删除会话
     *
     * @param sessionId 会话ID
     */
    public abstract void removeSession(long sessionId) throws IOException;

    protected String getLogoutUrl() {
        if (this.properties == null) {
            return LOGOUT_URL;
        }
        return this.properties.getLogoutUrl();
    }

    protected String getIdToSessionKey(long object) {
        if (this.properties == null) {
            return ID_TO_SESSION + object;
        }
        return properties.getPrefixSessionId() + object;
    }

    protected String getAccessToSessionKey(String object) {
        if (this.properties == null) {
            return ACCESS_TO_SESSION + object;
        }
        return properties.getPrefixSessionToken() + object;
    }

    protected byte[] serializeKey(String object) {
        return this.stringRedisSerializer.serialize(object);
    }

    protected byte[] serialize(Object object) {
        try {
            return this.objectMapper.writeValueAsBytes(object);
        } catch (JsonProcessingException e) {
            logger.error(e.getMessage(), e.getCause());
        }
        return new byte[0];
    }

    protected Session deserialize(byte[] data) {
        try {
            return this.objectMapper.readValue(data, Session.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
