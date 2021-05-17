package com.zf1976.ant.common.security.support.session;

/**
 * 回话存储接口
 *
 * @author mac
 * @date 2021/5/16
 */
public interface SessionStore {

    /**
     * 获取当前会话
     *
     * @return {@link Session}
     */
    Session getSession();

    /**
     * 根据token获取会话
     *
     * @param token 令牌
     * @return {@link Session}
     */
    Session getSession(String token);

    /**
     * 根据session id获取会话
     *
     * @param sessionId 会话ID
     * @return {@link Session}
     */
    Session getSession(long sessionId);

    /**
     * 以token为key，存储session
     *
     * @param token   令牌
     * @param session 会话
     */
    void storeSession(String token, Session session);

    /**
     * 删除当前会话
     */
    void removeSession();

    /**
     * 根据token删除会话
     *
     * @param token 令牌
     */
    void removeSession(String token);

    /**
     * 根据会话ID删除会话
     *
     * @param sessionId 会话ID
     */
    void removeSession(long sessionId);

    /**
     * 当前会话是否为资源所有者
     *
     * @return {@link boolean}
     */
    boolean isOwner();

}
