package com.zf1976.ant.common.component.session.service;

import com.zf1976.ant.common.component.session.Session;

/**
 * @author mac
 * Create by Ant on 2020/9/29 19:14
 */
public interface SessionService {

    /**
     * 保存会话信息
     *
     * @param session 详情
     * @param token       令牌
     */
    void save(String token, Session session);

    /**
     * 更新会话
     *
     * @param token       令牌
     * @param session session
     */
    void update(String token, Session session);

    /**
     * 更新会话
     *
     * @param id id
     * @param session session
     */
    void update(Long id, Session session);

    /**
     * 更新会话
     *
     * @param id id
     * @param session session
     * @param expired  expired timestamp
     */
    void update(Long id, Session session, Long expired);

    /**
     * 强制下线
     *
     * @param id session id
     */
    void remove(Long id);

    /**
     * 强制下线
     *
     * @param token token
     */
    void remove(String token);

    /**
     * 查询会话
     *
     * @param id id
     * @return session
     */
    Session get(Long id);

    /**
     * 查询会话
     *
     * @param token token
     * @return session
     */
    Session get(String token);

    /**
     * 查询session过期时间
     *
     * @param id token
     * @return timestamp
     */
    Long getExpired(Long id);

    /**
     * 根据token获取session id
     *
     * @param token token
     * @return id
     */
    Long getSessionId(String token);

}
